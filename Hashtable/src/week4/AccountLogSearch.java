package week4;

import java.util.*;

public class AccountLogSearch {

    // ---------------- LINEAR SEARCH ----------------
    static void linearSearch(String[] arr, String target) {
        int first = -1, last = -1;
        int comparisons = 0;

        for (int i = 0; i < arr.length; i++) {
            comparisons++;

            if (arr[i].equals(target)) {
                if (first == -1) first = i;
                last = i;
            }
        }

        System.out.println("\nLinear Search:");
        if (first == -1) {
            System.out.println("Not found");
        } else {
            System.out.println("First occurrence: " + first);
            System.out.println("Last occurrence: " + last);
        }
        System.out.println("Comparisons: " + comparisons);
        System.out.println("Time Complexity: O(n)");
    }

    // ---------------- BINARY SEARCH (any occurrence) ----------------
    static int binarySearch(String[] arr, String target, int low, int high, Counter counter) {
        while (low <= high) {
            counter.comparisons++;

            int mid = (low + high) / 2;

            int cmp = arr[mid].compareTo(target);

            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    // Find first and last occurrence using binary expansion
    static int findFirst(String[] arr, String target, int index, Counter counter) {
        int res = index;

        int low = 0, high = index - 1;

        while (low <= high) {
            counter.comparisons++;
            int mid = (low + high) / 2;

            if (arr[mid].equals(target)) {
                res = mid;
                high = mid - 1;
            } else if (arr[mid].compareTo(target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return res;
    }

    static int findLast(String[] arr, String target, int index, Counter counter) {
        int res = index;

        int low = index + 1, high = arr.length - 1;

        while (low <= high) {
            counter.comparisons++;
            int mid = (low + high) / 2;

            if (arr[mid].equals(target)) {
                res = mid;
                low = mid + 1;
            } else if (arr[mid].compareTo(target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return res;
    }

    // ---------------- HELPER CLASS ----------------
    static class Counter {
        int comparisons = 0;
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of logs: ");
        int n = sc.nextInt();
        sc.nextLine();

        String[] logs = new String[n];

        System.out.println("Enter account IDs:");
        for (int i = 0; i < n; i++) {
            logs[i] = sc.nextLine();
        }

        System.out.print("Enter target account ID: ");
        String target = sc.nextLine();

        // ---------------- LINEAR SEARCH ----------------
        linearSearch(logs, target);

        // ---------------- SORT FOR BINARY SEARCH ----------------
        Arrays.sort(logs);

        System.out.println("\nSorted Logs: " + Arrays.toString(logs));

        Counter counter = new Counter();

        // binary search base
        int index = binarySearch(logs, target, 0, n - 1, counter);

        System.out.println("\nBinary Search:");
        if (index == -1) {
            System.out.println("Not found");
        } else {
            int first = findFirst(logs, target, index, counter);
            int last = findLast(logs, target, index, counter);

            System.out.println("First occurrence: " + first);
            System.out.println("Last occurrence: " + last);
            System.out.println("Count occurrences: " + (last - first + 1));
        }

        System.out.println("Comparisons: " + counter.comparisons);
        System.out.println("Time Complexity: O(log n)");
    }
}