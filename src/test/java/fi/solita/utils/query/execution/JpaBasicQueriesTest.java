package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.range;
import static fi.solita.utils.functional.Option.None;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.PartTimeEmployee;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.QueryUtils.OptionalAttributeNeedOptionTypeException;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import jakarta.persistence.EntityNotFoundException;

public class JpaBasicQueriesTest extends QueryTestBase {

    @Autowired
    private JpaBasicQueries dao;

    @Autowired
    private JpaCriteriaQuery query;

    @Test
    public void persist() {
        Id<Department> id = dao.persist(new Department());
        assertTrue(dao.find(id).isDefined());
    }

    @Test
    public void isManaged() {
        Department dep1 = new Department();
        persist(dep1);
        Department dep2 = new Department();

        assertTrue(dao.isManaged(dao.get(dep1.getId())));
        assertFalse(dao.isManaged(dep2));
    }

    @Test
    public void remove() {
        Department dep = new Department();
        persist(dep);

        assertTrue(dao.find(dep.getId()).isDefined());
        dao.remove(dep.getId());
        assertFalse(dao.find(dep.getId()).isDefined());
    }

    @Test
    public void removeAll() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        dao.removeAll(query.single(dep1.getId()));
        em.flush();
        em.clear();

        assertFalse(dao.find(dep1.getId()).isDefined());
        assertTrue(dao.find(dep2.getId()).isDefined());
    }
    
    @Test
    public void removeAll_over1000() {
        Department dep = new Department();
        persist(dep);
        for (@SuppressWarnings("unused") int i: range(1, 1001)) {
            persist(new Department());
        }

        dao.removeAll(query.all(Department.class));
        em.flush();
        em.clear();

        assertFalse(dao.find(dep.getId()).isDefined());
    }

    @Test
    public void get() {
        Department dep = new Department();
        persist(dep);
        em.flush();
        em.clear();

        Department notProxy = dao.get(dep.getId());
        assertTrue(notProxy.getClass().equals(Department.class));
        assertTrue(Hibernate.isInitialized(notProxy));
        assertEquals(dep.getId(), notProxy.getId());
    }

    @Test
    public void getProxy() {
        Department dep = new Department();
        persist(dep);
        em.flush();
        em.clear();

        Department proxy = dao.toProxy(dep.getId());
        assertFalse(Hibernate.isInitialized(proxy));
        assertEquals(dep.getId(), proxy.getId());
    }
    
    @Test
    public void getProxyFromEntity() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        em.flush();
        em.clear();

        Employee v = dao.get(emp.getId());
        long fetched = em.unwrap(Session.class).getSessionFactory().getStatistics().getEntityFetchCount();
        Department proxy = dao.toProxy(v, Employee_.mandatoryDepartment);
        assertEquals(fetched, em.unwrap(Session.class).getSessionFactory().getStatistics().getEntityFetchCount());
        assertFalse(Hibernate.isInitialized(proxy));
    }
    
    @Test
    public void getProxyFromEntity_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        em.flush();
        em.clear();

        Employee v = dao.get(emp.getId());
        long fetched = em.unwrap(Session.class).getSessionFactory().getStatistics().getEntityFetchCount();
        Option<Municipality> proxy = dao.toProxy(v, Cast.optional(Employee_.optionalMunicipality));
        assertEquals(fetched, em.unwrap(Session.class).getSessionFactory().getStatistics().getEntityFetchCount());
        assertEquals(None(), proxy);
    }
    @Test
    public void getProxyFromEntity_some() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        em.flush();
        em.clear();

        Employee v = dao.get(emp.getId());
        long fetched = em.unwrap(Session.class).getSessionFactory().getStatistics().getEntityFetchCount();
        Option<Municipality> proxy = dao.toProxy(v, Cast.optional(Employee_.optionalMunicipality));
        assertEquals(fetched, em.unwrap(Session.class).getSessionFactory().getStatistics().getEntityFetchCount());
        assertFalse(Hibernate.isInitialized(proxy.get()));
    }
    
    @Test(expected = OptionalAttributeNeedOptionTypeException.class)
    public void getProxyFromEntity_failsIfMissingOptional() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        em.flush();
        em.clear();

        Employee v = dao.get(emp.getId());
        dao.toProxy(v, Employee_.optionalMunicipality);
    }
    
    @Test
    public void getProxyFromEntity_succeedsIfAdditionalOptional() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        em.flush();
        em.clear();

        Employee v = dao.get(emp.getId());
        dao.toProxy(v, Cast.optional(Employee_.mandatoryDepartment));
    }
    
    @Test
    public void getProxies_List() {
        Department dep = new Department();
        Employee emp = new Employee("a", dep);
        Employee emp2 = new Employee("b", dep);
        persist(dep, emp, emp2);
        em.flush();
        em.clear();

        Department v = dao.get(dep.getId());
        assertEquals(newList(emp.getId(), emp2.getId()), newList(map(Employee_.getId, dao.getProxies(v, Department_.employees))));
    }
    
    @Test
    public void getProxies_Set() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("a", dep, mun);
        Employee emp2 = new Employee("b", dep, mun);
        persist(mun, dep, emp, emp2);
        em.flush();
        em.clear();

        Municipality v = dao.get(mun.getId());
        assertEquals(newSet(emp.getId(), emp2.getId()), newSet(map(Employee_.getId, dao.getProxies(v, Municipality_.emps))));
    }
    
    @Test
    public void getProxiesFromProxy() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("a", dep, mun);
        Employee emp2 = new Employee("b", dep, mun);
        persist(mun, dep, emp, emp2);
        em.flush();
        em.clear();

        Municipality v = dao.toProxy(mun.getId());
        assertEquals(newSet(emp.getId(), emp2.getId()), newSet(map(Employee_.getId, dao.getProxies(v, Municipality_.emps))));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getProxyFromProxyFails() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        em.flush();
        em.clear();

        Employee v = dao.toProxy(emp.getId());
        assertFalse(Hibernate.isInitialized(v));
        dao.toProxy(v, Employee_.mandatoryDepartment);
    }

    @Test
    public void find() {
        Department dep = new Department();
        persist(dep);

        assertEquals(dep.getId(), dao.find(dep.getId()).get().getId());
        em.remove(dao.get(dep.getId()));
        assertFalse(dao.find(dep.getId()).isDefined());
    }

    @Test(expected = EntityNotFoundException.class)
    public void get_notFound() {
        Department dep = new Department();
        persist(dep);
        Id<Department> id = dep.getId();
        em.remove(dao.get(id));
        em.flush();
        em.clear();

        dao.get(id);
    }

    @Test
    public void toProxy_withClass() {
        Department dep = new Department();
        PartTimeEmployee emp = new PartTimeEmployee("", dep);
        persist(dep, emp);
        em.flush();
        em.clear();

        Employee proxy = dao.toProxy(Employee.class, emp.getId());
        assertFalse(Hibernate.isInitialized(proxy));
        assertEquals(emp.getId(), proxy.getId());
    }

    @Test
    public void toProxies_iterable() {
        Department dep = new Department();
        Employee emp1 = new Employee("a", dep);
        Employee emp2 = new Employee("b", dep);
        persist(dep, emp1, emp2);
        em.flush();
        em.clear();

        Iterable<Employee> proxies = dao.toProxies(newList(emp1.getId(), emp2.getId()));
        assertEquals(newList(emp1.getId(), emp2.getId()), newList(map(Employee_.getId, proxies)));
    }

    @Test
    public void toProxies_classAndIterable() {
        Department dep = new Department();
        PartTimeEmployee emp1 = new PartTimeEmployee("a", dep);
        PartTimeEmployee emp2 = new PartTimeEmployee("b", dep);
        persist(dep, emp1, emp2);
        em.flush();
        em.clear();

        Iterable<Employee> proxies = dao.toProxies(Employee.class, newList(emp1.getId(), emp2.getId()));
        assertEquals(newList(emp1.getId(), emp2.getId()), newList(map(Employee_.getId, proxies)));
    }
}
