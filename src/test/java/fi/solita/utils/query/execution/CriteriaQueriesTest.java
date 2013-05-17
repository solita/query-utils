package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Department__;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee__;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.generation.Restrict;

public class CriteriaQueriesTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaCriteriaQueries dao;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Restrict restrict;

    @Test
    public void count() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(0, dao.count(query.all(Employee.class)));
        assertEquals(1, dao.count(query.single(dep1.getId())));
        assertEquals(2, dao.count(query.all(Department.class)));
    }

    @Test
    public void exists() {
        em.persist(new Department());

        assertFalse(dao.exists(query.all(Employee.class)));
        assertTrue(dao.exists(query.all(Department.class)));
    }

    @Test
    public void get_single() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.get(query.single(dep.getId())).getId());
    }

    @Test(expected = NoResultException.class)
    public void get_empty() {
        dao.get(query.all(Department.class));
    }

    @Test(expected = NonUniqueResultException.class)
    public void get_multiple() {
        em.persist(new Department());
        em.persist(new Department());

        dao.get(query.all(Department.class));
    }

    @Test
    public void find_single() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.find(query.single(dep.getId())).get().getId());
    }

    @Test
    public void find_empty() {
        assertFalse(dao.find(query.all(Department.class)).isDefined());
    }

    @Test(expected = NonUniqueResultException.class)
    public void find_multiple() {
        em.persist(new Department());
        em.persist(new Department());

        dao.find(query.all(Department.class));
    }

    @Test
    public void findFirst_single() {
        em.persist(new Department());

        assertTrue(dao.findFirst(allDepartmentsOrdered()).isDefined());
    }

    @Test
    public void findFirst_empty() {
        assertFalse(dao.findFirst(allDepartmentsOrdered()).isDefined());
    }

    @Test
    public void findFirst_multiple() {
        em.persist(new Department());
        em.persist(new Department());

        assertTrue(dao.findFirst(allDepartmentsOrdered()).isDefined());
    }

    @Test
    public void findFirst_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.findFirst(query.all(Department.class), Order.by(Department_.name)).get().getId());
        assertEquals(dep2.getId(), dao.findFirst(query.all(Department.class), Order.by(Department_.name).desc).get().getId());
    }

    @Test
    public void getList() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        CriteriaQuery<Department> q = query.all(Department.class);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(dao.getList(q), Department__.getId)));

        assertEquals(newList(dep1.getId(), dep2.getId()), newList(map(dao.getList(q, Order.by(Department_.name)), Department__.getId)));
        assertEquals(newList(dep2.getId(), dep1.getId()), newList(map(dao.getList(q, Order.by(Department_.name).desc), Department__.getId)));

        assertEquals(newList(dep2.getId()), newList(map(dao.getList(q, Page.of(0, 1), Order.by(Department_.name).desc), Department__.getId)));
        assertEquals(newList(dep1.getId(), dep2.getId()), newList(map(dao.getList(q, Page.of(0, 2), Order.by(Department_.name)), Department__.getId)));
    }

    @Test(expected = NoOrderingSpecifiedException.class)
    public void getList_pagingWithoutOrderingFails() {
        dao.getList(query.all(Department.class), Page.of(0, 2));
    }

    @Test
    public void getList_pagingWithOrderingDefinedInTheQuery() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        CriteriaQuery<Department> qOrdered = query.all(Department.class);
        qOrdered.orderBy(em.getCriteriaBuilder().asc(qOrdered.getRoots().iterator().next().get("name")));
        assertEquals(newList(dep1.getId()), newList(map(dao.getList(qOrdered, Page.of(0, 1)), Department__.getId)));
        assertEquals(newList(dep2.getId()), newList(map(dao.getList(qOrdered, Page.of(1, 1)), Department__.getId)));
    }

    @Test
    public void getList_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId(), dep2.getId()), newList(map(dao.getList(query.all(Department.class), Order.by(Department_.name)), Department__.getId)));
        assertEquals(newList(dep2.getId(), dep1.getId()), newList(map(dao.getList(query.all(Department.class), Order.by(Department_.name).desc), Department__.getId)));
    }

    @Test
    public void getList_paged_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId()), newList(map(dao.getList(query.all(Department.class), Page.FIRST.withSize(1), Order.by(Department_.name)), Department__.getId)));
        assertEquals(newList(dep2.getId()), newList(map(dao.getList(query.all(Department.class), Page.FIRST.withSize(1), Order.by(Department_.name).desc), Department__.getId)));
    }

    @Test
    public void getList_pagingWithListAttribute() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals(newList(emp.getId()), newList(map(dao.getList(query.related(dep, Department_.employees), Page.of(0, 2)), Employee__.getId)));
    }

    private CriteriaQuery<Department> allDepartmentsOrdered() {
        CriteriaQuery<Department> qOrdered = query.all(Department.class);
        return qOrdered.orderBy(em.getCriteriaBuilder().asc(qOrdered.getRoots().iterator().next().get("name")));
    }
}
