package fi.solita.utils.query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.solita.utils.functional.Function0;

public class EntityManagerProvider extends Function0<EntityManager> {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager apply() {
        return em;
    }

}
