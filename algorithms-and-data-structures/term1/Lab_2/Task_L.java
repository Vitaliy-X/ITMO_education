package Lab_2;

import java.util.ArrayDeque;
import java.util.Scanner;

public class Task_L {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int a[] = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
        }

        ArrayDeque<Integer> res = findSequence(a, n);
        System.out.println(res.size());

        while (!res.isEmpty()) {
            System.out.print(res.removeLast() + " ");
        }
    }

    static ArrayDeque<Integer> findSequence(int a[], int n) {
        int dp[] = new int[n];
        int ask[] = new int[n];

        for (int i = 0; i < n; i++) {
            ask[i] = -1;
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (a[i] > a[j] && dp[i] - 1 < dp[j]) {
                    ask[i] = j;
                    dp[i] = dp[j] + 1;
                }
            }
        }

        int last = 0;
        int length = dp[0];
        for (int i = 0; i < n; i++) {
            if (length < dp[i]) {
                length = dp[i];
                last = i;
            }
        }

        ArrayDeque<Integer> res = new ArrayDeque<>();
        while (last >= 0) {
            res.addLast(a[last]);
            last = ask[last];
        }
        return res;
    }
}