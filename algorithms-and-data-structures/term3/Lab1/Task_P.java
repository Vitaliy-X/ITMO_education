package Lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task_P {
    static class Point {
        long x, y;

        Point(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    private final List<Point> points;
    private final List<Long> minimumDistances;
    private final List<Boolean> usedPoints;

    public Task_P(int numberOfPoints) {
        points = new ArrayList<>();
        minimumDistances = new ArrayList<>();
        usedPoints = new ArrayList<>();

        for (int i = 0; i < numberOfPoints; i++) {
            minimumDistances.add(Long.MAX_VALUE);
            usedPoints.add(false);
        }
    }

    public void addPoint(long x, long y) {
        points.add(new Point(x, y));
    }

    public double calculateMinimumSpanningTree() {
        usedPoints.set(0, true);
        minimumDistances.set(0, -1L);

        for (int i = 1; i < points.size(); i++) {
            minimumDistances.set(i, distanceSquared(points.get(0), points.get(i)));
        }

        double totalDistance = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            int nearestPointIndex = findNearestUnusedPoint();
            totalDistance += Math.sqrt(minimumDistances.get(nearestPointIndex));
            usedPoints.set(nearestPointIndex, true);
            updateDistances(nearestPointIndex);
        }

        return totalDistance;
    }

    private int findNearestUnusedPoint() {
        int index = -1;
        for (int i = 0; i < points.size(); i++) {
            if (!usedPoints.get(i)) {
                index = i;
                break;
            }
        }

        for (int i = index + 1; i < points.size(); i++) {
            if (!usedPoints.get(i) && minimumDistances.get(index) > minimumDistances.get(i)) {
                index = i;
            }
        }

        return index;
    }

    private void updateDistances(int pointIndex) {
        for (int i = 0; i < points.size(); i++) {
            if (!usedPoints.get(i)) {
                long newDist = distanceSquared(points.get(Math.max(i, pointIndex)), points.get(Math.min(i, pointIndex)));
                if (minimumDistances.get(i) > newDist) {
                    minimumDistances.set(i, newDist);
                }
            }
        }
    }

    private static long distanceSquared(Point a, Point b) {
        long dx = a.x - b.x;
        long dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Task_P processor = new Task_P(n);

        for (int i = 0; i < n; i++) {
            long x = sc.nextLong();
            long y = sc.nextLong();
            processor.addPoint(x, y);
        }

        double result = processor.calculateMinimumSpanningTree();
        System.out.println(result);
        sc.close();
    }
}
