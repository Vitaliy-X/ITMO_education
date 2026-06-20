package solution

import java.io.Serializable

sealed interface ProcMessage : Serializable

data class RMessage(
    val value: Long,
) : ProcMessage

/**
 * Distributed Dijkstra algorithm implementation.
 * All functions are called from the single main thread.
 *
 * @author Vitaliy Gavrilyuk
 */
class DijkstraProcessImpl(private val env: Environment) : DijkstraProcess {
    private data object Ok : ProcMessage {
        private fun readResolve(): Any = Ok
    }

    private data object Attach : ProcMessage {
        private fun readResolve(): Any = Attach
    }

    private data object Detach : ProcMessage {
        private fun readResolve(): Any = Detach
    }

    private var initiator = false
    private var openRequests = 0L
    private var attachedChildren = 0L

    private var currentDistance: Long? = null
    private var predecessor: Int? = null

    override fun onComputationStart() {
        initiator = true
        currentDistance = 0L
        propagate()
        completeIfPossible()
    }

    override fun onMessage(srcId: Int, message: Any) {
        when (message) {
            is Ok -> handleOk()
            is Attach -> handleAttach()
            is Detach -> handleDetach()
            is RMessage -> handleRelax(srcId, message.value)
        }
        completeIfPossible()
    }

    private fun handleOk() {
        openRequests--
    }

    private fun handleAttach() {
        openRequests--
        attachedChildren++
    }

    private fun handleDetach() {
        attachedChildren--
    }

    private fun handleRelax(from: Int, candidate: Long) {
        val known = currentDistance

        if (known == null || candidate < known) {
            switchParent(from)
            currentDistance = candidate
            env.send(from, Attach)
            propagate()
        } else {
            env.send(from, Ok)
        }
    }

    private fun switchParent(newParent: Int) {
        val oldParent = predecessor
        if (oldParent != null) {
            env.send(oldParent, Detach)
        }
        predecessor = newParent
    }

    private fun propagate() {
        val base = currentDistance ?: return

        for (neighbor in env.neighbours.keys) {
            val edgeWeight = env.neighbours[neighbor] ?: continue
            if (neighbor == env.processId) continue

            env.send(neighbor, RMessage(base + edgeWeight))
            openRequests++
        }
    }

    private fun completeIfPossible() {
        if (openRequests != 0L || attachedChildren != 0L) return

        if (initiator) {
            env.finishExecution()
            return
        }

        val p = predecessor ?: return
        env.send(p, Detach)
        predecessor = null
    }

    override val distance: Long?
        get() = currentDistance

    private fun dbgParent(): Int? =
        predecessor
}
