package week1;

import java.util.*;

public class DNSCacheSystem {

    // Custom Entry to store IP and Expiry metadata
    class DNSEntry {
        String ip;
        long expiryTime;

        DNSEntry(String ip, int ttlSeconds) {
            this.ip = ip;
            // Current time + TTL converted to milliseconds
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int CAPACITY = 1000;
    private int hits = 0;
    private int misses = 0;

    // LinkedHashMap with 'accessOrder = true' handles LRU eviction automatically
    private final Map<String, DNSEntry> cache = new LinkedHashMap<>(CAPACITY, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
            return size() > CAPACITY;
        }
    };

    /**
     * Resolves a domain name to an IP address.
     */
    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        // CASE 1: Cache Hit
        if (entry != null && !entry.isExpired()) {
            hits++;
            return "(Cache HIT) " + entry.ip;
        }

        // CASE 2: Cache Miss (Expired or not present)
        misses++;
        if (entry != null && entry.isExpired()) {
            cache.remove(domain); // Clean up expired entry
        }

        // Simulate Upstream DNS Query
        String resolvedIp = "192.168.1." + new Random().nextInt(255);
        int ttl = 5; // 5 second TTL for demonstration

        cache.put(domain, new DNSEntry(resolvedIp, ttl));
        return "(Cache MISS/EXPIRED -> Upstream Query) " + resolvedIp;
    }

    public void printStats() {
        double total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits / total) * 100;
        System.out.println("\n--- DNS Cache Stats ---");
        System.out.println("Hits: " + hits + " | Misses: " + misses);
        System.out.printf("Hit Rate: %.2f%%\n", hitRate);
    }

    public static void main(String[] args) throws InterruptedException {
        DNSCacheSystem dns = new DNSCacheSystem();
        Scanner scanner = new Scanner(System.in);

        System.out.println("DNS Resolver Active (TTL set to 5 seconds for testing)");

        while (true) {
            System.out.print("\nEnter domain to resolve (or 'stats', 'exit'): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("exit")) break;
            if (input.equals("stats")) {
                dns.printStats();
                continue;
            }

            long start = System.nanoTime();
            String result = dns.resolve(input);
            long end = System.nanoTime();

            System.out.println(result);
            System.out.printf("Lookup Time: %.3f ms\n", (end - start) / 1_000_000.0);
        }
        scanner.close();
    }
}