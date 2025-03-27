package demo.task1.repositories.impl;

import demo.task1.models.Account;
import demo.task1.repositories.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
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
        EntityManager em = getEntityManager();
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Account> cq = cb.createQuery(Account.class);
            Root<Account> root = cq.from(Account.class); // kinda like FROM in SQL

            Predicate namePredicate = cb.equal(root.get("name"), name);
            Predicate addressPredicate = cb.equal(root.get("address"), address);

            cq.where(cb.and(namePredicate, addressPredicate));

            try{
                Account account = em.createQuery(cq)
                        .setMaxResults(1)
                        .getResultList()
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (account != null) {
                    return Optional.of(account);
                } else {
                    return Optional.empty();
                }
            } catch (NoResultException e) {
                return Optional.empty();
            }
        } finally {
            if(em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
