package repetition;

import java.util.*;

public class Task_C {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int m = scanner.nextInt();
        scanner.nextLine();

        ParkingManager parkingManager = new ParkingManager();
        for (int i = 0; i < m; ++i) {
            String event = scanner.nextLine();
            char action = event.charAt(0);
            int carNumber = Integer.parseInt(event.substring(2));

            if (action == '+') {
                int assignedSpot = parkingManager.parkCar(carNumber);
                System.out.println(assignedSpot);
            } else if (action == '-') {
                parkingManager.removeCar(carNumber);
            }
        }
    }
}

class ParkingManager {
    private final TreeSet<Integer> availableSpots;
    private final Map<Integer, Integer> carToSpot;
    private int nextSpot;

    public ParkingManager() {
        availableSpots = new TreeSet<>();
        carToSpot = new HashMap<>();
        nextSpot = 1;
    }

    public int parkCar(int carNumber) {
        int assignedSpot = !availableSpots.isEmpty() ? availableSpots.pollFirst() : nextSpot++;
        carToSpot.put(carNumber, assignedSpot);
        return assignedSpot;
    }

    public void removeCar(int carNumber) {
        int spot = carToSpot.remove(carNumber);
        availableSpots.add(spot);
    }
}