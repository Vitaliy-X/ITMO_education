package Lab_4;

import java.util.Scanner;

public class Task_E {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int [] a = new int[n];
        for (int i = 0; i < n; i++) {
            extracted(in.nextInt());
            System.out.println();
        }
        in.close();
    }

    private static void extracted(int x) {
        int multiplier = 2;

        while (x > 1 && multiplier <= Math.sqrt(x)) {
            if (x % multiplier == 0) {
                System.out.print(multiplier + " ");
                x /= multiplier;
            }
            else if (multiplier == 2) {
                multiplier++;
            }
            else {
                multiplier += 2;
            }
        }
        if (x != 1) {
            System.out.print(x);
        }
    }
}
