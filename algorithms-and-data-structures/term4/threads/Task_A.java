package threads;

import java.util.*;

public class Task_A {
    static class Connection {
        int start, end, capacity;
        long flow = 0;

        Connection(int start, int end, int capacity) {
            this.start = start;
            this.end = end;
            this.capacity = capacity;
        }
    }

    static int nodeCount, edgeCount;
    static List<Connection> connections = new ArrayList<>();

    static long depthFirstSearch(List<List<Integer>> graph, boolean[] visited, int currentNode, long currentFlow) {
        if (currentNode == nodeCount) {
            return currentFlow;
        }
        if (visited[currentNode]) {
            return 0;
        }

        visited[currentNode] = true;
        for (int neighbor : graph.get(currentNode)) {
            Connection conn = connections.get(neighbor);
            if (conn.flow < conn.capacity) {
                long delta = depthFirstSearch(graph, visited, conn.end, Math.min(currentFlow, conn.capacity - conn.flow));
                if (delta > 0) {
                    conn.flow += delta;
                    connections.get(neighbor ^ 1).flow -= delta;
                    return delta;
                }
            }
        }

        return 0;
    }

    private static List<List<Integer>> readInput(Scanner input) {
        nodeCount = input.nextInt();
        edgeCount = input.nextInt();
        List<List<Integer>> graph = new ArrayList<>(nodeCount + 1);
        for (int i = 0; i <= nodeCount; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < edgeCount; i++) {
            int start = input.nextInt();
            int end = input.nextInt();
            int capacity = input.nextInt();
            graph.get(start).add(2 * i);
            connections.add(new Connection(start, end, capacity));
            graph.get(end).add(2 * i + 1);
            connections.add(new Connection(end, start, capacity));
        }

        return graph;
    }

    private static long calculateMaxFlow(List<List<Integer>> graph) {
        long maxFlow = 0;
        while (true) {
            boolean[] visited = new boolean[nodeCount + 1];
            long delta = depthFirstSearch(graph, visited, 1, Long.MAX_VALUE);
            if (delta == 0) {
                break;
            }
            maxFlow += delta;
        }
        return maxFlow;
    }

    private static void printResult(long maxFlow) {
        System.out.println(maxFlow);
        for (int i = 0; i < edgeCount; i++) {
            System.out.println(connections.get(2 * i).flow);
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        List<List<Integer>> graph = readInput(input);
        long maxFlow = calculateMaxFlow(graph);
        printResult(maxFlow);
        input.close();
    }
}