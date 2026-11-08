package model;

import exception.InsufficientBalanceException;

public class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 500;

    public SavingsAccount(String accNo, String name, String pin, double opening) {
        super(accNo, name, pin, opening);
    }

    @Override
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (balance - amount < MIN_BALANCE)
            throw new InsufficientBalanceException("Savings account must keep minimum Rs." + MIN_BALANCE);
        balance -= amount;
        addTransaction("WITHDRAW", amount);
    }

    @Override
    public String getType() { return "Savings"; }
}