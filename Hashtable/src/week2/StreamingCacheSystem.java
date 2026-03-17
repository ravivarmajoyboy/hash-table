package week2;

import java.util.*;

public class StreamingCacheSystem {
    // Configuration
    private final int L1_SIZE = 3; // Small size for demonstration
    private final int L2_SIZE = 10;
    private final int PROMOTION_THRESHOLD = 2;

    // Statistics
    private double l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;

    // Data Tiers
    private final Map<String, String> l1Cache; // Memory
    private final Map<String, String> l2Cache = new HashMap<>(); // SSD
    private final Map<String, Integer> accessFrequency = new HashMap<>(); // Tracking
    private final Map<String, String> database = new HashMap<>(); // Database (L3)

    public StreamingCacheSystem() {
        // L1 Implementation using LinkedHashMap for automatic LRU
        this.l1Cache = new LinkedHashMap<>(L1_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                if (size() > L1_SIZE) {
                    System.out.println("[L1] Evicting " + eldest.getKey() + " to L2");
                    l2Cache.put(eldest.getKey(), eldest.getValue());
                    return true;
                }
                return false;
            }
        };

        // Pre-fill Database (L3)
        database.put("v1", "Stranger Things");
        database.put("v2", "The Crown");
        database.put("v3", "Bridgerton");
        database.put("v4", "The Witcher");
        database.put("v5", "Black Mirror");
    }

    public String getVideo(String videoId) {
        totalRequests++;

        // --- TIER 1: MEMORY ---
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            return l1Cache.get(videoId) + " (L1 HIT - 0.5ms)";
        }

        // --- TIER 2: SSD ---
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            String data = l2Cache.get(videoId);

            // Logic for promotion to L1
            int count = accessFrequency.getOrDefault(videoId, 0) + 1;
            accessFrequency.put(videoId, count);

            if (count >= PROMOTION_THRESHOLD) {
                System.out.println("[Promotion] " + videoId + " moved L2 -> L1");
                l1Cache.put(videoId, data);
                l2Cache.remove(videoId);
            }
            return data + " (L2 HIT - 5ms)";
        }

        // --- TIER 3: DATABASE ---
        l3Hits++;
        String data = database.get(videoId);
        if (data != null) {
            System.out.println("[L3] Fetching from DB. Adding to L2.");
            l2Cache.put(videoId, data);
            accessFrequency.put(videoId, 1);
            return data + " (L3 HIT - 150ms)";
        }

        return "Video Not Found";
    }

    public void printStats() {
        System.out.println("\n--- Cache Performance Report ---");
        System.out.printf("L1 Hit Rate: %.1f%%\n", (l1Hits / totalRequests) * 100);
        System.out.printf("L2 Hit Rate: %.1f%%\n", (l2Hits / totalRequests) * 100);
        System.out.printf("L3 Hit Rate: %.1f%%\n", (l3Hits / totalRequests) * 100);
        System.out.println("L1 Content: " + l1Cache.keySet());
    }

    public static void main(String[] args) {
        StreamingCacheSystem netflix = new StreamingCacheSystem();

        // Simulate access patterns
        System.out.println(netflix.getVideo("v1")); // L3 Hit
        System.out.println(netflix.getVideo("v1")); // L2 Hit
        System.out.println(netflix.getVideo("v1")); // L1 Hit (Promoted)
        System.out.println(netflix.getVideo("v2")); // L3 Hit
        System.out.println(netflix.getVideo("v3")); // L3 Hit
        System.out.println(netflix.getVideo("v4")); // L3 Hit (Will trigger L1 eviction)

        netflix.printStats();
    }
}
