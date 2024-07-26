package repetition;

import java.util.*;
import java.util.stream.IntStream;

public class Task_N {
    static class Road {
        int to, id;

        Road(int to, int id) {
            this.to = to;
            this.id = id;
        }
    }

    static List<Road>[] adjList;
    static int[] parent, depth, heavyChild, chainHead, position, segmentTree;
    static int currentPos;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final int n = scanner.nextInt();
        final int m = scanner.nextInt();

        initializeGraph(n);
        readEdges(scanner, n);
        initializeDataStructures(n);
        performDFS(1);
        decomposeTree(1, 1);
        processQueries(scanner, m);
    }

    static void initializeGraph(int n) {
        adjList = new ArrayList[n + 1];
        IntStream.rangeClosed(1, n).forEach(i -> adjList[i] = new ArrayList<>());
    }

    static void readEdges(Scanner scanner, final int n) {
        IntStream.range(1, n).forEach(i -> {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            adjList[u].add(new Road(v, i));
            adjList[v].add(new Road(u, i));
        });
    }

    static void initializeDataStructures(final int n) {
        parent = new int[n + 1];
        depth = new int[n + 1];
        heavyChild = new int[n + 1];
        chainHead = new int[n + 1];
        position = new int[n + 1];
        segmentTree = new int[4 * n];

        Arrays.fill(heavyChild, -1);
        currentPos = 0;
    }

    static int performDFS(final int node) {
        int size = 1;
        int maxSubtreeSize = 0;
        for (Road road : adjList[node]) {
            int neighbor = road.to;
            if (neighbor == parent[node]) continue;
            parent[neighbor] = node;
            depth[neighbor] = depth[node] + 1;
            int subtreeSize = performDFS(neighbor);
            if (subtreeSize > maxSubtreeSize) {
                maxSubtreeSize = subtreeSize;
                heavyChild[node] = neighbor;
            }
            size += subtreeSize;
        }
        return size;
    }

    static void decomposeTree(int node, int head) {
        chainHead[node] = head;
        position[node] = currentPos++;
        if (heavyChild[node] != -1) {
            decomposeTree(heavyChild[node], head);
        }
        adjList[node].stream().mapToInt(road -> road.to).filter(neighbor -> neighbor != parent[node]
                && neighbor != heavyChild[node]).forEach(neighbor -> decomposeTree(neighbor, neighbor));
    }

    static void processQueries(Scanner scanner, final int m) {
        IntStream.range(0, m).forEach(i -> {
            char queryType = scanner.next().charAt(0);
            if (queryType == 'P') {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                updatePath(u, v);
            } else if (queryType == 'Q') {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                System.out.println(queryPath(u, v));
            }
        });
    }

    static void updatePath(int u, int v) {
        while (chainHead[u] != chainHead[v]) {
            if (depth[chainHead[u]] > depth[chainHead[v]]) {
                int temp = u;
                u = v;
                v = temp;
            }
            updateSegmentTree(1, 0, currentPos - 1, position[chainHead[v]], position[v]);
            v = parent[chainHead[v]];
        }
        if (depth[u] > depth[v]) {
            int temp = u;
            u = v;
            v = temp;
        }
        updateSegmentTree(1, 0, currentPos - 1, position[u] + 1, position[v]);
    }

    static void updateSegmentTree(int node, int start, int end, int l, int r) {
        if (l > r) return;
        if (start == l && end == r) {
            segmentTree[node]++;
        } else {
            int mid = (start + end) / 2;
            updateSegmentTree(node * 2, start, mid, l, Math.min(r, mid));
            updateSegmentTree(node * 2 + 1, mid + 1, end, Math.max(l, mid + 1), r);
        }
    }

    static int queryPath(int u, int v) {
        int result = 0;
        while (chainHead[u] != chainHead[v]) {
            if (depth[chainHead[u]] > depth[chainHead[v]]) {
                int temp = u;
                u = v;
                v = temp;
            }
            result += querySegmentTree(1, 0, currentPos - 1, position[chainHead[v]], position[v]);
            v = parent[chainHead[v]];
        }
        if (depth[u] > depth[v]) {
            int temp = u;
            u = v;
            v = temp;
        }
        result += querySegmentTree(1, 0, currentPos - 1, position[u] + 1, position[v]);
        return result;
    }

    static int querySegmentTree(int node, int start, int end, int l, int r) {
        if (l > r) return 0;
        if (start == l && end == r) {
            return segmentTree[node];
        } else {
            int mid = (start + end) / 2;
            return segmentTree[node] + querySegmentTree(node * 2, start, mid, l, Math.min(r, mid)) + querySegmentTree(node * 2 + 1, mid + 1, end, Math.max(l, mid + 1), r);
        }
    }
}