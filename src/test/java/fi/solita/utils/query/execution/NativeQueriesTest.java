package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.generation.Cast;
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
        persist(dep);

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
        persist(new Department(), new Department());

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
        persist(dep);

        assertEquals(dep.getId(), dao.find(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).get().getId());
    }

    @Test
    public void find_sql_empty() {
        assertFalse(dao.find(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void find_sql_multiple() {
        persist(new Department(), new Department());

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
        persist(new Department());

        assertTrue(dao.findFirst(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void findFirst_sql_empty() {
        assertFalse(dao.findFirst(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void findFirst_sql_multiple() {
        persist(new Department(), new Department());

        assertTrue(dao.findFirst(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))).isDefined());
    }

    @Test
    public void getList_sql() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, dao.getMany(NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class))))));
    }

    @Test
    public void getList_sql_paging() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(newList(dep1.getId()), newList(map(Department_.getId, dao.getMany(NativeQuery.of("select * from Department order by id").returns(typeProvider.type(Department.class)), Page.FIRST.withSize(1)))));
        assertEquals(newList(dep2.getId()), newList(map(Department_.getId, dao.getMany(NativeQuery.of("select * from Department order by id").returns(typeProvider.type(Department.class)), Page.FIRST.withSize(1).nextPage()))));
    }
    
    @Test
    public void count() {
        Department dep = new Department();
        persist(dep);
        em.flush();

        NativeQuery<Department.ID> q = NativeQuery.of("select id as c from Department")
                                 .returns("c", typeProvider.idType(Department.class));
        assertEquals(1, dao.count(q));
    }

    @Test
    public void get_sql() {
        Department dep = new Department();
        persist(dep);
        em.flush();

        NativeQuery<Department.ID> q = NativeQuery.of("select id as eka from Department")
                                 .returns("eka", typeProvider.idType(Department.class));
        assertEquals(dep.getId(), dao.get(q));
    }

    @Test
    public void get_sql_entity() {
        Department dep = new Department();
        persist(dep);

        NativeQuery<Department> q = NativeQuery.of("select * from Department").returns(typeProvider.type(Department.class));
        assertEquals(dep.getId(), dao.get(q).getId());
    }

    @Test
    public void get_sql_pair() {
        Department dep = new Department();
        persist(dep);
        em.flush();

        NativeQuery<Pair<Department.ID, Department.ID>> q = NativeQuery.of("select id as eka, id as toka from Department")
                .returns("eka", typeProvider.idType(Department.class))
                .returns("toka", typeProvider.idType(Department.class));
        assertEquals(Pair.of(dep.getId(), dep.getId()), dao.get(q));
    }

    @Test
    public void get_sql_array_multiple() {
        Department dep = new Department();
        persist(dep);
        em.flush();

        NativeQuery<Tuple3<Department.ID, Department.ID, Department.ID>> q = NativeQuery.of("select id as eka, id as toka, id as kolmas from Department")
                .returns("eka", typeProvider.idType(Department.class))
                .returns("toka", typeProvider.idType(Department.class))
                .returns("kolmas", typeProvider.idType(Department.class));
        Tuple3<Department.ID, Department.ID, Department.ID> tuple3 = dao.get(q);
        assertArrayEquals(new Object[]{dep.getId(), dep.getId(), dep.getId()}, new Object[]{tuple3._1, tuple3._2, tuple3._3});
    }
    
    @Test
    public void get_sql_optional() {
        Department dep = new Department(new Money(42));
        persist(dep);
        em.flush();

        NativeQuery<Option<Money>> q = NativeQuery.of("select optionalBudget as eka from Department")
                                  .returns("eka", Cast.optional(typeProvider.type(Money.class)));
        assertEquals(Some(dep.getBudget()), dao.get(q));
    }
    
    @Test
    public void get_sql_optional_none() {
        Department dep = new Department();
        persist(dep);
        em.flush();

        NativeQuery<Option<Money>> q = NativeQuery.of("select optionalBudget as eka from Department")
                                  .returns("eka", Cast.optional(typeProvider.type(Money.class)));
        assertEquals(None(), dao.get(q));
    }
    
    @Test
    public void get_sql_optional_entity() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        persist(mun);
        persist(dep);

        NativeQuery<Option<Municipality>> q = NativeQuery.of("select m.* from Department d left join Municipality m on d.optionalDepMunicipality_id=m.id")
                                        .returnsOptional(Cast.optional(typeProvider.type(Municipality.class)));
        assertEquals(mun.getId(), dao.get(q).get().getId());
    }
    
    @Test
    public void get_sql_optional_entity_none() {
        Department dep = new Department();
        persist(dep);

        NativeQuery<Option<Municipality>> q = NativeQuery.of("select m.* from Department d left join Municipality m on d.optionalDepMunicipality_id=m.id")
                                        .returnsOptional(Cast.optional(typeProvider.type(Municipality.class)));
        assertEquals(None(), dao.get(q));
    }

    @Test
    public void execute_sql() {
        Department dep = new Department("original");
        persist(dep);
        em.flush();

        int affected = dao.execute(NativeQuery.of("update Department set mandatoryDepName='updated'"));
        assertEquals(1, affected);
    }

    @Test
    public void execute_sql_no_rows() {
        int affected = dao.execute(NativeQuery.of("update Department set mandatoryDepName='updated'"));
        assertEquals(0, affected);
    }

    @Test
    public void exists_sql_true() {
        persist(new Department());

        assertTrue(dao.exists(NativeQuery.of("select id as c from Department").returns("c", typeProvider.idType(Department.class))));
    }

    @Test
    public void exists_sql_false() {
        assertFalse(dao.exists(NativeQuery.of("select id as c from Department").returns("c", typeProvider.idType(Department.class))));
    }

    @Test
    public void get_sql_with_constructor() {
        Department dep = new Department("foo");
        persist(dep);
        em.flush();

        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department")
                .returns("n", typeProvider.type(String.class));
        String name = dao.get(q, new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertEquals("FOO", name);
    }

    @Test
    public void find_sql_with_constructor() {
        Department dep = new Department("bar");
        persist(dep);
        em.flush();

        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department")
                .returns("n", typeProvider.type(String.class));
        Option<String> name = dao.find(q, new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertEquals(Some("BAR"), name);
    }

    @Test
    public void find_sql_with_constructor_empty() {
        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department")
                .returns("n", typeProvider.type(String.class));
        Option<String> name = dao.find(q, new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertFalse(name.isDefined());
    }

    @Test
    public void findFirst_sql_with_constructor() {
        persist(new Department("first"), new Department("second"));

        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department order by mandatoryDepName")
                .returns("n", typeProvider.type(String.class));
        Option<String> name = dao.findFirst(q, new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertEquals(Some("FIRST"), name);
    }

    @Test
    public void findFirst_sql_with_constructor_empty() {
        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department")
                .returns("n", typeProvider.type(String.class));
        assertFalse(dao.findFirst(q, new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        }).isDefined());
    }

    @Test
    public void getMany_sql_with_constructor() {
        persist(new Department("a"), new Department("b"));

        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department")
                .returns("n", typeProvider.type(String.class));
        Collection<String> names = dao.getMany(q, new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertEquals(newSet("A", "B"), newSet(names));
    }

    @Test
    public void getMany_sql_paging_with_constructor() {
        persist(new Department("a"), new Department("b"));

        NativeQuery<String> q = NativeQuery.of("select mandatoryDepName as n from Department order by mandatoryDepName")
                .returns("n", typeProvider.type(String.class));
        Collection<String> firstPage = dao.getMany(q, Page.FIRST.withSize(1), new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertEquals(newList("A"), newList(firstPage));
        Collection<String> secondPage = dao.getMany(q, Page.FIRST.withSize(1).nextPage(), new Apply<String, String>() {
            public String apply(String s) { return s.toUpperCase(); }
        });
        assertEquals(newList("B"), newList(secondPage));
    }

    @Test
    public void setParameter_sql() {
        Department dep1 = new Department("foo");
        Department dep2 = new Department("bar");
        persist(dep1, dep2);
        em.flush();

        NativeQuery<Department.ID> q = NativeQuery.of("select id as c from Department where mandatoryDepName = :name")
                .returns("c", typeProvider.idType(Department.class))
                .setParameter("name", "foo");
        assertEquals(dep1.getId(), dao.get(q));
    }

    @Test
    public void setParameter_sql_with_type() {
        Department dep1 = new Department("foo");
        Department dep2 = new Department("bar");
        persist(dep1, dep2);
        em.flush();

        NativeQuery<Department.ID> q = NativeQuery.of("select id as c from Department where mandatoryDepName = :name")
                .returns("c", typeProvider.idType(Department.class))
                .setParameter("name", "bar", typeProvider.type(String.class));
        assertEquals(dep2.getId(), dao.get(q));
    }

    @Test
    public void setParameterList_sql() {
        Department dep1 = new Department("foo");
        Department dep2 = new Department("bar");
        Department dep3 = new Department("baz");
        persist(dep1, dep2, dep3);
        em.flush();

        NativeQuery<Department.ID> q = NativeQuery.of("select id as c from Department where mandatoryDepName in (:names)")
                .returns("c", typeProvider.idType(Department.class))
                .setParameterList("names", newList("foo", "bar"));
        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(dao.getMany(q)));
    }
}
