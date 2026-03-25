package week3;

import java.util.*;

class Transaction {
    String id;
    double fee;
    String timestamp;

    Transaction(String id, double fee, String timestamp) {
        this.id = id;
        this.fee = fee;
        this.timestamp = timestamp;
    }
}

public class TransactionSorting {

    // Convert HH:MM to minutes for comparison
    static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // Bubble Sort (by fee)
    static void bubbleSort(ArrayList<Transaction> list) {
        int n = list.size();
        int swaps = 0, passes = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            passes++;

            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {
                    Transaction temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                    swaps++;
                }
            }

            if (!swapped) break;
        }

        System.out.println("\nBubbleSort (by fee):");
        for (Transaction t : list) {
            System.out.println(t.id + ":" + t.fee);
        }
        System.out.println("Passes: " + passes + ", Swaps: " + swaps);
    }

    // Insertion Sort (by fee + timestamp, stable)
    static void insertionSort(ArrayList<Transaction> list) {
        for (int i = 1; i < list.size(); i++) {
            Transaction key = list.get(i);
            int j = i - 1;

            while (j >= 0 &&
                    (list.get(j).fee > key.fee ||
                            (list.get(j).fee == key.fee &&
                                    timeToMinutes(list.get(j).timestamp) > timeToMinutes(key.timestamp)))) {

                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }

        System.out.println("\nInsertionSort (fee + timestamp):");
        for (Transaction t : list) {
            System.out.println(t.id + ":" + t.fee + "@" + t.timestamp);
        }
    }

    // Detect outliers
    static void findOutliers(ArrayList<Transaction> list) {
        System.out.println("\nHigh-fee outliers (>50):");
        boolean found = false;

        for (Transaction t : list) {
            if (t.fee > 50) {
                System.out.println(t.id + " : " + t.fee);
                found = true;
            }
        }

        if (!found) {
            System.out.println("none");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of transactions: ");
        int n = sc.nextInt();
        sc.nextLine();

        ArrayList<Transaction> list = new ArrayList<>();

        System.out.println("Enter transactions (id fee timestamp HH:MM):");
        for (int i = 0; i < n; i++) {
            String id = sc.next();
            double fee = sc.nextDouble();
            String time = sc.next();
            list.add(new Transaction(id, fee, time));
        }

        if (n <= 100) {
            bubbleSort(new ArrayList<>(list));
        } else if (n <= 1000) {
            insertionSort(new ArrayList<>(list));
        } else {
            System.out.println("Dataset too large for these algorithms.");
        }

        findOutliers(list);
    }
}