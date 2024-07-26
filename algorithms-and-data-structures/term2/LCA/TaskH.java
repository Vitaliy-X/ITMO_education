//package Lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TaskH {
    static final int MAX_SIZE = 300000;

    static List<List<Integer>> adjacencyList;
    static int n;
    static int[] parents, depths, sizes, indexInTree;
    static long[] segmentTree, lazyPropagation;
    static long[] precomputedValues;

    public static void main(String[] args) throws IOException {
        MyScanner sc = new MyScanner();

        n = sc.nextInt();

        adjacencyList = new ArrayList<>(MAX_SIZE);
        for (int i = 0; i < MAX_SIZE; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        parents = new int[MAX_SIZE];
        depths = new int[MAX_SIZE];
        sizes = new int[MAX_SIZE];
        indexInTree = new int[MAX_SIZE];
        segmentTree = new long[4 * MAX_SIZE];
        lazyPropagation = new long[4 * MAX_SIZE];
        precomputedValues = new long[MAX_SIZE];

        for (int i = 0; i < n - 1; i++) {
            int a = sc.nextInt() - 1;
            int b = sc.nextInt() - 1;

            adjacencyList.get(a).add(b);
            adjacencyList.get(b).add(a);
        }

        depthFirstSearch(0, 0);
        getSize(0);
        build(0);
        precomputeValues();

        int m = sc.nextInt();
        for (int i = 0; i < m; i++) {
            String c = sc.next();

            int a = sc.nextInt() - 1;
            if (!c.equals("+")) {
                System.out.println(get(1, 0, MAX_SIZE - 1, indexInTree[a], indexInTree[a]));
            } else {
                int b = sc.nextInt() - 1;
                int d = sc.nextInt();
                addToPath(a, b, d);
            }
        }

        sc.close();
    }

    static int getSize(int u) {
        int result = 0;

        for (int v : adjacencyList.get(u)) {
            if (parents[u] != v) {
                result += getSize(v) + 1;
            }
        }
        return sizes[u] = result + 1;
    }

    static void depthFirstSearch(int u, int depth) {
        for (int v : adjacencyList.get(u)) {
            if (parents[u] != v) {
                parents[v] = u;
                depths[v] = depth + 1;
                depthFirstSearch(v, depth + 1);
            }
        }
    }

    static void build(int vertex) {
        indexInTree[vertex] = vertex;

        int maxSize = -1, maxIndex = 0;
        for (int adjacentVertex : adjacencyList.get(vertex)) {
            if (maxSize < sizes[adjacentVertex] && parents[vertex] != adjacentVertex) {
                maxSize = sizes[adjacentVertex];
                maxIndex = adjacentVertex;
            }
        }

        if (maxSize != -1) {
            build(maxIndex);
        }

        for (int v : adjacencyList.get(vertex)) {
            if (parents[vertex] != v && maxIndex != v) {
                build(v);
            }
        }
    }

    static long get(int vertex, int left, int right, int queryLeft, int queryRight) {
        push(vertex, right - left + 1);
        if (left >= queryLeft && right <= queryRight) return segmentTree[vertex];
        if (right < queryLeft || left > queryRight) return 0;

        int middle = (left + right) / 2;
        return get(2 * vertex, left, middle, queryLeft, queryRight)
                + get(2 * vertex + 1, middle + 1, right, queryLeft, queryRight);
    }

    static void push(int vertex, int length) {
        if (length != 1) {
            lazyPropagation[2 * vertex] += lazyPropagation[vertex];
            lazyPropagation[2 * vertex + 1] += lazyPropagation[vertex];
        }

        segmentTree[vertex] += lazyPropagation[vertex];
        lazyPropagation[vertex] = 0;
    }

    static void add(int vertex, int left, int right, int queryLeft, int queryRight, long value) {
        push(vertex, right - left + 1);
        if (queryLeft <= left && right <= queryRight) {
            lazyPropagation[vertex] += value;
            push(vertex, right - left + 1);
            return;
        }
        if (queryRight < left || right < queryLeft) {
            return;
        }

        int middle = (left + right) / 2;
        add(2 * vertex, left, middle, queryLeft, queryRight, value);
        add(2 * vertex + 1, middle + 1, right, queryLeft, queryRight, value);
    }

    static void addToPath(int left, int right, long value) {
        if (indexInTree[left] != indexInTree[right]) {
            do {
                if (depths[indexInTree[left]] < depths[indexInTree[right]]) {
                    int temp = left;
                    left = right;
                    right = temp;
                }
                add(1, 0, MAX_SIZE - 1, indexInTree[indexInTree[left]], indexInTree[left], value);
                left = parents[indexInTree[left]];
            } while (indexInTree[left] != indexInTree[right]);
        }

        if (depths[indexInTree[left]] > depths[indexInTree[right]]) {
            int temp = left;
            left = right;
            right = temp;
        }

        add(1, 0, MAX_SIZE - 1, indexInTree[left], indexInTree[right], value);
    }

    static void precomputeValues() {
        for (int i = 0; i < n; i++) {
            precomputedValues[i] = get(1, 0, MAX_SIZE - 1, indexInTree[i], indexInTree[i]);
        }
    }

    static class Pair {
        int node;
        int weight;

        Pair(int node, int weight) {
            this.node = node;
            this.weight = weight;
        }
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
}