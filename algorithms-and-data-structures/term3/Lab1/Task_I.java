package Lab1;

import java.util.*;
import java.util.stream.IntStream;

public class Task_I {
    static int nodeCount, edgeCount, time = 0, componentCount = 0;
    static ArrayList<Integer>[] graph;
    static boolean[] visited;
    static int[] timeIn, timeOut, result;
    static HashMap<Pair, Integer> edgeCountMap = new HashMap<>();
    static ArrayList<Pair> bridges = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        nodeCount = scanner.nextInt();
        edgeCount = scanner.nextInt();

        graph = new ArrayList[nodeCount + 1];
        for (int i = 0; i <= nodeCount; i++) {
            graph[i] = new ArrayList<>();
        }

        visited = new boolean[nodeCount + 1];
        timeIn = new int[nodeCount + 1];
        timeOut = new int[nodeCount + 1];
        result = new int[nodeCount];

        IntStream.range(0, edgeCount).map(i -> scanner.nextInt()).forEach(node1 -> {
            int node2 = scanner.nextInt();
            Pair pair = new Pair(Math.max(node1, node2), Math.min(node1, node2));
            edgeCountMap.put(pair, edgeCountMap.getOrDefault(pair, 0) + 1);
            graph[node1].add(node2);
            graph[node2].add(node1);
        });

        IntStream.rangeClosed(1, nodeCount).filter(node -> !visited[node]).forEach(node -> depthFirstSearch(node, -1));

        bridges.forEach(bridge -> {
            int parent = bridge.first, child = bridge.second;
            graph[parent].remove((Integer) child);
            graph[child].remove((Integer) parent);
        });

        IntStream.rangeClosed(1, nodeCount).filter(node -> visited[node]).forEach(node -> {
            componentCount++;
            checkComponent(node);
        });

        System.out.println(componentCount);
        Arrays.stream(result).mapToObj(component -> component + " ").forEach(System.out::print);
    }

    static void depthFirstSearch(int node, int parent) {
        timeOut[node] = timeIn[node] = ++time;
        visited[node] = true;
        for (int neighbour : graph[node]) {
            if (neighbour == parent) {
                continue;
            }

            if (visited[neighbour]) {
                timeOut[node] = Math.min(timeOut[node], timeIn[neighbour]);
            } else {
                depthFirstSearch(neighbour, node);
                timeOut[node] = Math.min(timeOut[node], timeOut[neighbour]);
            }
        }

        if (parent != -1 && timeOut[node] == timeIn[node] && edgeCountMap.get(new Pair(Math.max(node, parent), Math.min(node, parent))) == 1) {
            bridges.add(new Pair(parent, node));
        }
    }

    static void checkComponent(int node) {
        visited[node] = false;
        graph[node].stream().mapToInt(neighbour -> neighbour).filter(neighbour -> visited[neighbour]).forEach(Task_I::checkComponent);
        result[node - 1] = componentCount;
    }

    static class Pair {
        int first, second;

        Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return first == pair.first && second == pair.second;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
}
