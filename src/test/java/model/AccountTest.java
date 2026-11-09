package model;

import exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void depositIncreasesBalance() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        acc.deposit(500);
        assertEquals(1500, acc.getBalance());
    }

    @Test
    void depositNegativeAmountThrowsException() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        assertThrows(IllegalArgumentException.class, () -> acc.deposit(-100));
    }

    @Test
    void depositZeroThrowsException() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        assertThrows(IllegalArgumentException.class, () -> acc.deposit(0));
    }

    @Test
    void savingsWithdrawReducesBalance() throws InsufficientBalanceException {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 5000);
        acc.withdraw(1000);
        assertEquals(4000, acc.getBalance());
    }

    @Test
    void savingsCannotGoBelowMinimumBalance() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        assertThrows(InsufficientBalanceException.class, () -> acc.withdraw(600));
        assertEquals(1000, acc.getBalance());
    }

    @Test
    void currentAllowsOverdraftWithinLimit() throws InsufficientBalanceException {
        Account acc = new CurrentAccount("ACC2", "Test", "1234", 1000);
        acc.withdraw(5500);
        assertEquals(-4500, acc.getBalance());
    }

    @Test
    void currentCannotExceedOverdraftLimit() {
        Account acc = new CurrentAccount("ACC2", "Test", "1234", 1000);
        assertThrows(InsufficientBalanceException.class, () -> acc.withdraw(6100));
        assertEquals(1000, acc.getBalance());
    }

    @Test
    void correctPinIsAccepted() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        assertTrue(acc.checkPin("1234"));
    }

    @Test
    void wrongPinIsRejected() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        assertFalse(acc.checkPin("9999"));
    }

    @Test
    void transactionIsRecordedInHistory() {
        Account acc = new SavingsAccount("ACC1", "Test", "1234", 1000);
        acc.deposit(200);
        assertEquals(1, acc.getHistory().size());
    }
}
