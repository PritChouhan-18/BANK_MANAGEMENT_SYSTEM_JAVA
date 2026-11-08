package model;

import exception.InsufficientBalanceException;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    private final String accNo;
    private final String holderName;
    private final String pin;
    protected double balance;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String accNo, String holderName, String pin, double openingBalance) {
        this.accNo = accNo;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = openingBalance;
    }

    public String getAccNo() { return accNo; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public List<Transaction> getHistory() { return history; }
    public boolean checkPin(String input) { return pin.equals(input); }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        balance += amount;
        addTransaction("DEPOSIT", amount);
    }

    public abstract void withdraw(double amount) throws InsufficientBalanceException;

    public abstract String getType();

    protected void addTransaction(String type, double amount) {
        history.add(new Transaction(type, amount, balance));
    }
}