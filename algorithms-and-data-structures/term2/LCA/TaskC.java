package LCA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TaskC {
    static int n;
    static List<List<Pair>> adj;
    static int[][] parent;
    static int[][] minWeight;
    static int[] depth;

    static class Pair {
        int node;
        int weight;

        Pair(int node, int weight) {
            this.node = node;
            this.weight = weight;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(reader.readLine());

        adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
        parent = new int[n + 1][(int) (Math.log(n) / Math.log(2)) + 1];
        minWeight = new int[n + 1][(int) (Math.log(n) / Math.log(2)) + 1];
        depth = new int[n + 1];

        for (int i = 2; i <= n; i++) {
            StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
            int x = Integer.parseInt(tokenizer.nextToken());
            int y = Integer.parseInt(tokenizer.nextToken());
            adj.get(i).add(new Pair(x, y));
            adj.get(x).add(new Pair(i, y));
        }

        dfs(1, 0, 0, Integer.MAX_VALUE);

        int m = Integer.parseInt(reader.readLine());

        for (int i = 0; i < m; i++) {
            StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
            int u = Integer.parseInt(tokenizer.nextToken());
            int v = Integer.parseInt(tokenizer.nextToken());
            int minWeight = getMinWeight(u, v);
            System.out.println(minWeight);
        }
    }

    private static void dfs(int node, int parent, int d, int edgeWeight) {
        depth[node] = d;
        TaskC.parent[node][0] = parent;
        minWeight[node][0] = edgeWeight;

        for (int i = 1; i < TaskC.parent[node].length; i++) {
            TaskC.parent[node][i] = TaskC.parent[TaskC.parent[node][i - 1]][i - 1];
            minWeight[node][i] = Math.min(minWeight[node][i - 1], minWeight[TaskC.parent[node][i - 1]][i - 1]);
        }

        for (Pair child : adj.get(node)) {
            if (child.node != parent) {
                dfs(child.node, node, d + 1, child.weight);
            }
        }
    }

    private static int getLCA(int u, int v) {
        if (depth[u] < depth[v]) {
            int temp = u;
            u = v;
            v = temp;
        }

        for (int i = parent[u].length - 1; i >= 0; i--) {
            if (depth[u] - (1 << i) >= depth[v]) {
                u = parent[u][i];
            }
        }

        if (u == v) {
            return u;
        }

        for (int i = parent[u].length - 1; i >= 0; i--) {
            if (parent[u][i] != parent[v][i]) {
                u = parent[u][i];
                v = parent[v][i];
            }
        }

        return parent[u][0];
    }

    private static int getMinWeight(int u, int v) {
        int lca = getLCA(u, v);
        int minWeightPath = Integer.MAX_VALUE;

        minWeightPath = getMinWeightPath(u, lca, minWeightPath);

        minWeightPath = getMinWeightPath(v, lca, minWeightPath);

        return minWeightPath;
    }

    private static int getMinWeightPath(int v, int lca, int minWeightPath) {
        for (int i = parent[v].length - 1; i >= 0; i--) {
            if (depth[v] - (1 << i) >= depth[lca]) {
                minWeightPath = Math.min(minWeightPath, minWeight[v][i]);
                v = parent[v][i];
            }
        }
        return minWeightPath;
    }
}