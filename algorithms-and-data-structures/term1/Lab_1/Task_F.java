package Lab_1;

import java.util.Scanner;

public class Task_F {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int k = in.nextInt();
        long[] array = new long[n];
        long[] requests = new long[k];
        for (int i = 0; i < n; i++) {
            array[i] = in.nextInt();
        }
        for (int i = 0; i < k; i++) {
            requests[i] = in.nextInt();
        }
        for (long el: requests) {
            binSearch(array, el);
        }
        in.close();
    }

    public static void binSearch(long [] arr, long x) {
        int l = -1;
        int r = arr.length;
        while (r - l > 1) {
            int mid = (r + l) / 2;
            if (arr[mid] >= x) {
                r = mid;
            } else {
                l = mid;
            }
        }
        if (r == arr.length) {
            r = arr.length - 1;
        }
        if (l == -1) {
            l++;
        }
        double lx = Math.abs(arr[l] - x);
        double rx = Math.abs(arr[r] - x);
        if (lx < rx) {
            System.out.println(arr[l]);
        } else if (lx > rx) {
            System.out.println(arr[r]);
        } else {
            System.out.println(Math.min(arr[l], arr[r]));
        }
    }
}