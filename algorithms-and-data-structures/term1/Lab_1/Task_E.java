package Lab_1;

import java.util.Arrays;
import java.util.Scanner;

public class Task_E {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = in.nextInt();
        }
        array = mergeSort(array);
        StringBuilder str = new StringBuilder();
        int k = in.nextInt();
        for (int i = 0; i < k; i++) {
            int l = in.nextInt();
            int r = in.nextInt();
            int idx_l = searchRange(array, l, 1);
            int idx_r = searchRange(array, r, 0);
            str.append(idx_r - idx_l + 1);
            str.append(" ");
        }
        System.out.println(str);
        in.close();
    }

    public static int searchRange(int[] arr, int l, int r) {
        int left = -1;
        int right = arr.length;
        if (r == 1) {
            while (right - left > 1) {
                int mid = (left + right) / 2;
                if (arr[mid] >= l) {
                    right = mid;
                } else {
                    left = mid;
                }
            }
            return right;
        } else {
            while (right - left > 1) {
                int mid = (left + right) / 2;
                if (arr[mid] <= l) {
                    left = mid;
                } else {
                    right = mid;
                }
            }
            return left;
        }
    }

    private static int[] mergeSort(int [] arr){
        if (arr.length < 2) {
            return arr;
        }
        int [] lArr = mergeSort(Arrays.copyOfRange(arr, 0, arr.length/2));
        int [] rArr = mergeSort(Arrays.copyOfRange(arr, arr.length/2, arr.length));
        int [] result = new int[lArr.length + rArr.length];

        int n = 0;
        int m = 0;
        int k = 0;

        while (n < lArr.length && m < rArr.length) {
            if (lArr[n] <= rArr[m]) {
                result[k] = lArr[n];
                n++;
            } else {
                result[k] = rArr[m];
                m++;
            }
            k++;
        }
        while (n < lArr.length) {
            result[k] = lArr[n];
            n++;
            k++;
        }
        while (m < rArr.length) {
            result[k] = rArr[m];
            m++;
            k++;
        }
        return result;
    }
}