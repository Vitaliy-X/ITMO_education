package Lab_4;

import java.util.Scanner;

public class Task_A {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        in.close();
        if (n == 2 || n == 4) {
            System.out.println("True");
        } else if (!isUnusual(n)) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
    }
    private static boolean isUnusual(int num) {
        int cnt = 0;
        for (int i = 2; i < Math.sqrt(num); i++) {
            if (num % i == 0) {
                cnt++;
            }
        }
        return cnt == 1;
    }
}
