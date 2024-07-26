package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Task_E {
    static final long UNDEFINED = Integer.MAX_VALUE;
    static int rows, cols;
    static List<List<Cell>> grid;
    static Vertex source, sink;

    static class Vertex {
        int distance;
        boolean visited = false;
        int unusedEdges = 0;
        List<Edge> edges;

        public Vertex() {
            distance = (int) UNDEFINED;
            edges = new ArrayList<>();
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getUnusedEdges() {
            return unusedEdges;
        }

        public void reset() {
            distance = (int) UNDEFINED;
            unusedEdges = 0;
        }

        public void constructWall() {
            edges.get(0).initial.setCellType('+');
        }

        public boolean isCandidate() {
            return !edges.isEmpty() && edges.get(0).initial.getCellType() == '.';
        }
    }

    static class Edge {
        long capacity;
        long flow = 0;
        Cell initial;
        Vertex end;
        Edge reverse;

        public Edge(long capacity, Vertex end, Cell initial) {
            this.capacity = capacity;
            this.end = end;
            this.initial = initial;
        }

        public long getFlow() {
            return flow;
        }

        public void setFlow(long flow) {
            this.flow = flow;
        }

        public long getCapacity() {
            return capacity;
        }

        public boolean isFull() {
            return flow != capacity;
        }
    }

    static class Cell {
        char cellType;
        Vertex input;
        Vertex output;

        public Cell(char cellType) {
            this.cellType = cellType;
            input = new Vertex();
            output = new Vertex();
            switch (cellType) {
                case '.' -> input.edges.add(new Edge(1, output, this));
                case '-' -> input.edges.add(new Edge(UNDEFINED, output, this));
                case 'A' -> {
                    input = null;
                    source = output;
                }
                case 'B' -> {
                    output = null;
                    sink = input;
                }
                default -> {
                    input = null;
                    output = null;
                }
            }
        }

        public char getCellType() {
            return cellType;
        }

        public void setCellType(char cellType) {
            this.cellType = cellType;
        }

        public boolean isMountain() {
            return cellType == '#';
        }

        public void reset() {
            if (input != null) {
                input.reset();
            }
            if (output != null) {
                output.reset();
            }
        }
    }

    static void linkEdges(Vertex a, Vertex b) {
        a.edges.get(a.edges.size() - 1).reverse = b.edges.get(b.edges.size() - 1);
        b.edges.get(b.edges.size() - 1).reverse = a.edges.get(a.edges.size() - 1);
    }

    static void connectCells(Cell a, Cell b) {
        if (a.isMountain() || b.isMountain()) {
            return;
        }
        if (a.output != null && b.input != null) {
            a.output.edges.add(new Edge(UNDEFINED, b.input, a));
            b.input.edges.add(new Edge(0, a.output, b));
            linkEdges(a.output, b.input);
        }
        if (b.output != null && a.input != null) {
            b.output.edges.add(new Edge(UNDEFINED, a.input, b));
            a.input.edges.add(new Edge(0, b.output, a));
            linkEdges(b.output, a.input);
        }
    }

    static void resetValues() {
        for (List<Cell> row : grid) {
            for (Cell cell : row) {
                cell.reset();
            }
        }
    }

    static boolean bfs() {
        resetValues();
        source.setDistance(0);
        List<Vertex> queue = new ArrayList<>();
        queue.add(source);
        while (!queue.isEmpty()) {
            Vertex v = queue.remove(0);
            for (Edge edge : v.edges) {
                if (edge.end.getDistance() == UNDEFINED && edge.isFull()) {
                    edge.end.setDistance(v.getDistance() + 1);
                    queue.add(edge.end);
                }
            }
        }
        return sink.getDistance() != UNDEFINED;
    }

    static long dfs(Vertex v, long currentFlow) {
        if (v == sink || currentFlow == 0) {
            return currentFlow;
        }
        while (v.getUnusedEdges() < v.edges.size()) {
            Edge edge = v.edges.get(v.getUnusedEdges());
            if (edge.end.getDistance() == v.getDistance() + 1) {
                long delta = dfs(edge.end, Math.min(currentFlow, edge.getCapacity() - edge.getFlow()));
                if (delta != 0) {
                    edge.setFlow(edge.getFlow() + delta);
                    if (edge.reverse != null) {
                        edge.reverse.setFlow(edge.reverse.getFlow() - delta);
                    }
                    return delta;
                }
            }
            v.unusedEdges++;
        }
        return 0;
    }

    static long maxFlow() {
        long totalFlow = 0;
        while (bfs()) {
            long flow;
            while ((flow = dfs(source, UNDEFINED)) != 0) {
                totalFlow += flow;
            }
        }
        return totalFlow;
    }

    static void findReachableVertices(Vertex v, List<Vertex> reachable) {
        v.visited = true;
        for (Edge edge : v.edges) {
            if (!edge.end.visited && edge.isFull()) {
                findReachableVertices(edge.end, reachable);
            }
        }
        reachable.add(v);
    }

    static void buildWalls() {
        List<Vertex> reachable = new ArrayList<>();
        findReachableVertices(source, reachable);
        for (Vertex v : reachable) {
            if (v.isCandidate()) {
                for (Edge edge : v.edges) {
                    if (!edge.end.visited && edge.getFlow() == 1) {
                        v.constructWall();
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        rows = scanner.nextInt();
        cols = scanner.nextInt();
        grid = new ArrayList<>();
        scanner.nextLine();
        for (int i = 0; i < rows; i++) {
            grid.add(new ArrayList<>());
            String line = scanner.nextLine();
            for (int j = 0; j < cols; j++) {
                grid.get(i).add(new Cell(line.charAt(j)));
                if (i > 0) {
                    connectCells(grid.get(i).get(j), grid.get(i - 1).get(j));
                }
                if (j > 0) {
                    connectCells(grid.get(i).get(j), grid.get(i).get(j - 1));
                }
            }
        }

        long answer = maxFlow();
        if (answer >= UNDEFINED) {
            System.out.println(-1);
        } else {
            buildWalls();
            System.out.println(answer);
            for (List<Cell> row : grid) {
                System.out.println(row.stream().map(cell -> String.valueOf(cell.getCellType())).collect(Collectors.joining()));
            }
        }
    }
}
