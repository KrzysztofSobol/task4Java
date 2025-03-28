package demo.task1.repositories;

import demo.task1.models.Account;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends GenericDao<Account, Long> {
    Account create(String name, String address, BigDecimal balance);
    Optional<Account> findByNameAndAddress(String name, String address);
    List<Account> findByNameStartWith(String prefix);
    List<Account> findByBalanceBetween(BigDecimal min, BigDecimal max);
    List<Account> findByTheRichest();
    List<Account> findByEmptyHistory();
    List<Account> findByMostOperations(); //findABaller would be a fire method name
}
