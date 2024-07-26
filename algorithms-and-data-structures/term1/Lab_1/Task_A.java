package Lab_1;

import java.util.Arrays;
import java.util.Scanner;

public class Task_A {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int [] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = in.nextInt();
        }
        array = mergeSort(array);
        for (int i: array) {
            System.out.print(i + " ");
        }
        in.close();
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