import java.util.Arrays;
import java.util.Scanner;

public class ReverseEven {
    public ReverseEven() {
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int cnt = 0;
        int count = 0;
        int[] num = new int[1];

        int[][] arr;
        for(arr = new int[1][1]; scan.hasNextLine(); cnt = 0) {
            String sc = scan.nextLine();

            Scanner in;
            for(in = new Scanner(sc); in.hasNextInt(); ++cnt) {
                if (cnt >= num.length) {
                    num = Arrays.copyOf(num, num.length * 2);
                }

                num[cnt] = in.nextInt();
            }

            if (count >= arr.length) {
                arr = Arrays.copyOf(arr, arr.length * 2);
            }

            in.close();
            arr[count] = Arrays.copyOf(num, cnt);
            ++count;
        }

        arr = Arrays.copyOf(arr, count);

        for(int i = arr.length - 1; i >= 0; --i) {
            for(int j = arr[i].length - 1; j >= 0; --j) {
                if (arr[i][j] % 2 == 0) {
                    System.out.print(arr[i][j] + " ");
                }
            }

            System.out.println();
        }

        scan.close();
    }
}
