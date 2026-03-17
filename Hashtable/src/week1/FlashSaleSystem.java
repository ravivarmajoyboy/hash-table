package week1;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleManager {
    // productId -> currentStock (Atomic for thread-safety)
    private Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // productId -> Queue of userIds (FIFO waiting list)
    private Map<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public FlashSaleManager() {
        // Setup initial stock: 100 units for iPhone 15
        inventory.put("IPHONE15_256GB", new AtomicInteger(100));
        waitingLists.put("IPHONE15_256GB", new ConcurrentLinkedQueue<>());
    }

    /**
     * Checks stock in O(1) time
     */
    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    /**
     * Core purchase logic with concurrency protection
     */
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) return "Product not found.";

        // Attempt to decrement ONLY if stock > 0
        // We use a loop with compareAndSet to handle high-contention safely
        while (true) {
            int currentStock = stock.get();
            if (currentStock <= 0) {
                // Out of stock, add to waiting list
                Queue<Integer> waitList = waitingLists.get(productId);
                waitList.add(userId);

                // Convert queue to array to find position (index + 1)
                int position = new ArrayList<>(waitList).indexOf(userId) + 1;
                return "Added to waiting list, position #" + position;
            }

            // Atomically update: if stock is still currentStock, set to currentStock - 1
            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                return "Success, " + (currentStock - 1) + " units remaining";
            }
            // If compareAndSet fails, another thread won the race; loop and try again
        }
    }

    public static void main(String[] args) {
        FlashSaleManager sale = new FlashSaleManager();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("--- ⚡ FLASH SALE LIVE: IPHONE 15 ⚡ ---");

        while (true) {
            System.out.println("\n1. Check Stock\n2. Buy Item\n3. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.println("Stock: " + sale.checkStock("IPHONE15_256GB"));
            } else if (choice.equals("2")) {
                int mockUserId = random.nextInt(100000);
                String result = sale.purchaseItem("IPHONE15_256GB", mockUserId);
                System.out.println("User " + mockUserId + ": " + result);
            } else {
                break;
            }
        }
    }
}