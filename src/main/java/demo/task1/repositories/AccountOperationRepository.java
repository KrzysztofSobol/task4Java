package demo.task1.repositories;

import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.models.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface AccountOperationRepository extends GenericDao<AccountOperation, Long> {

    AccountOperation createOperation(Account account, BigDecimal amount, OperationType type);

    AccountOperation createTransferOperation(Account sourceAccount, Account destinationAccount,
                                             BigDecimal amount, OperationType type, String title);

    List<AccountOperation> findByAccount(Account account);
    List<AccountOperation> findByDateRange(Long id, LocalDateTime from, LocalDateTime to);
    OperationType findByMostFrequentType(Long id);
}