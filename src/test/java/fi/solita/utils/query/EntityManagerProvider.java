package fi.solita.utils.query;

import fi.solita.utils.functional.Function0;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class EntityManagerProvider extends Function0<EntityManager> {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager apply() {
        return em;
    }

}
