package org.example.management;

import org.example.exceptions.AccountNotFoundException;
import org.example.exceptions.InsufficientFundsException;
import org.example.exceptions.NegativeNumberException;
import org.example.models.Account;
import org.example.models.AccountType;
import org.example.models.CheckingAccount;
import org.example.models.Customer;
import org.example.models.SavingsAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;

public class Bank {
    private static final Logger logger = LoggerFactory.getLogger(Bank.class);
    private HashMap<String, Account> accounts;
    private LinkedList<String> transactionHistory;

    public Bank(HashMap<String, Account> accounts) {
        this.accounts = accounts;
        this.transactionHistory = new LinkedList<>();
    }



    //Custom Methods
    public Account getAccount(String id) {
        if (accounts.containsKey(id)) {
            return accounts.get(id);
        } else throw new AccountNotFoundException("Account with id " + id + " not found");
    }

    public Account openAccount(Customer customer, AccountType type, double initialBalance) throws NegativeNumberException {
        if (customer == null) throw new NullPointerException("Customer cannot be null");
        if (initialBalance < 0) throw new NegativeNumberException("Initial balance cannot be negative");

        Account account = switch (type) {
            case CHECKING -> new CheckingAccount(initialBalance, customer);
            case SAVING -> new SavingsAccount(initialBalance, customer);
        };

        accounts.put(account.getAccountNumber(), account);
        logger.info("Opened {} account {} for customer {}", type, account.getAccountNumber(), customer.getId());
        return account;
    }


    public void deposit(String accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        if (account == null) throw new NullPointerException("Account cannot be null");

        account.deposit(amount);
        transactionHistory.add(LocalDateTime.now() + " - Deposited " + amount + " into account " + account.getAccountNumber());

        logger.info("Deposited {} into account {}, new balance={}",
                amount,
                account.getAccountNumber(),
                account.getBalance());

    }

    public void withdraw(String accountNumber , double amount) throws InsufficientFundsException {
        Account account = getAccount(accountNumber);
        if (account == null) throw new NullPointerException("Account cannot be null");
        account.withdraw(amount);

        transactionHistory.add(LocalDateTime.now() + " - Withdraw " + amount + " from account " + account.getAccountNumber());
        logger.info("Withdraw {} from account {}, new balance={}",
                amount,
                account.getAccountNumber(),
                account.getBalance());
    }


    //Setters and Getters

    public LinkedList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(LinkedList<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(HashMap<String, Account> accounts) {
        this.accounts = accounts;
    }
}
