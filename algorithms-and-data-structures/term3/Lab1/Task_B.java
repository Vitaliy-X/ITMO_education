package Lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Task_B {
    public static int[] color;

    public static void main(String[] args) {
        int n, m;
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        m = scanner.nextInt();

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < m; i++) {
            graph.get(scanner.nextInt()-1).add(scanner.nextInt()-1);
        }

        color = new int[n];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            if (color[i] == 0 && DFS(graph, i, stack)) {
                System.out.println(-1);
                return;
            }
        }

        while (!stack.isEmpty()) {
            System.out.print((stack.pop() + 1) + " ");
        }
    }

    public static boolean DFS(List<List<Integer>> graph, int v, Stack<Integer> stack) {
        color[v] = 1;
        for (int neighbor : graph.get(v)) {
            if (color[neighbor] == 1) {
                return true;
            } else if (color[neighbor] == 0 && DFS(graph, neighbor, stack)) {
                return true;
            }
        }
        color[v] = 2;
        stack.push(v);
        return false;
    }
}