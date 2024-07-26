package threads;

import java.util.*;

public class Task_C {
    static class Connection {
        int start, end, capacity;
        int flow = 0;

        Connection(int start, int end, int capacity) {
            this.start = start;
            this.end = end;
            this.capacity = capacity;
        }
    }

    static int nodeCount, edgeCount, source, sink, extendedNodeCount;
    static List<Connection> graphEdges = new ArrayList<>();
    static List<Integer> distances;

    static boolean performBFS(final List<List<Integer>> graph) {
        initializeDistances();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            for (int edgeId : graph.get(currentNode)) {
                Connection edge = graphEdges.get(edgeId);
                if (edge.flow < edge.capacity && distances.get(edge.end) == extendedNodeCount) {
                    distances.set(edge.end, distances.get(currentNode) + 1);
                    queue.add(edge.end);
                }
            }
        }

        return distances.get(sink) != extendedNodeCount;
    }

    static int performDFS(final List<List<Integer>> graph, final List<Integer> pointers, final int currentNode, final int minCapacity) {
        if (currentNode == sink || minCapacity == 0) {
            return minCapacity;
        }

        while (pointers.get(currentNode) < graph.get(currentNode).size()) {
            Connection edge = graphEdges.get(graph.get(currentNode).get(pointers.get(currentNode)));
            if (distances.get(edge.end) == distances.get(currentNode) + 1) {
                int delta = performDFS(graph, pointers, edge.end, Math.min(minCapacity, edge.capacity - edge.flow));
                if (delta != 0) {
                    edge.flow += delta;
                    graphEdges.get(graph.get(currentNode).get(pointers.get(currentNode)) ^ 1).flow -= delta;
                    return delta;
                }
            }
            pointers.set(currentNode, pointers.get(currentNode) + 1);
        }

        return 0;
    }

    static void findPath(final List<List<Integer>> graph, final int currentNode, final List<Integer> path) {
        if (currentNode == sink) {
            path.add(currentNode);
            return;
        }
        for (int edgeId : graph.get(currentNode)) {
            if (graphEdges.get(edgeId).flow == 1) {
                graphEdges.get(edgeId).flow = 0;
                path.add(currentNode);
                findPath(graph, graphEdges.get(edgeId).end, path);
                return;
            }
        }
    }

    static String buildPathString(final List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int node : path) {
            sb.append(node).append(" ");
        }
        return sb.toString().trim();
    }

    static void readInput(Scanner scanner, final List<List<Integer>> graph) {
        nodeCount = scanner.nextInt();
        edgeCount = scanner.nextInt();
        source = scanner.nextInt();
        sink = scanner.nextInt();
        extendedNodeCount = nodeCount + 1;
        initializeGraph(graph);
        for (int i = 0; i < edgeCount; ++i) {
            int start = scanner.nextInt();
            int end = scanner.nextInt();
            addEdge(graph, start, end, i);
        }
    }

    static void initializeDistances() {
        distances = new ArrayList<>(Collections.nCopies(extendedNodeCount, extendedNodeCount));
        distances.set(source, 0);
    }

    static void initializeGraph(final List<List<Integer>> graph) {
        for (int i = 0; i < extendedNodeCount; i++) {
            graph.add(new ArrayList<>());
        }
    }

    static void addEdge(final List<List<Integer>> graph, final int start, final int end, final int index) {
        graph.get(start).add(2 * index);
        graphEdges.add(new Connection(start, end, 1));
        graph.get(end).add(2 * index + 1);
        graphEdges.add(new Connection(end, start, 0));
    }

    static void findAndPrintPaths(final List<List<Integer>> graph) {
        List<Integer> path1 = new ArrayList<>();
        findPath(graph, source, path1);
        List<Integer> path2 = new ArrayList<>();
        findPath(graph, source, path2);

        System.out.println(buildPathString(path2));
        System.out.println(buildPathString(path1));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<List<Integer>> graph = new ArrayList<>(extendedNodeCount);

        readInput(scanner, graph);

        int maxFlow = 0;
        while (performBFS(graph)) {
            List<Integer> pointers = new ArrayList<>(Collections.nCopies(extendedNodeCount, 0));
            while (true) {
                int flow = performDFS(graph, pointers, source, extendedNodeCount);
                if (flow == 0) {
                    break;
                }
                maxFlow += flow;
            }
        }

        if (maxFlow < 2) {
            System.out.println("NO");
            return;
        }

        System.out.println("YES");
        findAndPrintPaths(graph);
    }
}