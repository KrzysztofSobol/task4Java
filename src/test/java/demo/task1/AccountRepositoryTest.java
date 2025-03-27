package demo.task1;

import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.repositories.AccountRepository;
import demo.task1.repositories.impl.AccountRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountRepositoryTest {

    private static AccountRepository accountRepository;

    @BeforeAll
    static void setup() {
        accountRepository = new AccountRepositoryImpl();
    }

    @AfterEach
    public void clearData() {
        for(Account a : accountRepository.findAll()){
            accountRepository.delete(a);
        }
    }

    // create
    @Test
    void test_createAccount() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        assert account != null;
        assert account.getId() != null;
    }

    // save
    @Test
    void test_saveAccount() {
        Account account = accountRepository.create("a","b", BigDecimal.ZERO);

        account.setName("c");
        account.setAddress("d");
        account.setBalance(BigDecimal.TEN);

        accountRepository.update(account);
        Optional<Account> found = accountRepository.findById(account.getId());

        found.ifPresent(foundAccount -> {
           assert foundAccount.getId().equals(account.getId());
           assert foundAccount.getName().equals(account.getName());
           assert foundAccount.getAddress().equals(account.getAddress());
           assert foundAccount.getBalance().equals(account.getBalance());
        });
    }

    @Test
    void test_saveAccount_when_null() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        account.setId(null);

        assertThrows(IllegalArgumentException.class, () -> accountRepository.update(account));
    }

    @Test
    void test_saveAccount_when_id_doesnt_exist() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        account.setId((long)999999);

        assertThrows(IllegalArgumentException.class, () -> accountRepository.update(account));
    }

    // findById
    @Test
    void test_findById() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        Long id = account.getId();
        Optional<Account> found = accountRepository.findById(id);

        assert found.isPresent();
        assert found.get().getId().equals(id);
    }

    @Test
    void test_findById_when_empty() {
        Optional<Account> found = accountRepository.findById((long)0);

        assert found.isEmpty();
    }

    @Test
    void test_findById_when_null() {
        Long id = null;

        assertThrows(IllegalArgumentException.class, () -> accountRepository.findById(id));
    }

    // findByNameAndAddress
    @Test
    void test_findByNameAndAddress() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        Optional<Account> found = accountRepository.findByNameAndAddress(account.getName(),account.getAddress());

        assert found.isPresent();
        assert found.get().getName().equals(account.getName());
        assert found.get().getAddress().equals(account.getAddress());
    }

    @Test
    void test_findByNameAndAddress_when_null() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        Optional<Account> found = accountRepository.findByNameAndAddress(null, null);

        assert found.isEmpty();
    }

    @Test
    void test_findByNameAndAddress_when_wrong_name() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        Optional<Account> found = accountRepository.findByNameAndAddress("x2",account.getAddress());

        assert found.isEmpty();
    }

    @Test
    void test_findByNameAndAddress_when_wrong_address() {
        Account account = accountRepository.create("x","y", BigDecimal.ZERO);
        Optional<Account> found = accountRepository.findByNameAndAddress(account.getName(),"y2");

        assert found.isEmpty();
    }

    @Test
    void test_findByNameAndAddress_when_both_wrong() {
        accountRepository.create("x","y", BigDecimal.ZERO);
        Optional<Account> found = accountRepository.findByNameAndAddress("x2","y2");

        assert found.isEmpty();
    }

    @Test
    void test_deleteAccount() {
        Account a = accountRepository.create("x", "y", BigDecimal.ZERO);
        accountRepository.delete(a);

        Optional<Account> found = accountRepository.findById(a.getId());
        assert found.isEmpty();
    }
    
    @Test
    void test_findAllAccount() {
        accountRepository.create("a", "b", BigDecimal.ZERO);
        accountRepository.create("c", "d", BigDecimal.ZERO);
        accountRepository.create("e", "f", BigDecimal.ZERO);

        List<Account> accountList = accountRepository.findAll();
        assertEquals(3, accountList.size());
    }
}
