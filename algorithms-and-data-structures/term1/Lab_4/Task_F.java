package Lab_4;

import java.util.Scanner;

public class Task_F {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int [] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
        }
        in.close();

        int res = 0;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                break;
            }
            res = gcd(res, a[i+1]);
        }
        System.out.println(res);
    }
    private static int gcd(int n1, int n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcd(n2, n1 % n2);
    }
}
