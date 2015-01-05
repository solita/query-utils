package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.emptySet;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.functional.Functional.tail;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
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
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class RelatedValueTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;
    
    @Test
    public void getRelatedValue_id() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c1(literal(ID._), Related.value(Employee_.mandatoryDepartment, Department_.id)));
        assertEquals(dep.getId(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_id_some_mandatory() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c5(literal(OPTIONAL_ID._), Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.id))));
        assertEquals(Some(mun.getId()), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_id_none_mandatory() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c5(literal(OPTIONAL_ID._), Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.id))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c2(literal(VALUE._), Related.value(Employee_.mandatoryDepartment, Department_.mandatoryDepName)));
        assertEquals(dep.getMandatoryName(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_some_mandatory() {
        Department dep = new Department(new Money(42));
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.id))));
        assertEquals(Some(mun.getId()), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_none_mandatory() {
        Department dep = new Department(new Money(42));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.id))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_mandatory_some() {
        Department dep = new Department(new Money(42));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalBudget))));
        assertEquals(Some(dep.getBudget()), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_mandatory_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalBudget))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_some_some() {
        Department dep = new Department();
        Municipality mun = new Municipality("foo");
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Related.value(Employee_.optionalMunicipality, Cast.optional(Municipality_.optionalArea))));
        assertEquals(Some(mun.getOptionalArea()), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_some_none() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Related.value(Employee_.optionalMunicipality, Cast.optional(Municipality_.optionalArea))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_value_none_optional() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c7(literal(OPTIONAL_VALUE._), Related.value(Employee_.optionalMunicipality, Cast.optional(Municipality_.optionalArea))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c3(literal(ENTITY._), Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference)));
        assertEquals(dep, dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_some_mandatory() {
        Department mandep = new Department("b");
        Employee emp = new Employee("", mandep);
        Department dep = new Department("a", emp);
        persist(mandep, emp, dep);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment))), Order.by(Department_.mandatoryDepName));
        assertEquals(Some(mandep), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_none_mandatory() {
        Department dep = new Department("a");
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_mandatory_some() {
        Department mandep = new Department();
        Employee manemp = new Employee("b", mandep);
        Department dep = new Department("", manemp);
        Employee emp = new Employee("a", dep);
        persist(mandep, manemp, dep, emp);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Employee.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalManager))), Order.by(Employee_.mandatoryName));
        assertEquals(Some(manemp), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_mandatory_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalManager))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_some_some() {
        Department mandep = new Department("b");
        Municipality mun = new Municipality();
        Employee emp = new Employee("", mandep, mun);
        Department dep = new Department("a", emp);
        persist(mandep, mun, emp, dep);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalMunicipality))), Order.by(Department_.mandatoryDepName));
        assertEquals(Some(mun), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_some_none() {
        Department mandep = new Department("b");
        Employee emp = new Employee("", mandep);
        Department dep = new Department("a", emp);
        persist(mandep, emp, dep);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalMunicipality))), Order.by(Department_.mandatoryDepName));
        assertEquals(None(), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity_none_optional() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c6(literal(OPTIONAL_ENTITY._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalMunicipality))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id() {
        Department mandep = new Department("b");
        Employee emp = new Employee("", mandep);
        Department dep = new Department("a", emp);
        persist(mandep, emp, dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c1(literal(ID._), Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference)));
        assertEquals(mandep.getId(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_mandatory_some() {
        Department d = new Department("a");
        Employee manager = new Employee("a", d);
        Department dep = new Department("b", manager);
        Employee emp = new Employee("b", dep);
        persist(d, manager, dep, emp);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Employee.class), Dto_.c5(literal(OPTIONAL_ID._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalManager))), Order.by(Employee_.mandatoryName));
        assertEquals(None(), head(dtos).value);
        assertEquals(Some(manager.getId()), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_mandatory_none() {
        Department dep = new Department("b");
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c5(literal(OPTIONAL_ID._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalManager))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_some_mandatory() {
        Department mandep = new Department("b");
        Employee emp = new Employee("", mandep);
        Department dep = new Department("a", emp);
        persist(mandep, emp, dep);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c5(literal(OPTIONAL_ID._), Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment))), Order.by(Department_.mandatoryDepName));
        assertEquals(Some(mandep.getId()), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_none_mandatory() {
        Department dep = new Department("a");
        persist(dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c5(literal(OPTIONAL_ID._), Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_some_some() {
        Department mandep = new Department("b");
        Municipality mun = new Municipality();
        Employee emp = new Employee("", mandep, mun);
        Department dep = new Department("a", emp);
        persist(mandep, mun, emp, dep);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c5(literal(OPTIONAL_ID._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalMunicipality))), Order.by(Department_.mandatoryDepName));
        assertEquals(Some(mun.getId()), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_some_none() {
        Department mandep = new Department("b");
        Employee emp = new Employee("", mandep);
        Department dep = new Department("a", emp);
        persist(mandep, emp, dep);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c5(literal(OPTIONAL_ID._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalMunicipality))), Order.by(Department_.mandatoryDepName));
        assertEquals(None(), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_entity2id_none_optional() {
        Department dep = new Department();
        Employee emp = new Employee("foo", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c5(literal(OPTIONAL_ID._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalMunicipality))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable() {
        Department dep = new Department(new Report(42));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c4(literal(EMBEDDABLE._), Related.value(Employee_.mandatoryDepartment, Department_.mandatoryReport)));
        assertEquals(new Report(42), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_nullinside() {
        Department dep = new Department(new Report(null));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c4(literal(EMBEDDABLE._), Related.value(Employee_.mandatoryDepartment, Department_.mandatoryReport)));
        assertEquals(null, ((Report)dto.value).getYear());
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_some_mandatory() {
        Department dep = new Department();
        Municipality mun = new Municipality(new Report(42));
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport))));
        assertEquals(Some(new Report(42)), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_none_mandatory() {
        Municipality mun = new Municipality(new Report(42));
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        persist(dep, emp, mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport))));
        assertEquals(Some(new Report(42)), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_mandatory_some() {
        Department dep = new Department(new Report(42));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalDepReport))));
        assertEquals(Some(new Report(42)), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_mandatory_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Related.value(Employee_.mandatoryDepartment, Cast.optional(Department_.optionalDepReport))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_some_some() {
        Department d = new Department("a");
        Employee manager = new Employee("", d, new Report(42));
        Department dep = new Department("b", manager);
        Employee emp = new Employee("", dep);
        persist(d, manager, dep, emp);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalReport))), Order.by(Department_.mandatoryDepName));
        assertEquals(None(), head(dtos).value);
        assertEquals(Some(new Report(42)), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_some_none() {
        Department d = new Department("a");
        Employee manager = new Employee("", d);
        Department dep = new Department("b", manager);
        Employee emp = new Employee("", dep);
        persist(d, manager, dep, emp);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalReport))), Order.by(Department_.mandatoryDepName));
        assertEquals(None(), head(dtos).value);
        assertEquals(None(), head(tail(dtos)).value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_embeddable_none_optional() {
        Department dep = new Department("foo");
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c8(literal(OPTIONAL_EMBEDDABLE._), Related.value(Department_.optionalManager, Cast.optional(Employee_.optionalReport))));
        assertEquals(None(), dto.value);
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_some_empty() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        persist(mun, dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c13(literal(SET_OF_IDS._), Related.set(Department_.optionalDepMunicipality, Municipality_.emps)));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_none() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        persist(mun, dep);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c13(literal(SET_OF_IDS._), Related.set(Department_.optionalDepMunicipality, Municipality_.emps)));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_some_ids() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        Employee emp = new Employee("", dep, mun);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c13(literal(SET_OF_IDS._), Related.set(Department_.optionalDepMunicipality, Municipality_.emps)));
        assertEquals(newSet(emp.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_some_values() {
        Municipality mun = new Municipality(newSet(42));
        Department dep = new Department(mun);
        persist(dep, mun);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c14(literal(SET_OF_VALUES._), Related.set(Department_.optionalDepMunicipality, Municipality_.postalCodes)));
        assertEquals(newSet(42), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_some_entities() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c15(literal(SET_OF_ENTITIES._), Related.set(Department_.optionalDepMunicipality, Municipality_.emps)));
        assertEquals(newSet(emp), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_multiple_none() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c14(literal(SET_OF_VALUES._), Related.set(Municipality_.emps, Employee_.optionalSalary)));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_multiple_some() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        emp.setOptionalSalary(new Money(42));
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c14(literal(SET_OF_VALUES._), Related.set(Municipality_.emps, Employee_.optionalSalary)));
        assertEquals(newSet(new Money(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_multiple_values() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        emp.setOptionalSalary(new Money(42));
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c14(literal(SET_OF_VALUES._), Related.set(Municipality_.emps, Employee_.mandatoryName)));
        assertEquals(newSet(emp.getName()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_multiple_entities() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        emp.setOptionalSalary(new Money(42));
        persist(dep, mun, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Municipality.class), Dto_.c15(literal(SET_OF_ENTITIES._), Related.set(Municipality_.emps, Employee_.mandatoryDepartment)));
        assertEquals(newSet(dep), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    
    @Test
    public void getRelatedValue_set_embeddable() {
        Municipality mun = new Municipality(newSet(new Report(42)), false);
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c16(literal(SET_OF_EMBEDDABLES._), Related.set(Employee_.optionalMunicipality, Municipality_.reports)));
        assertEquals(newSet(new Report(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_embeddable_empty() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp = new Employee("", dep, mun);
        persist(mun, dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c16(literal(SET_OF_EMBEDDABLES._), Related.set(Employee_.optionalMunicipality, Municipality_.reports)));
        assertEquals(emptySet(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_some_empty() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c17(literal(LIST_OF_IDS._), Related.list(Employee_.optionalDepartment, Department_.employees)));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_some_ids() {
        Department dep = new Department();
        Employee emp = new Employee("", dep).setOptionalDepartment(dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c17(literal(LIST_OF_IDS._), Related.list(Employee_.optionalDepartment, Department_.employees)));
        assertEquals(newList(emp.getId()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_some_values() {
        Department dep = new Department(newList(42));
        Employee emp = new Employee("", dep).setOptionalDepartment(dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c18(literal(LIST_OF_VALUES._), Related.list(Employee_.optionalDepartment, Department_.numbers)));
        assertEquals(newList(42), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_some_entities() {
        Department dep = new Department();
        Employee emp = new Employee("", dep).setOptionalDepartment(dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c19(literal(LIST_OF_ENTITIES._), Related.list(Employee_.optionalDepartment, Department_.employees)));
        assertEquals(newList(emp), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_multiple_none() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c18(literal(LIST_OF_VALUES._), Related.list(Department_.employees, Employee_.optionalSalary)));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_multiple_some() {
        Department dep = new Department();
        Employee emp = new Employee("", dep).setOptionalSalary(new Money(42));
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c18(literal(LIST_OF_VALUES._), Related.list(Department_.employees, Employee_.optionalSalary)));
        assertEquals(newList(new Money(42)), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_multiple_values() {
        Department dep = new Department();
        Employee emp = new Employee("", dep).setOptionalSalary(new Money(42));
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c18(literal(LIST_OF_VALUES._), Related.list(Department_.employees, Employee_.mandatoryName)));
        assertEquals(newList(emp.getName()), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_multiple_entities() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        emp.setOptionalSalary(new Money(42));
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Department.class), Dto_.c19(literal(LIST_OF_ENTITIES._), Related.list(Department_.employees, Employee_.mandatoryDepartment)));
        assertEquals(newList(dep), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    
    @Test
    public void getRelatedValue_list_embeddable() {
        Department dep = new Department("", newList(new Report(42)));
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c20(literal(LIST_OF_EMBEDDABLES._), Related.list(Employee_.mandatoryDepartment, Department_.reports)));
        assertEquals(newList(Some(new Report(42))), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_list_embeddable_empty() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);
        long queryCount = getQueryCount();

        Dto dto = dao.get(query.all(Employee.class), Dto_.c20(literal(LIST_OF_EMBEDDABLES._), Related.list(Employee_.mandatoryDepartment, Department_.reports)));
        assertEquals(emptyList(), dto.value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_many_set_some_multiple() {
        Municipality mun1 = new Municipality();
        Municipality mun2 = new Municipality();
        Department dep1 = new Department(mun1);
        Department dep2 = new Department(mun2);
        Department dep3 = new Department();
        Employee emp1 = new Employee("", dep1, mun1);
        Employee emp2 = new Employee("", dep1, mun1);
        persist(dep1, dep2, dep3, mun1, mun2, emp1, emp2);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Department.class), Dto_.c15(literal(SET_OF_ENTITIES._), Related.set(Department_.optionalDepMunicipality, Municipality_.emps)), Order.by(Department_.id));
        assertEquals(newSet(emp1, emp2), head(dtos).value);
        assertEquals(emptySet(), head(tail(dtos)).value);
        assertEquals(emptySet(), last(dtos).value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void getRelatedValue_set_list() {
        Municipality mun1 = new Municipality();
        Department dep1 = new Department(mun1);
        Department dep2 = new Department(mun1);
        Department dep3 = new Department();
        Employee emp1 = new Employee("", dep1);
        Employee emp2 = new Employee("", dep2);
        persist(dep1, dep2, dep3, mun1, emp1, emp2);
        long queryCount = getQueryCount();

        List<Dto> dtos = dao.getMany(query.all(Municipality.class), Dto_.c19(literal(LIST_OF_ENTITIES._), Related.list(Municipality_.deps, Department_.employees)), Order.by(Municipality_.id));
        assertEquals(1, dtos.size());
        assertEquals(newList(emp1, emp2), head(dtos).value);
        
        assertEquals(2, getQueryCount() - queryCount);
    }
}
