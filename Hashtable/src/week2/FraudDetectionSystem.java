package week2;

import java.util.*;

public class FraudDetectionSystem {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        long timestamp; // epoch milliseconds

        Transaction(int id, int amount, String merchant, long timestamp) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "{id:" + id + ", amt:" + amount + ", merchant:'" + merchant + "'}";
        }
    }

    /**
     * Finds pairs that sum to target in O(n) time.
     * Logic: For every 'x', check if 'target - x' exists in the map.
     */
    public List<String> findTwoSum(List<Transaction> transactions, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<String> results = new ArrayList<>();

        for (Transaction tx : transactions) {
            int complement = target - tx.amount;
            if (map.containsKey(complement)) {
                results.add("Pair found: " + map.get(complement).id + " & " + tx.id);
            }
            map.put(tx.amount, tx);
        }
        return results;
    }

    /**
     * Detects potential duplicate payments: same amount and merchant, different IDs.
     */
    public void detectDuplicates(List<Transaction> transactions) {
        // Key: amount + merchant name
        Map<String, List<Transaction>> activityMap = new HashMap<>();

        for (Transaction tx : transactions) {
            String key = tx.amount + "_" + tx.merchant;
            activityMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tx);
        }

        System.out.println("\n--- Duplicate Check ---");
        activityMap.forEach((key, list) -> {
            if (list.size() > 1) {
                System.out.println("Suspicious Activity for " + key + ": " + list);
            }
        });
    }

    /**
     * Two-Sum with a Time Window (e.g., within 1 hour / 3,600,000 ms)
     */
    public List<String> findTwoSumWithTimeWindow(List<Transaction> transactions, int target, long windowMs) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<String> results = new ArrayList<>();

        for (Transaction tx : transactions) {
            int complement = target - tx.amount;
            if (map.containsKey(complement)) {
                Transaction prev = map.get(complement);
                if (Math.abs(tx.timestamp - prev.timestamp) <= windowMs) {
                    results.add("Time-Linked Pair: " + prev.id + " & " + tx.id);
                }
            }
            map.put(tx.amount, tx);
        }
        return results;
    }

    public static void main(String[] args) {
        FraudDetectionSystem system = new FraudDetectionSystem();
        long now = System.currentTimeMillis();

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1, 500, "Amazon", now),
                new Transaction(2, 300, "Starbucks", now + 1000 * 60 * 15), // 15 mins later
                new Transaction(3, 200, "Target", now + 1000 * 60 * 30),    // 30 mins later
                new Transaction(4, 500, "Amazon", now + 1000 * 5)          // Duplicate check
        );

        System.out.println("Classic Two-Sum (Target 500): " + system.findTwoSum(transactions, 500));
        System.out.println("Time-Window Two-Sum (Target 500, 1hr): " + system.findTwoSumWithTimeWindow(transactions, 500, 3600000));

        system.detectDuplicates(transactions);
    }
}
