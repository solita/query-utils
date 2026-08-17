package fi.solita.utils.query;

import fi.solita.utils.functional.Function0;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

public class EntityManagerFactoryProvider extends Function0<EntityManagerFactory> {
    
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Override
    public EntityManagerFactory apply() {
        return emf;
    }

}
