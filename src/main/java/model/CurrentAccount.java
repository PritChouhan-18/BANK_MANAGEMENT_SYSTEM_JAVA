package model;

import exception.InsufficientBalanceException;

public class CurrentAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 5000;

    public CurrentAccount(String accNo, String name, String pin, double opening) {
        super(accNo, name, pin, opening);
    }

    @Override
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (balance - amount < -OVERDRAFT_LIMIT)
            throw new InsufficientBalanceException("Overdraft limit of Rs." + OVERDRAFT_LIMIT + " exceeded.");
        balance -= amount;
        addTransaction("WITHDRAW", amount);
    }

    @Override
    public String getType() { return "Current"; }
}