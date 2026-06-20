package mutex

/**
 * Distributed mutual exclusion implementation.
 * All functions are called from the single main thread.
 *
 * @author Vitaliy Gavrilyuk
 */
class ProcessImpl(private val env: Environment) : Process {
    private enum class Msg { REQ, FORK }
    private val self = env.processId
    private val total = env.nProcesses

    private data class Link(
        var have: Boolean = false,
        var dirty: Boolean = false,
        var asked: Boolean = false,
        var pending: Boolean = false
    )

    private val link = Array(total + 1) { Link() }
    private var need = false
    private var inside = false

    init {
        for (el in 1..total) {
            if (el == self) {
                continue
            }
            val owns = self > el
            link[el].have = owns
            link[el].dirty = owns
        }
    }

    override fun onMessage(srcId: Int, message: Message) {
        message.parse {
            when (readEnum<Msg>()) {
                Msg.REQ -> onReq(srcId)
                Msg.FORK -> onFork(srcId)
            }
        }
    }

    override fun onLockRequest() {
        need = true
        requestMissing()
        tryEnter()
    }

    override fun onUnlockRequest() {
        inside = false
        need = false
        env.unlocked()
        markDirty()
        flushPending()
    }

    private fun requestMissing() {
        for (el in 1..total) {
            if (el == self) {
                continue
            }
            val st = link[el]
            if (!st.have && !st.asked) {
                st.asked = true
                sendReq(el)
            }
        }
    }

    private fun tryEnter() {
        if (inside || !need) {
            return
        }
        if (!haveAll()) {
            return
        }
        inside = true
        env.locked()
    }

    private fun haveAll(): Boolean {
        for (el in 1..total) {
            if (el == self) {
                continue
            }
            if (!link[el].have) {
                return false
            }
        }
        return true
    }

    private fun onReq(from: Int) {
        val st = link[from]
        st.pending = true
        // send fork only if allowed by dirty rule
        if (!st.have) {
            return
        }
        if (!this.inside && (!need || st.dirty)) {
            giveFork(from)
        }
    }

    private fun onFork(from: Int) {
        val st = link[from]
        st.have = true
        st.dirty = false
        st.asked = false

        if (!inside && !need && st.pending) {
            giveFork(from)
            return
        }

        tryEnter()
    }

    private fun giveFork(to: Int) {
        val st = link[to]
        sendFork(to)
        st.have = false
        st.dirty = false
        st.pending = false

        if (need && !st.asked) {
            st.asked = true
            sendReq(to)
        }
    }

    private fun markDirty() {
        for (el in 1..total) {
            if (el == self) {
                continue
            }
            val st = link[el]
            if (st.have) {
                st.dirty = true
            }
        }
    }

    private fun flushPending() {
        for (el in 1..total) {
            if (el == self) continue
            val st = link[el]

            if (st.pending && st.have && st.dirty) {
                giveFork(el)
            }
        }
    }

    private fun sendFork(to: Int) {
        env.send(to) {
            writeEnum(Msg.FORK)
        }
    }

    private fun sendReq(to: Int) {
        env.send(to) {
            writeEnum(Msg.REQ)
        }
    }

    private fun tmpCount() {
        var cnt = 0
        for (el in 1..total) {
            if (el == self) continue
            if (link[el].have) {
                cnt++
            }
        }
        System.out.println(cnt)
    }
}
