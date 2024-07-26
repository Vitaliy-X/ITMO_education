//package Lab_3;

import java.util.Scanner;

public class Task_C {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int [] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
        }
        for (int el : a) {
            if (isSimple(el)) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
        in.close();
    }

    private static boolean isSimple(int num) {
        if (num % 2 == 0) {
            return false;
        }
        for (int i = 3; i < Math.sqrt(num) + 1; i+=2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
