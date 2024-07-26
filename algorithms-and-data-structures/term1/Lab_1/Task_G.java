package Lab_1;

import java.util.Scanner;

public class Task_G {
    public static void main(String[] args) {
        int cnt = 0;
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        in.close();

        int xerox_1 = Math.min(x, y);

        if (n == 1) {
            System.out.println(xerox_1);
            return;
        }

        n--;
        cnt += xerox_1;

        int countx = x;
        int county = y;
        while (n > 0) {
            if (countx == 0) {
                n--;
                countx = x;
            }
            if (county == 0) {
                n--;
                county = y;
            }
            cnt++;
            countx--;
            county--;
        }
        cnt--;
        System.out.println(cnt);
    }
}