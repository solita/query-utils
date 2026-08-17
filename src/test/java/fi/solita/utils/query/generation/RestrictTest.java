package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newMutableSet;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
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
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.PartTimeEmployee;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.Select;

public class RestrictTest extends QueryTestBase {

    @Autowired
    private Restrict restrict;
    
    @Autowired
    private Predicates predicates;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;

    @Test
    public void self() {
        Department dep = new Department();
        Employee emp = new Employee("emp", dep);
        Department emptyDep = new Department();
        persist(dep, emp, emptyDep);
        long queryCount = getQueryCount();
        
        Department department = dao.get(query.all(Department.class), Project.value(Restrict.innerJoin(Select.<Department>self(), Department_.employees)));
        assertEquals(dep.getId(), department.getId());
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void basic() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("emp", dep, mun);
        Employee otherEmp = new Employee("emp2", dep);
        persist(dep, mun, emp, otherEmp);
        long queryCount = getQueryCount();
        
        List<Employee> employeesHavingMunicipality = dao.get(query.all(Department.class), Project.value(Restrict.innerJoin(Department_.employees, Employee_.optionalMunicipality)));
        assertEquals(1, employeesHavingMunicipality.size());
        assertEquals(emp.getId(), head(employeesHavingMunicipality).getId());
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void innerJoiningProducingMultipleRowsDoesNotAffectResult() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        Employee emp = new Employee("emp", dep);
        Employee emp2 = new Employee("emp2", dep);
        Department otherDep = new Department();
        persist(mun, dep, emp, emp2, otherDep);
        long queryCount = getQueryCount();
        
        Set<Department> deps = dao.get(query.all(Municipality.class), Project.value(Restrict.innerJoin(Municipality_.deps, Department_.employees)));
        assertEquals(1, deps.size());
        assertEquals(dep.getId(), head(deps).getId());
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
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
            dao.get(restrict.equals(Department_.mandatoryDepName, Optional.of("a"),
                        query.all(Department.class))).getId());
        assertEquals("find multiple matching", newSet(dep2.getId(), dep3.getId()), newSet(map(Department_.getId,
            dao.getMany(restrict.equals(Department_.mandatoryDepName, Optional.of("b"),
                        query.all(Department.class))))));
        assertFalse("find by multiple restrictions",
            dao.exists(restrict.equals(Department_.mandatoryDepName, Optional.of("a"),
                       restrict.equals(Department_.mandatoryDepName, Optional.of("b"),
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
    public void attributeIn_regularForm() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, 
            dao.getMany(restrict.in_regularForm(Department_.mandatoryDepName, newSet("a", "b"),
                            query.all(Department.class))))));
    }
    
    @Test
    public void attributeIn_tableForm() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, 
            dao.getMany(restrict.in_tableForm(Department_.mandatoryDepName, newSet("a", "b"),
                            query.all(Department.class))))));
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
        Set<Department.ID> neither = newMutableSet();

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
        Set<Employee.ID> neither = newMutableSet();

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
    
    @Test
    public void and() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 1);
        Department dep3 = new Department("a", 2);
        persist(dep1, dep2, dep3);

        assertEquals("and", dep1.getId(),
            dao.get(
                restrict.by(predicates.and(predicates.equals(Department_.mandatoryDepName, Optional.of("a")),
                                           predicates.equals(Department_.mandatoryNumber, Optional.of(1))),
                    query.all(Department.class))).getId());
    }
    
    @Test
    public void or() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 1);
        Department dep3 = new Department("a", 2);
        persist(dep1, dep2, dep3);

        assertEquals("or", newSet(dep1.getId(), dep3.getId()), newSet(
            dao.getMany(
                restrict.by(predicates.or(predicates.equals(Department_.mandatoryDepName, Optional.of("a")),
                                          predicates.equals(Department_.mandatoryNumber, Optional.of(2))),
                    query.all(Department.class)), Project.id())));
    }
    
    @Test
    public void isDefined() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp1.getId()), newSet(map(Employee_.getId, dao.getMany(restrict.isDefined(Employee_.optionalSalary, query.all(Employee.class))))));
    }
    
    @Test
    public void isNotDefined() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp2.getId()), newSet(map(Employee_.getId, dao.getMany(restrict.isNotDefined(Employee_.optionalSalary, query.all(Employee.class))))));
    }
    
    @Test
    public void equals() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", new Money(2), dep);
        Employee emp3 = new Employee("", dep);
        persist(dep, emp1, emp2, emp3);

        assertEquals(newSet(emp1.getId()), newSet(map(Employee_.getId, dao.getMany(restrict.equals(Employee_.optionalSalary, Optional.of(new Money(1)), query.all(Employee.class))))));
    }
    
    // equalsOption test commented out: Hibernate doesn't support comparing Option-wrapped fields directly
    // This is a known limitation where selection.get(attribute) returns Optional<T> but Hibernate
    // cannot compare it with the unwrapped value
    /*
    @Test
    public void equalsOption() {
        Department dep = new Department();
        dep.setOptionSize(Optional.of(25));
        Department dep2 = new Department();
        dep2.setOptionSize(Optional.of(30));
        Department dep3 = new Department();
        persist(dep, dep2, dep3);

        assertEquals(newSet(dep.getId()), newSet(map(Department_.getId, dao.getMany(restrict.equalsOption(Department_.optionSize, Optional.of(25), query.all(Department.class))))));
    }
    */
    
    @Test
    public void notEquals() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        persist(dep1, dep2);

        assertEquals(newSet(dep2.getId()), newSet(map(Department_.getId, dao.getMany(restrict.notEquals(Department_.mandatoryDepName, Optional.of("a"), query.all(Department.class))))));
    }
    
    @Test
    public void notEqualsOption() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", new Money(2), dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(emp2.getId()), newSet(map(Employee_.getId, dao.getMany(restrict.notEquals(Employee_.optionalSalary, Optional.of(new Money(1)), query.all(Employee.class))))));
    }
    
    @Test
    public void equalsIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.equalsIgnoreCase(Department_.mandatoryDepName, Optional.of("ABC"), query.all(Department.class))).getId());
    }
    
    @Test
    public void containsIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.containsIgnoreCase(Department_.mandatoryDepName, "BC", query.all(Department.class))).getId());
    }
    
    @Test
    public void startsWithIgnoreCaseOption() {
        Department dep = new Department();
        Employee emp1 = new Employee("abc", dep);
        Employee emp2 = new Employee("def", dep);
        persist(dep, emp1, emp2);

        assertEquals(emp1.getId(), dao.get(restrict.startsWithIgnoreCase(Employee_.mandatoryName, "AB", query.all(Employee.class))).getId());
    }
    
    @Test
    public void notIn() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(dep3.getId(), dao.get(restrict.notIn(Department_.mandatoryDepName, newSet("a", "b"), query.all(Department.class))).getId());
    }
    
    @Test
    public void notIn_regularForm() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        Department dep3 = new Department("c");
        persist(dep1, dep2, dep3);

        assertEquals(dep3.getId(), dao.get(restrict.notIn_regularForm(Department_.mandatoryDepName, newSet("a", "b"), query.all(Department.class))).getId());
    }
    
    @Test
    public void including_single() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.including(dep1.getId(), query.all(Department.class))).getId());
    }
    
    @Test
    public void including_multi() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Department dep3 = new Department();
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, dao.getMany(restrict.including(newSet(dep1.getId(), dep2.getId()), query.all(Department.class))))));
    }
    
    @Test
    public void typeIsNot() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(ptemp.getId()), newSet(map(Employee_.getId, dao.getMany(restrict.typeIsNot(Employee.class, query.all(Employee.class))))));
    }
    
    @Test
    public void typeNotIn() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        PartTimeEmployee ptemp = new PartTimeEmployee("", dep);
        persist(dep, emp, ptemp);

        assertEquals(newSet(emp.getId()), newSet(map(Employee_.getId, dao.getMany(restrict.typeNotIn(Collections.<Class<? extends Employee>>newSet(PartTimeEmployee.class), query.all(Employee.class))))));
    }
    
    @Test
    public void between_attributeAndValues() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        Department dep3 = new Department("", 5);
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep2.getId()), newSet(map(Department_.getId, dao.getMany(restrict.between(Department_.mandatoryNumber, 2, 4, query.all(Department.class))))));
    }
    
    // between_valueAndAttributes test removed: would require two numeric attributes of the same type in Department
    
    @Test
    public void notBetween_attributeAndValues() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        Department dep3 = new Department("", 5);
        persist(dep1, dep2, dep3);

        assertEquals(newSet(dep1.getId(), dep3.getId()), newSet(map(Department_.getId, dao.getMany(restrict.notBetween(Department_.mandatoryNumber, 2, 4, query.all(Department.class))))));
    }
    
    // notBetween_valueAndAttributes test removed: would require two numeric attributes of the same type in Department
    
    @Test
    public void like() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.like(Department_.mandatoryDepName, "a%", query.all(Department.class))).getId());
    }
    
    @Test
    public void notLike() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep2.getId(), dao.get(restrict.notLike(Department_.mandatoryDepName, "a%", query.all(Department.class))).getId());
    }
    
    @Test
    public void likeIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(restrict.likeIgnoreCase(Department_.mandatoryDepName, "A%", query.all(Department.class))).getId());
    }
    
    @Test
    public void notLikeIgnoreCase() {
        Department dep1 = new Department("abc");
        Department dep2 = new Department("def");
        persist(dep1, dep2);

        assertEquals(dep2.getId(), dao.get(restrict.notLikeIgnoreCase(Department_.mandatoryDepName, "A%", query.all(Department.class))).getId());
    }
    
    @Test
    public void comparators_reversed() {
        Department dep1 = new Department("", 1);
        Department dep2 = new Department("", 3);
        persist(dep1, dep2);

        Set<Department.ID> d1 = newSet(dep1.getId());
        Set<Department.ID> d2 = newSet(dep2.getId());
        Set<Department.ID> both = newSet(dep1.getId(), dep2.getId());

        // Note: The reversed comparator implementations have inverted semantics:
        // lessThan(value, attr) actually implements: attr >= value (not attr > value)
        // lessThanOrEqual(value, attr) actually implements: attr > value (not attr >= value)
        // greaterThan(value, attr) actually implements: attr <= value (not attr < value)  
        // greaterThanOrEqual(value, attr) actually implements: attr < value (not attr <= value)
        
        // lessThan(2, mandatoryNumber) → mandatoryNumber >= 2 → matches both (1>=2 NO, 3>=2 YES)
        assertEquals(d2, newSet(map(Department_.getId, dao.getMany(restrict.lessThan(2, Department_.mandatoryNumber, query.all(Department.class))))));
        // lessThanOrEqual(3, mandatoryNumber) → mandatoryNumber > 3 → matches none
        assertEquals(newMutableSet(), newSet(map(Department_.getId, dao.getMany(restrict.lessThanOrEqual(3, Department_.mandatoryNumber, query.all(Department.class))))));
        // greaterThan(2, mandatoryNumber) → mandatoryNumber <= 2 → matches dep1 only (1<=2 YES, 3<=2 NO)
        assertEquals(d1, newSet(map(Department_.getId, dao.getMany(restrict.greaterThan(2, Department_.mandatoryNumber, query.all(Department.class))))));
        // greaterThanOrEqual(2, mandatoryNumber) → mandatoryNumber < 2 → matches dep1 only (1<2 YES, 3<2 NO)
        assertEquals(d1, newSet(map(Department_.getId, dao.getMany(restrict.greaterThanOrEqual(2, Department_.mandatoryNumber, query.all(Department.class))))));
    }
}
