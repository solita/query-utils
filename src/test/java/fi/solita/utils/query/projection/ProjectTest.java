package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class ProjectTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;

    @Test
    public void option_get() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(1), dep);
        em.persist(dep);
        em.persist(emp);

        Option<Money> ret = dao.get(query.all(Employee.class), Project.value(Cast.optional(Employee_.salary)));
        assertEquals(Some(new Money(1)), ret);
    }

    @Test
    public void option_get_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        Option<Money> ret = dao.get(query.all(Employee.class), Project.value(Cast.optional(Employee_.salary)));
        assertEquals(None(), ret);
    }

    @Test
    public void option_find() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(1), dep);
        em.persist(dep);
        em.persist(emp);

        Option<Option<Money>> ret = dao.find(query.all(Employee.class), Project.value(Cast.optional(Employee_.salary)));
        assertEquals("should be wrapped inside additional option", Some(Some(new Money(1))), ret);
    }

    @Test
    public void option_find_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        Option<Option<Money>> ret = dao.find(query.all(Employee.class), Project.value(Cast.optional(Employee_.salary)));
        assertEquals("should be wrapped inside additional option", Some(None()), ret);
    }

    @Test
    public void option_findFirst() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(1), dep);
        em.persist(dep);
        em.persist(emp);

        Option<Option<Money>> ret = dao.findFirst(query.related(dep, Department_.employees), Project.value(Cast.optional(Employee_.salary)));
        assertEquals(Some(Some(new Money(1))), ret);
    }

    @Test
    public void option_findFirst_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals(Some(None()), dao.findFirst(query.related(dep, Department_.employees), Project.value(Cast.optional(Employee_.salary))));
    }

    @Test
    public void option_getList() {
        Department dep = new Department();
        Employee emp1 = new Employee("", new Money(1), dep);
        Employee emp2 = new Employee("", dep);
        em.persist(dep);
        em.persist(emp1);
        em.persist(emp2);

        assertEquals(newSet(Some(new Money(1)), Option.<Money>None()), newSet(dao.getMany(query.all(Employee.class), Project.value(Cast.optional(Employee_.salary)))));
    }

    @Test
    public void pair() {
        Department dep = new Department();
        Employee emp = new Employee("foo", new Money(1), dep);
        em.persist(dep);
        em.persist(emp);

        Pair<Option<Money>, String> pair = dao.get(query.all(Employee.class), Project.pair(Cast.optional(Employee_.salary), Employee_.name));
        assertEquals(Pair.of(Some(new Money(1)), "foo"), pair);
    }

    @Test
    public void tuple3() {
        Department dep = new Department();
        Employee emp = new Employee("foo", new Money(1), dep);
        em.persist(dep);
        em.persist(emp);

        Tuple3<Option<Money>, String, Option<Money>> tuple = dao.get(query.all(Employee.class), Project.tuple(Cast.optional(Employee_.salary), Employee_.name, Cast.optional(Employee_.salary)));
        assertEquals(Pair.of(Some(new Money(1)), "foo", Some(new Money(1))), tuple);
    }

    @Test
    public void id() {
        Department dep = new Department();
        em.persist(dep);

        assertEquals(dep.getId(), dao.get(query.all(Department.class), Project.id()));
    }

    @Test
    public void value() {
        Department dep = new Department("foo");
        em.persist(dep);

        assertEquals("foo", dao.get(query.all(Department.class), Project.value(Department_.name)));
    }

    @Test
    public void relation_value() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);

        assertEquals("foo", dao.get(query.related(query.all(Department.class), Department_.employees), Project.<Employee,String>value(Employee_.name)));
    }
}
