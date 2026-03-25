package week3;

import java.util.*;

class Client {
    String name;
    int riskScore;
    double accountBalance;

    Client(String name, int riskScore, double accountBalance) {
        this.name = name;
        this.riskScore = riskScore;
        this.accountBalance = accountBalance;
    }
}

public class Clientscore {

    // Bubble Sort (ascending by riskScore)
    static void bubbleSort(Client[] arr) {
        int n = arr.length;
        int swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].riskScore > arr[j + 1].riskScore) {

                    Client temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                    swaps++;
                    swapped = true;

                    // visualize swap
                    System.out.println("Swapped: " + arr[j].name + " <-> " + arr[j + 1].name);
                }
            }

            if (!swapped) break;
        }

        System.out.println("\nBubble Sort (Ascending riskScore):");
        for (Client c : arr) {
            System.out.println(c.name + ":" + c.riskScore);
        }

        System.out.println("Total Swaps: " + swaps);
    }

    // Insertion Sort (descending riskScore, then accountBalance)
    static void insertionSort(Client[] arr) {
        for (int i = 1; i < arr.length; i++) {
            Client key = arr[i];
            int j = i - 1;

            while (j >= 0 &&
                    (arr[j].riskScore < key.riskScore ||
                            (arr[j].riskScore == key.riskScore &&
                                    arr[j].accountBalance < key.accountBalance))) {

                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }

        System.out.println("\nInsertion Sort (Desc riskScore + balance):");
        for (Client c : arr) {
            System.out.println(c.name + ":" + c.riskScore + " | " + c.accountBalance);
        }
    }

    // Top N risky clients
    static void topRiskClients(Client[] arr, int topN) {
        System.out.println("\nTop " + topN + " Risk Clients:");
        for (int i = 0; i < Math.min(topN, arr.length); i++) {
            System.out.println(arr[i].name + " (" + arr[i].riskScore + ")");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of clients: ");
        int n = sc.nextInt();
        sc.nextLine();

        Client[] arr1 = new Client[n];
        Client[] arr2 = new Client[n];

        System.out.println("Enter client data (name riskScore balance):");

        for (int i = 0; i < n; i++) {
            String name = sc.next();
            int risk = sc.nextInt();
            double bal = sc.nextDouble();

            arr1[i] = new Client(name, risk, bal);
            arr2[i] = new Client(name, risk, bal);
        }

        // Bubble Sort
        bubbleSort(arr1);

        // Insertion Sort
        insertionSort(arr2);

        // Top 10 risky clients from insertion sorted array
        topRiskClients(arr2, 10);
    }
}