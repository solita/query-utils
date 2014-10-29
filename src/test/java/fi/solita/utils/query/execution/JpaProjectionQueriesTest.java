package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.emptySet;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
import fi.solita.utils.query.Dto.COLLECTION_OF_EMBEDDABLES;
import fi.solita.utils.query.Dto.COLLECTION_OF_ENTITIES;
import fi.solita.utils.query.Dto.COLLECTION_OF_IDS;
import fi.solita.utils.query.Dto.COLLECTION_OF_VALUES;
import fi.solita.utils.query.Dto.EMBEDDABLE;
import fi.solita.utils.query.Dto.ENTITY;
import fi.solita.utils.query.Dto.ID;
import fi.solita.utils.query.Dto.LIST_OF_EMBEDDABLES;
import fi.solita.utils.query.Dto.LIST_OF_ENTITIES;
import fi.solita.utils.query.Dto.LIST_OF_IDS;
import fi.solita.utils.query.Dto.LIST_OF_VALUES;
import fi.solita.utils.query.Dto.OPTIONAL_EMBEDDABLE;
import fi.solita.utils.query.Dto.OPTIONAL_ENTITY;
import fi.solita.utils.query.Dto.OPTIONAL_ID;
import fi.solita.utils.query.Dto.OPTIONAL_VALUE;
import fi.solita.utils.query.Dto.SET_OF_EMBEDDABLES;
import fi.solita.utils.query.Dto.SET_OF_ENTITIES;
import fi.solita.utils.query.Dto.SET_OF_IDS;
import fi.solita.utils.query.Dto.SET_OF_VALUES;
import fi.solita.utils.query.Dto.VALUE;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.Report;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.generation.Restrict;

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
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c1(literal(ID._), Department_.id));
        assertEquals(dep.getId(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value() {
        Department dep = new Department("foo");
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c2(literal(VALUE._), Department_.mandatoryDepName));
        assertEquals(dep.getMandatoryName(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value_some() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", new Money(42), dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Cast.optional(Employee_.optionalSalary)));
        assertEquals(emp.getOptionalSalary(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_value_none() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Cast.optional(Employee_.optionalSalary)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c3(literal(ENTITY._), Employee_.mandatoryDepartment));
        assertEquals(dep, dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity_some() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Cast.optional(Employee_.optionalMunicipality)));
        assertEquals(Some(mun), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity_none() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Cast.optional(Employee_.optionalMunicipality)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c1(literal(ID._), Employee_.mandatoryDepartment));
        assertEquals(dep.getId(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id_some() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c5(literal(OPTIONAL_ID._), Cast.optional(Employee_.optionalMunicipality)));
        assertEquals(Some(mun.getId()), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_entity2id_none() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c5(literal(OPTIONAL_ID._), Cast.optional(Employee_.optionalMunicipality)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable() {
        Municipality mun = new Municipality(new Report(42));
        persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c4(literal(EMBEDDABLE._), Municipality_.mandatoryReport));
        assertEquals((Integer)42, ((Report)dto.value).getYear());
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable_nullinside() {
        Municipality mun = new Municipality(new Report(null));
        persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c4(literal(EMBEDDABLE._), Municipality_.mandatoryReport));
        assertEquals(null, ((Report)dto.value).getYear());
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable_some() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep, new Report(42));
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Cast.optional(Employee_.optionalReport)));
        assertEquals(Some(new Report(42)), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void get_dto_embeddable_none() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Cast.optional(Employee_.optionalReport)));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    private static final Set<?> asSet(Dto dto) {
        return newSet((Collection<?>)dto.value);
    }
    
    private static final List<?> asList(Dto dto) {
        return newList((Collection<?>)dto.value);
    }
    
    @Test
    public void get_dto_set_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c13(literal(SET_OF_IDS._), Municipality_.emps));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertTrue(asSet(dao.get(query.all(Municipality.class), Dto_.c9(literal(COLLECTION_OF_IDS._), Municipality_.emps))).isEmpty());
    }
    
    @Test
    public void get_dto_set_id() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c13(literal(SET_OF_IDS._), Municipality_.emps));
        assertEquals(newSet(emp.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newSet(emp.getId()), asSet(dao.get(query.all(Municipality.class), Dto_.c9(literal(COLLECTION_OF_IDS._), Municipality_.emps))));
    }
    
    @Test
    public void get_dto_set_value() {
        Department dep = new Department();
        Municipality mun = new Municipality(newSet(42));
        Employee emp = new Employee("foo", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c14(literal(SET_OF_VALUES._), Municipality_.postalCodes));
        assertEquals(newSet(42), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newSet(42), asSet(dao.get(query.all(Municipality.class), Dto_.c10(literal(COLLECTION_OF_VALUES._), Municipality_.postalCodes))));
    }
    
    @Test
    public void get_dto_set_entity() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("foo", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c15(literal(SET_OF_ENTITIES._), Municipality_.emps));
        assertEquals(newSet(emp), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newSet(emp), asSet(dao.get(query.all(Municipality.class), Dto_.c11(literal(COLLECTION_OF_ENTITIES._), Municipality_.emps))));
    }
    
    @Test
    public void get_dto_set_entity_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c15(literal(SET_OF_ENTITIES._), Municipality_.emps));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(emptySet(), asSet(dao.get(query.all(Municipality.class), Dto_.c11(literal(COLLECTION_OF_ENTITIES._), Municipality_.emps))));
    }
    
    @Test
    public void get_dto_set_embeddable() {
        Municipality mun = new Municipality(newSet(new Report(42)), false);
        persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c16(literal(SET_OF_EMBEDDABLES._), Municipality_.reports));
        assertEquals(newSet(new Report(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newSet(new Report(42)), asSet(dao.get(query.all(Municipality.class), Dto_.c12(literal(COLLECTION_OF_EMBEDDABLES._), Municipality_.reports))));
    }
    
    @Test
    public void get_dto_set_embeddable_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c16(literal(SET_OF_EMBEDDABLES._), Municipality_.reports));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(emptySet(), asSet(dao.get(query.all(Municipality.class), Dto_.c12(literal(COLLECTION_OF_EMBEDDABLES._), Municipality_.reports))));
    }
    
    @Test
    public void get_dto_list_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c17(literal(LIST_OF_IDS._), Department_.employees));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(emptyList(), asList(dao.get(query.all(Department.class), Dto_.c9(literal(COLLECTION_OF_IDS._), Department_.employees))));
    }
    
    @Test
    public void get_dto_list_id() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c17(literal(LIST_OF_IDS._), Department_.employees));
        assertEquals(newList(emp.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newList(emp.getId()), asList(dao.get(query.all(Department.class), Dto_.c9(literal(COLLECTION_OF_IDS._), Department_.employees))));
    }
    
    @Test
    public void get_dto_list_value() {
        Department dep = new Department(newList(42));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c18(literal(LIST_OF_VALUES._), Department_.numbers));
        assertEquals(newList(42), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newList(42), asList(dao.get(query.all(Department.class), Dto_.c10(literal(COLLECTION_OF_VALUES._), Department_.numbers))));
    }
    
    @Test
    public void get_dto_list_entity() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c19(literal(LIST_OF_ENTITIES._), Department_.employees));
        assertEquals(newList(emp), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newList(emp), asList(dao.get(query.all(Department.class), Dto_.c11(literal(COLLECTION_OF_ENTITIES._), Department_.employees))));
    }
    
    @Test
    public void get_dto_list_entity_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c19(literal(LIST_OF_ENTITIES._), Department_.employees));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(emptyList(), asList(dao.get(query.all(Department.class), Dto_.c11(literal(COLLECTION_OF_ENTITIES._), Department_.employees))));
    }
    
    @Test
    public void get_dto_list_embeddable() {
        Department dep = new Department("", newList(new Report(42)));
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c20(literal(LIST_OF_EMBEDDABLES._), Department_.reports));
        assertEquals(newList(new Report(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(newList(new Report(42)), asList(dao.get(query.all(Department.class), Dto_.c12(literal(COLLECTION_OF_EMBEDDABLES._), Department_.reports))));
    }
    
    @Test
    public void get_dto_list_embeddable_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c20(literal(LIST_OF_EMBEDDABLES._), Department_.reports));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
        
        assertEquals(emptyList(), asList(dao.get(query.all(Department.class), Dto_.c12(literal(COLLECTION_OF_EMBEDDABLES._), Department_.reports))));
    }
    
    @Test
    public void get_dto_many_set_some_multiple() {
        Municipality mun1 = new Municipality();
        Municipality mun2 = new Municipality();
        Department dep = new Department();
        Employee emp1 = new Employee("", dep, mun1);
        Employee emp2 = new Employee("", dep, mun1);
        persist(dep, mun1, mun2, emp1, emp2);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Municipality.class), Dto_.c15(literal(SET_OF_ENTITIES._), Municipality_.emps), Order.by(Municipality_.id));
        assertEquals(newSet(emp1, emp2), head(dtos).value);
        assertEquals(emptySet(), last(dtos).value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
}
