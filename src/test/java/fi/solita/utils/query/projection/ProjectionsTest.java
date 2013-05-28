package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
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

import fi.solita.utils.query.*;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.projection.Project;

public class ProjectionsTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaProjectionQueries dao;

    @Autowired
    private JpaCriteriaQuery query;

    @Test
    public void get_projectSingle() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.get(query.single(dep.getId()), Project.id()));
    }

    @Test(expected = NoResultException.class)
    public void get_projectEmpty() {
        dao.get(query.all(Department.class), Project.id());
    }

    @Test(expected = NonUniqueResultException.class)
    public void get_projectMultiple() {
        em.persist(new Department());
        em.persist(new Department());

        dao.get(query.all(Department.class), Project.id());
    }

    @Test
    public void find_projectSingle() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.find(query.single(dep.getId()), Project.id()).get());
    }

    @Test
    public void find_projectEmpty() {
        assertFalse(dao.find(query.all(Department.class), Project.id()).isDefined());
    }

    @Test(expected = NonUniqueResultException.class)
    public void find_projectMultiple() {
        em.persist(new Department());
        em.persist(new Department());

        dao.find(query.all(Department.class), Project.id());
    }

    @Test
    public void findFirst_projectSingle() {
        em.persist(new Department());

        assertTrue(dao.findFirst(allDepartmentsOrdered(), Project.id()).isDefined());
    }

    @Test
    public void findFirst_projectEmpty() {
        assertFalse(dao.findFirst(allDepartmentsOrdered(), Project.id()).isDefined());
    }

    @Test
    public void findFirst_projectMultiple() {
        em.persist(new Department());
        em.persist(new Department());

        assertTrue(dao.findFirst(allDepartmentsOrdered(), Project.id()).isDefined());
    }

    @Test
    public void findFirst_project_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.findFirst(query.all(Department.class), Project.id(), Order.by(Department_.name)).get());
        assertEquals(dep2.getId(), dao.findFirst(query.all(Department.class), Project.id(), Order.by(Department_.name).desc).get());
    }

    @Test
    public void getList_projection() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(dao.getMany(query.all(Department.class), Project.id())));
    }

    @Test
    public void getList_pagingWithListAttribute_projection() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals(newList(emp.getId()), dao.getMany(query.related(dep, Department_.employees), Project.id(), Page.of(0, 2)));
    }

    @Test
    public void getList_projection_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId(), dep2.getId()), dao.getMany(query.all(Department.class), Project.id(), Order.by(Department_.name)));
        assertEquals(newList(dep2.getId(), dep1.getId()), dao.getMany(query.all(Department.class), Project.id(), Order.by(Department_.name).desc));
    }

    @Test
    public void getList_projection_paged_ordered() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(newList(dep1.getId()), dao.getMany(query.all(Department.class), Project.id(), Page.FIRST.withSize(1), Order.by(Department_.name)));
        assertEquals(newList(dep2.getId()), dao.getMany(query.all(Department.class), Project.id(), Page.FIRST.withSize(1), Order.by(Department_.name).desc));
    }
    
    @Test
    public void related_query() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals(emp.getId(), dao.get(
                query.related(
                        query.single(dep.getId()),
                        Department_.employees), Project.<Employee>id()));
    }

    private CriteriaQuery<Department> allDepartmentsOrdered() {
        CriteriaQuery<Department> qOrdered = query.all(Department.class);
        return qOrdered.orderBy(em.getCriteriaBuilder().asc(qOrdered.getRoots().iterator().next().get("name")));
    }
}
