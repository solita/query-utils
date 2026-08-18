package fi.solita.utils.query;

import java.util.function.Supplier;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

public class EntityManagerFactoryProvider implements Supplier<EntityManagerFactory> {
    
    @PersistenceUnit
    private EntityManagerFactory emf;

    public EntityManagerFactory apply() {
        return get();
    }

    @Override
    public EntityManagerFactory get() {
        return emf;
    }
}
