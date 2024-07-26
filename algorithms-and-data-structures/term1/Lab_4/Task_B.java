package Lab_4;

import java.util.Arrays;

public class Task_B {
    public static void main(String[] args) {

    }

    private static boolean isSimple(int N) {
        boolean [] arr = new boolean[N];
        Arrays.fill(arr, true);
        arr[1] = false;
        for (int i=2; i*i < N; i++)
            if (arr[i])
                for (int j=i*i; j < N; j+=i)
                    arr[j] = false;
        return true;
    }
}
