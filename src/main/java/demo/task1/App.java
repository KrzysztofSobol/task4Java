package demo.task1;

import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.models.OperationType;
import demo.task1.repositories.AccountOperationRepository;
import demo.task1.repositories.AccountRepository;
import demo.task1.repositories.impl.AccountOperationRepositoryImpl;
import demo.task1.repositories.impl.AccountRepositoryImpl;
import demo.task1.services.Bank;
import demo.task1.services.impl.BankImpl;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
                    deposit();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    checkBalance();
                    break;
                case 4:
                    transfer();
                    break;
                case 5:
                    createAccount();
                    break;
                case 6:
                    findAccount();
                    break;
                case 7:
                    findByNameStartWith();
                    break;
                case 8:
                    findByBalanceBetween();
                    break;
                case 9:
                    findByTheRichest();
                    break;
                case 10:
                    findByEmptyHistory();
                    break;
                case 11:
                    findByMostOperations();
                    break;
                case 12:
                    findByDateRange();
                    break;
                case 13:
                    findByMostFrequentType();
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
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Check Balance");
        System.out.println("4. Transfer Money");
        System.out.println("5. Create New Account");
        System.out.println("0. Exit");

        System.out.println("\n===== @NamedQuery Methods =====");
        System.out.println("===== Find Account/s =====");
        System.out.println("6. By name and address");
        System.out.println("7. That start with (...)");
        System.out.println("8. That have (...) range of money");
        System.out.println("9. That are the most wealthy");
        System.out.println("10. With empty history");
        System.out.println("11. With the most operations");

        System.out.println("\n===== Find Operations =====");
        System.out.println("12. From (date) to (date)");
        System.out.println("13. That are the most frequent");

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

    private static void findByNameStartWith(){
        System.out.println("Enter the prefix: ");
        String prefix = scanner.nextLine();

        List<Account> list = bank.findByNameStartWith(prefix);
        printAccounts(list);
    }

    private static void findByBalanceBetween(){
        System.out.println("Enter the minimum balance: ");
        BigDecimal min = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.println("Enter the maximum balance: ");
        BigDecimal max = scanner.nextBigDecimal();
        scanner.nextLine();

        List<Account> list = bank.findByBalanceBetween(min, max);
        printAccounts(list);
    }

    private static void findByTheRichest(){
        List<Account> list = bank.findByTheRichest();
        printAccounts(list);
    }

    private static void findByEmptyHistory(){
        List<Account> list = bank.findByEmptyHistory();
        printAccounts(list);
    }

    private static void findByMostOperations(){
        List<Account> list = bank.findByMostOperations();
        printAccounts(list);
    }

    private static void findByDateRange(){
        System.out.println("Enter the id of the account: ");
        Long sourceId = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Enter start date (yyyy-MM-dd): ");
        String fromDateStr = scanner.nextLine();

        System.out.println("Enter end date (yyyy-MM-dd): ");
        String toDateStr = scanner.nextLine();

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = sdf.parse(fromDateStr);
            Date toDate = sdf.parse(toDateStr);

            List<AccountOperation> list = bank.findByDateRange(sourceId, fromDate, toDate);
            printOperations(list);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd");
        }
    }

    private static void findByMostFrequentType(){
        System.out.println("Enter the id of the account: ");
        Long sourceId = scanner.nextLong();
        scanner.nextLine();

        OperationType type = bank.findByMostFrequentType(sourceId);
        System.out.println("Most frequent type found is: " + type);
    }

    // print methods
    private static void printAccounts(List<Account> list){
        for(Account a: list){
            System.out.println("\n" + a.getId());
            System.out.println(a.getName() + " " + a.getAddress());
            System.out.println("   " + a.getBalance());
        }
    }

    private static void printOperations(List<AccountOperation> list){
        for(AccountOperation ao: list){
            System.out.println("\n" + ao.getId() + " " + ao.getAccount().getName());
            System.out.println(ao.getType());
            System.out.println(ao.getAmount());
            System.out.println(ao.getCreatedAt());
        }
    }

    public static double max(double a, double b) {
        return a>b ? a : b;
    }
}