package demo.task1.repositories;

import demo.task1.models.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends GenericDao<Account, Long> {
    Account create(String name, String address, BigDecimal balance);
    Optional<Account> findByNameAndAddress(String name, String address);
}
