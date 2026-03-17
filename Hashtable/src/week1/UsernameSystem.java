package week1;
    import java.util.*;

    public class UsernameSystem {
        private Map<String, Integer> userRegistry = new HashMap<>();
        private Map<String, Integer> attemptTracker = new HashMap<>();

        public UsernameSystem() {
            // Pre-populating with some "existing" users
            userRegistry.put("admin", 1);
            userRegistry.put("john_doe", 2);
            userRegistry.put("coder123", 3);
        }

        public boolean checkAvailability(String username) {
            if (userRegistry.containsKey(username)) {
                attemptTracker.put(username, attemptTracker.getOrDefault(username, 0) + 1);
                return false;
            }
            return true;
        }

        public List<String> suggestAlternatives(String username) {
            List<String> suggestions = new ArrayList<>();
            int suffix = 1;
            while (suggestions.size() < 3) {
                String candidate = username + suffix;
                if (!userRegistry.containsKey(candidate)) {
                    suggestions.add(candidate);
                }
                suffix++;
            }
            return suggestions;
        }

        public String getMostAttempted() {
            return attemptTracker.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " (" + e.getValue() + " attempts)")
                    .orElse("None yet");
        }

        public static void main(String[] args) {
            UsernameSystem system = new UsernameSystem();
            Scanner scanner = new Scanner(System.in);

            System.out.println("--- Social Media Registration System ---");

            while (true) {
                System.out.print("\nEnter username to check (or type 'exit'): ");
                String input = scanner.nextLine().trim().toLowerCase();

                if (input.equals("exit")) break;

                if (system.checkAvailability(input)) {
                    System.out.println("✅ Success! '" + input + "' is available.");
                    System.out.print("Would you like to register it? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        system.userRegistry.put(input, new Random().nextInt(10000));
                        System.out.println("Registered successfully!");
                    }
                } else {
                    System.out.println("❌ Sorry, '" + input + "' is already taken.");
                    System.out.println("Suggestions: " + system.suggestAlternatives(input));
                    System.out.println("Current most attempted: " + system.getMostAttempted());
                }
            }
            scanner.close();
            System.out.println("System closed.");
        }
    }


