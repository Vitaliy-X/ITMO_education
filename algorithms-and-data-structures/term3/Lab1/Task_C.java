package Lab1;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Task_C {
    static ArrayList<Integer>[] graph;
    static int[] colors;
    static boolean[] visited;
    static int currentColor = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        graph = new ArrayList[n+1];
        IntStream.rangeClosed(0, n).forEach(i -> graph[i] = new ArrayList<>());

        colors = new int[n+1];
        visited = new boolean[n+1];

        IntStream.range(0, m).map(i -> scanner.nextInt()).forEach(u -> {
            int v = scanner.nextInt();
            graph[u].add(v);
            graph[v].add(u);
        });

        IntStream.rangeClosed(1, n).filter(i -> !visited[i]).forEach(i -> {
            dfs(i);
            currentColor++;
        });

        IntStream.rangeClosed(1, n).mapToObj(i -> colors[i] + " ").forEach(System.out::print);
    }

    static void dfs(int node) {
        visited[node] = true;
        colors[node] = currentColor;
        graph[node].stream().mapToInt(neighbor -> neighbor).filter(neighbor -> !visited[neighbor]).forEach(Task_C::dfs);
    }
}
