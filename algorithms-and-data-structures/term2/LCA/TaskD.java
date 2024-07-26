package LCA;

import java.io.*;
import java.util.*;

public class TaskD {
    static class MyScanner {
        BufferedReader scanner;
        StringTokenizer tokenizer;

        public MyScanner() {
            scanner = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(scanner.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }

        public void close() throws IOException {
            scanner.close();
        }
    }

    static class Tree {
        int size, root, time, log;
        int[] parent, depth, tIn;
        int[][] dp;
        ArrayList<ArrayList<Integer>> edges;

        Tree(int size, MyScanner scanner) throws IOException {
            this.size = size;
            this.parent = new int[size + 12];
            this.depth = new int[size + 12];
            this.tIn = new int[size + 12];
            this.dp = new int[size + 12][log2(size) + 1];
            this.edges = new ArrayList<>();
            for (int i = 0; i <= size; i++) {
                edges.add(new ArrayList<>());
            }
            log = log2(size);

            readParents(scanner);
            setArr();
            dfs(root, 0);
        }

        public int calculateGroupAntiquity(Integer[] group) {
            Arrays.sort(group, new CompByTIn());
            if (group.length != 1) {
                int antiquity = depth[group[0]];
                int lca = group[0];
                for (int i = 1; i < group.length; i++) {
                    lca = findLCA(lca, group[i]);
                    antiquity += depth[group[i]] - depth[lca];
                    lca = group[i];
                }
                return antiquity + 1;
            } else {
                return depth[group[0]] + 1;
            }
        }

        private void dfs(int v, int d) {
            tIn[v] = time;
            time++;
            depth[v] = d;
            tIn[v] = time;
            for (int i = 0; i < edges.get(v).size(); i++) {
                int to = edges.get(v).get(i);
                dfs(to, d + 1);
            }
        }

        private void setArr() {
            for (int i = 1; i <= size; i++) {
                dp[i][0] = parent[i];
            }
            for (int j = 1; j <= log; j++) {
                for (int i = 1; i <= size; i++) {
                    dp[i][j] = dp[dp[i][j - 1]][j - 1];
                }
            }
        }

        public int findLCA(int v, int u) {
            if (depth[v] > depth[u]) {
                int tmp = v;
                v = u;
                u = tmp;
            }

            int h = depth[u] - depth[v];
            for (int i = log; i >= 0; i--) {
                if ((1 << i) <= h) {
                    h -= (1 << i);
                    u = dp[u][i];
                }
            }

            if (u == v) {
                return v;
            }

            for (int i = log; i >= 0; i--) {
                if (dp[v][i] != dp[u][i]) {
                    v = dp[v][i];
                    u = dp[u][i];
                }
            }

            return parent[v];
        }

        private void readParents(MyScanner scanner) throws IOException {
            for (int i = 1; i <= size; i++) {
                int tmp = scanner.nextInt();
                parent[i] = tmp;
                if (tmp == -1) {
                    root = i;
                    parent[i] = 0;
                } else {
                    edges.get(tmp).add(i);
                }
            }
        }

        class CompByTIn implements Comparator<Integer> {
            public int compare(Integer a, Integer b) {
                return Integer.compare(tIn[b], tIn[a]);
            }
        }
    }

    public static int log2(int n) {
        return log2(n / 2) + 1;
    }

    public static void main(String[] args) throws IOException {
        MyScanner scanner = new MyScanner();
        OutputStream out = new BufferedOutputStream(System.out);
        int n = scanner.nextInt();
        Tree tree = new Tree(n, scanner);

        int m = scanner.nextInt();
        Integer[][] groups = new Integer[m + 1][];

        for (int i = 0; i < m; i++) {
            int groupSize = scanner.nextInt();

            groups[i] = new Integer[groupSize];

            for (int j = 0; j < groupSize; j++) {
                groups[i][j] = scanner.nextInt();
            }
        }

        for (int i = 0; i < m; i++) {
            out.write(Integer.toString(tree.calculateGroupAntiquity(groups[i])).getBytes());
            out.write("\n".getBytes());
        }

        scanner.close();
        out.close();
    }
}