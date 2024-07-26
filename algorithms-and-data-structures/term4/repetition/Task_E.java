package repetition;

import java.util.Scanner;

public class Task_E {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String recipe = scanner.nextLine();
        int breadCount = scanner.nextInt();
        int sausageCount = scanner.nextInt();
        int cheeseCount = scanner.nextInt();
        int breadPrice = scanner.nextInt();
        int sausagePrice = scanner.nextInt();
        int cheesePrice = scanner.nextInt();
        long rubles = scanner.nextLong();

        long maxHamburgers = getMaxHamburgers(recipe, breadCount, sausageCount, cheeseCount,
                breadPrice, sausagePrice, cheesePrice, rubles);
        System.out.println(maxHamburgers);
    }

    private static long getMaxHamburgers(String recipe, int breadCount, int sausageCount, int cheeseCount,
                                         int breadPrice, int sausagePrice, int cheesePrice, long rubles) {
        int[] requiredIngredients = countIngredients(recipe);

        long low = 0, high = (long) 1e13;
        while (low < high) {
            long mid = (low + high + 1) / 2;
            if (canAffordHamburgers(mid, requiredIngredients, breadCount, sausageCount,
                    cheeseCount, breadPrice, sausagePrice, cheesePrice, rubles)) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }

    private static int[] countIngredients(String recipe) {
        int requiredBread = 0, requiredSausage = 0, requiredCheese = 0;
        for (char ingredient : recipe.toCharArray()) {
            if (ingredient == 'B') requiredBread++;
            else if (ingredient == 'S') requiredSausage++;
            else if (ingredient == 'C') requiredCheese++;
        }
        return new int[]{requiredBread, requiredSausage, requiredCheese};
    }

    private static boolean canAffordHamburgers(long hamburgers, int[] requiredIngredients, int breadCount,
                                               int sausageCount, int cheeseCount, int breadPrice,
                                               int sausagePrice, int cheesePrice, long rubles) {
        long neededBread = Math.max(0, hamburgers * requiredIngredients[0] - breadCount);
        long neededSausage = Math.max(0, hamburgers * requiredIngredients[1] - sausageCount);
        long neededCheese = Math.max(0, hamburgers * requiredIngredients[2] - cheeseCount);
        long totalCost = neededBread * breadPrice + neededSausage * sausagePrice + neededCheese * cheesePrice;
        return totalCost <= rubles;
    }
}