package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime time = LocalDateTime.now();

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        String t = time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        return t + " | " + type + " | Rs." + amount + " | Balance: Rs." + balanceAfter;
    }
}