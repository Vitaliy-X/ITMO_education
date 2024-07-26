//package Lab_3;

import java.util.Scanner;

public class Task_D {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int x = in.nextInt();
        extracted(x);
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
