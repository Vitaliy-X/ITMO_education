/*
     Model: a[1]..a[n]
     Invariant: for i=1..n: a[i] != null

     Let immutable(n): for i=1..n: a'[i] == a[i]

     Pred: element != null
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

     Pred: true
     Post: R == [queue[1]..queue[n]]
         toStr()
     */


package queue;

//    Model: a[1]..a[n]

public class ArrayQueueM {
    private Object [] elements = new Object[8];
    private int start;
    private int size;

//    Pred: element != null
//    Post: n' = n + 1 && a[n'] == element && immutable(n)
//    enqueue(element)
    public void enqueue(final Object element) {
        assert element != null;
        ensureCapacity(size);
        elements[(start + size++) % elements.length] = element;
    }

    private void ensureCapacity(int size) {
        if (size == 0) {
            size++;
        }
        if (elements.length <= size) {
            Object[] copy = new Object[2 * size];
            System.arraycopy(elements, start, copy, 0, elements.length - start);
            System.arraycopy(elements, 0, copy, elements.length - start, start);
            start = 0;
            elements = copy;
        }
    }

//    Pred: n >= 1
//    Post: R == a[1] && immutable(n) && n' == n
//    element()
    public Object element() {
        assert size >= 1;
        return elements[start];
    }


//    Pred: n >= 1
//    Post: n' == n - 1 && immutable(n') && R = a[1] && forall i = 1..n: a'[i] = a[i+1]
//    dequeue()
    public Object dequeue() {
        assert size >= 1;
        size--;
        Object res = elements[start];
        elements[start] = null;
        start = (start + 1) % elements.length;
        return res;
    }

//    Pred: true
//    Post: R == n && n' == n && immutable(n)
//    size()
    public int size() {
        return size;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
//    isEmpty()
    public boolean isEmpty() {
        return size == 0;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
//    clear()
    public void clear() {
        elements = new Object[8];
        start = 0;
        size = 0;
    }

//    Pred: true
//    Post: R == [queue[1]..queue[n]]
    public String toStr() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (size > 0) {
            str.append(elements[start]);
            for (int i = 1; i < size; i++) {
                str.append(", ");
                if (elements[(start+i) % elements.length] != null) {
                    str.append(elements[(start+i) % elements.length]);
                }
            }
        }
        str.append("]");
        return str.toString();
    }
}