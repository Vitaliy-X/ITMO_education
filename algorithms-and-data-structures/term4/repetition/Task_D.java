package repetition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Task_D {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] firstLine = reader.readLine().split(" ");
        int n = Integer.parseInt(firstLine[0]);
        int m = Integer.parseInt(firstLine[1]);
        int[] keys = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        long[] prefixSums = computePrefixSums(keys, n);

        List<int[]> queries = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            String[] lr = reader.readLine().split(" ");
            int l = Integer.parseInt(lr[0]);
            int r = Integer.parseInt(lr[1]);
            queries.add(new int[]{l, r});
        }

        List<Integer> results = processQueries(keys, prefixSums, queries);
        StringBuilder output = new StringBuilder();
        results.forEach(result -> output.append(result).append("\n"));
        System.out.print(output.toString());
    }

    private static long[] computePrefixSums(final int[] keys, final int n) {
        long[] prefixSums = new long[n + 1];
        IntStream.rangeClosed(1, n).forEach(i -> prefixSums[i] = prefixSums[i - 1] + keys[i - 1]);
        return prefixSums;
    }

    private static List<Integer> processQueries(int[] keys, long[] prefixSums, List<int[]> queries) {
        List<Integer> results = new ArrayList<>();
        TreeSet<Integer> keySet = Arrays.stream(keys).boxed().collect(Collectors.toCollection(TreeSet::new));

        queries.forEach(query -> {
            int l = query[0];
            int r = query[1];
            double average = (prefixSums[r] - prefixSums[l - 1]) / (double) (r - l + 1);
            Integer result = keySet.ceiling((int) Math.ceil(average));
            results.add(result);
        });
        return results;
    }
}