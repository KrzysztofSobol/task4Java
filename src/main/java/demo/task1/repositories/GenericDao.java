package demo.task1.repositories;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T,K> {
    void save(T t);
    void delete(T t);
    void update (T t);
    Optional<T> findById(K id);
    List<T> findAll();
    boolean exists(K id);
}
