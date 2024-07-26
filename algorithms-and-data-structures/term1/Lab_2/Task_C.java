package Lab_2;

import java.util.HashMap;
import java.util.Scanner;

public class Task_C {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Deque queue = new Deque(n);
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int a = in.nextInt();
            if (a == 1) {
                queue.add(in.nextInt());
            } else if (a == 2) {
                queue.removeFirst();
            } else if (a == 3) {
                queue.pop();
            } else if (a == 4) {
                answer.append(queue.getQueue(in.nextInt()));
                answer.append("\n");
            } else {
                answer.append(queue.getFirst());
                answer.append("\n");
            }
        }
        System.out.println(answer);
        in.close();
    }
}

class Deque {
    private int index = 0;
    private int del = 0;
    private final int [] array;
    private HashMap<Integer, Integer> map;

    public Deque(int size) {
        this.array = new int[size/2+20];
        this.map = new HashMap<>(size/2+20);
    }

    public void add(Integer item) {
        map.put(item, index);
        array[index++] = item;
    }

    public int getFirst(){
        return array[del];
    }

    public void pop() {
        index--;
    }

    public void removeFirst() {
        del++;
    }

    public int getQueue(int q){
        return map.get(q) - del;
    }
}