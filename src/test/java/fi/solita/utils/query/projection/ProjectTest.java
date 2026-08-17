package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newSet;
import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class ProjectTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;

    @Test
    public void option_get() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(1), dep);
        persist(dep, emp);

        Optional<Money> ret = dao.get(query.all(Employee.class), Project.value(Cast.optional(Employee_.optionalSalary)));
        assertEquals(Optional.of(new Money(1)), ret);
    }

    @Test
    public void option_getNone() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);

        Optional<Money> ret = dao.get(query.all(Employee.class), Project.value(Cast.optional(Employee_.optionalSalary)));
        assertEquals(Optional.empty(), ret);
    }

    @Test
    public void option_find() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(1), dep);
        persist(dep, emp);

        Optional<Optional<Money>> ret = dao.find(query.all(Employee.class), Project.value(Cast.optional(Employee_.optionalSalary)));
        assertEquals("should be wrapped inside additional option", Optional.of(Optional.of(new Money(1))), ret);
    }

    @Test
    public void option_findNone() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);

        Optional<Optional<Money>> ret = dao.find(query.all(Employee.class), Project.value(Cast.optional(Employee_.optionalSalary)));
        assertEquals("should be wrapped inside additional option", Optional.of(Optional.empty()), ret);
    }

    @Test
    public void option_findFirst() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(1), dep);
        persist(dep, emp);

        Optional<Optional<Money>> ret = dao.findFirst(query.related(dep, Department_.employees), Project.value(Cast.optional(Employee_.optionalSalary)));
        assertEquals(Optional.of(Optional.of(new Money(1))), ret);
    }

    @Test
    public void option_findFirstNone() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);

        assertEquals(Optional.of(Optional.empty()), dao.findFirst(query.related(dep, Department_.employees), Project.value(Cast.optional(Employee_.optionalSalary))));
    }

    @Test
    public void option_getList() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);

        assertEquals(newSet(Optional.of(new Money(1)), Optional.empty()), newSet(dao.getMany(query.all(Employee.class), Project.value(Cast.optional(Employee_.optionalSalary)))));
    }

    @Test
    public void pair() {
        Department dep = new Department();
        Employee emp = new Employee("foo", new Money(1), dep);
        persist(dep, emp);

        Pair<Optional<Money>, String> pair = dao.get(query.all(Employee.class), Project.pair(Cast.optional(Employee_.optionalSalary), Employee_.mandatoryName));
        assertEquals(Pair.of(Optional.of(new Money(1)), "foo"), pair);
    }

    @Test
    public void tuple3() {
        Department dep = new Department();
        Employee emp = new Employee("foo", new Money(1), dep);
        persist(dep, emp);

        Tuple3<Optional<Money>, String, Optional<Money>> tuple = dao.get(query.all(Employee.class), Project.tuple(Cast.optional(Employee_.optionalSalary), Employee_.mandatoryName, Cast.optional(Employee_.optionalSalary)));
        assertEquals(Pair.of(Optional.of(new Money(1)), "foo", Optional.of(new Money(1))), tuple);
    }

    @Test
    public void id() {
        Department dep = new Department();
        persist(dep);

        assertEquals(dep.getId(), dao.get(query.all(Department.class), Project.id()));
    }

    @Test
    public void value() {
        Department dep = new Department("foo");
        persist(dep);

        assertEquals("foo", dao.get(query.all(Department.class), Project.value(Department_.mandatoryDepName)));
    }
    
    @Test
    public void value_entity() {
        Department dep = new Department();
        Employee emp = new Employee("foo", new Money(1), dep);
        persist(dep, emp);

        assertEquals(dep.getId(), dao.get(query.all(Employee.class), Project.value(Employee_.mandatoryDepartment)).getId());
    }

    @Test
    public void relation_value() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);

        assertEquals("foo", dao.get(query.related(Department_.employees, query.all(Department.class)), Project.<Employee,String>value(Employee_.mandatoryName)));
    }
    
    @Test
    public void max() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 2);
        persist(dep1, dep2);

        assertEquals(Optional.of(2), dao.get(query.all(Department.class), Project.max(Department_.mandatoryNumber)));
    }
    
    @Test
    public void max_empty() {
        assertEquals(Optional.empty(), dao.get(query.all(Department.class), Project.max(Department_.mandatoryNumber)));
    }
    
    @Test
    public void min() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 2);
        persist(dep1, dep2);

        assertEquals(Optional.of(1), dao.get(query.all(Department.class), Project.min(Department_.mandatoryNumber)));
    }
    
    @Test
    public void min_empty() {
        assertEquals(Optional.empty(), dao.get(query.all(Department.class), Project.min(Department_.mandatoryNumber)));
    }

    @Test
    public void sum() {
        Department dep1 = new Department("a", 1);
        Department dep2 = new Department("b", 2);
        persist(dep1, dep2);

        assertEquals(Optional.of(3), dao.get(query.all(Department.class), Project.sum(Department_.mandatoryNumber)));
    }

    @Test
    public void sum_empty() {
        assertEquals(Optional.empty(), dao.get(query.all(Department.class), Project.sum(Department_.mandatoryNumber)));
    }

    @Test
    public void greatest() {
        Department dep1 = new Department("aaa");
        Department dep2 = new Department("bbb");
        persist(dep1, dep2);

        assertEquals(Optional.of("bbb"), dao.get(query.all(Department.class), Project.greatest(Department_.mandatoryDepName)));
    }

    @Test
    public void greatest_empty() {
        assertEquals(Optional.empty(), dao.get(query.all(Department.class), Project.greatest(Department_.mandatoryDepName)));
    }

    @Test
    public void least() {
        Department dep1 = new Department("aaa");
        Department dep2 = new Department("bbb");
        persist(dep1, dep2);

        assertEquals(Optional.of("aaa"), dao.get(query.all(Department.class), Project.least(Department_.mandatoryDepName)));
    }

    @Test
    public void least_empty() {
        assertEquals(Optional.empty(), dao.get(query.all(Department.class), Project.least(Department_.mandatoryDepName)));
    }

    @Test
    public void tuple1() {
        Department dep = new Department("foo");
        persist(dep);

        String val = dao.get(query.all(Department.class), Project.value(Department_.mandatoryDepName));
        assertEquals("foo", val);
    }

    @Test
    public void tuple2() {
        Department dep = new Department("foo", 42);
        persist(dep);

        Tuple2<String, Integer> tuple = dao.get(query.all(Department.class), Project.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber));
        assertEquals(Pair.of("foo", 42), tuple);
    }

    @Test
    public void tuple4() {
        Department dep = new Department("dep");
        Employee emp = new Employee("bar", new Money(2), dep);
        persist(dep, emp);

        Tuple4<Optional<Money>, String, Optional<Money>, String> tuple = dao.get(
            query.all(Employee.class),
            Project.tuple(Cast.optional(Employee_.optionalSalary), Employee_.mandatoryName, Cast.optional(Employee_.optionalSalary), Employee_.mandatoryName));
        assertEquals(Pair.of(Optional.of(new Money(2)), "bar", Optional.of(new Money(2)), "bar"), tuple);
    }

    @Test
    public void value_plural() {
        Department dep = new Department(java.util.Arrays.asList(1, 2, 3));
        persist(dep);

        java.util.List<Integer> numbers = dao.get(query.all(Department.class), Project.value(Department_.numbers));
        assertEquals(java.util.Arrays.asList(1, 2, 3), numbers);
    }
}
