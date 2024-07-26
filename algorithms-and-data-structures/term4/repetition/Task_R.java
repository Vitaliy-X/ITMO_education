package repetition;

import java.util.*;

public class Task_R {
    static class Cell implements Comparable<Cell> {
        int row, col, fuel;

        Cell(int row, int col, int fuel) {
            this.row = row;
            this.col = col;
            this.fuel = fuel;
        }

        @Override
        public int compareTo(Cell other) {
            return Integer.compare(this.fuel, other.fuel);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        char[][] grid = readGrid(scanner, n, m);
        int result = findMinimumFuel(grid, n, m);
        System.out.println(result);
    }

    private static char[][] readGrid(Scanner scanner, int n, int m) {
        char[][] grid = new char[n][m];
        for (int i = 0; i < n; i++) {
            grid[i] = scanner.nextLine().toCharArray();
        }
        return grid;
    }

    private static int findMinimumFuel(char[][] grid, int n, int m) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        char[] dirChars = {'N', 'S', 'W', 'E'};
        int[][] fuel = initializeFuelGrid(n, m);

        PriorityQueue<Cell> pq = new PriorityQueue<>();
        pq.add(new Cell(0, 0, 0));
        fuel[0][0] = 0;

        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            int row = current.row;
            int col = current.col;
            int currentFuel = current.fuel;

            if (row == n - 1 && col == m - 1) {
                return currentFuel;
            }

            moveUsingAccelerator(grid, directions, dirChars, fuel, pq, row, col, currentFuel);
            moveToAdjacentCells(directions, fuel, pq, row, col, currentFuel);
        }
        return -1;
    }

    private static int[][] initializeFuelGrid(int n, int m) {
        int[][] fuel = new int[n][m];
        for (int[] row : fuel) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return fuel;
    }

    private static void moveUsingAccelerator(char[][] grid, int[][] directions, char[] dirChars, int[][] fuel,
                                             PriorityQueue<Cell> pq, int row, int col, int currentFuel) {
        for (int i = 0; i < 4; i++) {
            if (grid[row][col] == dirChars[i]) {
                int newRow = row + directions[i][0];
                int newCol = col + directions[i][1];
                if (isValidCell(newRow, newCol, grid.length, grid[0].length) && currentFuel < fuel[newRow][newCol]) {
                    fuel[newRow][newCol] = currentFuel;
                    pq.add(new Cell(newRow, newCol, currentFuel));
                }
            }
        }
    }

    private static void moveToAdjacentCells(int[][] directions, int[][] fuel, PriorityQueue<Cell> pq,
                                            int row, int col, int currentFuel) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            if (isValidCell(newRow, newCol, fuel.length, fuel[0].length) && currentFuel + 1 < fuel[newRow][newCol]) {
                fuel[newRow][newCol] = currentFuel + 1;
                pq.add(new Cell(newRow, newCol, currentFuel + 1));
            }
        }
    }

    private static boolean isValidCell(int row, int col, int n, int m) {
        return row >= 0 && row < n && col >= 0 && col < m;
    }
}