package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.emptySet;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.FunctionalImpl.map;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
import fi.solita.utils.query.Dto.ID;
import fi.solita.utils.query.Dto.LIST_OF_IDS;
import fi.solita.utils.query.Dto.SET_OF_IDS;
import fi.solita.utils.query.Dto.VALUE;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class RelatedProjectionTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;
    
    @Test
    public void getRelatedProjection_single() {
        Department dep = new Department("", 42);
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Employee.class), Dto_.c20(literal(VALUE._), Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryNumber))));
        assertEquals(42, dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_single() {
        Department dep = new Department("", 42);
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Employee.class), Project.value(Related.projection(Employee_.mandatoryDepartment, Dto_.c20(literal(VALUE._), Department_.mandatoryNumber))));
        assertEquals(42, dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_set() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("", dep, mun);
        Employee emp2 = new Employee("", dep, mun);
        persist(dep, mun, emp1, emp2);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Municipality.class), Dto_.c18(literal(SET_OF_IDS._), Related.projection(Municipality_.emps, Project.id())));
        assertEquals(newSet(emp1.getId(), emp2.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_set() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("", dep, mun);
        Employee emp2 = new Employee("", dep, mun);
        persist(dep, mun, emp1, emp2);
        long queryCount = getQueryCount();
        
        Set<Dto> dtos = dao.get(query.all(Municipality.class), Project.value(Related.projection(Municipality_.emps, Dto_.c7(literal(ID._), Employee_.id))));
        assertEquals(newSet(emp1.getId(), emp2.getId()), newSet(map(dtos, Dto_.value)));
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_set_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Municipality.class), Dto_.c18(literal(SET_OF_IDS._), Related.projection(Municipality_.emps, Project.id())));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_set_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();
        
        Set<Dto> dtos = dao.get(query.all(Municipality.class), Project.value(Related.projection(Municipality_.emps, Dto_.c7(literal(ID._), Employee_.id))));
        assertEquals(emptySet(), dtos);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_list() {
        Department dep = new Department();
        Employee emp1 = new Employee("", dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Department.class), Dto_.c10(literal(LIST_OF_IDS._), Related.projection(Department_.employees, Project.id())));
        assertEquals(newList(emp1.getId(), emp2.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_list() {
        Department dep = new Department();
        Employee emp1 = new Employee("", dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);
        long queryCount = getQueryCount();
        
        List<Dto> dtos = dao.get(query.all(Department.class), Project.value(Related.projection(Department_.employees, Dto_.c7(literal(ID._), Employee_.id))));
        assertEquals(newList(emp1.getId(), emp2.getId()), newList(map(dtos, Dto_.value)));
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_list_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Department.class), Dto_.c10(literal(LIST_OF_IDS._), Related.projection(Department_.employees, Project.id())));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_list_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();
        
        List<Dto> dtos = dao.get(query.all(Department.class), Project.value(Related.projection(Department_.employees, Dto_.c7(literal(ID._), Employee_.id))));
        assertEquals(emptyList(), dtos);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_relatedValue() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        Employee emp = new Employee("emp", dep);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();
        
        Option<Id<Municipality>> munid = dao.get(query.all(Employee.class), Project.value(Cast.optional(Related.projection(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepMunicipality),
                                                                                                     Project.<Municipality>id()))));
        assertEquals(mun.getId(), munid.get());
        
        assertEquals(2, getQueryCount() - queryCount);
    }
}
