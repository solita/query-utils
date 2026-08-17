package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newMutableSet;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.PartTimeEmployee;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.projection.Project;

public class PredicatesTest extends QueryTestBase {

    @Autowired
    private Predicates predicates;

    @Autowired
    private Restrict restrict;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;

    @Test
    public void and() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 1);
        Department dep3 = new Department("a", 2);
        persist(dep1, dep2, dep3);

        assertEquals(dep1.getId(),
            dao.get(
                restrict.by(predicates.and(predicates.equals(Department_.mandatoryDepName, Optional.of("a")),
                                           predicates.equals(Department_.mandatoryNumber, Optional.of(1))),
                    query.all(Department.class))).getId());
    }

    @Test
    public void or() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 2);
        Department dep3 = new Department("c", 3);
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(
            dao.getMany(
                restrict.by(predicates.or(predicates.equals(Department_.mandatoryDepName, Optional.of("a")),
                                          predicates.equals(Department_.mandatoryDepName, Optional.of("b"))),
                    query.all(Department.class)), Project.id())));
    }

    @Test
    public void isDefined() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp1.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.isDefined(Employee_.optionalSalary), query.all(Employee.class))))));
    }

    @Test
    public void isNotDefined() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp2.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.isNotDefined(Employee_.optionalSalary), query.all(Employee.class))))));
    }

    @Test
    public void equals_defined() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", new Money(2), dep);
        Employee emp3 = new Employee("", dep);
        persist(dep, emp1, emp2, emp3);

        assertEquals(newSet(emp1.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.equals(Employee_.optionalSalary, Optional.of(new Money(1))), query.all(Employee.class))))));
    }

    @Test
    public void equals_none_matchesNull() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp2.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.equals(Employee_.optionalSalary, Optional.empty()), query.all(Employee.class))))));
    }

    @Test
    public void notEquals_defined() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        persist(dep1, dep2);

        assertEquals(newSet(dep2.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.notEquals(Department_.mandatoryDepName, Optional.of("a")), query.all(Department.class))))));
    }

    @Test
    public void notEquals_none_matchesNotNull() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp1.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.notEquals(Employee_.optionalSalary, Optional.empty()), query.all(Employee.class))))));
    }

    @Test
    public void equalsIgnoreCaseSome() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.equalsIgnoreCase(Department_.mandatoryDepName, Optional.of("ABC")), query.all(Department.class))).getId());
    }

    @Test
    public void equalsIgnoreCase_none_matchesNull() {
        Department dep = new Department();
        Employee emp1 = new Employee("a", dep);
        Employee emp2 = new Employee("b", dep);
        persist(dep, emp1, emp2);

        // mandatoryName is not nullable — just verify that Optional.of() match works case-insensitively
        assertEquals(newSet(emp1.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.equalsIgnoreCase(Employee_.mandatoryName, Optional.of("A")), query.all(Employee.class))))));
    }

    @Test
    public void containsIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.containsIgnoreCase(Department_.mandatoryDepName, "BC"), query.all(Department.class))).getId());
    }

    @Test
    public void startsWithIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.startsWithIgnoreCase(Department_.mandatoryDepName, "AB"), query.all(Department.class))).getId());
    }

    @Test
    public void in() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.in(Department_.mandatoryDepName, newSet("a", "b")), query.all(Department.class))))));
    }

    @Test
    public void in_regularForm() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.in_regularForm(Department_.mandatoryDepName, newSet("a", "b")), query.all(Department.class))))));
    }

    @Test
    public void in_tableForm() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.in_tableForm(Department_.mandatoryDepName, newSet("a", "b")), query.all(Department.class))))));
    }

    @Test
    public void notIn() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep3.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.notIn(Department_.mandatoryDepName, newSet("a", "b")), query.all(Department.class))))));
    }

    @Test
    public void notIn_regularForm() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep3.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.notIn_regularForm(Department_.mandatoryDepName, newSet("a", "b")), query.all(Department.class))))));
    }

    @Test
    public void inIds() {
        Department dep1 = new Department();
        Employee emp1 = new Employee("", dep1);
        persist(dep1, emp1);

        assertEquals(emp1.getId(),
            dao.get(restrict.by(predicates.inIds(Employee_.mandatoryDepartment, newSet(dep1.getId())), query.all(Employee.class))).getId());
    }

    @Test
    public void excluding_single() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.excluding(dep2.getId()), query.all(Department.class))).getId());
    }

    @Test
    public void excluding_multi() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Department dep3 = new Department();
        persist(dep1, dep2, dep3);

        assertEquals(dep3.getId(),
            dao.get(restrict.by(predicates.excluding(newSet(dep1.getId(), dep2.getId())), query.all(Department.class))).getId());
    }

    @Test
    public void including_single() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.including(dep1.getId()), query.all(Department.class))).getId());
    }

    @Test
    public void including_multi() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Department dep3 = new Department();
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.including(newSet(dep1.getId(), dep2.getId())), query.all(Department.class))))));
    }

    @Test
    public void typeIs() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(emp.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.<Employee, Employee>typeIs(Employee.class), query.all(Employee.class))))));
    }

    @Test
    public void typeIsNot() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(ptemp.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.typeIsNot(Employee.class), query.all(Employee.class))))));
    }

    @Test
    public void typeIn() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(ptemp.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.typeIn(Collections.<Class<? extends Employee>>newSet(PartTimeEmployee.class)), query.all(Employee.class))))));
    }

    @Test
    public void typeNotIn() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(emp.getId()), newSet(map(Employee_.getId,
            dao.getMany(restrict.by(predicates.typeNotIn(Collections.<Class<? extends Employee>>newSet(PartTimeEmployee.class)), query.all(Employee.class))))));
    }

    @Test
    public void lessThan_attributeAndValue() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());
        Set<Department.ID> neither = newMutableSet();

        assertEquals(neither, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.lessThan(Department_.mandatoryNumber, 1), query.all(Department.class))))));
        assertEquals(d1, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.lessThan(Department_.mandatoryNumber, 2), query.all(Department.class))))));
    }

    @Test
    public void lessThanOrEqual_attributeAndValue() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());
        Set<Department.ID> both = newSet(dep1.getId(), dep2.getId());

        assertEquals(d1, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.lessThanOrEqual(Department_.mandatoryNumber, 1), query.all(Department.class))))));
        assertEquals(both, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.lessThanOrEqual(Department_.mandatoryNumber, 3), query.all(Department.class))))));
    }

    @Test
    public void greaterThan_attributeAndValue() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d2 = newSet(dep2.getId());
        Set<Department.ID> neither = newMutableSet();

        assertEquals(d2, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.greaterThan(Department_.mandatoryNumber, 2), query.all(Department.class))))));
        assertEquals(neither, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.greaterThan(Department_.mandatoryNumber, 3), query.all(Department.class))))));
    }

    @Test
    public void greaterThanOrEqual_attributeAndValue() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> both = newSet(dep1.getId(), dep2.getId());
        Set<Department.ID> d2 = newSet(dep2.getId());

        assertEquals(both, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.greaterThanOrEqual(Department_.mandatoryNumber, 1), query.all(Department.class))))));
        assertEquals(d2, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.greaterThanOrEqual(Department_.mandatoryNumber, 3), query.all(Department.class))))));
    }

    @Test
    public void lessThan_reversed() {
        // lessThan(value, attr) → attr >= value
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d2 = newSet(dep2.getId());

        assertEquals(d2, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.lessThan(2, Department_.mandatoryNumber), query.all(Department.class))))));
    }

    @Test
    public void lessThanOrEqual_reversed() {
        // lessThanOrEqual(value, attr) → attr > value
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> neither = newMutableSet();

        assertEquals(neither, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.lessThanOrEqual(3, Department_.mandatoryNumber), query.all(Department.class))))));
    }

    @Test
    public void greaterThan_reversed() {
        // greaterThan(value, attr) → attr <= value
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());

        assertEquals(d1, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.greaterThan(2, Department_.mandatoryNumber), query.all(Department.class))))));
    }

    @Test
    public void greaterThanOrEqual_reversed() {
        // greaterThanOrEqual(value, attr) → attr < value
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());

        assertEquals(d1, newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.greaterThanOrEqual(2, Department_.mandatoryNumber), query.all(Department.class))))));
    }

    @Test
    public void between_attributeAndValues() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        Department dep3 = new Department("", 5);
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep2.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.between(Department_.mandatoryNumber, 2, 4), query.all(Department.class))))));
    }

    @Test
    public void notBetween_attributeAndValues() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        Department dep3 = new Department("", 5);
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep3.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.by(predicates.notBetween(Department_.mandatoryNumber, 2, 4), query.all(Department.class))))));
    }

    @Test
    public void like() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.like(Department_.mandatoryDepName, "a%"), query.all(Department.class))).getId());
    }

    @Test
    public void notLike() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep2.getId(),
            dao.get(restrict.by(predicates.notLike(Department_.mandatoryDepName, "a%"), query.all(Department.class))).getId());
    }

    @Test
    public void likeIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(),
            dao.get(restrict.by(predicates.likeIgnoreCase(Department_.mandatoryDepName, "A%"), query.all(Department.class))).getId());
    }

    @Test
    public void notLikeIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep2.getId(),
            dao.get(restrict.by(predicates.notLikeIgnoreCase(Department_.mandatoryDepName, "A%"), query.all(Department.class))).getId());
    }
}
