package Lab_2;

import java.util.Scanner;

public class Task_A {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        MinStack stack = new MinStack(n);
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int a = in.nextInt();
            if (a == 1) {
                stack.push(in.nextInt());
            } else if (a == 2) {
                stack.pop();
            } else if (a == 3) {
                answer.append(stack.getMin());
                answer.append("\n");
            }
        }
        System.out.println(answer);
        in.close();
    }
}


class MinStack {
    private final Stack stackMain;
    private final Stack stackMin;

    public MinStack(int size) {
        stackMain = new Stack(size);
        stackMin = new Stack(size);
    }

    public void push(int val) {
        stackMain.push(val);
        if (stackMin.isEmpty()) {
            stackMin.push(val);
        }
        else {
            if (stackMin.peek() >= val) {
                stackMin.push(val);
            }
        }
    }

    public int pop() {
        int top = stackMain.pop();
        if (top == stackMin.peek()) {
            stackMin.pop();
        }
        return top;
    }

    public int getMin() {
        return stackMin.peek();
    }
}

//class Stack {
//    int [] A;
//    int top = -1;
//    int size;
//
//    Stack(int size) {
//        this.size = size;
//        this.A = new int[size];
//    }
//
//    void push(Integer X) {
//        top++;
//        A[top] = X;
//    }
//    Integer peek() {
//        return A[top];
//    }
//    Integer pop() {
//        int obj = peek();
//        top--;
//        return obj;
//    }
//    boolean isEmpty() { return top == -1; }
//}