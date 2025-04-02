package demo.task1.services;

import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.models.OperationType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface Bank {
    /**
     * Tworzy nowe lub zwraca id istniejącego konta.
     *
     * @param name nazwa wlasciciela
     * @param address adres wlasciciela
     * @return id utworzonego lub istniejacego konta.
     */
    Long createAccount(String name, String address);

    /**
     * Znajduje identyfikator konta.
     *
     * @param name nazwa wlasciciela
     * @param address adres wlasciciela
     * @return id konta lub null, gdy brak konta o podanych parametrach
     */
    Long findAccount(String name, String address);

    /**
     * Dodaje srodki do konta.
     *
     * @param id id konta
     * @param amount srodki
     * @throws AccountIdException gdy id konta jest nieprawidlowe
     */
    void deposit(Long id, BigDecimal amount);

    /**
     * Zwraca ilosc srodkow na koncie.
     *
     * @param id id konta
     * @return srodki
     * @throws AccountIdException gdy id konta jest nieprawidlowe
     */
    BigDecimal getBalance(Long id);

    /**
     * Pobiera srodki z konta.
     *
     * @param id id konta
     * @param amount srodki
     * @throws AccountIdException gdy id konta jest nieprawidlowe
     * @throws InsufficientFundsException gdy srodki na koncie nie sa
     * wystarczajace do wykonania operacji
     */
    void withdraw(Long id, BigDecimal amount);

    /**
     * Przelewa srodki miedzy kontami.
     *
     * @param idSource id konta
     * @param idDestination id konta
     * @param amount srodki
     * @throws AccountIdException gdy id konta jest nieprawidlowe
     * @throws InsufficientFundsException gdy srodki na koncie nie sa
     * wystarczajace do wykonania operacji
     */
    void transfer(Long idSource, Long idDestination, BigDecimal amount, String title);

    List<Account> findByNameStartWith(String prefix);
    List<Account> findByBalanceBetween(BigDecimal min, BigDecimal max);
    List<Account> findByTheRichest();
    List<Account> findByEmptyHistory();
    List<Account> findByMostOperations();
    List<AccountOperation> findByDateRange(Long id, Date from, Date to);
    OperationType findByMostFrequentType(Long id);

    class InsufficientFundsException extends RuntimeException {
    };

    class AccountIdException extends RuntimeException {
    };
}
