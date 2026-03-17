package week2;

import java.util.*;

public class AutocompleteSystem {

    // Trie Node Structure
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        // Cache the top 10 suggestions at this prefix level for O(1) suggestion retrieval
        List<String> topSuggestions = new ArrayList<>();
        boolean isEndOfQuery = false;
    }

    private TrieNode root = new TrieNode();
    private Map<String, Integer> queryFrequency = new HashMap<>();

    /**
     * Updates the frequency of a query and re-optimizes the Trie path.
     * Time Complexity: O(L) where L is query length.
     */
    public void updateFrequency(String query) {
        query = query.toLowerCase();
        queryFrequency.put(query, queryFrequency.getOrDefault(query, 1) + 1);

        // Insert into Trie and update caches along the path
        TrieNode current = root;
        for (char c : query.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
            updateNodeCache(current, query);
        }
        current.isEndOfQuery = true;
    }

    // Maintains the Top 10 list at a specific node
    private void updateNodeCache(TrieNode node, String query) {
        if (!node.topSuggestions.contains(query)) {
            node.topSuggestions.add(query);
        }

        // Sort based on frequency and keep only top 10
        node.topSuggestions.sort((a, b) ->
                queryFrequency.get(b).compareTo(queryFrequency.get(a)));

        if (node.topSuggestions.size() > 10) {
            node.topSuggestions.remove(10);
        }
    }

    /**
     * Returns suggestions in O(L) time where L is prefix length.
     * The <50ms requirement is met because we don't search; we just return the cache.
     */
    public List<String> getSuggestions(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(c)) return Collections.emptyList();
            current = current.children.get(c);
        }
        return current.topSuggestions;
    }

    public static void main(String[] args) {
        AutocompleteSystem engine = new AutocompleteSystem();

        // Simulating trending data
        engine.updateFrequency("java tutorial");
        engine.updateFrequency("java tutorial");
        engine.updateFrequency("javascript");
        engine.updateFrequency("java download");

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Search Engine Autocomplete ---");

        while (true) {
            System.out.print("\nType search query: ");
            String input = scanner.nextLine();
            if (input.equals("exit")) break;

            long start = System.nanoTime();
            List<String> results = engine.getSuggestions(input);
            long end = System.nanoTime();

            System.out.println("Suggestions: " + results);
            System.out.printf("Response time: %.3f ms\n", (end - start) / 1_000_000.0);
        }
        scanner.close();
    }
}
