package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.emptySet;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.Dto.ENTITY;
import fi.solita.utils.query.Dto.ID;
import fi.solita.utils.query.Dto.LIST_OF_ENTITIES;
import fi.solita.utils.query.Dto.LIST_OF_IDS;
import fi.solita.utils.query.Dto.LIST_OF_VALUES;
import fi.solita.utils.query.Dto.OPTIONAL_ENTITY;
import fi.solita.utils.query.Dto.OPTIONAL_ID;
import fi.solita.utils.query.Dto.OPTIONAL_VALUE;
import fi.solita.utils.query.Dto.SET_OF_ENTITIES;
import fi.solita.utils.query.Dto.SET_OF_IDS;
import fi.solita.utils.query.Dto.SET_OF_VALUES;
import fi.solita.utils.query.Dto.VALUE;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class RelatedTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;
    
    @Test
    public void get_dto_id() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c3(literal(ID._), Related.projection(Employee_.department, Project.id())));
        assertEquals(dep.getId(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_id_some() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c10(literal(OPTIONAL_ID._), Cast.optional(Related.projection(Employee_.municipality, Project.id()))));
        assertEquals(Some(mun.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_id_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c10(literal(OPTIONAL_ID._), Cast.optional(Related.projection(Employee_.municipality, Project.id()))));
        assertEquals(None(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    /*@Test
    public void get_dto_value() {
        Department dep = new Department("foo");
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c12(literal(VALUE._), Department_.name));
        ssertEquals(dep.getName(), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value_some() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", new Money(42), dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_VALUE._), Cast.optional(Employee_.salary)));
        ssertEquals(emp.getSalary(), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value_none() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_VALUE._), Cast.optional(Employee_.salary)));
        ssertEquals(None(), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c1(literal(ENTITY._), Employee_.department));
        ssertEquals(dep, dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity_some() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Cast.optional(Employee_.municipality)));
        ssertEquals(Some(mun), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity_none() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Cast.optional(Employee_.municipality)));
        ssertEquals(None(), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c2(literal(ID._), Employee_.department));
        ssertEquals(dep.getId(), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id_some() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_ID._), Cast.optional(Employee_.municipality)));
        ssertEquals(Some(mun.getId()), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id_none() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_ID._), Cast.optional(Employee_.municipality)));
        ssertEquals(None(), dto.value);
        
        ssertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_empty() {
        Municipality mun = new Municipality();
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c10(literal(SET_OF_IDS._), Municipality_.employees));
        ssertEquals(emptySet(), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_id() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c10(literal(SET_OF_IDS._), Municipality_.employees));
        ssertEquals(newSet(emp.getId()), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_value() {
        Department dep = new Department();
        Municipality mun = new Municipality(newSet(42));
        Employee emp = new Employee("foo", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c11(literal(SET_OF_VALUES._), Municipality_.postalCodes));
        ssertEquals(newSet(42), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_entity() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        em.persist(dep);
        em.persist(mun);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c9(literal(SET_OF_ENTITIES._), Municipality_.employees));
        ssertEquals(newSet(emp), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_entity_empty() {
        Municipality mun = new Municipality();
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c9(literal(SET_OF_ENTITIES._), Municipality_.employees));
        ssertEquals(emptySet(), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_empty() {
        Department dep = new Department();
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c4(literal(LIST_OF_IDS._), Department_.employees));
        ssertEquals(emptyList(), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_id() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c4(literal(LIST_OF_IDS._), Department_.employees));
        ssertEquals(newList(emp.getId()), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_value() {
        Department dep = new Department(newList(42));
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c5(literal(LIST_OF_VALUES._), Department_.numbers));
        ssertEquals(newList(42), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_entity() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c3(literal(LIST_OF_ENTITIES._), Department_.employees));
        ssertEquals(newList(emp), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_entity_empty() {
        Department dep = new Department();
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c3(literal(LIST_OF_ENTITIES._), Department_.employees));
        ssertEquals(emptyList(), dto.value);
        
        ssertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_embedded() {
         fail();
    }
    
    @Test
    public void get_embedded_set() {
         fail();
    }*/
}
