package week4;

import java.util.*;

public class Risklookup {

    // ---------------- LINEAR SEARCH ----------------
    static void linearSearch(int[] arr, int target) {
        int comparisons = 0;
        boolean found = false;

        for (int i = 0; i < arr.length; i++) {
            comparisons++;

            if (arr[i] == target) {
                found = true;
                System.out.println("Linear: Found at index " + i);
                break;
            }
        }

        System.out.println("\nLinear Search:");
        if (!found) {
            System.out.println("threshold=" + target + " → not found");
        }
        System.out.println("Comparisons: " + comparisons);
        System.out.println("Time Complexity: O(n)");
    }

    // ---------------- FLOOR (≤ target) ----------------
    static int floor(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        int res = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (arr[mid] == target) return arr[mid];

            if (arr[mid] < target) {
                res = arr[mid];
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    }

    // ---------------- CEILING (≥ target) ----------------
    static int ceiling(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        int res = -1;

        while (low <= high) {
            int mid = (low + high) / 2;

            if (arr[mid] == target) return arr[mid];

            if (arr[mid] > target) {
                res = arr[mid];
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return res;
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of risk bands: ");
        int n = sc.nextInt();

        int[] arr = new int[n];

        System.out.println("Enter risk values:");
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }

        System.out.print("Enter threshold: ");
        int target = sc.nextInt();

        // Linear search (unsorted scan simulation)
        linearSearch(arr, target);

        // Sort for binary operations
        Arrays.sort(arr);

        System.out.println("\nSorted Risks: " + Arrays.toString(arr));

        int floorVal = floor(arr, target);
        int ceilVal = ceiling(arr, target);

        System.out.println("\nBinary Search Results:");
        System.out.println("Floor (" + target + "): " + floorVal);
        System.out.println("Ceiling (" + target + "): " + ceilVal);
        System.out.println("Comparisons (theoretical): O(log n)");
    }
}