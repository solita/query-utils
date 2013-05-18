package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.query.*;
import fi.solita.utils.query.execution.JpaCriteriaQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.generation.Restrict;

public class RestrictTest extends QueryTestBase {

    @Autowired
    private Restrict restrict;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaCriteriaQueries dao;

    @Test
    public void innerJoin() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals(dep.getId(), dao.get(restrict.innerJoin(Department_.employees, query.all(Department.class))).getId());
    }

    @Test
    public void innerJoin_empty() {
        Department dep = new Department();
        em.persist(dep);

        assertFalse(dao.exists(restrict.innerJoin(Department_.employees, query.all(Department.class))));
    }

    @Test
    public void attributeEquals() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);
        em.persist(dep3);

        assertEquals("find single matching", dep1.getId(),
            dao.get(restrict.attributeEquals(Department_.name, Some("a"),
                        query.all(Department.class))).getId());
        assertEquals("find multiple matching", newSet(dep2.getId(), dep3.getId()), newSet(map(
            dao.getMany(restrict.attributeEquals(Department_.name, Some("b"),
                        query.all(Department.class))),
                        Department__.getId)));
        assertFalse("find by multiple restrictions",
            dao.exists(restrict.attributeEquals(Department_.name, Some("a"),
                       restrict.attributeEquals(Department_.name, Some("b"),
                        query.all(Department.class)))));
    }

    @Test
    public void attributeIn() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.get(restrict.attributeIn(Department_.name, newSet("a", "c"),
                            query.all(Department.class))).getId());
    }

    @Test
    public void attributeStartsWithIgnoreCase() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("ba");
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.get(restrict.attributeStartsWithIgnoreCase(Department_.name, "a",
                                            query.all(Department.class))).getId());
    }

    @Test
    public void exclude() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        em.persist(dep1);
        em.persist(dep2);

        assertEquals(dep1.getId(), dao.get(restrict.exclude(dep2.getId(), query.all(Department.class))).getId());
    }

    @Test
    public void exclude_multi() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Department dep3 = new Department();
        em.persist(dep1);
        em.persist(dep2);
        em.persist(dep3);

        assertEquals("exclude multiple", dep3.getId(), dao.get(restrict.exclude(newSet(dep1.getId(), dep2.getId()), query.all(Department.class))).getId());
    }

    @Test
    public void byType() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        em.persist(dep);
        em.persist(emp);
        em.persist(ptemp);

        assertEquals(newSet(emp.getId(), ptemp.getId()), newSet(map(dao.getMany(query.all(Employee.class)), Employee__.getId)));
        assertEquals(newSet(emp.getId())               , newSet(map(dao.getMany(restrict.byType(Employee.class, query.all(Employee.class))), Employee__.getId)));
        assertEquals(newSet(ptemp.getId())             , newSet(map(dao.getMany(restrict.byTypes(Collections.<Class<? extends Employee>>newSet(PartTimeEmployee.class), query.all(Employee.class))), Employee__.getId)));
    }

    @Test
    public void comparators_number() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        em.persist(dep1);
        em.persist(dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());
        Set<Department.ID> d2 = newSet(dep2.getId());
        Set<Department.ID> both = newSet(dep1.getId(), dep2.getId());
        Set<Department.ID> neither = newSet();

        assertEquals(both,    newSet(map(dao.getMany(query.all(Department.class)), Department__.getId)));

        assertEquals(both,    newSet(map(dao.getMany(restrict.greaterThanOrEqual(Department_.number, 1, query.all(Department.class))), Department__.getId)));
        assertEquals(d2,      newSet(map(dao.getMany(restrict.greaterThanOrEqual(Department_.number, 3, query.all(Department.class))), Department__.getId)));
        assertEquals(neither, newSet(map(dao.getMany(restrict.greaterThanOrEqual(Department_.number, 4, query.all(Department.class))), Department__.getId)));

        assertEquals(d2,      newSet(map(dao.getMany(restrict.greaterThan(Department_.number, 2, query.all(Department.class))), Department__.getId)));
        assertEquals(neither, newSet(map(dao.getMany(restrict.greaterThan(Department_.number, 3, query.all(Department.class))), Department__.getId)));

        assertEquals(d1,      newSet(map(dao.getMany(restrict.lessThanOrEqual(Department_.number, 1, query.all(Department.class))), Department__.getId)));
        assertEquals(both,    newSet(map(dao.getMany(restrict.lessThanOrEqual(Department_.number, 3, query.all(Department.class))), Department__.getId)));

        assertEquals(neither, newSet(map(dao.getMany(restrict.lessThan(Department_.number, 1, query.all(Department.class))), Department__.getId)));
        assertEquals(d1,      newSet(map(dao.getMany(restrict.lessThan(Department_.number, 2, query.all(Department.class))), Department__.getId)));
        assertEquals(both,    newSet(map(dao.getMany(restrict.lessThan(Department_.number, 4, query.all(Department.class))), Department__.getId)));
    }

    @Test
    public void comparators_numeric() throws Exception {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", new Money(3), dep);
        em.persist(dep);
        em.persist(emp1);
        em.persist(emp2);

        Set<Employee.ID> e1 = newSet(emp1.getId());
        Set<Employee.ID> e2 = newSet(emp2.getId());
        Set<Employee.ID> both = newSet(emp1.getId(), emp2.getId());
        Set<Employee.ID> neither = newSet();

        assertEquals(both,    newSet(map(dao.getMany(query.all(Employee.class)), Employee__.getId)));

        assertEquals(both,    newSet(map(dao.getMany(restrict.greaterThanOrEqual(Employee_.salary, new Money(1), query.all(Employee.class))), Employee__.getId)));
        assertEquals(e2,      newSet(map(dao.getMany(restrict.greaterThanOrEqual(Employee_.salary, new Money(3), query.all(Employee.class))), Employee__.getId)));
        assertEquals(neither, newSet(map(dao.getMany(restrict.greaterThanOrEqual(Employee_.salary, new Money(4), query.all(Employee.class))), Employee__.getId)));

        assertEquals(e2,      newSet(map(dao.getMany(restrict.greaterThan(Employee_.salary, new Money(2), query.all(Employee.class))), Employee__.getId)));
        assertEquals(neither, newSet(map(dao.getMany(restrict.greaterThan(Employee_.salary, new Money(3), query.all(Employee.class))), Employee__.getId)));

        assertEquals(e1,      newSet(map(dao.getMany(restrict.lessThanOrEqual(Employee_.salary, new Money(1), query.all(Employee.class))), Employee__.getId)));
        assertEquals(both,    newSet(map(dao.getMany(restrict.lessThanOrEqual(Employee_.salary, new Money(3), query.all(Employee.class))), Employee__.getId)));

        assertEquals(neither, newSet(map(dao.getMany(restrict.lessThan(Employee_.salary, new Money(1), query.all(Employee.class))), Employee__.getId)));
        assertEquals(e1,      newSet(map(dao.getMany(restrict.lessThan(Employee_.salary, new Money(2), query.all(Employee.class))), Employee__.getId)));
        assertEquals(both,    newSet(map(dao.getMany(restrict.lessThan(Employee_.salary, new Money(4), query.all(Employee.class))), Employee__.getId)));
    }
}
