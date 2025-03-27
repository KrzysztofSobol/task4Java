package demo.task1.repositories.impl;

import demo.task1.repositories.GenericDao;
import demo.task1.utils.JpaFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class GenericDaoImpl<T,K> implements GenericDao<T,K> {
    private final Class<T> type;

    protected EntityManager getEntityManager() {
        return JpaFactory.getEntityManager();
    }

    public GenericDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class) pt.getActualTypeArguments()[0];
    }

    @Override
    public void save(T entity) {
        try(EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public void delete(T entity) {
        try(EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            entity = em.merge(entity);
            em.remove(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public void update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
    }

    @Override
    public Optional<T> findById(K id) {
        try(EntityManager em = getEntityManager()) {
            T dto = em.find(type, id);
            return Optional.ofNullable(dto);
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(type);
            Root<T> rootEntry = cq.from(type);

            CriteriaQuery<T> all = cq.select(rootEntry);
            TypedQuery<T> allQuery = em.createQuery(all);
            return allQuery.getResultList();
        }
    }

    public boolean exists(K id) {
        try (EntityManager em = getEntityManager()) { // auto closes the EntityManager, insane
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> rootEntry = cq.from(type);

            Predicate idPredicate = cb.equal(rootEntry.get("id"), id);
            cq.select(cb.count(rootEntry)).where(idPredicate);

            Long count = em.createQuery(cq).getSingleResult();
            return count > 0;
        }
    }
}
























