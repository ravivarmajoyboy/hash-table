package week1;

import java.util.*;
import java.util.concurrent.*;

public class AnalyticsDashboard {
    // 1. URL -> Total View Count
    private Map<String, Integer> pageViews = new ConcurrentHashMap<>();

    // 2. URL -> Set of unique User IDs
    private Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();

    // 3. Traffic Source -> Count
    private Map<String, Integer> sourceTracker = new ConcurrentHashMap<>();

    /**
     * Processes an incoming event in O(1) average time.
     */
    public void processEvent(String url, String userId, String source) {
        // Increment total views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Track unique visitors (Set handles duplicates automatically)
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        // Track traffic source
        sourceTracker.put(source, sourceTracker.getOrDefault(source, 0) + 1);
    }

    /**
     * Generates the Top 10 dashboard.
     * Uses a Min-Heap to find top N in O(N log K) time.
     */
    public void getDashboard() {
        System.out.println("\n--- 📊 REAL-TIME DASHBOARD (Live) ---");

        // Use a PriorityQueue to find Top 10 efficiently
        PriorityQueue<Map.Entry<String, Integer>> topPages = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            topPages.offer(entry);
            if (topPages.size() > 10) {
                topPages.poll(); // Remove the smallest to keep only the largest 10
            }
        }

        // Display Top Pages
        List<Map.Entry<String, Integer>> sortedTop = new ArrayList<>(topPages);
        sortedTop.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("Top Pages:");
        for (int i = 0; i < sortedTop.size(); i++) {
            String url = sortedTop.get(i).getKey();
            int views = sortedTop.get(i).getValue();
            int uniques = uniqueVisitors.get(url).size();
            System.out.printf("%d. %s - %d views (%d unique)\n", (i+1), url, views, uniques);
        }

        // Display Traffic Sources
        System.out.println("\nTraffic Sources:");
        int totalViews = pageViews.values().stream().mapToInt(Integer::intValue).sum();
        sourceTracker.forEach((source, count) -> {
            double percent = (double) count / totalViews * 100;
            System.out.printf("%s: %.1f%%\n", source, percent);
        });
    }

    public static void main(String[] args) throws InterruptedException {
        AnalyticsDashboard dashboard = new AnalyticsDashboard();
        Random rand = new Random();
        String[] sources = {"Google", "Facebook", "Direct", "Twitter"};
        String[] urls = {"/news/breaking", "/sports/final", "/tech/review", "/weather/local", "/finance/stocks"};

        System.out.println("Starting high-traffic stream simulation...");

        // Simulate 1 million page views (briefly)
        for (int i = 0; i < 50; i++) {
            dashboard.processEvent(
                    urls[rand.nextInt(urls.length)],
                    "user_" + rand.nextInt(1000),
                    sources[rand.nextInt(sources.length)]
            );

            // Every 10 events, show the dashboard
            if (i % 10 == 0) {
                dashboard.getDashboard();
                Thread.sleep(1000); // Wait for visibility
            }
        }
    }
}
