package demo.task1.repositories.impl;

import demo.task1.models.Account;
import demo.task1.repositories.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.eclipse.persistence.internal.libraries.asm.tree.TryCatchBlockNode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl extends GenericDaoImpl<Account, Long> implements AccountRepository {

    @Override
    public Account create(String name, String address, BigDecimal balance) {
        Account account = new Account(name, address, balance);
        save(account);
        return account;
    }

    @Override
    public void update(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        if (!exists(account.getId())) {
            throw new IllegalArgumentException("Account with ID " + account.getId() + " not found");
        }

        try(EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<Account> findByNameAndAddress(String name, String address) {
        try (EntityManager em = getEntityManager()) {
            try {
                return Optional.of(
                        em.createNamedQuery("Account.findByNameAndAddress", Account.class)
                                .setParameter("name", name)
                                .setParameter("address", address)
                                .getSingleResult()
                );
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }
    }


    public List<Account> findByNameStartWith(String prefix){
        try(EntityManager em = getEntityManager()){
            return em.createNamedQuery("Account.findByNameStartWith", Account.class)
                    .setParameter("prefix", prefix + "%")
                    .getResultList();
        }
    }

    @Override
    public List<Account> findByBalanceBetween(BigDecimal min, BigDecimal max) {
        try(EntityManager em = getEntityManager()){
            return em.createNamedQuery("Account.findByBalanceBetween", Account.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
        }
    }

    @Override
    public List<Account> findByTheRichest() {
        try(EntityManager em = getEntityManager()){
            return em.createNamedQuery("Account.findByTheRichest", Account.class)
                    .getResultList();
        }
    }

    @Override
    public List<Account> findByEmptyHistory() {
        try(EntityManager em = getEntityManager()){
            return em.createNamedQuery("Account.findByEmptyHistory", Account.class)
                    .getResultList();
        }
    }

    @Override
    public List<Account> findByMostOperations() {
        try(EntityManager em = getEntityManager()){
            return em.createNamedQuery("Account.findByMostOperations", Account.class)
                    .getResultList();
        }
    }
}
