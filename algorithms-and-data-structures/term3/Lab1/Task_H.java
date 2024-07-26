package Lab1;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Task_H {
    static class ArticulationPointSearch {
        static Set<Long> articulationPoints = new TreeSet<>();
        static long timestamp = 0;

        static void findArticulationPointsUtil(List<List<Long>> graph, long u, boolean[] visited, long[] disc, long[] low, long parent, boolean[] isAP) {
            int children = 0;
            visited[(int) u] = true;
            disc[(int) u] = low[(int) u] = ++timestamp;

            for (long v : graph.get((int) u)) {
                if (visited[(int) v]) {
                    if (v != parent) {
                        low[(int) u] = Math.min(low[(int) u], disc[(int) v]);
                    }
                } else {
                    children++;
                    findArticulationPointsUtil(graph, v, visited, disc, low, u, isAP);

                    low[(int) u] = Math.min(low[(int) u], low[(int) v]);

                    if (parent != -1 && low[(int) v] >= disc[(int) u]) {
                        isAP[(int) u] = true;
                    }
                }
            }

            if (parent == -1 && children > 1) {
                isAP[(int) u] = true;
            }
        }

        static void findArticulationPoints(List<List<Long>> graph, long n) {
            long[] discoveryTimes = new long[(int) n];
            long[] lowTimes = new long[(int) n];
            boolean[] visitedNodes = new boolean[(int) n];
            boolean[] isArticulationPoint = new boolean[(int) n];
            long parentNode = -1;

            LongStream.range(0, n).filter(u -> !visitedNodes[(int) u]).forEachOrdered(u -> findArticulationPointsUtil(graph, u, visitedNodes, discoveryTimes, lowTimes, parentNode, isArticulationPoint));
            LongStream.range(0, n).filter(u -> isArticulationPoint[(int) u]).forEachOrdered(u -> articulationPoints.add(u));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        long n = scanner.nextLong();
        long m = scanner.nextLong();

        List<List<Long>> graph = IntStream.iterate(0, i -> i < n, i -> i + 1).<List<Long>>mapToObj(i -> new ArrayList<>()).collect(Collectors.toList());

        LongStream.range(0, m).map(i -> scanner.nextLong()).forEachOrdered(u -> {
            long v = scanner.nextLong();
            graph.get((int) (u - 1)).add(v - 1);
            graph.get((int) (v - 1)).add(u - 1);
        });

        ArticulationPointSearch.findArticulationPoints(graph, n);

        System.out.println(ArticulationPointSearch.articulationPoints.size());
        ArticulationPointSearch.articulationPoints.stream().mapToLong(i -> i).mapToObj(i -> (i + 1) + " ").forEach(System.out::print);
    }
}