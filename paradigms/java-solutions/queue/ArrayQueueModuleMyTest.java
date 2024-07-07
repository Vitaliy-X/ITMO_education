package queue;

import static queue.ArrayQueueModule.*;

public class ArrayQueueModuleMyTest {
    public static void main(String[] args) {
        enqueue("el");
        clear();

        fill("test", 7);
        System.out.println(size());
        clear();

        fill("test", 32);
        testing();
    }

    public static void fill(String str, int size) {
        for (int i = 0; i < size; i++) {
            enqueue(str + i);
        }
    }

    public static void testing() {
        System.out.println(toStr());
        System.out.println(string());
        while (!isEmpty()){
            System.out.println(dequeue());
        }
        System.out.println(string());

        enqueue(new ArrayQueueM());
        toStr();
        clear();
        toStr();
    }

    public static String string() {
        return "current element: " + element() + System.lineSeparator() + "size = " + size();
    }
}
