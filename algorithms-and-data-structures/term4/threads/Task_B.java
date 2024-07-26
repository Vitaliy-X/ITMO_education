package threads;

import java.util.*;

public class Task_B {
    static class Connection {
        int start, end, capacity;
        long flow = 0;

        Connection(int start, int end, int capacity) {
            this.start = start;
            this.end = end;
            this.capacity = capacity;
        }
    }

    static int nodeCount, edgeCount, extendedNodeCount;
    static List<Connection> graphEdges = new ArrayList<>();
    static List<Integer> distances;

    static boolean performBFS(List<List<Integer>> graph) {
        initializeDistances();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);

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

        return distances.get(nodeCount) != extendedNodeCount;
    }

    static void performBFS2(List<List<Integer>> graph, List<Boolean> visited) {
        initializeVisited(visited);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            for (int edgeId : graph.get(currentNode)) {
                Connection edge = graphEdges.get(edgeId);
                if (edge.flow < edge.capacity && !visited.get(edge.end)) {
                    visited.set(edge.end, true);
                    queue.add(edge.end);
                }
            }
        }
    }

    static long performDFS(List<List<Integer>> graph, List<Integer> pointers, int currentNode, long minCapacity) {
        if (currentNode == nodeCount || minCapacity == 0) {
            return minCapacity;
        }

        while (pointers.get(currentNode) < graph.get(currentNode).size()) {
            Connection edge = graphEdges.get(graph.get(currentNode).get(pointers.get(currentNode)));
            if (distances.get(edge.end) == distances.get(currentNode) + 1) {
                long delta = performDFS(graph, pointers, edge.end, Math.min(minCapacity, edge.capacity - edge.flow));
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

    static void readInput(Scanner scanner, List<List<Integer>> graph, Map<Connection, Integer> edgeToIdMap) {
        nodeCount = scanner.nextInt();
        edgeCount = scanner.nextInt();
        extendedNodeCount = nodeCount + 1;
        initializeGraph(graph);
        for (int i = 0; i < edgeCount; ++i) {
            int start = scanner.nextInt();
            int end = scanner.nextInt();
            int capacity = scanner.nextInt();
            addEdge(graph, edgeToIdMap, start, end, capacity, i);
        }
    }

    static long calculateMaxFlow(List<List<Integer>> graph) {
        long maxFlow = 0;
        while (performBFS(graph)) {
            List<Integer> pointers = new ArrayList<>(Collections.nCopies(extendedNodeCount, 0));
            while (true) {
                long flow = performDFS(graph, pointers, 1, extendedNodeCount);
                if (flow == 0) {
                    break;
                }
                maxFlow += flow;
            }
        }
        return maxFlow;
    }

    static List<Integer> findMinCutEdges(List<List<Integer>> graph, Map<Connection, Integer> edgeToIdMap) {
        List<Boolean> visitedBFS2 = new ArrayList<>(Collections.nCopies(extendedNodeCount, false));
        performBFS2(graph, visitedBFS2);
        List<Integer> result = new ArrayList<>();
        for (Connection edge : graphEdges) {
            if (visitedBFS2.get(edge.start) && !visitedBFS2.get(edge.end)) {
                result.add(edgeToIdMap.get(edge));
            }
        }
        return result;
    }

    static void initializeDistances() {
        distances = new ArrayList<>(Collections.nCopies(extendedNodeCount, extendedNodeCount));
        distances.set(1, 0);
    }

    static void initializeVisited(List<Boolean> visited) {
        visited.set(1, true);
    }

    static void initializeGraph(List<List<Integer>> graph) {
        for (int i = 0; i < extendedNodeCount; i++) {
            graph.add(new ArrayList<>());
        }
    }

    static void addEdge(List<List<Integer>> graph, Map<Connection, Integer> edgeToIdMap, int start, int end, int capacity, int index) {
        graph.get(start).add(2 * index);
        graphEdges.add(new Connection(start, end, capacity));
        edgeToIdMap.put(graphEdges.get(graphEdges.size() - 1), index);
        graph.get(end).add(2 * index + 1);
        graphEdges.add(new Connection(end, start, capacity));
        edgeToIdMap.put(graphEdges.get(graphEdges.size() - 1), index);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<List<Integer>> graph = new ArrayList<>(extendedNodeCount);
        Map<Connection, Integer> edgeToIdMap = new HashMap<>();

        readInput(scanner, graph, edgeToIdMap);
        long maxFlow = calculateMaxFlow(graph);
        List<Integer> minCutEdges = findMinCutEdges(graph, edgeToIdMap);

        System.out.println(minCutEdges.size() + " " + maxFlow);
        minCutEdges.forEach(edgeId -> System.out.print((edgeId + 1) + " "));
    }
}