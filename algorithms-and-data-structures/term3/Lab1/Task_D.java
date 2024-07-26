package Lab1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.IntStream;

public class Task_D {
    static ArrayList<Integer>[] graph, reverseGraph, condensedGraph;
    static boolean[] visited;
    static Stack<Integer> order;
    static int[] component;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        graph = new ArrayList[n+1];
        reverseGraph = new ArrayList[n+1];
        condensedGraph = new ArrayList[n+1];
        IntStream.rangeClosed(0, n).forEach(i -> {
            graph[i] = new ArrayList<>();
            reverseGraph[i] = new ArrayList<>();
            condensedGraph[i] = new ArrayList<>();
        });

        visited = new boolean[n+1];
        order = new Stack<>();
        component = new int[n+1];

        IntStream.range(0, m).map(i -> scanner.nextInt()).forEach(u -> {
            int v = scanner.nextInt();
            graph[u].add(v);
            reverseGraph[v].add(u);
        });

        IntStream.rangeClosed(1, n).filter(i -> !visited[i]).forEach(Task_D::dfs1);

        Arrays.fill(visited, false);
        int color = 1;
        if (!order.isEmpty()) {
            do {
                int v = order.pop();
                if (!visited[v]) {
                    dfs2(v, color++);
                }
            } while (!order.isEmpty());
        }

        IntStream.rangeClosed(1, n).forEach(v -> {
            graph[v].stream().mapToInt(u -> u).filter(u -> component[v] != component[u]).forEach(u -> condensedGraph[component[v]].add(component[u]));
        });

        int edges = IntStream.range(0, color).map(v -> (int) condensedGraph[v].stream().distinct().count()).sum();

        System.out.println(edges);
    }

    static void dfs1(int v) {
        visited[v] = true;
        graph[v].stream().mapToInt(u -> u).filter(u -> !visited[u]).forEach(Task_D::dfs1);
        order.push(v);
    }

    static void dfs2(int v, int color) {
        visited[v] = true;
        component[v] = color;
        reverseGraph[v].stream().mapToInt(u -> u).filter(u -> !visited[u]).forEach(u -> dfs2(u, color));
    }
}
