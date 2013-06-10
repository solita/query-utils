package fi.solita.utils.query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
@Transactional
public abstract class QueryTestBase {

    @PersistenceContext
    protected EntityManager em;
    
    protected void persist(IEntity... entities) {
        for (IEntity e: entities) {
            em.persist(e);
        }
    }

    @Before
    public void resetDatabase() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        try {
            em.createNativeQuery("TRUNCATE TABLE Employee").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE Department").executeUpdate();
        } finally {
            em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        }
    }
    
    protected long getQueryCount() {
        return em.unwrap(Session.class).getSessionFactory().getStatistics().getQueryExecutionCount();
    }
}
