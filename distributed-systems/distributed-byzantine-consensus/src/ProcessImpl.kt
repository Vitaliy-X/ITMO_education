package byzantine

import java.util.HashMap

/**
 * Distributed Byzantine consensus implementation.
 *
 * @author Vitaliy Gavrilyuk
 */
class ProcessImpl(private val env: Environment) : Process {
    private enum class Kind {
        ROUND1,
        ROUND2,
    }

    private enum class Flag {
        EMPTY,
        VALUE,
    }

    private val seen1 = HashMap<Int, Int?>()

    override fun onStart() {
        seen1.put(env.processId, env.proposal)
        sendRound1()
    }

    override fun onPhaseCompletion(phase: Int, messages: Map<Int, Message>) {
        if (phase == 1) {
            finishPhase1(messages)
        } else {
            finishPhase2(messages)
        }
    }

    private fun finishPhase1(messages: Map<Int, Message>) {
        collectRound1(messages)
        sendRound2()
    }

    private fun finishPhase2(messages: Map<Int, Message>) {
        val reports = collectRound2(messages)
        val restored = restoreAll(reports)
        env.decide(majority(restored))
    }

    private fun sendRound1() {
        for (id in 1..env.nProcesses) {
            if (id == env.processId) {
                continue
            }
            env.send(id, Message {
                writeEnum(Kind.ROUND1)
                writeInt(env.proposal)
            })
        }
    }

    private fun collectRound1(messages: Map<Int, Message>) {
        (1..env.nProcesses).forEach { id ->
            val value = if (id == env.processId) env.proposal else readRound1(messages[id])
            seen1.put(id, value)
        }
    }

    private fun sendRound2() {
        (1..env.nProcesses).forEach { to ->
            if (to == env.processId) {
                return@forEach
            }
            env.send(to, buildRound2Message())
        }
    }

    private fun buildRound2Message(): Message =
        Message {
            writeEnum(Kind.ROUND2)
            for (from in 1..env.nProcesses) {
                val value = seen1[from]
                if (value == null) {
                    writeEnum(Flag.EMPTY)
                } else {
                    writeEnum(Flag.VALUE)
                    writeInt(value)
                }
            }
        }

    private fun collectRound2(messages: Map<Int, Message>): Map<Int, IntArray?> {
        val result = HashMap<Int, IntArray?>()
        (1..env.nProcesses).forEach { id ->
            if (id == env.processId) {
                return@forEach
            }
            result.put(id, readRound2(messages[id]))
        }
        return result
    }

    private fun restoreAll(reports: Map<Int, IntArray?>): IntArray {
        val result = IntArray(env.nProcesses + 1)
        (1..env.nProcesses).forEach { src ->
            result[src] = restoreOne(src, reports)
        }
        return result
    }

    private fun restoreOne(src: Int, reports: Map<Int, IntArray?>): Int {
        val count = HashMap<Int, Int>()

        (1..env.nProcesses).forEach { observer ->
            if (observer == src) {
                return@forEach
            }
            val value = observedValue(observer, src, reports)
            val normalized = value ?: 0
            inc(count, normalized)
        }

        return best(count)
    }

    private fun observedValue(observer: Int, src: Int, reports: Map<Int, IntArray?>): Int? {
        if (observer == env.processId) {
            return seen1[src]
        }

        val arr = reports[observer] ?: return null
        val value = arr[src]
        return if (value == Int.MIN_VALUE) null else value
    }

    private fun readRound1(message: Message?): Int? {
        if (message == null) {
            return null
        }

        return try {
            message.parse {
                val kind = readEnum<Kind>()
                if (kind != Kind.ROUND1) {
                    return null
                }
                readInt()
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun readRound2(message: Message?): IntArray? {
        if (message == null) {
            return null
        }

        return try {
            message.parse {
                val kind = readEnum<Kind>()
                if (kind != Kind.ROUND2) {
                    return null
                }

                val result = IntArray(env.nProcesses + 1)
                for (i in 0..env.nProcesses) {
                    result[i] = Int.MIN_VALUE
                }

                for (id in 1..env.nProcesses) {
                    val flag = readEnum<Flag>()
                    result[id] = if (flag == Flag.EMPTY) Int.MIN_VALUE else readInt()
                }

                result
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun majority(values: IntArray): Int {
        val count = HashMap<Int, Int>()
        for (id in 1..env.nProcesses) {
            inc(count, values[id])
        }
        return best(count)
    }

    private fun inc(map: HashMap<Int, Int>, key: Int) {
        map.put(key, (map[key] ?: 0) + 1)
    }

    private fun best(count: Map<Int, Int>): Int {
        var ans = 0
        var cnt = -1

        for (entry in count.entries) {
            val value = entry.key
            val cur = entry.value
            if (cur > cnt || (cur == cnt && value < ans)) {
                cnt = cur
                ans = value
            }
        }

        return ans
    }

    private fun dbgSeen(id: Int): Int? {
        return seen1[id]
    }
}
