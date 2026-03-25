package week4;

import java.util.*;

class Asset {
    String name;
    double returnRate;
    double volatility;

    Asset(String name, double returnRate, double volatility) {
        this.name = name;
        this.returnRate = returnRate;
        this.volatility = volatility;
    }
}

public class Portfoliosorting {

    // ---------------- MERGE SORT (Stable - returnRate ASC) ----------------
    static void mergeSort(Asset[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            merge(arr, left, mid, right);
        }
    }

    static void merge(Asset[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Asset[] L = new Asset[n1];
        Asset[] R = new Asset[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];

        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i].returnRate <= R[j].returnRate) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // ---------------- QUICK SORT (DESC returnRate + ASC volatility) ----------------
    static void quickSort(Asset[] arr, int low, int high) {
        if (low < high) {

            // Hybrid optimization: switch to insertion sort for small partitions
            if (high - low < 10) {
                insertionSort(arr, low, high);
                return;
            }

            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Median-of-three pivot selection
    static int medianOfThree(Asset[] arr, int low, int high) {
        int mid = (low + high) / 2;

        double a = arr[low].returnRate;
        double b = arr[mid].returnRate;
        double c = arr[high].returnRate;

        if ((a > b && a < c) || (a < b && a > c)) return low;
        if ((b > a && b < c) || (b < a && b > c)) return mid;
        return high;
    }

    static int partition(Asset[] arr, int low, int high) {

        // choose pivot (median-of-three)
        int pivotIndex = medianOfThree(arr, low, high);

        swap(arr, pivotIndex, high);

        Asset pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {

            // DESC returnRate, if equal then ASC volatility
            if (arr[j].returnRate > pivot.returnRate ||
                    (arr[j].returnRate == pivot.returnRate &&
                            arr[j].volatility < pivot.volatility)) {

                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    // ---------------- INSERTION SORT (for small partitions) ----------------
    static void insertionSort(Asset[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            Asset key = arr[i];
            int j = i - 1;

            while (j >= low &&
                    (arr[j].returnRate < key.returnRate ||
                            (arr[j].returnRate == key.returnRate &&
                                    arr[j].volatility > key.volatility))) {

                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }
    }

    static void swap(Asset[] arr, int i, int j) {
        Asset temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ---------------- PRINT ----------------
    static void print(String title, Asset[] arr) {
        System.out.println("\n" + title);
        for (Asset a : arr) {
            System.out.println(a.name + ": " + a.returnRate + "% | vol=" + a.volatility);
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of assets: ");
        int n = sc.nextInt();

        Asset[] a1 = new Asset[n];
        Asset[] a2 = new Asset[n];

        System.out.println("Enter assets (name returnRate volatility):");

        for (int i = 0; i < n; i++) {
            String name = sc.next();
            double ret = sc.nextDouble();
            double vol = sc.nextDouble();

            a1[i] = new Asset(name, ret, vol);
            a2[i] = new Asset(name, ret, vol);
        }

        // Merge Sort (ASC returnRate)
        mergeSort(a1, 0, n - 1);
        print("Merge Sort (returnRate ASC, stable):", a1);

        // Quick Sort (DESC returnRate + ASC volatility)
        quickSort(a2, 0, n - 1);
        print("Quick Sort (returnRate DESC + volatility ASC):", a2);
    }
}
