package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.emptySet;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
import fi.solita.utils.query.Dto.EMBEDDABLE;
import fi.solita.utils.query.Dto.ENTITY;
import fi.solita.utils.query.Dto.ID;
import fi.solita.utils.query.Dto.LIST_OF_ENTITIES;
import fi.solita.utils.query.Dto.LIST_OF_IDS;
import fi.solita.utils.query.Dto.LIST_OF_VALUES;
import fi.solita.utils.query.Dto.SET_OF_ENTITIES;
import fi.solita.utils.query.Dto.SET_OF_IDS;
import fi.solita.utils.query.Dto.SET_OF_VALUES;
import fi.solita.utils.query.Dto.VALUE;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.Report;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class RelatedProjectionTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;
    
    @Test
    public void getRelatedProjection_single() {
        Department dep = new Department("", 42);
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Employee.class), Dto_.$$$(literal(VALUE.a), Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryNumber))));
        assertEquals(42, dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_single() {
        Department dep = new Department("", 42);
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Employee.class), Project.value(Related.projection(Employee_.mandatoryDepartment, Dto_.$$$(literal(VALUE.a), Department_.mandatoryNumber))));
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
        
        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$(literal(SET_OF_IDS.a), Related.projection(Municipality_.emps, Project.id())));
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
        
        Set<Dto> dtos = dao.get(query.all(Municipality.class), Project.value(Related.projection(Municipality_.emps, Dto_.$$(literal(ID.a), Employee_.id))));
        assertEquals(newSet(emp1.getId(), emp2.getId()), newSet(map(Dto_.value, dtos)));
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_set_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$(literal(SET_OF_IDS.a), Related.projection(Municipality_.emps, Project.id())));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_set_empty() {
        Municipality mun = new Municipality();
        persist(mun);
        long queryCount = getQueryCount();
        
        Set<Dto> dtos = dao.get(query.all(Municipality.class), Project.value(Related.projection(Municipality_.emps, Dto_.$$(literal(ID.a), Employee_.id))));
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
        
        Dto dto = dao.get(query.all(Department.class), Dto_.$$$$$$$$$$$$$$$$$$(literal(LIST_OF_IDS.a), Related.projection(Department_.employees, Project.id())));
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
        
        List<Dto> dtos = dao.get(query.all(Department.class), Project.value(Related.projection(Department_.employees, Dto_.$$(literal(ID.a), Employee_.id))));
        assertEquals(newList(emp1.getId(), emp2.getId()), newList(map(Dto_.value, dtos)));
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_list_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();
        
        Dto dto = dao.get(query.all(Department.class), Dto_.$$$$$$$$$$$$$$$$$$(literal(LIST_OF_IDS.a), Related.projection(Department_.employees, Project.id())));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedProjection_value_list_empty() {
        Department dep = new Department();
        persist(dep);
        long queryCount = getQueryCount();
        
        List<Dto> dtos = dao.get(query.all(Department.class), Project.value(Related.projection(Department_.employees, Dto_.$$(literal(ID.a), Employee_.id))));
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
        
        Optional<Id<Municipality>> munid = dao.get(query.all(Employee.class), Project.value(Cast.optional(Related.projection(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepMunicipality),
                                                                                                     Project.<Municipality>id()))));
        assertEquals(mun.getId(), munid.get());
        
        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_single_value() {
        Department dep = new Department("testName", 42);
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        String name = dao.get(query.all(Employee.class), Project.value(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryDepName))));
        assertEquals("testName", name);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_single_entity() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.$$$$(literal(ENTITY.a), Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatorySelfReference))));
        assertEquals(dep, dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_single_embeddable() {
        Department dep = new Department(new Report(99));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.$$$$$(literal(EMBEDDABLE.a), Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryReport))));
        assertEquals(new Report(99), dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_set_value() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("alice", dep, mun);
        Employee emp2 = new Employee("bob", dep, mun);
        persist(dep, mun, emp1, emp2);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$$(literal(SET_OF_VALUES.a), Related.projection(Municipality_.emps, Project.value(Employee_.mandatoryName))));
        assertEquals(newSet("alice", "bob"), dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_value_set_value() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("alice", dep, mun);
        Employee emp2 = new Employee("bob", dep, mun);
        persist(dep, mun, emp1, emp2);
        long queryCount = getQueryCount();

        Set<String> names = dao.get(query.all(Municipality.class), Project.value(Related.projection(Municipality_.emps, Project.value(Employee_.mandatoryName))));
        assertEquals(newSet("alice", "bob"), names);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_set_entity() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("", dep, mun);
        Employee emp2 = new Employee("", dep, mun);
        persist(dep, mun, emp1, emp2);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$$$(literal(SET_OF_ENTITIES.a), Related.projection(Municipality_.emps, Project.value(Employee_.mandatoryDepartment))));
        assertEquals(newSet(dep), dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_set_embeddable() {
        Municipality mun = new Municipality(newSet(new Report(77)), false);
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();

        // Employee_.optionalMunicipality is optional so must use Cast.optional.
        // Related.projection with an optional singular returns Optional<Set<Report>>.
        Optional<Set<Report>> reports = dao.get(query.all(Employee.class), Project.value(Cast.optional(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.reports)))));
        assertEquals(newSet(new Report(77)), reports.get());

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_set_embeddable_empty() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();

        Optional<Set<Report>> reports = dao.get(query.all(Employee.class), Project.value(Cast.optional(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.reports)))));
        assertEquals(emptySet(), reports.get());

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_list_value() {
        Department dep = new Department();
        Employee emp1 = new Employee("first", dep);
        Employee emp2 = new Employee("second", dep);
        persist(dep, emp1, emp2);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.$$$$$$$$$$$$$$$$$$$(literal(LIST_OF_VALUES.a), Related.projection(Department_.employees, Project.value(Employee_.mandatoryName))));
        assertEquals(newList("first", "second"), dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_value_list_value() {
        Department dep = new Department();
        Employee emp1 = new Employee("first", dep);
        Employee emp2 = new Employee("second", dep);
        persist(dep, emp1, emp2);
        long queryCount = getQueryCount();

        List<String> names = dao.get(query.all(Department.class), Project.value(Related.projection(Department_.employees, Project.value(Employee_.mandatoryName))));
        assertEquals(newList("first", "second"), names);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_list_entity() {
        Department dep = new Department();
        Employee emp1 = new Employee("", dep);
        Employee emp2 = new Employee("", dep);
        persist(dep, emp1, emp2);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.$$$$$$$$$$$$$$$$$$$$(literal(LIST_OF_ENTITIES.a), Related.projection(Department_.employees, Project.value(Employee_.mandatoryDepartment))));
        assertEquals(newList(dep, dep), dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_list_embeddable() {
        Department dep = new Department("foo", newList(new Report(55)));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        // Related.projection with a singular->singular gives SingularAttribute wrapping the result.
        // Department_.reports is a ListAttribute, so Project.value(Department_.reports) gives List<Report>.
        // The overall result is SingularAttribute<Employee, List<Report>>, not ListAttribute.
        List<Report> reports = dao.get(query.all(Employee.class), Project.value(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.reports))));
        assertEquals(newList(new Report(55)), reports);

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_list_embeddable_empty() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        List<Report> reports = dao.get(query.all(Employee.class), Project.value(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.reports))));
        assertEquals(emptyList(), reports);

        assertEquals(3, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_multiple() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("a", dep1, mun);
        Employee emp2 = new Employee("b", dep2, mun);
        persist(dep1, dep2, mun, emp1, emp2);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Employee.class),
                Dto_.$$$(literal(VALUE.a), Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryNumber))),
                Order.by(Employee_.mandatoryName));
        assertEquals(2, dtos.size());
        assertEquals(0, dtos.get(0).value);
        assertEquals(0, dtos.get(1).value);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_relatedValue_collection() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        Employee emp1 = new Employee("", dep, mun);
        Employee emp2 = new Employee("", dep, mun);
        persist(mun, dep, emp1, emp2);
        long queryCount = getQueryCount();

        Set<Id<Employee>> ids = dao.get(query.all(Department.class), Project.value(Related.projection(Related.set(Department_.optionalDepMunicipality, Municipality_.emps), Project.<Employee>id())));
        assertEquals(newSet(emp1.getId(), emp2.getId()), ids);

        assertEquals(2, getQueryCount() - queryCount);
    }

    @Test
    public void getRelatedProjection_collection() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp1 = new Employee("", dep, mun);
        Employee emp2 = new Employee("", dep, mun);
        persist(dep, mun, emp1, emp2);
        long queryCount = getQueryCount();

        // Using SetAttribute as the relation - Related.projection(SetAttribute, projection) returns SetAttribute<E, R>
        // Using Dto constructor projection wrapped in set result
        Dto dto = dao.get(query.all(Municipality.class), Dto_.$$$$$$$$$$$$$$(literal(SET_OF_IDS.a), Related.projection(Municipality_.emps, Project.<Employee>id())));
        assertEquals(newSet(emp1.getId(), emp2.getId()), dto.value);

        assertEquals(2, getQueryCount() - queryCount);
    }
}
