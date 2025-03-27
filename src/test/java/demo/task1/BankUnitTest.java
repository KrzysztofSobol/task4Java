package demo.task1;

import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.repositories.AccountOperationRepository;
import demo.task1.repositories.AccountRepository;
import demo.task1.repositories.impl.AccountOperationRepositoryImpl;
import demo.task1.repositories.impl.AccountRepositoryImpl;
import demo.task1.services.Bank;
import demo.task1.services.impl.BankImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BankUnitTest {
    static private BankImpl bank;
    static private AccountRepository accountRepository;
    static private AccountOperationRepository accountOperationRepository;

    @BeforeAll
    static void setup() {
        accountRepository = new AccountRepositoryImpl();
        accountOperationRepository = new AccountOperationRepositoryImpl();
        AccountOperationRepository accountOperationRepository = new AccountOperationRepositoryImpl();
        bank = new BankImpl(accountRepository, accountOperationRepository);
    }

    @AfterEach
    public void clearData() {
        for(AccountOperation ao : accountOperationRepository.findAll()){
            accountOperationRepository.delete(ao);
        }

        for(Account a : accountRepository.findAll()){
            accountRepository.delete(a);
        }
    }

    @Test
    void test_create_account_if_account_exists() {
        Long id = bank.createAccount("x","y");
        Long id2 = bank.createAccount("x","y");

        assert id.equals(id2);
    }

    @Test
    void test_findAccount() {
        Long id = bank.createAccount("x","y");
        Long foundId = bank.findAccount("x","y");

        assert id.equals(foundId);
    }

    @Test
    void test_findAccount_if_account_doesnt_exists() {
        Long foundId = bank.findAccount("x","y");

        assert foundId == null;
    }

    @Test
    void test_deposit() {
        Long id = bank.createAccount("x","y");

        bank.deposit(id, BigDecimal.valueOf(1));
        bank.deposit(id, BigDecimal.valueOf(10));
        bank.deposit(id, BigDecimal.valueOf(100));

        BigDecimal result = bank.getBalance(id);

        assertEquals(BigDecimal.valueOf(111), result);
    }

    @Test
    void test_deposit_zero() {
        Long id = bank.createAccount("x","y");

        bank.deposit(id, BigDecimal.valueOf(0));

        BigDecimal result = bank.getBalance(id);

        assertEquals(result, BigDecimal.ZERO);
    }

    @Test
    void test_deposit_negative() {
        Long id = bank.createAccount("x","y");

        bank.deposit(id, BigDecimal.valueOf(-10));

        BigDecimal result = bank.getBalance(id);

        assert result.equals(BigDecimal.valueOf(-10));
    }

    @Test
    void test_deposit_floats() {
        Long id = bank.createAccount("x","y");

        bank.deposit(id, BigDecimal.valueOf(5.5));
        bank.deposit(id, BigDecimal.valueOf(5.5));
        bank.deposit(id, BigDecimal.valueOf(10.9));

        BigDecimal result = bank.getBalance(id);

        assertEquals(BigDecimal.valueOf(21.9), result);
    }

    @Test
    void test_deposit_null() {
        Long id = bank.createAccount("x","y");

        bank.deposit(id, null);

        BigDecimal result = bank.getBalance(id);

        assertEquals(result, BigDecimal.ZERO);
    }

    @Test
    void test_deposit_not_existing_id() {
        assertThrows(Bank.AccountIdException.class, () -> bank.deposit(1L, BigDecimal.ZERO));
    }

    @Test
    void test_deposit_null_id() {
        assertThrows(Bank.AccountIdException.class, () -> bank.deposit(null, BigDecimal.ZERO));
    }

    @Test
    void test_deposit_when_account_doesnt_exists() {
        assertThrows(Bank.AccountIdException.class, () -> bank.deposit(1L, BigDecimal.ONE));
    }

    @Test
    void test_getBalance() {
        Long id = bank.createAccount("x","y");

        bank.deposit(id, BigDecimal.ONE);
        BigDecimal result = bank.getBalance(id);

        assertEquals(result, BigDecimal.ONE);
    }

    @Test
    void test_getBalance_with_no_previous_deposits() {
        Long id = bank.createAccount("x","y");

        BigDecimal balance = bank.getBalance(id);

        assertEquals(balance, BigDecimal.ZERO);
    }

    @Test
    void test_getBalance_when_account_doesnt_exists() {
        assertThrows(Bank.AccountIdException.class, () -> bank.getBalance(1L));
    }

    @Test
    void test_getBalance_when_null_id() {
        assertThrows(Bank.AccountIdException.class, () -> bank.getBalance(null));
    }

    @Test
    void test_withdraw() {
        Long id = bank.createAccount("x","y");
        bank.deposit(id, BigDecimal.ONE);
        bank.withdraw(id, BigDecimal.ONE);
        BigDecimal balance = bank.getBalance(id);

        assertEquals(balance, BigDecimal.ZERO);
    }

    @Test
    void test_withdraw_null() {
        Long id = bank.createAccount("x","y");
        bank.withdraw(id, null);
        BigDecimal balance = bank.getBalance(id);

        assertEquals(balance, BigDecimal.ZERO);
    }

    @Test
    void test_withdraw_when_account_doesnt_exists() {
        assertThrows(Bank.AccountIdException.class, () -> bank.withdraw(1L, BigDecimal.ONE));
    }

    @Test
    void test_withdraw_when_null_id() {
        assertThrows(Bank.AccountIdException.class, () -> bank.withdraw(null, BigDecimal.ONE));
    }

    @Test
    void test_withdraw_when_no_funds(){
        Long id = bank.createAccount("x","y");
        assertThrows(Bank.InsufficientFundsException.class, () -> bank.withdraw(id, BigDecimal.ONE));
    }

    @Test
    void test_withdraw_when_not_enough_funds(){
        Long id = bank.createAccount("x","y");
        bank.deposit(id, BigDecimal.ONE);
        assertThrows(Bank.InsufficientFundsException.class, () -> bank.withdraw(id, BigDecimal.TEN));
    }

    @Test
    void test_transfer() {
        Long id1 = bank.createAccount("x","y");
        Long id2 = bank.createAccount("a","b");

        bank.deposit(id1, BigDecimal.ONE);
        bank.transfer(id1, id2, BigDecimal.ONE, "test");

        BigDecimal balance = bank.getBalance(id2);
        assertEquals(balance, BigDecimal.ONE);
    }

    @Test
    void test_transfer_when_receiver_doesnt_exists() {
        Long id = bank.createAccount("x","y");
        bank.deposit(id, BigDecimal.ONE);
        assertThrows(Bank.AccountIdException.class, () -> bank.transfer(id, 2L, BigDecimal.ONE, "test"));
    }

    @Test
    void test_transfer_when_sender_doesnt_exists() {
        Long id = bank.createAccount("x","y");
        assertThrows(Bank.AccountIdException.class, () -> bank.transfer(2L, id, BigDecimal.ONE, "test"));
    }

    @Test
    void test_transfer_when_receiver_is_null() {
        Long id = bank.createAccount("x","y");
        bank.deposit(id, BigDecimal.ONE);
        assertThrows(Bank.AccountIdException.class, () -> bank.transfer(id, null, BigDecimal.ONE, "test"));
    }

    @Test
    void test_transfer_when_sender_is_null() {
        Long id = bank.createAccount("x","y");
        assertThrows(Bank.AccountIdException.class, () -> bank.transfer(null, id, BigDecimal.ONE, "test"));
    }

    @Test
    void test_transfer_when_not_enough_funds() {
        Long id1 = bank.createAccount("x","y");
        Long id2 = bank.createAccount("a","b");

        assertThrows(Bank.InsufficientFundsException.class, () -> bank.transfer(id1, id2, BigDecimal.ONE, "test"));
    }

    @Test
    void test_transfer_when_not_enough_funds_2() {
        Long id1 = bank.createAccount("x","y");
        Long id2 = bank.createAccount("a","b");

        bank.deposit(id1, BigDecimal.ONE);

        assertThrows(Bank.InsufficientFundsException.class, () -> bank.transfer(id1, id2, BigDecimal.TEN, "test"));
    }
}
