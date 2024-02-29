package fi.solita.utils.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import fi.solita.utils.functional.Function0;

public class EntityManagerProvider extends Function0<EntityManager> {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager apply() {
        return em;
    }

}
