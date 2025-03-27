package demo.task1.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaFactory {
    private static JpaFactory instance;
    private EntityManagerFactory emf;

    private JpaFactory(){
        emf = Persistence.createEntityManagerFactory("demo");
    }

    public static JpaFactory getInstance(){
        if(instance == null){
            instance = new JpaFactory();
        }
        return instance;
    }

    public static EntityManager getEntityManager(){
        return getInstance().emf.createEntityManager();
    }
}
