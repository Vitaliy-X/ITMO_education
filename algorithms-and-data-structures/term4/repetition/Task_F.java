//package repetition;

import java.util.*;
import java.util.stream.IntStream;

public class Task_F {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int numTables = input.nextInt();
        Long[] tableSizes = getInputArray(input, numTables);
        int numCompanies = input.nextInt();
        Long[] companySizes = getInputArray(input, numCompanies);
        long maxSeatedPeople = getMaxSeatedPeople(tableSizes, companySizes);
        System.out.println(maxSeatedPeople);
    }

    private static Long[] getInputArray(Scanner input, int length) {
        return IntStream.range(0, length).mapToObj(i -> input.nextLong()).toArray(Long[]::new);
    }

    private static long getMaxSeatedPeople(final Long[] tableSizes, final Long[] companySizes) {
        Arrays.sort(tableSizes, Collections.reverseOrder());
        Arrays.sort(companySizes, Collections.reverseOrder());

        int tableIndex = 0, companyIndex = 0;
        long totalSeated = 0;

        while (tableIndex < tableSizes.length && companyIndex < companySizes.length) {
            if (tableSizes[tableIndex] >= companySizes[companyIndex]) {
                totalSeated += companySizes[companyIndex];
                tableIndex++;
            }
            companyIndex++;
        }

        return totalSeated;
    }
}