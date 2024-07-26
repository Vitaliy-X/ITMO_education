package Lab_2;

import java.util.ArrayList;
import java.util.Scanner;

public class Task_B {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String [] bolls = in.nextLine().split(" ");
        in.close();

        Stack_ stack = new Stack_();
        int tmp;
        for (int i = 0; i < bolls.length; i++) {
            tmp = Integer.parseInt(bolls[i]);
            if (tmp == stack.peek() && tmp == stack.peek_()) {
                while (Integer.parseInt(bolls[i]) == tmp) {
                    i++;
                    stack.push(tmp);
                    stack.pop();
                    if (i == bolls.length) {
                        break;
                    }
                }
                i--;
                stack.pop();
                stack.pop();
                continue;
            }
            stack.push(tmp);
        }
        System.out.println(stack.count);
    }
}

class Stack_ {
    ArrayList<Integer> A;
    int top = -1;
//    int size;
    int count = 0;

    Stack_() {
//        this.size = size;
        this.A = new ArrayList<>();
    }

    void push(Integer X) {
        top++;
        A.add(X);
    }
    Integer peek() {
        if (isEmpty()) {
            return -1;
        }
        return A.get(top);
    }
    Integer peek_() {
        if (top == 0 || isEmpty()) {
            return -1;
        }
        return A.get(top-1);
    }
    Integer pop() {
        count++;
        int obj = peek();
        A.remove(top);
        top--;
        return obj;
    }
    boolean isEmpty() { return top == -1; }
}