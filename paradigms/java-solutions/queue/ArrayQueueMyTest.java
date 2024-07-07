package queue;

public class ArrayQueueMyTest {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();

        fill(queue, "el", 8);
        System.out.println(queue.size());
        queue.clear();

        fill(queue, "obj", 10);
        testing(queue);

        queue.clear();
        fill(queue, "[a]", 3);
        queue.dequeue();
        fill(queue, 5, 6);
        queue.dequeue();
        System.out.println("count \"[a]\" = " + queue.count(null));
    }

    public static void fill(ArrayQueue queue, Object obj, int size) {
        for (int i = 0; i < size; i++) {
            queue.enqueue(obj);
        }
    }

    public static void testing(ArrayQueue queue) {
        System.out.println(queue.toStr());
        System.out.println(toString(queue));
        while (!queue.isEmpty()){
            System.out.println(queue.dequeue());
        }
        System.out.println(toString(queue));

        queue.enqueue(new ArrayQueue());
        queue.toStr();
        queue.clear();
        queue.toStr();
    }


    public static String toString(ArrayQueue queue) {
        return "current element: " + queue.element() + System.lineSeparator() + "size = " + queue.size();
    }
}
