package org.example.models;

import org.example.exceptions.InsufficientFundsException;
import org.example.exceptions.NegativeNumberException;

import java.util.UUID;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private Customer customer;


    // Constructor
    public Account(double balance, Customer customer) {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = balance;
        this.customer = customer;
    }

    // Custom Methods
    public void deposit(double amount) throws NegativeNumberException {
        if (amount > 0) balance += amount;
        else  throw new NegativeNumberException("Invalid amount");

    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (balance >= amount) {
            balance -= amount;
        } else throw new InsufficientFundsException("Insufficient Funds");
    }

    public abstract AccountType getAccountType();


    //Setters and Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) throws NegativeNumberException {
        if (balance>0){
            this.balance = balance;
        } else  throw new NegativeNumberException("Number cannot be negative");
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) throws NullPointerException{
        if (customer != null) this.customer = customer;
        else throw new NullPointerException("Customer cannot be null");
    }


    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", customer=" + customer +
                '}';
    }
}
