package demo.task1;

import demo.task1.models.Account;
import demo.task1.repositories.AccountOperationRepository;
import demo.task1.repositories.AccountRepository;
import demo.task1.repositories.impl.AccountOperationRepositoryImpl;
import demo.task1.repositories.impl.AccountRepositoryImpl;
import demo.task1.services.Bank;
import demo.task1.services.impl.BankImpl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final Scanner scanner = new Scanner(System.in);
    private static final AccountRepository accountRepository = new AccountRepositoryImpl();
    private static final AccountOperationRepository accountOperationRepository = new AccountOperationRepositoryImpl();
    private static final Bank bank = new BankImpl(accountRepository, accountOperationRepository);

    static {
        try {
            InputStream configFile = App.class.getClassLoader().getResourceAsStream("logging.properties");

            if (configFile == null) {
                throw new IOException("Could not find logging.properties in resources");
            }

            LogManager.getLogManager().readConfiguration(configFile);
            configFile.close();

            logger.info("Logging configuration loaded successfully");
        } catch (IOException e) {
            logger.severe("Could not load logging configuration:");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Long clientID = bank.createAccount("Krzysztof", "PB");
        Optional<Account> currentClient = accountRepository.findById(clientID);

        Account client = currentClient.orElse(null);
        if(client == null) {
            System.out.println("Client not found");
            return;
        }

        System.out.println("Welcome to the bank Mr: " + client.getName());

        while(true){
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    findAccount();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    checkBalance();
                    break;
                case 5:
                    transfer();
                    break;
                case 6:
                    createAccount();
                    break;
                case 0:
                    System.out.println("Thank you for using our bank. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n===== BANK MENU =====");
        System.out.println("1. Find Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. Transfer Money");
        System.out.println("6. Create New Account");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void findAccount(){
        System.out.println("Enter account name: ");
        String name = scanner.nextLine();
        System.out.println("Enter account address: ");
        String address = scanner.nextLine();

        Long id = bank.findAccount(name, address);
        if (id != null) {
            System.out.println("Account found with ID: " + id);
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void deposit(){
        System.out.println("Enter the id of the account to deposit: ");
        Long id = scanner.nextLong();
        System.out.println("Enter the amount to deposit: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            bank.deposit(id, amount);
            System.out.println("Deposit successful");
            System.out.println("New balance: " + bank.getBalance(id));
        } catch (Bank.AccountIdException e) {
            System.out.println("Error: Account not found");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void withdraw() {
        System.out.println("Enter the id of the account to withdraw from: ");
        Long id = scanner.nextLong();
        System.out.println("Enter the amount to withdraw: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            bank.withdraw(id, amount);
            System.out.println("Withdrawal successful");
            System.out.println("New balance: " + bank.getBalance(id));
        } catch (Bank.AccountIdException e) {
            System.out.println("Error: Account not found");
        } catch (Bank.InsufficientFundsException e) {
            System.out.println("Error: Insufficient funds");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkBalance() {
        System.out.println("Enter the id of the account to check balance: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try {
            BigDecimal balance = bank.getBalance(id);
            System.out.println("Current balance: " + balance);
        } catch (Bank.AccountIdException e) {
            System.out.println("Error: Account not found");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void transfer() {
        System.out.println("Enter the id of the source account: ");
        Long sourceId = scanner.nextLong();
        System.out.println("Enter the id of the destination account: ");
        Long destId = scanner.nextLong();
        System.out.println("Enter the amount to transfer: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.println("Enter the title of the transfer: ");
        String title = scanner.nextLine();

        try {
            bank.transfer(sourceId, destId, amount, title);
            System.out.println("Transfer successful");
            System.out.println("Source account balance: " + bank.getBalance(sourceId));
            System.out.println("Destination account balance: " + bank.getBalance(destId));
        } catch (Bank.AccountIdException e) {
            System.out.println("Error: One or both accounts not found");
        } catch (Bank.InsufficientFundsException e) {
            System.out.println("Error: Insufficient funds in source account");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createAccount() {
        System.out.println("Enter name for the new account: ");
        String name = scanner.nextLine();
        System.out.println("Enter address for the new account: ");
        String address = scanner.nextLine();

        Long id = bank.createAccount(name, address);
        System.out.println("New account created with ID: " + id);
    }


    public static double max(double a, double b) {
        return a>b ? a : b;
    }
}