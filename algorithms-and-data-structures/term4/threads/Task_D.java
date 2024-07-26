package threads;

import java.util.*;
import java.util.stream.IntStream;

public class Task_D {
    static int numTeams, source, sink;
    static long totalFlow = 0;
    static final int MAX_TEAMS = 105;
    static final int INFINITY = Integer.MAX_VALUE;

    static List<List<Match>> matchGraph = new ArrayList<>(MAX_TEAMS);
    static int[] teamScores = new int[MAX_TEAMS];
    static char[][] matchTable = new char[MAX_TEAMS][MAX_TEAMS];
    static List<Set<Integer>> pendingMatches = new ArrayList<>(MAX_TEAMS);
    static int[] requiredScores = new int[MAX_TEAMS];
    static boolean[] visited = new boolean[MAX_TEAMS];

    static {
        for (int i = 0; i < MAX_TEAMS; i++) {
            matchGraph.add(new ArrayList<>());
            pendingMatches.add(new HashSet<>());
        }
    }

    static class Match {
        int from, to;
        long capacity, flow;
        Match reverseMatch = null;

        Match(int from, int to, long capacity, long flow) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = flow;
        }
    }

    static void addMatch(int from, int to, long capacity) {
        Match forwardMatch = new Match(from, to, capacity, 0);
        Match backwardMatch = new Match(to, from, 0, 0);
        forwardMatch.reverseMatch = backwardMatch;
        backwardMatch.reverseMatch = forwardMatch;
        matchGraph.get(from).add(forwardMatch);
        matchGraph.get(to).add(backwardMatch);
    }

    static long depthFirstSearch(int node, long minCapacity) {
        if (node == sink) {
            return minCapacity;
        }
        visited[node] = true;

        for (Match match : matchGraph.get(node)) {
            if (!visited[match.to] && match.flow < match.capacity) {
                long pushedFlow = depthFirstSearch(match.to, Math.min(minCapacity, match.capacity - match.flow));
                if (pushedFlow > 0) {
                    match.flow += pushedFlow;
                    match.reverseMatch.flow -= pushedFlow;
                    return pushedFlow;
                }
            }
        }
        return 0;
    }

    static void dinicAlgorithm() {
        while (true) {
            Arrays.fill(visited, false);
            long pushedFlow = depthFirstSearch(source, INFINITY);
            totalFlow += pushedFlow;
            if (pushedFlow == 0) {
                break;
            }
        }
    }

    static long calculatePotentialPoints(int team) {
        return pendingMatches.get(team).stream().mapToInt(opponent -> opponent)
                .filter(opponent -> opponent > team).mapToLong(opponent -> 3).sum();
    }

    static void initializeMatchTable(Scanner scanner) {
        for (int i = 1; i <= numTeams; i++) {
            String line = scanner.next();
            for (int j = 1; j <= numTeams; j++) {
                char result = line.charAt(j - 1);
                matchTable[i][j] = result;
                updateTeamScores(i, result);
                if (result == '.') {
                    pendingMatches.get(i).add(j);
                }
            }
        }
    }

    static void updateTeamScores(int team, char result) {
        switch (result) {
            case 'W':
                teamScores[team] += 3;
                break;
            case 'w':
                teamScores[team] += 2;
                break;
            case 'L':
                teamScores[team] += 0;
                break;
            case 'l':
                teamScores[team] += 1;
                break;
        }
    }

    static void initializeRequiredScores(Scanner scanner) {
        IntStream.rangeClosed(1, numTeams).forEach(i -> {
            int score = scanner.nextInt();
            requiredScores[i] = Math.max(0, score - teamScores[i]);
        });
    }

    static void addSourceAndSinkEdges() {
        IntStream.rangeClosed(1, numTeams).forEach(i -> addMatch(source, i, calculatePotentialPoints(i)));
        IntStream.rangeClosed(1, numTeams).forEach(i -> addMatch(i, sink, requiredScores[i]));
    }

    static void addPendingMatchEdges() {
        IntStream.rangeClosed(1, numTeams).forEach(i -> {
            for (int opponent : pendingMatches.get(i)) {
                if (opponent > i) {
                    addMatch(i, opponent, 3);
                }
            }
        });
    }

    static void updateMatchTable() {
        IntStream.rangeClosed(1, numTeams).forEachOrdered(i -> {
            for (Match match : matchGraph.get(i)) {
                if (match.to != sink && match.to != source && i < match.to) {
                    updateMatchResult(i, match);
                }
            }
        });
    }

    static void updateMatchResult(int team, Match match) {
        switch ((int) match.flow) {
            case 0:
                matchTable[team][match.to] = 'W';
                matchTable[match.to][team] = 'L';
                break;
            case 1:
                matchTable[team][match.to] = 'w';
                matchTable[match.to][team] = 'l';
                break;
            case 2:
                matchTable[team][match.to] = 'l';
                matchTable[match.to][team] = 'w';
                break;
            case 3:
                matchTable[team][match.to] = 'L';
                matchTable[match.to][team] = 'W';
                break;
        }
    }

    static void printMatchTable() {
        for (int i = 1; i <= numTeams; i++) {
            for (int j = 1; j <= numTeams; j++) {
                System.out.print(matchTable[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        numTeams = scanner.nextInt();
        source = 0;
        sink = numTeams + 1;

        initializeMatchTable(scanner);
        initializeRequiredScores(scanner);
        addSourceAndSinkEdges();
        addPendingMatchEdges();
        dinicAlgorithm();
        updateMatchTable();
        printMatchTable();
    }
}