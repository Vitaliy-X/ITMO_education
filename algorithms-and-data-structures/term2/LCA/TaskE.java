package LCA;

import java.io.*;
import java.util.*;

public class TaskE {
    public static void main(String[] args) throws IOException {
        MyScanner reader = new MyScanner();
        BufferedOutputStream out = new BufferedOutputStream(System.out);
        int n = reader.nextInt();
        int v, u;
        Tree tree = new Tree(n);
        for (int i = 0; i < n; i++) {
            String query = reader.next();

            switch (query) {
                case "+" -> {
                    v = reader.nextInt();
                    tree.add(v);
                }
                case "-" -> {
                    v = reader.nextInt();
                    tree.kill(v);
                }
                case "?" -> {
                    v = reader.nextInt();
                    u = reader.nextInt();
                    System.out.println(tree.aliveLca(v, u));
                }
            }
        }
        out.close();
    }

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
        boolean[] alive;
        int[] parent, depth;
        int[][] dp;
        int root, curNumber, log;

        Tree(int maxSize) {
            maxSize += 10;
            root = 1;
            curNumber = 1;
            alive = new boolean[maxSize];
            this.parent = new int[maxSize];
            this.depth = new int[maxSize];
            this.dp = new int[maxSize][log2(maxSize) + 1];
            log = log2(maxSize);

            alive[1] = true;
            parent[1] = 0;
            depth[1] = 0;
        }

        public int aliveLca(int v, int u) {
            int lca = lca(v, u);
            return findAliveParent(lca);
        }


        private int findAliveParent(int lca) {
            if (alive[lca]) {
                return lca;
            }

            return parent[lca] = findAliveParent(parent[lca]);
        }

        public int lca(int v, int u) {
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

        public void add(int p) {
            curNumber++;
            depth[curNumber] = depth[p] + 1;
            alive[curNumber] = true;
            parent[curNumber] = p;
            dp[curNumber][0] = p;
            for (int i = 1; i <= log2(curNumber); i++) {
                dp[curNumber][i] = dp[dp[curNumber][i - 1]][i - 1];
            }
        }

        public void kill(int v) {
            alive[v] = false;
        }
    }

    public static int log2(int n) {
        if (n == 0) {
            return 0;
        }
        return log2(n / 2) + 1;
    }
}