package week1;

import java.util.*;

public class PlagiarismDetector {
    // Map: n-gram (hash) -> Set of Document IDs containing it
    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    // Map: Document ID -> Total n-gram count (to calculate percentage)
    private Map<String, Integer> docSizeMap = new HashMap<>();

    private final int N = 5; // Using 5-grams (sequences of 5 words)

    /**
     * Adds a document to the database after breaking it into n-grams.
     */
    public void indexDocument(String docId, String content) {
        List<String> ngrams = generateNgrams(content);
        docSizeMap.put(docId, ngrams.size());

        for (String ngram : ngrams) {
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    /**
     * Analyzes a new document against the indexed database.
     */
    public void analyzeDocument(String newDocContent) {
        List<String> queryNgrams = generateNgrams(newDocContent);
        int totalNgrams = queryNgrams.size();

        // Map: Potential Source DocID -> Count of matching n-grams
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String ngram : queryNgrams) {
            if (ngramIndex.containsKey(ngram)) {
                for (String sourceDocId : ngramIndex.get(ngram)) {
                    matchCounts.put(sourceDocId, matchCounts.getOrDefault(sourceDocId, 0) + 1);
                }
            }
        }

        System.out.println("--- Plagiarism Report ---");
        System.out.println("Extracted " + totalNgrams + " n-grams from submission.");

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String sourceId = entry.getKey();
            int matches = entry.getValue();
            double similarity = (double) matches / totalNgrams * 100;

            System.out.printf("-> Found %d matches with %s\n", matches, sourceId);
            System.out.printf("   Similarity: %.1f%% %s\n",
                    similarity, getVerdict(similarity));
        }
    }

    private List<String> generateNgrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(j < N - 1 ? " " : "");
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    private String getVerdict(double score) {
        if (score > 50) return "[!!! PLAGIARISM DETECTED !!!]";
        if (score > 15) return "[SUSPICIOUS]";
        return "[CLEAN]";
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();
        Scanner scanner = new Scanner(System.in);

        // Pre-fill database with an example
        detector.indexDocument("essay_092.txt", "the quick brown fox jumps over the lazy dog and runs away");
        detector.indexDocument("essay_089.txt", "it was a bright cold day in april and the clocks were striking thirteen");

        System.out.println("Plagiarism Detector Online (N=5)");

        while (true) {
            System.out.println("\nEnter document text to analyze (or 'exit'):");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) break;

            detector.analyzeDocument(input);
        }
        scanner.close();
    }
}
