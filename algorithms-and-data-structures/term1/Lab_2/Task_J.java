package Lab_2;

import java.util.Scanner;

public class Task_J {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String [] str = in.nextLine().split(" ");
        int n = Integer.parseInt(str[0]);
        int k = Integer.parseInt(str[1]);
        str = in.nextLine().split(" ");
        in.close();

        int [] dp = new int[n-1];
        for (int i = 1; i < n - 1; i++) {
            dp[i] = Integer.MIN_VALUE;
        }
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < k + 1; j++) {
                if (i - j < 0) {
                    break;
                }
                dp[i] = Math.max(dp[i - j], dp[i]);
            }
            dp[i] += Integer.parseInt(str[i]);
        }
        System.out.println(dp[dp.length-1]);
        for (int i = 0; i < n - 1; i++) {

        }
    }
}