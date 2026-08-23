package org.example;


import org.example.exceptions.AccountNotFoundException;
import org.example.exceptions.InsufficientFundsException;
import org.example.exceptions.NegativeNumberException;
import org.example.management.Bank;
import org.example.models.Account;
import org.example.models.AccountType;
import org.example.models.CheckingAccount;
import org.example.models.Customer;
import org.example.models.SavingsAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static void main() {
        HashMap<String, Account> accounts = new HashMap<>();
        Map<String, Customer> customers = new HashMap<>();

        Customer customer1 = new Customer("Kareem Emad","kareememad8561@gmail.com");
        customers.put(customer1.getId(), customer1);
        Account savingAccount1 = new SavingsAccount(1000,customer1);
        accounts.put(savingAccount1.getAccountNumber(),savingAccount1);

        Customer customer2 = new Customer("Ibraham Soltan","ibrhim123@gmail.com");
        customers.put(customer2.getId(), customer2);
        Account checkingAccount1 = new CheckingAccount(50000,customer2);
        accounts.put(checkingAccount1.getAccountNumber(),checkingAccount1);

        Bank bank = new Bank(accounts);
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> openAccount(bank, customers, scanner);
                case "2" -> deposit(bank, scanner);
                case "3" -> withdraw(bank, scanner);
                case "4" -> checkBalance(bank, scanner);
                case "5" -> viewTransactionHistory(bank);
                case "6" -> displayAccounts(accounts);
                case "0" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }

        scanner.close();
    }

    private static void displayAccounts(HashMap<String, Account> accounts) {
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            System.out.println(entry.getValue().getAccountNumber());
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Bank Account System =====");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. View Transaction History");
        System.out.println("6. List Accounts");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void openAccount(Bank bank, Map<String, Customer> customers, Scanner scanner) {
        System.out.print("Customer name: ");
        String name = scanner.nextLine();
        System.out.print("Customer email: ");
        String email = scanner.nextLine();

        System.out.print("Account type (1=Saving, 2=Checking): ");
        String typeChoice = scanner.nextLine().trim();
        AccountType type = typeChoice.equals("2") ? AccountType.CHECKING : AccountType.SAVING;

        System.out.print("Initial balance: ");
        double initialBalance = readDouble(scanner);

        try {
            Customer customer = new Customer(name, email);
            customers.put(customer.getId(), customer);
            Account account = bank.openAccount(customer, type, initialBalance);
            System.out.println("Account opened. Account number: " + account.getAccountNumber());
        } catch (NegativeNumberException | NullPointerException e) {
            System.out.println("Failed to open account: " + e.getMessage());
        }
    }

    private static void deposit(Bank bank, Scanner scanner) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.print("Amount: ");
        double amount = readDouble(scanner);

        try {
            bank.deposit(accountNumber, amount);
            System.out.println("Deposit successful.");
        } catch (AccountNotFoundException | NegativeNumberException e) {
            System.out.println("Failed to deposit: " + e.getMessage());
        }
    }

    private static void withdraw(Bank bank, Scanner scanner) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.print("Amount: ");
        double amount = readDouble(scanner);

        try {
            bank.withdraw(accountNumber, amount);
            System.out.println("Withdrawal successful.");
        } catch (AccountNotFoundException | InsufficientFundsException e) {
            System.out.println("Failed to withdraw: " + e.getMessage());
        }
    }

    private static void checkBalance(Bank bank, Scanner scanner) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            Account account = bank.getAccount(accountNumber);
            System.out.println("Balance: " + account.getBalance());
        } catch (AccountNotFoundException e) {
            System.out.println("Failed to check balance: " + e.getMessage());
        }
    }

    private static void viewTransactionHistory(Bank bank) {
        if (bank.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        bank.getTransactionHistory().forEach(System.out::println);
    }



    private static double readDouble(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number, please try again: ");
            }
        }
    }
}
