package Lab_1;

import java.util.Scanner;

public class Task_B {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] array = new int[n];
        int max = 0;
        for (int i = 0; i < n; i++) {
            array[i] = in.nextInt();
            if (max < array[i]) {
                max = array[i];
            }
        }
        array = countingSort(array, max);
        for (int i : array) {
            System.out.print(i + " ");
        }
        in.close();
    }

    private static int[] countingSort(int[] arr, int max) {
        int [] C = new int[max + 1];
        for (int i: arr) {
            C[i]++;
        }
        int b = 0;
        for (int i = 0; i < max + 1; i++) {
            for (int j = 0; j < C[i]; j++) {
                arr[b] = i;
                b++;
            }
        }
        return arr;
    }
}