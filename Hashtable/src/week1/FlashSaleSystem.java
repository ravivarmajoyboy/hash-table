package week1;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleSystem {
    // productId -> currentStock (Atomic for thread-safety)
    private Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // productId -> Queue of userIds (FIFO waiting list)
    private Map<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public FlashSaleSystem() {
        // Initial setup for the flash sale
        inventory.put("IPHONE15", new AtomicInteger(100));
        waitingLists.put("IPHONE15", new ConcurrentLinkedQueue<>());
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) return "Error: Product not found.";

        // Attempt to decrement stock safely
        while (true) {
            int currentStock = stock.get();

            if (currentStock <= 0) {
                // Out of stock logic
                Queue<Integer> waitList = waitingLists.get(productId);
                if (!waitList.contains(userId)) {
                    waitList.add(userId);
                }
                // Determine position in list
                int position = getPositionInQueue(waitList, userId);
                return "❌ Sold Out! Added to waiting list at position #" + position;
            }

            // Atomic Compare-and-Swap (CAS)
            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                return "✅ Success! Item purchased. Units remaining: " + (currentStock - 1);
            }
            // If CAS failed, another thread updated the stock first; loop and try again.
        }
    }

    private int getPositionInQueue(Queue<Integer> queue, int userId) {
        int pos = 1;
        for (Integer id : queue) {
            if (id == userId) return pos;
            pos++;
        }
        return pos;
    }

    public static void main(String[] args) {
        FlashSaleSystem system = new FlashSaleSystem();
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();

        System.out.println("=== ⚡ FLASH SALE CONSOLE ⚡ ===");
        System.out.println("Item: IPHONE15 | Initial Stock: 100");

        while (true) {
            System.out.print("\nEnter User ID to attempt purchase (or '0' for random, 'exit' to quit): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) break;

            int userId;
            try {
                userId = input.equals("0") ? rand.nextInt(90000) + 10000 : Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            String result = system.purchaseItem("IPHONE15", userId);
            System.out.println("Result for User " + userId + ": " + result);
        }

        scanner.close();
        System.out.println("Sale Ended.");
    }
}