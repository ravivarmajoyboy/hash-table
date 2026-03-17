package week2;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiterSystem {

    // Token Bucket logic: Calculates tokens on-the-fly based on elapsed time
    class TokenBucket {
        private final long maxTokens;
        private final long refillRatePerMs;
        private final AtomicLong currentTokens;
        private long lastRefillTimestamp;

        public TokenBucket(long limitPerHour) {
            this.maxTokens = limitPerHour;
            // Calculate how many tokens are added every millisecond
            this.refillRatePerMs = limitPerHour / (60 * 60 * 1000L);
            this.currentTokens = new AtomicLong(limitPerHour);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (currentTokens.get() > 0) {
                currentTokens.decrementAndGet();
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timeElapsed = now - lastRefillTimestamp;
            long tokensToAdd = timeElapsed * refillRatePerMs;

            if (tokensToAdd > 0) {
                long newVal = Math.min(maxTokens, currentTokens.get() + tokensToAdd);
                currentTokens.set(newVal);
                lastRefillTimestamp = now;
            }
        }

        public long getRemaining() {
            refill();
            return currentTokens.get();
        }
    }

    // clientId -> their specific TokenBucket
    private Map<String, TokenBucket> clientLimits = new ConcurrentHashMap<>();
    private final long LIMIT = 1000;

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientLimits.computeIfAbsent(clientId, k -> new TokenBucket(LIMIT));

        if (bucket.tryConsume()) {
            return "Allowed (" + bucket.getRemaining() + " requests remaining)";
        } else {
            long secondsToReset = calculateSecondsToReset();
            return "Denied (0 requests remaining, retry after " + secondsToReset + "s)";
        }
    }

    private long calculateSecondsToReset() {
        // Simple fixed-window logic for the reset message
        return 3600 - ((System.currentTimeMillis() / 1000) % 3600);
    }

    public static void main(String[] args) {
        RateLimiterSystem limiter = new RateLimiterSystem();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- API Gateway Rate Limiter ---");
        System.out.println("Client Limit: 1000 requests per hour");

        while (true) {
            System.out.print("\nEnter Client ID (or 'exit'): ");
            String id = scanner.nextLine().trim();

            if (id.equalsIgnoreCase("exit")) break;

            // Simulate a burst of requests
            System.out.println("Checking limit for " + id + "...");
            System.out.println(limiter.checkRateLimit(id));
        }
        scanner.close();
    }
}
