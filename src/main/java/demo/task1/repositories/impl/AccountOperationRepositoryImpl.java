package demo.task1.repositories.impl;

import demo.task1.models.TransferOperation;
import demo.task1.repositories.AccountOperationRepository;
import demo.task1.models.Account;
import demo.task1.models.AccountOperation;
import demo.task1.models.OperationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class AccountOperationRepositoryImpl extends GenericDaoImpl<AccountOperation, Long> implements AccountOperationRepository {

    @Override
    public AccountOperation createOperation(Account account, BigDecimal amount, OperationType type) {
        AccountOperation operation = AccountOperation.builder()
                .account(account)
                .amount(amount)
                .type(type)
                .build();

        save(operation);
        return operation;
    }

    @Override
    public TransferOperation createTransferOperation(Account sourceAccount, Account destinationAccount, BigDecimal amount, OperationType type, String title) {
        TransferOperation operation = TransferOperation.builder()
                .account(type == OperationType.TRANSFER_OUT ? sourceAccount : destinationAccount)
                .otherAccount(type == OperationType.TRANSFER_OUT ? destinationAccount : sourceAccount)
                .amount(amount)
                .type(type)
                .title(title)
                .build();

        save(operation);
        return operation;
    }

    @Override
    public List<AccountOperation> findByAccount(Account account) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AccountOperation> cq = cb.createQuery(AccountOperation.class);
            Root<AccountOperation> root = cq.from(AccountOperation.class);

            cq.where(cb.equal(root.get("account"), account));
            cq.orderBy(cb.desc(root.get("timestamp")));

            return em.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<AccountOperation> findByDateRange(Long id, LocalDateTime from, LocalDateTime to) {
        try(EntityManager em = getEntityManager()) {
            return em.createNamedQuery("Operation.findByDateRange", AccountOperation.class)
                    .setParameter("accountId", id)
                    .setParameter("startDate", from)
                    .setParameter("endDate", to)
                    .getResultList();
        }
    }

    @Override
    public OperationType findByMostFrequentType(Long id) {
        try(EntityManager em = getEntityManager()) {
            return em.createNamedQuery("Operation.findByMostFrequentType", OperationType.class)
                    .setParameter("accountId", id)
                    .setMaxResults(1)
                    .getSingleResult();
        }
    }
}