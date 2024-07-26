package repetition;

import java.util.Scanner;
import java.util.stream.IntStream;

public class Task_A {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int rows = scanner.nextInt();
        int cols = scanner.nextInt();
        scanner.nextLine();

        char[][] landMap = readMap(scanner, rows);
        char[][] resultMap = markVillaSpots(landMap, rows, cols);

        printMap(resultMap, rows, cols);
    }

    private static char[][] readMap(Scanner scanner, int rows) {
        return IntStream.range(0, rows).mapToObj(i -> scanner.nextLine().toCharArray()).toArray(char[][]::new);
    }

    private static char[][] markVillaSpots(char[][] map, int rows, int cols) {
        char[][] result = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = map[i][j];
                if (map[i][j] == '.' && isAdjacentToWater(map, i, j, rows, cols)) {
                    result[i][j] = 'X';
                }
            }
        }
        return result;
    }

    private static boolean isAdjacentToWater(char[][] map, int i, int j, int rows, int cols) {
        if (i > 0 && map[i - 1][j] == '#') return true;
        if (i < rows - 1 && map[i + 1][j] == '#') return true;
        if (j > 0 && map[i][j - 1] == '#') return true;
        return j < cols - 1 && map[i][j + 1] == '#';
    }

    private static void printMap(char[][] map, int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }
}