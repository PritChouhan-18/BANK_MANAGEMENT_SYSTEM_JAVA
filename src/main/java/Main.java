import exception.InsufficientBalanceException;
import model.Account;
import service.BankService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService bank = new BankService();

        while (true) {
            System.out.println("\n1. Create Account  2. Login  3. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            try {
                if (choice == 1) {
                    System.out.print("Type (savings/current): ");
                    String type = sc.nextLine();
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("4-digit PIN: ");
                    String pin = sc.nextLine();
                    System.out.print("Opening balance: ");
                    double bal = sc.nextDouble();
                    sc.nextLine();
                    Account a = bank.createAccount(type, name, pin, bal);
                    System.out.println("Account created! Your number: " + a.getAccNo());

                } else if (choice == 2) {
                    System.out.print("Account no: ");
                    String no = sc.nextLine();
                    System.out.print("PIN: ");
                    String pin = sc.nextLine();
                    Account acc = bank.login(no, pin);
                    if (acc == null) {
                        System.out.println("Invalid login.");
                        continue;
                    }
                    customerMenu(sc, bank, acc);

                } else {
                    System.out.println("Thank you!");
                    break;
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static void customerMenu(Scanner sc, BankService bank, Account acc) {
        String accNo = acc.getAccNo();
        System.out.println("Welcome, " + acc.getHolderName() + " (" + acc.getType() + ")");

        while (true) {
            System.out.println("\n1.Deposit 2.Withdraw 3.Balance 4.Transfer 5.History 6.Logout");
            int c = sc.nextInt();
            sc.nextLine();
            try {
                switch (c) {
                    case 1 -> {
                        System.out.print("Amount: ");
                        bank.deposit(accNo, sc.nextDouble());
                        sc.nextLine();
                        System.out.println("Deposit successful.");
                    }
                    case 2 -> {
                        System.out.print("Amount: ");
                        bank.withdraw(accNo, sc.nextDouble());
                        sc.nextLine();
                        System.out.println("Withdrawal successful.");
                    }
                    case 3 -> System.out.println("Balance: Rs." + bank.getBalance(accNo));
                    case 4 -> {
                        System.out.print("To account: ");
                        String to = sc.nextLine();
                        System.out.print("Amount: ");
                        double amt = sc.nextDouble();
                        sc.nextLine();
                        bank.transfer(accNo, to, amt);
                        System.out.println("Transfer successful.");
                    }
                    case 5 -> bank.getHistory(accNo).forEach(System.out::println);
                    case 6 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (InsufficientBalanceException | RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}