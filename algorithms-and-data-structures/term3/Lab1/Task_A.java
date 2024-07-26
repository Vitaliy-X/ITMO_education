package Lab1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Task_A {
    static ArrayList<Integer>[] graph;
    static int[] color;
    static int[] parent;
    static int cycleStart, cycleEnd;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        graph = new ArrayList[n+1];
        IntStream.rangeClosed(0, n).forEach(i -> graph[i] = new ArrayList<>());

        color = new int[n+1];
        parent = new int[n+1];
        cycleStart = -1;

        IntStream.range(0, m).map(i -> scanner.nextInt()).forEach(u -> {
            int v = scanner.nextInt();
            graph[u].add(v);
        });

        for (int i = 1; i <= n; i++) {
            if (color[i] == 0 && dfs(i)) {
                break;
            }
        }

        if (cycleStart == -1) {
            System.out.println("NO");
        } else {
            System.out.println("YES");
            List<Integer> cycle = new ArrayList<>();
            cycle.add(cycleStart);
            for (int v = cycleEnd; v != cycleStart; v = parent[v]) {
                cycle.add(v);
            }
            cycle.add(cycleStart);
            Collections.reverse(cycle);

            for (int i = 1; i < cycle.size(); i++) {
                int v = cycle.get(i);
                System.out.print(v + " ");
            }
        }
    }

    static boolean dfs(int v) {
        color[v] = 1;
        for (int u : graph[v]) {
            if (color[u] != 0) {
                if (color[u] == 1) {
                    cycleEnd = v;
                    cycleStart = u;
                    return true;
                }
            } else {
                parent[u] = v;
                if (dfs(u)) {
                    return true;
                }
            }
        }
        color[v] = 2;
        return false;
    }
}