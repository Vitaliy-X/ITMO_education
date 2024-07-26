package LCA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TaskB {
    static int n;
    static List<List<Integer>> adj;
    static int[][] parent;
    static int[] depth;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(reader.readLine());

        adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
        parent = new int[n + 1][(int) (Math.log(n) / Math.log(2)) + 1];
        depth = new int[n + 1];

        for (int i = 2; i <= n; i++) {
            int x = Integer.parseInt(reader.readLine());
            adj.get(i).add(x);
            adj.get(x).add(i);
        }

        dfs(1, 0, 0);

        int m = Integer.parseInt(reader.readLine());

        for (int i = 0; i < m; i++) {
            StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
            int u = Integer.parseInt(tokenizer.nextToken());
            int v = Integer.parseInt(tokenizer.nextToken());
            int lca = getLCA(u, v);
            System.out.println(lca);
        }
    }

    private static void dfs(int node, int parent, int d) {
        depth[node] = d;
        TaskB.parent[node][0] = parent;

        for (int i = 1; i < TaskB.parent[node].length; i++) {
            TaskB.parent[node][i] = TaskB.parent[TaskB.parent[node][i - 1]][i - 1];
        }

        for (int child : adj.get(node)) {
            if (child != parent) {
                dfs(child, node, d + 1);
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
}