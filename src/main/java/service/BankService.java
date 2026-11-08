package service;

import dao.AccountDAO;
import dao.DBConnection;
import exception.InsufficientBalanceException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BankService {
    private final AccountDAO dao = new AccountDAO();

    public Account createAccount(String type, String name, String pin, double opening) {
        if (opening < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try {
                String accNo = dao.nextAccNo(conn);
                Account acc = type.equalsIgnoreCase("savings")
                        ? new SavingsAccount(accNo, name, pin, opening)
                        : new CurrentAccount(accNo, name, pin, opening);
                dao.save(conn, acc, pin);
                dao.saveTransaction(conn, accNo, "OPENING", opening, opening);
                conn.commit();
                return acc;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public Account login(String accNo, String pin) {
        try (Connection conn = DBConnection.get()) {
            Account acc = dao.findByAccNo(conn, accNo);
            if (acc == null || !acc.checkPin(pin)) return null;
            return acc;
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public void deposit(String accNo, double amount) {
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try {
                Account acc = dao.findByAccNo(conn, accNo);
                acc.deposit(amount);
                dao.updateBalance(conn, accNo, acc.getBalance());
                dao.saveTransaction(conn, accNo, "DEPOSIT", amount, acc.getBalance());
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public void withdraw(String accNo, double amount) throws InsufficientBalanceException {
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try {
                Account acc = dao.findByAccNo(conn, accNo);
                acc.withdraw(amount);
                dao.updateBalance(conn, accNo, acc.getBalance());
                dao.saveTransaction(conn, accNo, "WITHDRAW", amount, acc.getBalance());
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public void transfer(String fromNo, String toNo, double amount)
            throws InsufficientBalanceException {
        if (fromNo.equals(toNo))
            throw new IllegalArgumentException("Cannot transfer to same account.");
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try {
                Account from = dao.findByAccNo(conn, fromNo);
                Account to = dao.findByAccNo(conn, toNo);
                if (to == null) throw new IllegalArgumentException("Receiver account not found.");

                from.withdraw(amount);
                to.deposit(amount);

                dao.updateBalance(conn, fromNo, from.getBalance());
                dao.saveTransaction(conn, fromNo, "TRANSFER_OUT", amount, from.getBalance());
                dao.updateBalance(conn, toNo, to.getBalance());
                dao.saveTransaction(conn, toNo, "TRANSFER_IN", amount, to.getBalance());

                conn.commit();   // both updates succeed together
            } catch (Exception e) {
                conn.rollback(); // or neither happens
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public double getBalance(String accNo) {
        try (Connection conn = DBConnection.get()) {
            return dao.findByAccNo(conn, accNo).getBalance();
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public List<String> getHistory(String accNo) {
        try (Connection conn = DBConnection.get()) {
            return dao.getHistory(conn, accNo);
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }
}