import java.util.Arrays;
import java.util.Scanner;

public class Reverse {
    public static void main(String[] args) {

        MyScanner scan = new MyScanner(System.in);
        int cnt = 0;
        int count = 0;
        int [] num = new int[1];
        int [][] arr = new int[1][1];

        while (scan.hasNextLine()) {
            String str = scan.nextLine();
            MyScanner sc = new MyScanner(str);

            while (sc.hasNextInt()) {
                if (cnt >= num.length) {
                    num = Arrays.copyOf(num, num.length * 2);
                }
                num[cnt] = sc.nextInt();
                cnt++;
            }
            if (count >= arr.length) {
                arr = Arrays.copyOf(arr, arr.length * 2);
            }
            sc.close();
            arr[count] = Arrays.copyOf(num, cnt);
            count ++;
            cnt = 0;
//            num = new int[0];
        }
        arr = Arrays.copyOf(arr, count);
        for (int i = arr.length - 1; i >= 0; i--){
            for (int j = arr[i].length - 1; j >= 0; j--){
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
        scan.close();
    }
}