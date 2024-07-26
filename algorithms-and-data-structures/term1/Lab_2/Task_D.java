package Lab_2;

import java.util.ArrayList;
import java.util.Scanner;

public class Task_D {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Queue_ queue = new Queue_();
        for (int i = 0; i < n; i++) {
            String next = in.next();
            if (next.equals("+")) {
                queue.add(in.nextInt());
            } else if (next.equals("-")) {
                queue.go();
            } else {
                queue.addToMid(in.nextInt());
            }
        }
        System.out.println(queue.answer);
        in.close();
    }
}

class Queue_ {
    private final ArrayList<Integer> array = new ArrayList<>();
    public StringBuilder answer = new StringBuilder();

    public void add(int item) {
        array.add(item);
    }

    public void addToMid(int item){
        if (array.size() % 2 == 0) {
            array.add( array.size() / 2, item);
        } else {
            array.add(array.size() / 2 + 1, item);
        }
    }

    public void go() {
        answer.append(array.get(0));
        answer.append("\n");
        array.remove(0);
    }
}

class Queue {
    private final int [] array;
    private final int [] mid;
    protected StringBuilder answer = new StringBuilder();
    private int index = 0;
    private int midIndex = 0;
    private int del = 0;
    private int midDel = 0;

    Queue(int size) {
        this.array = new int[size];
        this.mid = new int[size];
    }

    public void add(int item) {
        array[index++] = item;
    }

    public void addToMid(int item) {
        if (array[midIndex] != 0) {
            array[midIndex] = -array[midIndex];
        }
        mid[midIndex++] = item;
//        if (index == del) {
//            mid[index] = item;
//        } else if ((index - del) % 2 == 0) {
//            int next = array[(index - del)/2 + del];
//            for (int i = (index - del)/2 + del; i < array.length - 1; i++) {
//                if (next == 0) {
//                    break;
//                }
//                int last = array[i+1];
//                array[i+1] = next;
//                next = last;
//            }
//            mid[(index - del)/2 + del] = item;
//        } else {
//            int next = array[(index - del)/2+1 + del];
//            for (int i = (index - del)/2+1 + del; i < array.length - 1; i++) {
//                if (next == 0) {
//                    break;
//                }
//                int last = array[i+1];
//                array[i+1] = next;
//                next = last;
//            }
//            mid[(index - del)/2 + 1 + del] = item;
//        }
    }

    public void go() {
        if (array[del] < 0) {
            answer.append(mid[midDel++]);
            answer.append("\n");
            array[del] = -array[del];
        } else {
            answer.append(array[del++]);
            answer.append("\n");
        }
    }
}