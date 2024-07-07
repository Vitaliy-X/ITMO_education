/*
     Model: a[1]..a[n]
     Invariant: for i=1..n: a[i] != null

     Let immutable(n): for i=1..n: a'[i] == a[i]

     Pred: element != null && queue != null
     Post: n' = n + 1 && a[n'] == element && immutable(n)
        enqueue(element)

     Pred: n >= 1
     Post: R == a[1] && immutable(n) && n' == n
         element()

     Pred: n >= 1
     Post: n' == n - 1 && immutable(n') && R = a[1]
         dequeue()

     Pred: true
     Post: R == n && n' == n && immutable(n)
         size()

     Pred: true
     Post: R == (n == 0) && n' == n && immutable(n)
         isEmpty()

     Pred: true
     Post: R == (n == 0) && n' == n && immutable(n)
         clear()

     Pred: queue != null
     Post: R == [queue[1]..queue[n]]
         toStr()
     */


package queue;

//   Model: a[1]..a[n]

public class ArrayQueueADT {
    private Object [] elements = new Object[8];
    private int start;
    private int size;

//    Pred: element != null && queue != null
//    Post: n' = n + 1 && a[n'] == element && immutable(n)
//    enqueue(element)
    public static void enqueue(final ArrayQueueADT queue, final Object element) {
        assert element != null && queue != null;
        ensureCapacity(queue, queue.size);
        queue.elements[(queue.start + queue.size++) % queue.elements.length] = element;
    }

    private static void ensureCapacity(final ArrayQueueADT queue, int size) {
        if (size == 0) {
            size++;
        }
        if (queue.elements.length <= size) {
            Object[] copy = new Object[2 * size];
            System.arraycopy(queue.elements, queue.start, copy, 0, queue.elements.length - queue.start);
            System.arraycopy(queue.elements, 0, copy, queue.elements.length - queue.start, queue.start);
            queue.start = 0;
            queue.elements = copy;
        }
    }

//    Pred: n >= 1
//    Post: R == a[1] && immutable(n) && n' == n
//    element()
    public static Object element(final ArrayQueueADT queue) {
        assert queue.size >= 1;
        return queue.elements[queue.start];
    }


//    Pred: n >= 1
//    Post: n' == n - 1 && immutable(n') && R = a[1]
//    dequeue()
    public static Object dequeue(final ArrayQueueADT queue) {
        assert queue.size >= 1;
        queue.size--;
        Object res = queue.elements[queue.start];
        queue.elements[queue.start] = null;
        queue.start = (queue.start + 1) % queue.elements.length;
        return res;
    }

//    Pred: true
//    Post: R == n && n' == n && immutable(n)
//    size()
    public static int size(final ArrayQueueADT queue) {
        return queue.size;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
//    isEmpty()
    public static boolean isEmpty(final ArrayQueueADT queue) {
        return queue.size == 0;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
//    clear()
    public static void clear(final ArrayQueueADT queue) {
        queue.elements = new Object[8];
        queue.start = 0;
        queue.size = 0;
    }

//    Pred: queue != null
//    Post: R == [queue[1]..queue[n]]
    public static String toStr(final ArrayQueueADT queue) {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (queue.elements[queue.start] != null) {
            str.append(queue.elements[queue.start]);
        }
        for (int i = 1; i < queue.size; i++) {
            str.append(", ");
            if (queue.elements[(queue.start+i) % queue.elements.length] != null) {
                str.append(queue.elements[(queue.start+i) % queue.elements.length]);
            }
        }
        str.append("]");
        return str.toString();
    }
}