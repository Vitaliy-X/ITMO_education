package queue;

public interface Queue {
//    Model: a[1]..a[n]
//    Invariant: for i=1..n: a[i] != null
//
//    Let immutable(n): for i=1..n: a'[i] == a[i]

//    Pred: element != null
//    Post: n' = n + 1 && a[n'] == element && immutable(n)
    void enqueue(Object element);

//    Pred: n >= 1
//    Post: R == a[1] && immutable(n) && n' == n
    Object element();

//    Pred: n >= 1
//    Post: n' == n - 1 && immutable(n') && R = a[1]
    Object dequeue();

//    Pred: true
//    Post: R == n && n' == n && immutable(n)
    int size();

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
    boolean isEmpty();

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(n)
    void clear();

//    Pred: true
//    Post: R == [queue[1]..queue[n]]
    String toStr();

//    Pred: queue != null
//    Post: number of elements == x
    int count(Object x);
}
