import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * @author Гаврилюк Виталий
 */
class FlatCombiningQueue<E> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = AtomicBoolean(false) // unlocked initially
    private val tasksForCombiner = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)

    override fun enqueue(element: E) {
        var flag = false
        var index = randomCellIndex()
        while (true) {
            if (combinerLock.compareAndSet(false, true)) {
                if (flag && handleWaiting(index)) return
                queue.addLast(element)
                processForCombiner()
                combinerLock.set(false)
                return
            } else {
                when {
                    tasksForCombiner.compareAndSet(index, null, element) -> flag = true
                    !flag -> index = randomCellIndex()
                }
            }
        }
    }

    override fun dequeue(): E? {
        var flag = false
        var index = randomCellIndex()
        while (true) {
            if (combinerLock.compareAndSet(false, true)) {
                if (flag) {
//                    almost handleWaiting
                    val currentTask = tasksForCombiner.get(index)
                    if (currentTask is Result<*>) {
                        tasksForCombiner.set(index, null)
                        combinerLock.set(false)
                        return currentTask.value as E?
                    } else {
                        tasksForCombiner.set(index, null)
                    }
                }
                val result = queue.removeFirstOrNull()
                processForCombiner()
                combinerLock.set(false)
                return result
            } else {
                when {
                    tasksForCombiner.compareAndSet(index, null, Dequeue) -> flag = true
                    !flag -> index = randomCellIndex()
                }
            }
        }
    }

    private fun handleWaiting(index: Int): Boolean {
        val currentTask = tasksForCombiner.get(index)
        return if (currentTask is Result<*>) {
            tasksForCombiner.set(index, null)
            combinerLock.set(false)
            true
        } else {
            tasksForCombiner.set(index, null)
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun processForCombiner() {
        for (i in 0 until TASKS_FOR_COMBINER_SIZE) {
            val task = tasksForCombiner.get(i)
            val taskAsElement: E? = task as? E?
            if (task is Dequeue) {
                tasksForCombiner.set(i, Result(queue.removeFirstOrNull()))
            } else if (taskAsElement != null && taskAsElement !is Result<*>) {
                queue.addLast(taskAsElement as E)
                tasksForCombiner.set(i, Result<E?>(taskAsElement))
            }
        }
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(tasksForCombiner.length())
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!

// TODO: Put this token in `tasksForCombiner` for dequeue().
// TODO: enqueue()-s should put the inserting element.
private object Dequeue

// TODO: Put the result wrapped with `Result` when the operation in `tasksForCombiner` is processed.
private class Result<V>(val value: V)
