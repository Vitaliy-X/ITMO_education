//package Lab_3;

import java.util.Scanner;

public class Task_H {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int a = in.nextInt();
        int b = in.nextInt();
        int c = in.nextInt();
        int d = in.nextInt();
        int up = (a * d) + (c * b);
        int low = b * d;
        int del = gcd(up, low);
        System.out.print(up / del + " " + low / del);
        in.close();
    }
    private static int gcd(int n1, int n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcd(n2, n1 % n2);
    }
}
