package Lab1;

import java.util.*;

public class Task_G {
    static class Edge {
        long id;
        long node;

        Edge(long first, long second) {
            this.id = first;
            this.node = second;
        }
    }

    static long timestemp = 0;
    static Set<Long> bridges = new TreeSet<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        long n = scanner.nextLong();
        long m = scanner.nextLong();

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (long i = 1; i <= m; ++i) {
            long u = scanner.nextLong();
            long v = scanner.nextLong();
            graph.get((int) (u - 1)).add(new Edge(i, v - 1));
            graph.get((int) (v - 1)).add(new Edge(i, u - 1));
        }

        findBridges(graph, n);

        System.out.println(bridges.size());
        for (long i : bridges) {
            System.out.println(i);
        }
    }

    static void findBridges(List<List<Edge>> graph, long n) {
        boolean[] visited = new boolean[(int) n];
        long[] disc = new long[(int) n];
        long[] low = new long[(int) n];
        long[] parent = new long[(int) n];
        Arrays.fill(parent, -1);

        for (long i = 0; i < n; i++) {
            if (!visited[(int) i]) {
                findBridgesUtil(graph, i, visited, disc, low, parent);
            }
        }
    }

    static void findBridgesUtil(List<List<Edge>> graph, long u, boolean[] visited, long[] disc, long[] low, long[] parent) {
        visited[(int) u] = true;
        disc[(int) u] = low[(int) u] = ++timestemp;

        for (Edge v : graph.get((int) u)) {
            if (!visited[(int) v.node]) {
                parent[(int) v.node] = u;
                findBridgesUtil(graph, v.node, visited, disc, low, parent);

                low[(int) u] = Math.min(low[(int) u], low[(int) v.node]);

                if (low[(int) v.node] > disc[(int) u]) {
                    bridges.add(v.id);
                }
            } else if (v.node != parent[(int) u]) {
                low[(int) u] = Math.min(low[(int) u], disc[(int) v.node]);
            }
        }
    }
}