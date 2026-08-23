package org.example.models;

public class CheckingAccount extends Account{
    private final double overdraftAllowance = 10_000;
    public CheckingAccount(double balance, Customer customer) {
        super(balance, customer);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CHECKING;
    }
}
