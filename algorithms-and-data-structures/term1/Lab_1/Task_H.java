package Lab_1;

import java.util.Scanner;

public class Task_H {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        double c = Double.parseDouble(in.next());
        in.close();
        double l_index = 0.0000001;
        double r_index = 1e10;

        while (r_index - l_index > 0.0000001) {
            double cen = (l_index + r_index) / 2;
                if ((Math.pow(cen, 2.0) + Math.sqrt(cen)) > c) {
                r_index = cen;
            } else {
                l_index = cen;
            }
        }
        System.out.printf("%.6f", r_index);
    }
}