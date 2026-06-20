package twopc

import java.util.HashMap
import java.util.LinkedHashSet

/**
 * Two-Phase Commit Transaction Manager implementation.
 *
 * @author Vitaliy Gavriliuk
 */
class TransactionManagerImpl(private val env: Environment) : TransactionManager {
    private class Tx {
        val participants = LinkedHashSet<Int>()
        var started = false
        var decision: Boolean? = null
        var done = false
    }

    private var nextId = 1L  //
    private val txs = HashMap<Long, Tx>()

    override fun beginTransaction(): Long {
        val id = nextId
        nextId += 1

        txs.put(id, Tx())
        return id
    }

    override fun addParticipant(transactionId: Long, participantId: Int) {
        val tx = txs.get(transactionId)

        if (tx == null) {
            return
        }

        if (!tx.started && !tx.done) {
            tx.participants.add(participantId)
        }
    }

    override fun commitTransaction(transactionId: Long) {
        val tx = txs.get(transactionId) ?: return

        if (tx.started || tx.done) {
            return
        }

        tx.started = true
        sendToAll(transactionId, tx, ParticipantRequest.PREPARE)
    }

    override fun rollbackTransaction(transactionId: Long) {
        val tx = txs.get(transactionId) ?: return

        if (tx.done) {
            return
        }

        tx.started = true
        finish(transactionId, tx, false)
    }

    override fun onVoteResult(transactionId: Long, votes: Map<Int, Message>) {
        val tx = txs.get(transactionId) ?: return

        if (!tx.started || tx.done || tx.decision != null) {
            return
        }

        val committed = canCommit(tx, votes)
        tx.decision = committed

        val request = if (committed) {
            ParticipantRequest.COMMIT
        } else {
            ParticipantRequest.ABORT
        }

        sendToAll(transactionId, tx, request)
    }

    override fun onAckResult(transactionId: Long, acks: Map<Int, Message>) {
        val tx = txs.get(transactionId) ?: return

        if (tx.done) {
            return
        }

        val committed = tx.decision ?: return  // ACK dont change the final decision
        finish(transactionId, tx, committed)
    }

    private fun canCommit(tx: Tx, votes: Map<Int, Message>): Boolean {
        for (participantId in tx.participants) {
            val vote = votes[participantId] ?: return false  // missing vote means phase-1 crash

            if (!isCommitVote(vote)) {
                return false
            }
        }

        return true
    }

    private fun isCommitVote(message: Message): Boolean {
        return message.parse {
            readEnum<ParticipantResponse>() == ParticipantResponse.VOTE_COMMIT
        }
    }

    private fun sendToAll(
        transactionId: Long,
        tx: Tx,
        request: ParticipantRequest,
    ) {
        for (participantId in tx.participants) {
            env.send(transactionId, participantId) {
                writeEnum(request)
            }
        }
    }

    private fun finish(
        transactionId: Long,
        tx: Tx,
        committed: Boolean,
    ) {
        if (tx.done) {
            return
        }

        tx.done = true
        env.result(transactionId, committed)
    }

    private fun dumpState(): String {
        val out = StringBuilder()

        for (entry in txs.entries) {
            val id = entry.key
            val tx = entry.value

            out.append("tx=")
                .append(id)
                .append(", participants=")
                .append(tx.participants)
                .append(", started=")
                .append(tx.started)
                .append(", decision=")
                .append(tx.decision)
                .append(", done=")
                .append(tx.done)
                .append('\n')
        }

        return out.toString()
    }
}
