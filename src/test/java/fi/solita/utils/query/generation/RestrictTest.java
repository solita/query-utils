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
    private Dao dao;

    @Test
    public void innerJoin() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);

        assertEquals(dep.getId(), dao.get(restrict.innerJoin(Department_.employees, query.all(Department.class))).getId());
    }

    @Test
    public void innerJoin_empty() {
        Department dep = new Department();
        persist(dep);

        assertFalse(dao.exists(restrict.innerJoin(Department_.employees, query.all(Department.class))));
    }

    @Test
    public void attributeEquals() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("b");
        persist(dep1, dep2, dep3);

        assertEquals("find single matching", dep1.getId(),
            dao.get(restrict.equals(Department_.mandatoryDepName, Some("a"),
                        query.all(Department.class))).getId());
        assertEquals("find multiple matching", newSet(dep2.getId(), dep3.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.equals(Department_.mandatoryDepName, Some("b"),
                        query.all(Department.class))))));
        assertFalse("find by multiple restrictions",
            dao.exists(restrict.equals(Department_.mandatoryDepName, Some("a"),
                       restrict.equals(Department_.mandatoryDepName, Some("b"),
                        query.all(Department.class)))));
    }

    @Test
    public void attributeIn() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.in(Department_.mandatoryDepName, newSet("a", "c"),
                            query.all(Department.class))).getId());
    }
    
    @Test
    public void attributeInIds() {
        Department dep1 = new Department("a");
        Employee emp1 = new Employee("c", dep1);
        persist(dep1, emp1);

        assertEquals(emp1.getId(), dao.get(restrict.inIds(Employee_.mandatoryDepartment, newSet(dep1.getId()),
                            query.all(Employee.class))).getId());
    }

    @Test
    public void attributeStartsWithIgnoreCase() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("ba");
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.startsWithIgnoreCase(Department_.mandatoryDepName, "a",
                                            query.all(Department.class))).getId());
    }

    @Test
    public void exclude() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.excluding(dep2.getId(), query.all(Department.class))).getId());
    }

    @Test
    public void exclude_multi() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Department dep3 = new Department();
        persist(dep1, dep2, dep3);

        assertEquals("exclude multiple", dep3.getId(), dao.get(restrict.excluding(newSet(dep1.getId(), dep2.getId()), query.all(Department.class))).getId());
    }

    @Test
    public void byType() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(emp.getId(), ptemp.getId()), newSet(map(Employee_.getId, dao.getMany(query.all(Employee.class)))));
        assertEquals(newSet(emp.getId())               , newSet(map(Employee_.getId, dao.getMany(restrict.typeIs(Employee.class, query.all(Employee.class))))));
        assertEquals(newSet(ptemp.getId())             , newSet(map(Employee_.getId, dao.getMany(restrict.typeIn(Collections.<Class<? extends Employee>>newSet(PartTimeEmployee.class), query.all(Employee.class))))));
    }

    @Test
    public void comparators_number() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());
        Set<Department.ID> d2 = newSet(dep2.getId());
        Set<Department.ID> both = newSet(dep1.getId(), dep2.getId());
        Set<Department.ID> neither = newSet();

        assertEquals(both,    newSet(map(Department_.getId, dao.getMany(query.all(Department.class)))));

        assertEquals(both,    newSet(map(Department_.getId, dao.getMany(restrict.greaterThanOrEqual(Department_.mandatoryNumber, 1, query.all(Department.class))))));
        assertEquals(d2,      newSet(map(Department_.getId, dao.getMany(restrict.greaterThanOrEqual(Department_.mandatoryNumber, 3, query.all(Department.class))))));
        assertEquals(neither, newSet(map(Department_.getId, dao.getMany(restrict.greaterThanOrEqual(Department_.mandatoryNumber, 4, query.all(Department.class))))));

        assertEquals(d2,      newSet(map(Department_.getId, dao.getMany(restrict.greaterThan(Department_.mandatoryNumber, 2, query.all(Department.class))))));
        assertEquals(neither, newSet(map(Department_.getId, dao.getMany(restrict.greaterThan(Department_.mandatoryNumber, 3, query.all(Department.class))))));

        assertEquals(d1,      newSet(map(Department_.getId, dao.getMany(restrict.lessThanOrEqual(Department_.mandatoryNumber, 1, query.all(Department.class))))));
        assertEquals(both,    newSet(map(Department_.getId, dao.getMany(restrict.lessThanOrEqual(Department_.mandatoryNumber, 3, query.all(Department.class))))));

        assertEquals(neither, newSet(map(Department_.getId, dao.getMany(restrict.lessThan(Department_.mandatoryNumber, 1, query.all(Department.class))))));
        assertEquals(d1,      newSet(map(Department_.getId, dao.getMany(restrict.lessThan(Department_.mandatoryNumber, 2, query.all(Department.class))))));
        assertEquals(both,    newSet(map(Department_.getId, dao.getMany(restrict.lessThan(Department_.mandatoryNumber, 4, query.all(Department.class))))));
    }

    @Test
    public void comparators_numeric() throws Exception {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", new Money(3), dep);
        persist(dep, emp1, emp2);

        Set<Employee.ID> e1 = newSet(emp1.getId());
        Set<Employee.ID> e2 = newSet(emp2.getId());
        Set<Employee.ID> both = newSet(emp1.getId(), emp2.getId());
        Set<Employee.ID> neither = newSet();

        assertEquals(both,    newSet(map(Employee_.getId, dao.getMany(query.all(Employee.class)))));

        assertEquals(both,    newSet(map(Employee_.getId, dao.getMany(restrict.greaterThanOrEqual(Employee_.optionalSalary, new Money(1), query.all(Employee.class))))));
        assertEquals(e2,      newSet(map(Employee_.getId, dao.getMany(restrict.greaterThanOrEqual(Employee_.optionalSalary, new Money(3), query.all(Employee.class))))));
        assertEquals(neither, newSet(map(Employee_.getId, dao.getMany(restrict.greaterThanOrEqual(Employee_.optionalSalary, new Money(4), query.all(Employee.class))))));

        assertEquals(e2,      newSet(map(Employee_.getId, dao.getMany(restrict.greaterThan(Employee_.optionalSalary, new Money(2), query.all(Employee.class))))));
        assertEquals(neither, newSet(map(Employee_.getId, dao.getMany(restrict.greaterThan(Employee_.optionalSalary, new Money(3), query.all(Employee.class))))));

        assertEquals(e1,      newSet(map(Employee_.getId, dao.getMany(restrict.lessThanOrEqual(Employee_.optionalSalary, new Money(1), query.all(Employee.class))))));
        assertEquals(both,    newSet(map(Employee_.getId, dao.getMany(restrict.lessThanOrEqual(Employee_.optionalSalary, new Money(3), query.all(Employee.class))))));

        assertEquals(neither, newSet(map(Employee_.getId, dao.getMany(restrict.lessThan(Employee_.optionalSalary, new Money(1), query.all(Employee.class))))));
        assertEquals(e1,      newSet(map(Employee_.getId, dao.getMany(restrict.lessThan(Employee_.optionalSalary, new Money(2), query.all(Employee.class))))));
        assertEquals(both,    newSet(map(Employee_.getId, dao.getMany(restrict.lessThan(Employee_.optionalSalary, new Money(4), query.all(Employee.class))))));
    }
}
