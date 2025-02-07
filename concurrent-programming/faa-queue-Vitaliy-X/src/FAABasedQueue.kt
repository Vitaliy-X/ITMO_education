import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray

/**
 * @author Гаврилюк Виталий
 *
 * TODO: Copy the code from `FAABasedQueueSimplified`
 * TODO: and implement the infinite array on a linked list
 * TODO: of fixed-size `Segment`s.
 */
class FAABasedQueue<E> : Queue<E> {
    private val enqIdx = AtomicLong(0)
    private val deqIdx = AtomicLong(0)
    private val segment = Segment(0)
    private val head: AtomicReference<Segment> = AtomicReference(segment)
    private val tail: AtomicReference<Segment> = AtomicReference(segment)

    override fun enqueue(element: E) {
        while (true) {
            val tailSegment = tail.get()
            val idx = enqIdx.getAndIncrement()
            val newTail = advanceSegment(tailSegment, idx, tail)

            if (newTail.trySetCell((idx % SEGMENT_SIZE).toInt(), element)) {
                return
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            if (deqIdx.get() >= enqIdx.get()) return null
            val curHead = head.get()
            val idx = deqIdx.getAndIncrement()
            val newHead = advanceSegment(curHead, idx, head)

            val result = newHead.tryGetAndSetCell((idx % SEGMENT_SIZE).toInt(), POISONED)
            if (result != null) {
                return result as E
            }
        }
    }

    private fun advanceSegment(cf: Segment, i: Long, ref: AtomicReference<Segment>): Segment {
        var currentSegment = cf
        if (currentSegment.id < i / SEGMENT_SIZE) {
            currentSegment = currentSegment.advanceToSegment(i / SEGMENT_SIZE)
            ref.set(currentSegment)
        }
        return currentSegment
    }
}

private class Segment(val id: Long) {
    val next = AtomicReference<Segment?>(null)
    val cells = AtomicReferenceArray<Any?>(SEGMENT_SIZE)

    fun advanceToSegment(targetId: Long): Segment {
        var current = this
        while (current.id < targetId) {
            current.next.compareAndSet(null, Segment(current.id + 1))
            current = current.next.get()!!
        }
        return current
    }

    fun trySetCell(index: Int, element: Any?): Boolean {
        return cells.compareAndSet(index, null, element)
    }

    fun tryGetAndSetCell(index: Int, newValue: Any?): Any? {
        return cells.getAndSet(index, newValue)
    }
}

// TODO: poison cells with this value.
private val POISONED = Any()

// DO NOT CHANGE THIS CONSTANT
private const val SEGMENT_SIZE = 2