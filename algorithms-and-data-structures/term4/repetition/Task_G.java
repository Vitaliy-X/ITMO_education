package repetition;

import java.util.Scanner;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Task_G {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int[] coins = new int[n];
        for (int i = 0; i < n; i++) {
            coins[i] = scanner.nextInt();
        }

        int result = calculateMaxCoins(n, coins);

        if (result != Integer.MIN_VALUE) {
            System.out.println(result);
        } else {
            System.out.println("Impossible");
        }
    }

    private static int calculateMaxCoins(int n, int[] coins) {
        int[] dp = initializeDPArray(n, coins[0]);
        IntStream.range(0, n).filter(i -> dp[i] != Integer.MIN_VALUE).forEach(i -> updateDPArray(dp, coins, i, n));
        return dp[n - 1];
    }

    private static int[] initializeDPArray(int n, int firstCoin) {
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MIN_VALUE);
        dp[0] = firstCoin;
        return dp;
    }

    private static void updateDPArray(int[] dp, int[] coins, int currentIndex, int n) {
        IntStream.rangeClosed(1, 3).map(jump -> currentIndex + jump)
                .filter(nextIndex -> nextIndex < n && coins[currentIndex] != coins[nextIndex])
                .forEach(nextIndex -> dp[nextIndex] = Math.max(dp[nextIndex], dp[currentIndex] + coins[nextIndex]));
    }
}