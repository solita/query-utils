package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.generation.QLQuery;

public class QLQueriesTest extends QueryTestBase {

    @Autowired
    private QLQueries dao;

    @Test
    public void get_hql_single() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.get(QLQuery.<Department>of("from Department")).getId());
    }

    @Test
    public void get_hql_empty() {
        QLQuery<Department> q = QLQuery.<Department>of("from Department");
        try {
            dao.get(q);
            fail("Should have failed");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void get_hql_multiple() {
        persist(new Department(), new Department());

        QLQuery<Department> q = QLQuery.<Department>of("from Department");
        try {
            dao.get(q);
            fail("Should have failed");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void find_hql_single() {
        Department dep = new Department();
        persist(dep);

        assertEquals(dep.getId(), dao.find(QLQuery.<Department>of("from Department")).get().getId());
    }

    @Test
    public void find_hql_empty() {
        assertFalse(dao.find(QLQuery.<Department>of("from Department")).isDefined());
    }

    @Test
    public void find_hql_multiple() {
        persist(new Department(), new Department());

        QLQuery<Department> q = QLQuery.<Department>of("from Department");
        try {
            dao.find(q);
            fail("Should have failed");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void findFirst_hql() {
        persist(new Department());

        assertTrue(dao.findFirst(QLQuery.<Department>of("from Department")).isDefined());
    }

    @Test
    public void findFirst_hql_empty() {
        assertFalse(dao.findFirst(QLQuery.<Department>of("from Department")).isDefined());
    }

    @Test
    public void findFirst_hql_multiple() {
        persist(new Department(), new Department());

        assertTrue(dao.findFirst(QLQuery.<Department>of("from Department")).isDefined());
    }

    @Test
    public void getList_hql() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(dao.getMany(QLQuery.<Department.ID>of("select id from Department"))));
    }

    @Test
    public void getList_hql_paging() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(newList(dep1.getId()), newList(map(Department_.getId, dao.getMany(QLQuery.<Department>of("from Department order by id"), Page.FIRST.withSize(1)))));
        assertEquals(newList(dep2.getId()), newList(map(Department_.getId, dao.getMany(QLQuery.<Department>of("from Department order by id"), Page.FIRST.withSize(1).nextPage()))));
    }

}
