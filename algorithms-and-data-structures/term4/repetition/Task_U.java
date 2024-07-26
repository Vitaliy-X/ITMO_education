//package repetition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Task_U {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        String str1 = reader.readLine().trim();
        String str2 = reader.readLine().trim();
        String str3 = reader.readLine().trim();

        String result = getMinimalSuperstring(str1, str2, str3);
        System.out.println(result.length());
        System.out.println(result);
    }

    private static String getMinimalSuperstring(final String str1, final String str2, final String str3) {
        String[] allCombinations = generateAllCombinations(str1, str2, str3);
        return findShortestCombination(allCombinations);
    }

    private static String[] generateAllCombinations(String str1, String str2, String str3) {
        return new String[]{
                combineStrings(combineStrings(str1, str2), str3),
                combineStrings(combineStrings(str1, str3), str2),
                combineStrings(combineStrings(str2, str1), str3),
                combineStrings(combineStrings(str2, str3), str1),
                combineStrings(combineStrings(str3, str1), str2),
                combineStrings(combineStrings(str3, str2), str1)
        };
    }

    private static String findShortestCombination(final String[] combinations) {
        String shortest = combinations[0];
        for (String combination : combinations) {
            if (combination.length() < shortest.length()) {
                shortest = combination;
            }
        }
        return shortest;
    }

    private static String combineStrings(String a, String b) {
        int overlap = findMaxOverlap(a, b);
        return a + b.substring(overlap);
    }

    private static int findMaxOverlap(String a, String b) {
        String combined = b + "#" + a;
        int[] lps = computeLPSArray(combined);
        return lps[lps.length - 1];
    }

    private static int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int idx = 1;
        while (idx < pattern.length()) {
            if (pattern.charAt(idx) == pattern.charAt(length)) {
                length++;
                lps[idx] = length;
                idx++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[idx] = 0;
                    idx++;
                }
            }
        }
        return lps;
    }
}