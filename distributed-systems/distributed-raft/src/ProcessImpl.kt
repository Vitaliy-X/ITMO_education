package raft

import raft.Message.AppendEntryResult
import raft.Message.AppendEntryRpc
import raft.Message.ClientCommandResult
import raft.Message.ClientCommandRpc
import raft.Message.RequestVoteResult
import raft.Message.RequestVoteRpc
import java.util.ArrayDeque
import java.util.HashSet

/**
 * Raft algorithm implementation.
 * All functions are called from the single main thread.
 *
 * @author Vitaliy Gavrilyuk
 */
class ProcessImpl(private val env: Environment) : Process {
    private val storage = env.storage
    private val machine = env.machine  // state machine

    private enum class Role {
        FOLLOWER,
        CANDIDATE,
        LEADER,
    }

    private class LeaderState(
        val nextIndex: IntArray,
        val matchIndex: IntArray,
        val replyIndexes: HashSet<Int>,
    )

    private companion object {
        const val NO_LEADER = -1
    }

    private var role = Role.FOLLOWER

    private var currentTerm: Int
    private var votedFor: Int?

    private var leaderId = NO_LEADER

    private var commitIndex = 0
    private var lastApplied = 0

    private val commandQueue = ArrayDeque<Command>()
    private val votes = HashSet<Int>()

    private var leaderState: LeaderState? = null

    init {
        val state = storage.readPersistentState()
        currentTerm = state.currentTerm
        votedFor = state.votedFor
        env.startTimeout(Timeout.ELECTION_TIMEOUT)
    }

    override fun onTimeout() {
        if (role == Role.LEADER) {
            broadcastAppend()
            env.startTimeout(Timeout.LEADER_HEARTBEAT_PERIOD)
        } else {
            startElection()
        }
    }

    override fun onMessage(srcId: Int, message: Message) {
        when (message) {
            is AppendEntryRpc -> onAppendEntry(srcId, message)
            is AppendEntryResult -> onAppendResult(srcId, message)
            is RequestVoteRpc -> onVoteRequest(srcId, message)
            is RequestVoteResult -> onVoteResult(srcId, message)
            is ClientCommandRpc -> onForwardedCommand(message)
            is ClientCommandResult -> onCommandResult(srcId, message)
        }
    }

    override fun onClientCommand(command: Command) {
        if (role == Role.LEADER) {
            appendCommand(command)
            return
        }
        if (role == Role.FOLLOWER) {
            handleQueuedCommand(command, true)
            return
        }
        handleQueuedCommand(command, false)
    }

    private fun onAppendEntry(srcId: Int, rpc: AppendEntryRpc) {
        if (rpc.term < currentTerm) {
            env.send(srcId, AppendEntryResult(currentTerm, null))
            return
        }

        if (rpc.term > currentTerm) {
            saveTermVote(rpc.term, null)
        }

        becomeFollower(srcId)

        if (!hasLog(rpc.prevLogId)) {
            env.send(srcId, AppendEntryResult(currentTerm, null))
            env.startTimeout(Timeout.ELECTION_TIMEOUT)
            return
        }

        if (rpc.entry != null) {
            dropRepliesFrom(rpc.entry.id.index)
            storage.appendLogEntry(rpc.entry)
        }

        val localLast = storage.readLastLogId().index
        val newCommit = if (rpc.leaderCommit < localLast) rpc.leaderCommit else localLast
        if (newCommit > commitIndex) {
            commitIndex = newCommit
            applyCommitted()
        }

        val lastOkIndex = if (rpc.entry == null) {
            rpc.prevLogId.index
        } else {
            rpc.entry.id.index
        }

        env.send(srcId, AppendEntryResult(currentTerm, lastOkIndex))
        flushCommands()
        env.startTimeout(Timeout.ELECTION_TIMEOUT)
    }

    private fun onAppendResult(srcId: Int, result: AppendEntryResult) {
        if (result.term < currentTerm) {
            return
        }

        if (result.term > currentTerm) {
            saveTermVote(result.term, null)
            becomeFollower(NO_LEADER)
            env.startTimeout(Timeout.ELECTION_TIMEOUT)
            return
        }

        if (role != Role.LEADER) {
            return
        }

        val state = leaderState ?: return

        if (result.lastIndex == null) {
            val prev = state.nextIndex[srcId]
            state.nextIndex[srcId] = if (prev > 1) prev - 1 else 1
            sendAppend(srcId)
            return
        }

        val localLast = storage.readLastLogId().index
        val ackIndex = if (result.lastIndex < localLast) result.lastIndex else localLast

        if (state.matchIndex[srcId] < ackIndex) {
            state.matchIndex[srcId] = ackIndex
        }
        if (state.nextIndex[srcId] < ackIndex + 1) {
            state.nextIndex[srcId] = ackIndex + 1
        }

        updateLeaderCommit()

        if (state.nextIndex[srcId] <= storage.readLastLogId().index) {
            sendAppend(srcId)
        }
    }


    private fun onVoteRequest(srcId: Int, rpc: RequestVoteRpc) {
        if (rpc.term < currentTerm) {
            env.send(srcId, RequestVoteResult(currentTerm, false))
            return
        }

        if (rpc.term > currentTerm) {
            saveTermVote(rpc.term, null)
            becomeFollower(NO_LEADER)
            env.startTimeout(Timeout.ELECTION_TIMEOUT)
        }

        val canVote = votedFor == null || votedFor == srcId
        val upToDate = isUpToDate(rpc.lastLogId)
        val grant = canVote && upToDate

        if (grant && votedFor != srcId) {
            saveTermVote(currentTerm, srcId)
        }

        env.send(srcId, RequestVoteResult(currentTerm, grant))
    }

    private fun onVoteResult(srcId: Int, result: RequestVoteResult) {
        if (result.term < currentTerm) {
            return
        }

        if (result.term > currentTerm) {
            saveTermVote(result.term, null)
            becomeFollower(NO_LEADER)
            env.startTimeout(Timeout.ELECTION_TIMEOUT)
            return
        }

        if (role != Role.CANDIDATE) {
            return
        }

        if (!result.voteGranted) {
            return
        }

        votes.add(srcId)
        if (votes.size > env.nProcesses / 2) {
            becomeLeader()
        }
    }

    private fun onForwardedCommand(rpc: ClientCommandRpc) {
        if (rpc.term > currentTerm) {
            saveTermVote(rpc.term, null)
            becomeFollower(NO_LEADER)
            env.startTimeout(Timeout.ELECTION_TIMEOUT)
        }

        onClientCommand(rpc.command)
    }

    private fun onCommandResult(srcId: Int, result: ClientCommandResult) {
        env.onClientCommandResult(result.result)

        if (result.term > currentTerm) {
            saveTermVote(result.term, null)
            becomeFollower(srcId)
            env.startTimeout(Timeout.ELECTION_TIMEOUT)
            flushCommands()
        }
    }

    private fun handleQueuedCommand(command: Command, mayForward: Boolean) {
        if (mayForward && leaderId != NO_LEADER) {
            env.send(leaderId, ClientCommandRpc(currentTerm, command))
            return
        }
        commandQueue.addLast(command)
    }

    private fun startElection() {
        role = Role.CANDIDATE
        leaderId = NO_LEADER
        leaderState = null

        saveTermVote(currentTerm + 1, env.processId)

        votes.clear()
        votes.add(env.processId)

        val lastLogId = storage.readLastLogId()
        var peer = 1
        while (peer <= env.nProcesses) {
            if (peer != env.processId) {
                env.send(peer, RequestVoteRpc(currentTerm, lastLogId))
            }
            peer++
        }

        env.startTimeout(Timeout.ELECTION_TIMEOUT)

        if (votes.size > env.nProcesses / 2) {
            becomeLeader()
        }
    }

    private fun becomeFollower(newLeaderId: Int) {
        role = Role.FOLLOWER
        leaderId = newLeaderId
        votes.clear()
        leaderState = null
    }

    private fun becomeLeader() {
        role = Role.LEADER
        leaderId = env.processId
        votes.clear()

        val lastLog = storage.readLastLogId().index
        val nextIndex = IntArray(env.nProcesses + 1)
        val matchIndex = IntArray(env.nProcesses + 1)
        val replyIndexes = HashSet<Int>()

        var peer = 1
        while (peer <= env.nProcesses) {
            nextIndex[peer] = lastLog + 1
            matchIndex[peer] = 0
            peer++
        }

        matchIndex[env.processId] = lastLog
        nextIndex[env.processId] = lastLog + 1

        leaderState = LeaderState(nextIndex, matchIndex, replyIndexes)

        broadcastAppend()
        env.startTimeout(Timeout.LEADER_HEARTBEAT_PERIOD)

        while (!commandQueue.isEmpty()) {
            appendCommand(commandQueue.removeFirst())
        }
    }

    private fun appendCommand(command: Command) {
        val state = leaderState
        if (state == null) {
            return
        }

        val prevId = storage.readLastLogId()
        val entry = LogEntry(
            LogId(prevId.index + 1, currentTerm),
            command,
        )

        storage.appendLogEntry(entry)
        state.replyIndexes.add(entry.id.index)

        state.matchIndex[env.processId] = entry.id.index
        state.nextIndex[env.processId] = entry.id.index + 1

        var peer = 1
        while (peer <= env.nProcesses) {
            if (peer != env.processId && state.nextIndex[peer] == entry.id.index) {
                env.send(peer, AppendEntryRpc(currentTerm, prevId, commitIndex, entry))
            }
            peer++
        }
    }

    private fun broadcastAppend() {
        if (role != Role.LEADER) {
            return
        }

        var peer = 1
        while (peer <= env.nProcesses) {
            if (peer != env.processId) {
                sendAppend(peer)
            }
            peer++
        }
    }

    private fun sendAppend(peerId: Int) {
        if (role != Role.LEADER) {
            return
        }

        val state = leaderState
        if (state == null) {
            return
        }

        val lastLog = storage.readLastLogId().index
        var next = state.nextIndex[peerId]
        if (next < 1) {
            next = 1
        }
        if (next > lastLog + 1) {
            next = lastLog + 1
        }
        state.nextIndex[peerId] = next

        val prevId = if (next == 1) {
            START_LOG_ID
        } else {
            val prev = storage.readLog(next - 1)
            prev?.id ?: START_LOG_ID
        }

        val entry = storage.readLog(next)
        env.send(peerId, AppendEntryRpc(currentTerm, prevId, commitIndex, entry))
    }

    private fun updateLeaderCommit() {
        if (role != Role.LEADER) {
            return
        }

        val lastLog = storage.readLastLogId().index
        var newCommit = commitIndex
        var index = commitIndex + 1

        while (index <= lastLog) {
            if (canCommit(index)) {
                newCommit = index
            }
            index++
        }

        if (newCommit > commitIndex) {
            commitIndex = newCommit
            applyCommitted()
        }
    }

    private fun canCommit(index: Int): Boolean {
        val state = leaderState ?: return false

        val entry = storage.readLog(index) ?: return false
        if (entry.id.term != currentTerm) {
            return false
        }

        var replicas = 1
        var peer = 1
        while (peer <= env.nProcesses) {
            if (peer != env.processId && state.matchIndex[peer] >= index) {  //
                replicas++
            }
            peer++
        }

        return replicas > env.nProcesses / 2
    }

    private fun applyCommitted() {
        val state = leaderState

        while (lastApplied < commitIndex) {
            lastApplied++
            val entry = storage.readLog(lastApplied) ?: continue
            val result = machine.apply(entry.command)

            val shouldReply = state?.replyIndexes?.remove(lastApplied) == true
            if (role == Role.LEADER && shouldReply) {
                if (entry.command.processId == env.processId) {
                    env.onClientCommandResult(result)
                } else {
                    env.send(entry.command.processId, ClientCommandResult(currentTerm, result))
                }
            }
        }
    }

    private fun flushCommands() {
        if (role == Role.CANDIDATE) {
            return
        }

        if (role == Role.LEADER) {
            while (!commandQueue.isEmpty()) {
                appendCommand(commandQueue.removeFirst())
            }
            return
        }

        if (leaderId == NO_LEADER) {
            return
        }

        while (!commandQueue.isEmpty()) {
            env.send(
                leaderId,
                ClientCommandRpc(currentTerm, commandQueue.removeFirst()),
            )
        }
    }

    private fun dropRepliesFrom(from: Int) {
        val state = leaderState ?: return
        val it = state.replyIndexes.iterator()
        while (it.hasNext()) {
            val index = it.next()
            if (index >= from) {
                it.remove()
            }
        }
    }

    private fun hasLog(logId: LogId): Boolean {
        if (logId.index == 0) {
            return true
        }
        val entry = storage.readLog(logId.index) ?: return false
        return entry.id == logId
    }

    private fun isUpToDate(lastLogId: LogId): Boolean {
        val myLast = storage.readLastLogId()
        return lastLogId.term > myLast.term ||
                (lastLogId.term == myLast.term && lastLogId.index >= myLast.index)
    }

    private fun saveTermVote(term: Int, vote: Int?) {
        currentTerm = term
        votedFor = vote
        storage.writePersistentState(PersistentState(term, vote))
    }

    private fun roleName(): String {
        return when (role) {
            Role.FOLLOWER -> "F"
            Role.CANDIDATE -> "C"
            Role.LEADER -> "L"
        }
    }

    private fun stateLine(): String {
        return "id=${env.processId} role=${roleName()} term=$currentTerm leader=$leaderId commit=$commitIndex applied=$lastApplied"
    }
}
