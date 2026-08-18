package fi.solita.utils.query;

import java.util.function.Supplier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class EntityManagerProvider implements Supplier<EntityManager> {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager get() {
        return em;
    }
    
    public EntityManager apply() {
        return get();
    }

}
