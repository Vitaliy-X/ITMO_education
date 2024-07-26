//package Lab_2;

import java.util.Scanner;
//import java.util.Stack;

public class Task_E {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        evalPostfix(in.nextLine());
        in.close();
    }
    public static void evalPostfix(String exp) {
        Stack stack = new Stack(exp.length());

        for (String s: exp.split(" ")) {
            if (Character.isDigit(s.charAt(0))) {
                stack.push(Integer.valueOf(s));
            } else {
                int x = stack.pop();
                int y = stack.pop();
                switch (s) {
                    case "+" -> stack.push(y + x);
                    case "-" -> stack.push(y - x);
                    case "*" -> stack.push(y * x);
                    case "/" -> stack.push(y / x);
                }
            }
        }
        System.out.println(stack.pop());
    }
}

class Stack {
    int [] A;
    int top = -1;
    int size;

    Stack(int size) {
        this.size = size;
        this.A = new int[size];
    }

    void push(Integer X) {
        top++;
        A[top] = X;
    }
    Integer peek() {
        return A[top];
    }
    Integer pop() {
        int obj = peek();
        top--;
        return obj;
    }
    boolean isEmpty() { return top == -1; }
}