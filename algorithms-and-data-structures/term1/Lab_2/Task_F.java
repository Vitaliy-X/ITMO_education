package Lab_2;

import java.util.ArrayList;
import java.util.Scanner;

public class Task_F {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Stack stack = new Stack(n);
        for (int i = 0; i < n; i++) {
            stack.push(in.nextInt());
        }
        ArrayList<Boolean> answer;
        stack.reverse();
        answer = sortStack(stack, n);
        if (answer == null) {
            System.out.println("impossible");
        } else {
            for (Boolean aBoolean : answer) {
                if (aBoolean) {
                    System.out.println("push");
                } else {
                    System.out.println("pop");
                }
            }
        }
        in.close();
    }

    public static ArrayList<Boolean> sortStack(Stack stack, int n) throws ArrayIndexOutOfBoundsException {
        Stack newStack = new Stack(n);
        ArrayList<Boolean> ans = new ArrayList<>();
        int [] res = new int[n];
        int count = 0;
        while (!stack.isEmpty()) {
            int tmp = stack.pop();
            while (!newStack.isEmpty()&&tmp > newStack.peek()) {
                res[count++] = newStack.pop();
                ans.add(false);
            }
            newStack.push(tmp);
            ans.add(true);
        }
        while (!newStack.isEmpty()) {
            res[count++] = newStack.pop();
            ans.add(false);
        }
        for (int i = 0; i < n - 1; i++) {
            if (res[i+1] < res[i]) {
                return null;
            }
        }
        return ans;
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
    void reverse() {
        int [] B = new int[size];
        for (int i = 0; i < size; i++) {
            B[i] = A[size - 1 - i];
        }
        A = B;
    }
}