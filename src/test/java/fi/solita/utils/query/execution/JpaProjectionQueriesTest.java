package fi.solita.utils.query.execution;

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
import fi.solita.utils.query.Report;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.generation.Restrict;

import fi.solita.utils.query.Dto.*;

public class JpaProjectionQueriesTest extends QueryTestBase {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaProjectionQueries dao;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Restrict restrict;
    
    @Test
    public void get_dto_id() {
        Department dep = new Department();
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c3(literal(ID._), Department_.id));
        assertEquals(dep.getId(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value() {
        Department dep = new Department("foo");
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c16(literal(VALUE._), Department_.name));
        assertEquals(dep.getName(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value_some() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", new Money(42), dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c11(literal(OPTIONAL_VALUE._), Cast.optional(Employee_.salary)));
        assertEquals(emp.getSalary(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value_none() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c11(literal(OPTIONAL_VALUE._), Cast.optional(Employee_.salary)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c2(literal(ENTITY._), Employee_.department));
        assertEquals(dep, dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
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

        Dto dto = dao.get(query.all(Employee.class), Dto_.c9(literal(OPTIONAL_ENTITY._), Cast.optional(Employee_.municipality)));
        assertEquals(Some(mun), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity_none() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c9(literal(OPTIONAL_ENTITY._), Cast.optional(Employee_.municipality)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c3(literal(ID._), Employee_.department));
        assertEquals(dep.getId(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
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

        Dto dto = dao.get(query.all(Employee.class), Dto_.c10(literal(OPTIONAL_ID._), Cast.optional(Employee_.municipality)));
        assertEquals(Some(mun.getId()), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id_none() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c10(literal(OPTIONAL_ID._), Cast.optional(Employee_.municipality)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable() {
        Municipality mun = new Municipality(new Report(42));
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c1(literal(EMBEDDABLE._), Municipality_.report));
        assertEquals(42, ((Report)dto.value).getYear());
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable_some() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep, new Report(42));
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Cast.optional(Employee_.report)));
        assertEquals(Some(new Report(42)), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable_none() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Cast.optional(Employee_.report)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_empty() {
        Municipality mun = new Municipality();
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c14(literal(SET_OF_IDS._), Municipality_.employees));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
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

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c14(literal(SET_OF_IDS._), Municipality_.employees));
        assertEquals(newSet(emp.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
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

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c15(literal(SET_OF_VALUES._), Municipality_.postalCodes));
        assertEquals(newSet(42), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
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

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c13(literal(SET_OF_ENTITIES._), Municipality_.employees));
        assertEquals(newSet(emp), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_entity_empty() {
        Municipality mun = new Municipality();
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c13(literal(SET_OF_ENTITIES._), Municipality_.employees));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_embeddable() {
        Municipality mun = new Municipality(newSet(new Report(42)), false);
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c12(literal(SET_OF_EMBEDDABLES._), Municipality_.reports));
        assertEquals(newSet(new Report(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_set_embeddable_empty() {
        Municipality mun = new Municipality();
        em.persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c12(literal(SET_OF_EMBEDDABLES._), Municipality_.reports));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_empty() {
        Department dep = new Department();
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c6(literal(LIST_OF_IDS._), Department_.employees));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_id() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c6(literal(LIST_OF_IDS._), Department_.employees));
        assertEquals(newList(emp.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_value() {
        Department dep = new Department(newList(42));
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c7(literal(LIST_OF_VALUES._), Department_.numbers));
        assertEquals(newList(42), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_entity() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        em.persist(dep);
        em.persist(emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c5(literal(LIST_OF_ENTITIES._), Department_.employees));
        assertEquals(newList(emp), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_entity_empty() {
        Department dep = new Department();
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c5(literal(LIST_OF_ENTITIES._), Department_.employees));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_embeddable() {
        Department dep = new Department("", newList(new Report(42)));
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c4(literal(LIST_OF_EMBEDDABLES._), Department_.reports));
        assertEquals(newList(new Report(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_list_embeddable_empty() {
        Department dep = new Department();
        em.persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c4(literal(LIST_OF_EMBEDDABLES._), Department_.reports));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
}
