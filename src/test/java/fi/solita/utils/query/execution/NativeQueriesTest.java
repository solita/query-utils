package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department__;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.NativeQuery.NativeQuerySingleEntity;

public class NativeQueriesTest extends QueryTestBase {

    @Autowired
    private NativeQueries dao;

    @Autowired
    private TypeProvider typeProvider;

    @Test
    public void get_sql_single() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.get(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).getId());
    }

    @Test
    public void get_sql_empty() {
        NativeQuerySingleEntity<Department> q = NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class));
        try {
            dao.get(q);
            fail("Should have failed");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void get_sql_multiple() {
        em.persist(new Department());
        em.persist(new Department());

        NativeQuery<Department> q = NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class));
        try {
            dao.get(q);
            fail("Should have failed");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void find_sql_single() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.find(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).get().getId());
    }

    @Test
    public void find_sql_empty() {
        assertFalse(dao.find(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void find_sql_multiple() {
        em.persist(new Department());
        em.persist(new Department());

        NativeQuerySingleEntity<Department> q = NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class));
        try {
            dao.find(q);
            fail("Should have failed");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void findFirst_sql() {
        em.persist(new Department());

        assertTrue(dao.findFirst(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void findFirst_sql_empty() {
        assertFalse(dao.findFirst(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void findFirst_sql_multiple() {
        em.persist(new Department());
        em.persist(new Department());

        assertTrue(dao.findFirst(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void getList_sql() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(dao.getList(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))), Department__.getId)));
    }

    @Test
    public void getList_sql_paging() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId()), newList(map(dao.getList(NativeQuery.of("select * from Department order by id").returns(typeProvider.type(Department.class)), Page.FIRST.withSize(1)), Department__.getId)));
        assertEquals(newList(dep2.getId()), newList(map(dao.getList(NativeQuery.of("select * from Department order by id").returns(typeProvider.type(Department.class)), Page.FIRST.withSize(1).nextPage()), Department__.getId)));
    }

    @Test
    public void get_sql() {
        Department dep = new Department();
        em.persist(dep);
        em.flush();

        NativeQuery<Department.ID> q = NativeQuery.of("select id as eka from Department")
                                 .returns("eka", typeProvider.idType(Department.class));
        assertEquals(dep.getId(), dao.get(q));
    }

    @Test
    public void get_sql_entity() {
        Department dep = new Department();
        em.persist(dep);

        NativeQuery<Department> q = NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class));
        assertEquals(dep.getId(), dao.get(q).getId());
    }

    @Test
    public void get_sql_pair() {
        Department dep = new Department();
        em.persist(dep);
        em.flush();

        NativeQuery<Pair<Department.ID, Department.ID>> q = NativeQuery.of("select id as eka, id as toka from Department")
                .returns("eka", typeProvider.idType(Department.class))
                .returns("toka", typeProvider.idType(Department.class));
        assertEquals(Pair.of(dep.getId(), dep.getId()), dao.get(q));
    }

    @Test
    public void get_sql_array_multiple() {
        Department dep = new Department();
        em.persist(dep);
        em.flush();

        NativeQuery<Tuple3<Department.ID, Department.ID, Department.ID>> q = NativeQuery.of("select id as eka, id as toka, id as kolmas from Department")
                .returns("eka", typeProvider.idType(Department.class))
                .returns("toka", typeProvider.idType(Department.class))
                .returns("kolmas", typeProvider.idType(Department.class));
        Tuple3<Department.ID, Department.ID, Department.ID> tuple3 = dao.get(q);
        assertArrayEquals(new Object[]{dep.getId(), dep.getId(), dep.getId()}, new Object[]{tuple3._1, tuple3._2, tuple3._3});
    }
}
