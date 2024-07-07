package queue;

public class LinkedQueueMyTest {
    public static void main(String[] args) {
        LinkedQueue queue = new LinkedQueue();
        queue.enqueue("el");
        queue.clear();

        fill(queue, "test", 7);
        System.out.println(queue.size());
        queue.clear();
        System.out.println(queue.toStr());

        queue.enqueue(new LinkedQueue());
        System.out.println(queue.toStr());
        queue.dequeue();

        fill(queue, "test", 6);
        System.out.println("count \"test2\" = " + queue.count("test2"));
        testing(queue);

        queue.clear();
        queue.enqueue(5);
        System.out.println("count \"5\" = " + queue.count(5));
    }

    public static void fill(LinkedQueue queue, Object obj, int size) {
        for (int i = 0; i < size; i++) {
            queue.enqueue(obj.toString()+i);
        }
    }

    public static void testing(LinkedQueue queue) {
        System.out.println(queue.toStr());
        System.out.println(toString(queue));
        while (!queue.isEmpty()){
            System.out.println(queue.dequeue());
        }
        System.out.println(toString(queue));

        queue.enqueue(new ArrayQueueM());
        queue.toStr();
        queue.clear();
        queue.toStr();
    }

    public static String toString(LinkedQueue queue) {
        return "current element: " + queue.element() + System.lineSeparator() + "size = " + queue.size();
    }
}