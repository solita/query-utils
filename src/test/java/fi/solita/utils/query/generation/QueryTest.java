package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.*;

import javax.persistence.NoResultException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.*;
import fi.solita.utils.functional.Collections;
import fi.solita.utils.query.execution.JpaCriteriaQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;


public class QueryTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaCriteriaQueries dao;

    @Test
    public void single() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.get(query.single(dep1.getId())).getId());
        assertEquals(dep2.getId(), dao.get(query.single(dep2.getId())).getId());
    }



    @Test
    public void all() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(dao.getList(query.all(Department.class)), Department__.getId)));
    }

    @Test
    public void ofIds() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals("find by single id", dep1.getId(), dao.get(query.ofIds(newList(dep1.getId()), Department.class)).getId());
        assertEquals("find by multiple ids", newSet(dep1.getId(), dep2.getId()), newSet(map(dao.getList(query.ofIds(newList(dep1.getId(), dep2.getId()), Department.class)), Department__.getId)));
    }

    @Test(expected = NoResultException.class)
    public void ofIds_empty() {
        dao.get(query.ofIds(Collections.<Id<Department>>emptyList(), Department.class));
    }

    @Test
    public void cast() {
        assertEquals(Department_.employees, Cast.cast(Department_.employees));
        assertEquals(Employee_.department, Cast.cast(Employee_.department));
        assertEquals(Employee_.department, Cast.castSuper(Employee_.department));
    }

    @Test
    public void related() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);

        assertEquals("relation toOne", dep.getId(), dao.get(query.related(emp, Employee_.department)).getId());
        assertEquals("relation toMany", emp.getId(), dao.get(query.related(dep, Department_.employees)).getId());
        assertEquals("relation toMany toOne", mun.getId(), dao.get(query.related(dep, Department_.employees, Employee_.municipality)).getId());
    }

    @Test
    public void related_variants() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);

        assertEquals(mun.getId(), dao.get(
            query.related(dep,
                Department_.employees,
                Employee_.municipality)).getId());

        assertEquals(emp.getId(), dao.get(
            query.related(dep,
                Department_.employees)).getId());
    }

    @Test
    public void related_query() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);

        assertEquals(mun.getId(), dao.get(
                query.related(
                        query.single(dep.getId()),
                    Department_.employees,
                    Employee_.municipality)).getId());

            assertEquals(emp.getId(), dao.get(
                query.related(
                        query.single(dep.getId()),
                        Department_.employees)).getId());
    }
}
