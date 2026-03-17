package week2;

import java.util.*;

public class SmartParkingLot {
    enum Status { EMPTY, OCCUPIED, DELETED }

    class Spot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;

        Spot() {}
    }

    private Spot[] lot;
    private final int capacity;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int totalParkings = 0;

    public SmartParkingLot(int size) {
        this.capacity = size;
        this.lot = new Spot[size];
        for (int i = 0; i < size; i++) lot[i] = new Spot();
    }

    // Custom hash function to map license plate to a spot
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode() % capacity);
    }

    /**
     * Assigns a spot using Linear Probing: index, (index+1)%size, (index+2)%size...
     */
    public String parkVehicle(String licensePlate) {
        if (occupiedCount >= capacity) return "Error: Lot is Full!";

        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Linear Probing logic
        while (lot[currentSpot].status == Status.OCCUPIED) {
            currentSpot = (currentSpot + 1) % capacity;
            probes++;
        }

        // Assign spot
        lot[currentSpot].licensePlate = licensePlate;
        lot[currentSpot].entryTime = System.currentTimeMillis();
        lot[currentSpot].status = Status.OCCUPIED;

        occupiedCount++;
        totalParkings++;
        totalProbes += probes;

        return String.format("Assigned spot #%d (%d probes)", currentSpot, probes);
    }

    /**
     * Frees a spot and calculates the fee.
     */
    public String exitVehicle(String licensePlate) {
        int initialSpot = hash(licensePlate);
        int currentSpot = initialSpot;
        int probes = 0;

        // Search for the vehicle using the same probing logic
        while (lot[currentSpot].status != Status.EMPTY) {
            if (lot[currentSpot].status == Status.OCCUPIED &&
                    lot[currentSpot].licensePlate.equals(licensePlate)) {

                long durationMs = System.currentTimeMillis() - lot[currentSpot].entryTime;
                double fee = (durationMs / 1000.0) * 0.50; // $0.50 per simulated second

                lot[currentSpot].status = Status.DELETED; // Use DELETED to keep probe chain intact
                lot[currentSpot].licensePlate = null;
                occupiedCount--;

                return String.format("Spot #%d freed. Fee: $%.2f", currentSpot, fee);
            }
            currentSpot = (currentSpot + 1) % capacity;
            if (currentSpot == initialSpot) break; // Wrapped around
        }

        return "Vehicle not found.";
    }

    public void getStatistics() {
        double occupancy = (double) occupiedCount / capacity * 100;
        double avgProbes = totalParkings == 0 ? 0 : (double) totalProbes / totalParkings;

        System.out.println("\n--- Parking Statistics ---");
        System.out.printf("Occupancy: %.1f%%\n", occupancy);
        System.out.printf("Average Probes per Parking: %.2f\n", avgProbes);
    }

    public static void main(String[] args) {
        SmartParkingLot mallParking = new SmartParkingLot(500);

        // Simulating collisions
        System.out.println(mallParking.parkVehicle("ABC-1234"));
        System.out.println(mallParking.parkVehicle("ABC-1235")); // Likely collision if hash is close
        System.out.println(mallParking.parkVehicle("XYZ-9999"));

        mallParking.getStatistics();

        System.out.println("\nExiting: " + mallParking.exitVehicle("ABC-1234"));
    }
}
