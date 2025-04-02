package demo.task1.services.impl;

import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.models.OperationType;
import demo.task1.repositories.AccountOperationRepository;
import demo.task1.repositories.AccountRepository;
import demo.task1.services.Bank;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class BankImpl implements Bank {

    private final AccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;
    private static final Logger logger = Logger.getLogger(BankImpl.class.getName());

    public BankImpl(AccountRepository accountRepository, AccountOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        logger.info("Bank instance created with repositories: " +
                accountRepository.getClass().getName() + ", " +
                operationRepository.getClass().getName());
    }

    // task 3 methods
    @Override
    public Long createAccount(String name, String address) {
        logger.fine("Creating account for: " + name + " with address: " + address);

        Optional<Account> account = accountRepository.findByNameAndAddress(name, address);
        if (account.isPresent()) { return account.get().getId(); }
        Long id = accountRepository.create(name,address,BigDecimal.ZERO).getId();

        logger.finer("Account created successfully with ID: " + id);
        return id;
    }

    @Override
    public Long findAccount(String name, String address) {
        logger.fine("Finding account for: " + name + " with address: " + address);

        Optional<Account> account = accountRepository.findByNameAndAddress(name, address);

        if(account.isPresent()){
            logger.finer("Account found: " + account.get().getId());
            return account.get().getId();
        } else {
            logger.finer("Account not found!");
            return null;
        }
    }

    @Override
    public void deposit(Long id, BigDecimal amount) {
        try {
            logger.fine("Making a deposit for" + id + " with amount: " + amount);
            Optional<Account> account = accountRepository.findById(id);
            if(account.isEmpty()) {
                logger.severe("Account with id: " + id + " not found for the deposit!");
                throw new AccountIdException();
            }

            Account ac = account.get();

            if(amount == null){
                logger.finest("Deposit amount is null, defaulting to ZERO");
                amount = BigDecimal.ZERO;
            }

            ac.setBalance(ac.getBalance().add(amount));
            operationRepository.createOperation(ac, amount, OperationType.DEPOSIT);

            logger.finer("Deposit successful for account: " + ac.getId());
            accountRepository.update(ac);
        } catch(IllegalArgumentException e) {
            logger.severe("Account with id: " + id + " not found for the deposit!");
            throw new AccountIdException();
        }
    }

    @Override
    public BigDecimal getBalance(Long id) {
        try {
            logger.fine("Getting balance for " + id);
            Optional<Account> account = accountRepository.findById(id);
            if(account.isEmpty()) {
                logger.severe("Account with id: " + id + " not found!");
                throw new AccountIdException();
            }

            BigDecimal balance = account.get().getBalance();
            logger.finer("Got balance: " + balance + " for account: " + account.get().getId());
            return balance;
        } catch (IllegalArgumentException e) {
            logger.severe("Account with id: " + id + " not found!");
            throw new AccountIdException();
        }
    }

    @Override
    public void withdraw(Long id, BigDecimal amount) {
        try {
            logger.fine("Making a withdrawal for " + id + " with amount: " + amount);
            Optional<Account> account = accountRepository.findById(id);
            if(account.isEmpty()) {
                logger.severe("Account with id: " + id + " not found for the withdraw!");
                throw new AccountIdException();
            }

            Account ac = account.get();
            BigDecimal currentBalance = ac.getBalance();

            if(amount == null){
                logger.finest("Withdraw amount is null, defaulting to ZERO");
                amount = BigDecimal.ZERO;
            }

            if(currentBalance.compareTo(amount) < 0) {
                logger.severe("Withdraw amount is insufficient for account: " + ac.getId());
                throw new InsufficientFundsException();
            }

            BigDecimal newBalance = currentBalance.subtract(amount);
            ac.setBalance(newBalance);
            operationRepository.createOperation(ac, amount, OperationType.WITHDRAW);

            logger.finer("Withdraw successful for account: " + ac.getId());
            logger.finer("Current balance: " + newBalance + " for account: " + ac.getId());
            accountRepository.update(ac);
        } catch (IllegalArgumentException e) {
            logger.severe("Account with id: " + id + " not found for the withdraw!");
            throw new AccountIdException();
        }
    }

    @Override
    public void transfer(Long idSource, Long idDestination, BigDecimal amount, String title) {
        try {
            logger.fine("Making a transfer from " + idSource + " to " + idDestination);
            Optional<Account> sourceAccount = accountRepository.findById(idSource);
            Optional<Account> destinationAccount = accountRepository.findById(idDestination);

            if(sourceAccount.isEmpty() || destinationAccount.isEmpty()) {
                logger.severe("Account with id: " + idSource + " or " + idDestination + " not found!");
                throw new AccountIdException();
            }

            Account sourceAc = sourceAccount.get();
            Account destAc = destinationAccount.get();

            if(sourceAc.getBalance().compareTo(amount) < 0) {
                logger.severe("Insufficient funds for account: " + sourceAc.getId());
                throw new InsufficientFundsException();
            }

            sourceAc.setBalance(sourceAc.getBalance().subtract(amount));
            destAc.setBalance(destAc.getBalance().add(amount));

            operationRepository.createTransferOperation(sourceAc, destAc, amount, OperationType.TRANSFER_OUT, title);
            operationRepository.createTransferOperation(sourceAc, destAc, amount, OperationType.TRANSFER_IN, title);

            accountRepository.update(sourceAc);
            accountRepository.update(destAc);

            logger.finer("Transfer successful for account: " + sourceAc.getId());
            logger.finer("Current balance of source: " + sourceAc.getBalance() + " after transfering: " + amount + " to destination: " + destAc.getBalance());
        } catch (IllegalArgumentException e) {
            logger.severe("Account with id: " + idSource + " or " + idDestination + " is invalid!");
            throw new AccountIdException();
        }
    }

    // task 4 methods
        // Account realted
    public List<Account> findByNameStartWith(String prefix){
        return accountRepository.findByNameStartWith(prefix);
    }

    public List<Account> findByBalanceBetween(BigDecimal min, BigDecimal max){
        return accountRepository.findByBalanceBetween(min, max);
    }

    public List<Account> findByTheRichest(){
        return accountRepository.findByTheRichest();
    }

    public List<Account> findByEmptyHistory(){
        return accountRepository.findByEmptyHistory();
    }

    public List<Account> findByMostOperations(){
        return accountRepository.findByMostOperations();
    }

    // Operations related
    public List<AccountOperation> findByDateRange(Long id, Date from, Date to){
        LocalDateTime fromDateTime = Instant.ofEpochMilli(from.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime toDateTime = Instant.ofEpochMilli(to.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return operationRepository.findByDateRange(id, fromDateTime ,toDateTime);
    }

    public OperationType findByMostFrequentType(Long id){
        return operationRepository.findByMostFrequentType(id);
    }
}
