package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
import fi.solita.utils.query.Dto.LIST_OF_VALUES;
import fi.solita.utils.query.Dto.SET_OF_VALUES;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class RelatedTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;
    
    // Tests for Related.set() methods with SetAttribute
    
    @Test
    public void set_2params_fromSetToValue() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("emp1", dep, mun);
        Employee emp2 = new Employee("emp2", dep, mun);
        persist(mun, dep, emp1, emp2);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$$(literal(SET_OF_VALUES.a), 
            Related.set(Municipality_.emps, Employee_.mandatoryName)));
        
        assertEquals(newSet("emp1", "emp2"), dto.value);
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void set_3params() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        persist(mun, dep);
        long queryCount = getQueryCount();
        
        // Test chaining through multiple attributes
        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$$(literal(SET_OF_VALUES.a), 
            Related.set(Municipality_.deps, Department_.optionalDepMunicipality, Municipality_.optionalArea)));
        
        assertNotNull(dto.value);
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void set_4params() {
        Municipality mun = new Municipality("area1");
        Department dep1 = new Department(mun);
        Department dep2 = new Department(mun);
        Employee emp = new Employee("test", dep1, mun);
        persist(mun, dep1, dep2, emp);
        long queryCount = getQueryCount();
        
        // Test even longer chains through set attributes
        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$$(literal(SET_OF_VALUES.a), 
            Related.set(Municipality_.deps, Department_.optionalDepMunicipality, 
                Municipality_.emps, Employee_.mandatoryName)));
        
        assertNotNull(dto.value);
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    // Tests for Related.list() methods with ListAttribute
    
    @Test
    public void list_2params_fromAttributeToList() {
        Department dep = new Department(newList(1, 2, 3));
        Employee emp = new Employee("test", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Employee.class), Dto_.$$$$$$$$$$$$$$$$$$$(literal(LIST_OF_VALUES.a), 
            Related.list(Employee_.mandatoryDepartment, Department_.numbers)));
        
        assertEquals(newList(1, 2, 3), dto.value);
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void list_2params_fromListToValue() {
        Department dep = new Department();
        Employee emp = new Employee("test", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Department.class), Dto_.$$$$$$$$$$$$$$$$$$$(literal(LIST_OF_VALUES.a), 
            Related.list(Department_.employees, Employee_.mandatoryName)));
        
        assertEquals(newList("test"), dto.value);
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    // Tests for Related.value() methods with multiple params (already tested in RelatedValueTest but adding examples)
    
    @Test
    public void value_3params() {
        Municipality mun = new Municipality("testArea");
        Department dep = new Department(mun);
        Employee emp = new Employee("test", dep, mun);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();
        
        // Since the chain contains optional attributes, we need to use Cast.optional()
        Dto dto = dao.get(query.all(Employee.class), Dto_.$$$$$$$$(literal(Dto.OPTIONAL_VALUE.a), 
            Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepMunicipality, Municipality_.optionalArea))));
        
        // optionalArea is an optional attribute, so it properly handles null values through the chain
        assertEquals(fi.solita.utils.functional.Option.Some("testArea"), dto.value);
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void value_4params() {
        // Using mandatorySelfReference which is automatically set to 'this' in constructors
        Municipality mun = new Municipality("outerArea");
        Department dep = new Department(mun);
        Employee emp = new Employee("test", dep);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();
        
        // Test 4-parameter value chain - all attributes in the chain must be present
        Dto dto = dao.get(query.all(Employee.class), Dto_.$$$$$$$$(literal(Dto.OPTIONAL_VALUE.a), 
            Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference, 
                Department_.optionalDepMunicipality, Municipality_.optionalArea))));
        
        // Since the chain goes through optional attributes, result is wrapped in Option
        assertEquals(fi.solita.utils.functional.Option.Some("outerArea"), dto.value);
        assertEquals(1, getQueryCount() - queryCount);
    }
}
