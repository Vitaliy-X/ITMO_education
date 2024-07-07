/*
     Model: a[1]..a[n] && n <= (maxSize == 50002)
     Invariant: for i=1..n: a[i] != null

     Let immutable(n): for i=1..n: a'[i] == a[i]

     Pred: element != null && n < maxSize
     Post: n' = n + 1 && a[n'] == element && immutable(n)
        enqueue(element)

     Pred: 1 <= n <= maxSize
     Post: R == a[1] && immutable(n) && n' == n
         element()

     Pred: 1 <= n <= maxSize
     Post: n' == n - 1 && immutable(n') && R = a[1]
         dequeue()

     Pred: n <= maxSize
     Post: R == n && n' == n && immutable(n)
         size()

     Pred: true
     Post: R == (n == 0) && n' == n && immutable(n)
         isEmpty()

     Pred: true
     Post: R == (n == 0) && n' == n && immutable(n)
         clear()
     */


package queue;

public class ArrayQueueLine {
    private static final int SIZE = 50001;
    private static Object [] elements = new Object[SIZE];
    private static int back = 0;
    private static int start = 0;
    private static int size = 0;

//    Pred: element != null && n < maxSize
//    Post: n' = n + 1 && a[n'] == element && immutable(n)
//    enqueue(element)
    public static void enqueue(final Object element) {
        assert element != null;
        if ((start == 0 && back == SIZE-1 || start == back + 1)) {
            System.out.println("full");
            start++;
        }
        if (size != SIZE) {
            size++;
        }
        elements[back++] = element;
        if (back == SIZE) {
            back = 0;
        }
    }

//    Pred: 1 <= n <= maxSize
//    Post: R == a[1] && immutable(n) && n' == n
//    element()
    public static Object element() {
        assert size >= 1;
        return elements[start];
    }


//    Pred: 1 <= n <= maxSize
//    Post: n' == n - 1 && immutable(n') && R = a[1]
//    dequeue()
    public static Object dequeue() {
        assert size >= 1;
        Object res = elements[start++];
        size--;
        if (start == SIZE) {
            start = 0;
        }
        return res;
    }

//    Pred: n <= maxSize
//    Post: R == n && n' == n && immutable(n)
//    size()
    public static int size() {
        return size;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
//    isEmpty()
    public static boolean isEmpty() {
        return size == 0;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
//    clear()
    public static void clear() {
        elements = new Object[SIZE];
        start = 0;
        back = 0;
        size = 0;
    }
}