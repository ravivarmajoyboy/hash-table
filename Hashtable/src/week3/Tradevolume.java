package week3;

import java.util.*;

class Trade {
    String id;
    int volume;

    Trade(String id, int volume) {
        this.id = id;
        this.volume = volume;
    }
}

public class Tradevolume {

    // Merge Sort (ASC - stable)
    static void mergeSort(Trade[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            merge(arr, left, mid, right);
        }
    }

    static void merge(Trade[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Trade[] L = new Trade[n1];
        Trade[] R = new Trade[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];

        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i].volume <= R[j].volume) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // Quick Sort (DESC)
    static void quickSort(Trade[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    static int partition(Trade[] arr, int low, int high) {
        Trade pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j].volume >= pivot.volume) {
                i++;
                Trade temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        Trade temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    // Merge two sorted arrays
    static Trade[] mergeLists(Trade[] a, Trade[] b) {
        Trade[] result = new Trade[a.length + b.length];

        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length) {
            if (a[i].volume <= b[j].volume) {
                result[k++] = a[i++];
            } else {
                result[k++] = b[j++];
            }
        }

        while (i < a.length) result[k++] = a[i++];
        while (j < b.length) result[k++] = b[j++];

        return result;
    }

    // Compute total volume
    static int totalVolume(Trade[] arr) {
        int sum = 0;
        for (Trade t : arr) sum += t.volume;
        return sum;
    }

    static void printTrades(String title, Trade[] arr) {
        System.out.println("\n" + title);
        for (Trade t : arr) {
            System.out.println(t.id + ":" + t.volume);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of trades: ");
        int n = sc.nextInt();

        Trade[] trades1 = new Trade[n];
        Trade[] trades2 = new Trade[n];

        System.out.println("Enter trade data (id volume):");

        for (int i = 0; i < n; i++) {
            String id = sc.next();
            int vol = sc.nextInt();

            trades1[i] = new Trade(id, vol);
            trades2[i] = new Trade(id, vol);
        }

        // Merge Sort (ASC)
        mergeSort(trades1, 0, n - 1);
        printTrades("MergeSort (Ascending):", trades1);

        // Quick Sort (DESC)
        quickSort(trades2, 0, n - 1);
        printTrades("QuickSort (Descending):", trades2);

        // Merge two halves (simulate morning + afternoon split)
        int mid = n / 2;
        Trade[] morning = Arrays.copyOfRange(trades1, 0, mid);
        Trade[] afternoon = Arrays.copyOfRange(trades1, mid, n);

        Trade[] merged = mergeLists(morning, afternoon);

        System.out.println("\nMerged morning+afternoon total volume: " + totalVolume(merged));
    }
}