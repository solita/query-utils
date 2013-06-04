package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.head;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class SelectTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;
    
    @Test
    public void literal_value() {
        Department dep = new Department();
        em.persist(dep);

        int value = dao.get(query.all(Department.class), Project.value(Select.literal(42)));
        assertEquals(42, value);
    }
    
    @Test
    public void literal_set() {
        Department dep = new Department();
        em.persist(dep);

        Set<Integer> value = dao.get(query.all(Department.class), Project.value(Select.literal(newSet(42))));
        assertEquals(newSet(42), value);
    }
    
    @Test
    public void literal_list() {
        Department dep = new Department();
        em.persist(dep);

        List<Integer> value = dao.get(query.all(Department.class), Project.value(Select.literal(newList(42))));
        assertEquals(newList(42), value);
    }

    @Test
    public void self() {
        Department dep = new Department();
        em.persist(dep);

        Collection<Pair<Department, String>> list = dao.getMany(
                query.all(Department.class),
                    Project.pair(Select.<Department>self(),
                                 Department_.name));

        assertEquals(dep.getId(), head(list)._1.getId());
    }
}
