package dao;

import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public String nextAccNo(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(acc_no, 4) AS UNSIGNED)), 1000) + 1 FROM accounts";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return "ACC" + rs.getInt(1);
        }
    }

    public void save(Connection conn, Account acc, String pin) throws SQLException {
        String sql = "INSERT INTO accounts (acc_no, holder_name, pin, type, balance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getAccNo());
            ps.setString(2, acc.getHolderName());
            ps.setString(3, pin);
            ps.setString(4, acc.getType());
            ps.setDouble(5, acc.getBalance());
            ps.executeUpdate();
        }
    }

    public Account findByAccNo(Connection conn, String accNo) throws SQLException {
        String sql = "SELECT holder_name, pin, type, balance FROM accounts WHERE acc_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String name = rs.getString("holder_name");
                String pin = rs.getString("pin");
                String type = rs.getString("type");
                double balance = rs.getDouble("balance");
                return type.equalsIgnoreCase("Savings")
                        ? new SavingsAccount(accNo, name, pin, balance)
                        : new CurrentAccount(accNo, name, pin, balance);
            }
        }
    }

    public void updateBalance(Connection conn, String accNo, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE acc_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accNo);
            ps.executeUpdate();
        }
    }

    public void saveTransaction(Connection conn, String accNo, String type,
                                double amount, double balanceAfter) throws SQLException {
        String sql = "INSERT INTO transactions (acc_no, type, amount, balance_after) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfter);
            ps.executeUpdate();
        }
    }

    public List<String> getHistory(Connection conn, String accNo) throws SQLException {
        String sql = "SELECT created_at, type, amount, balance_after FROM transactions "
                + "WHERE acc_no = ? ORDER BY id";
        List<String> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getTimestamp("created_at") + " | " + rs.getString("type")
                            + " | Rs." + rs.getDouble("amount")
                            + " | Balance: Rs." + rs.getDouble("balance_after"));
                }
            }
        }
        return list;
    }
}