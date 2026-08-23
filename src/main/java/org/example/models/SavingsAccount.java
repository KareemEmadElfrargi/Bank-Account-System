package org.example.models;

public class SavingsAccount extends Account {
    private final double intersetRate = 2.5;

    public SavingsAccount(double balance, Customer owner) {
        super(balance, owner);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVING;
    }


}
