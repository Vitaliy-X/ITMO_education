package queue;

import static queue.ArrayQueueADT.*;

public class ArrayQueueADTMyTest {
    public static void main(String[] args) {
        ArrayQueueADT queue = new ArrayQueueADT();

        fill(queue, "test", 7);
        System.out.println(size(queue));
        clear(queue);

        fill(queue, "test", 12);
        testing(queue);
    }

    public static void fill(ArrayQueueADT queue, String str, int size) {
        for (int i = 0; i < size; i++) {
            enqueue(queue, str + i);
        }
    }

    public static void testing(ArrayQueueADT queue) {
        System.out.println(toStr(queue));
        System.out.println(toString(queue));
        while (!isEmpty(queue)){
            System.out.println(dequeue(queue));
        }
        System.out.println(toString(queue));

        enqueue(queue, new ArrayQueueADT());
        toStr(queue);
        clear(queue);
        toStr(queue);
    }

    public static String toString(ArrayQueueADT queue) {
        return "current element: " + element(queue) + System.lineSeparator() + "size = " + size(queue);
    }
}
