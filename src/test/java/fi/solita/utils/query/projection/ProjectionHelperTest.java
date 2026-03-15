package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.emptySet;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto;
import fi.solita.utils.query.Dto.EMBEDDABLE;
import fi.solita.utils.query.Dto.ENTITY;
import fi.solita.utils.query.Dto.LIST_OF_IDS;
import fi.solita.utils.query.Dto.LIST_OF_VALUES;
import fi.solita.utils.query.Dto.OPTIONAL_EMBEDDABLE;
import fi.solita.utils.query.Dto.OPTIONAL_VALUE;
import fi.solita.utils.query.Dto.SET_OF_IDS;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.Report;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class ProjectionHelperTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;

    // -------------------------------------------------------------------------
    // javaType
    // -------------------------------------------------------------------------

    @Test
    public void javaType_singularAttribute_returnsBindableJavaType() {
        // SingularAttribute is Bindable → getBindableJavaType() is used
        Class<?> result = ProjectionHelper.javaType(Department_.mandatoryDepName);
        assertEquals(String.class, result);
    }

    @Test
    public void javaType_singularEntityAttribute_returnsEntityClass() {
        // SingularAttribute<Employee,Department> → bindable type is Department
        Class<?> result = ProjectionHelper.javaType(Employee_.mandatoryDepartment);
        assertEquals(Department.class, result);
    }

    @Test
    public void javaType_listAttribute_returnsElementType() {
        // ListAttribute is Bindable → getBindableJavaType() returns the element type
        Class<?> result = ProjectionHelper.javaType(Department_.employees);
        assertEquals(Employee.class, result);
    }

    @Test
    public void javaType_listAttributeOfIntegers_returnsIntegerClass() {
        // ListAttribute<Department,Integer> → element type is Integer
        Class<?> result = ProjectionHelper.javaType(Department_.numbers);
        assertEquals(Integer.class, result);
    }

    @Test
    public void javaType_singularIdAttribute_returnsIdSubclass() {
        // SingularAttribute<Department,Department.ID> → bindable type is Department.ID
        Class<?> result = ProjectionHelper.javaType(Department_.id);
        assertEquals(Department.ID.class, result);
    }

    // -------------------------------------------------------------------------
    // prepareProjectingQuery / finalizeProjectingQuery – via dao
    // (These methods are exercised end-to-end through the Dao infrastructure)
    // -------------------------------------------------------------------------

    @Test
    public void prepareProjectingQuery_idProjection_returnsId() {
        Department dep = new Department("test");
        persist(dep);

        Id<?> result = dao.get(query.all(Department.class), Project.id());

        assertEquals(dep.getId(), result);
    }

    @Test
    public void prepareProjectingQuery_singleValueAttribute_returnsValue() {
        Department dep = new Department("hello");
        persist(dep);

        String name = dao.get(query.all(Department.class), Project.value(Department_.mandatoryDepName));

        assertEquals("hello", name);
    }

    @Test
    public void prepareProjectingQuery_singularEntityAttribute_returnsEntity() {
        Department dep = new Department();
        Employee emp = new Employee("emp", dep);
        persist(dep, emp);

        // $$$$ (4 $'s) = Dto(ENTITY, IEntity<?>)
        Dto dto = dao.get(query.all(Employee.class),
                Dto_.$$$$(literal(ENTITY.a), Employee_.mandatoryDepartment));

        assertNotNull(dto.value);
        assertEquals(dep.getId(), ((Department) dto.value).getId());
    }

    @Test
    public void prepareProjectingQuery_optionalAttribute_present_returnsSome() {
        Department dep = new Department();
        Employee emp = new Employee("", new Money(99), dep);
        persist(dep, emp);

        Option<Money> result = dao.get(query.all(Employee.class),
                Project.value(Cast.optional(Employee_.optionalSalary)));

        assertEquals(Some(new Money(99)), result);
    }

    @Test
    public void prepareProjectingQuery_optionalAttribute_absent_returnsNone() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);

        Option<Money> result = dao.get(query.all(Employee.class),
                Project.value(Cast.optional(Employee_.optionalSalary)));

        assertEquals(None(), result);
    }

    @Test
    public void prepareProjectingQuery_twoAttributes_returnsTuple() {
        Department dep = new Department("name1", 42);
        persist(dep);

        Tuple2<String, Integer> result = dao.get(query.all(Department.class),
                Project.pair(Department_.mandatoryDepName, Department_.mandatoryNumber));

        assertEquals("name1", result._1);
        assertEquals(Integer.valueOf(42), result._2);
    }

    @Test
    public void prepareProjectingQuery_multipleEntities_returnsAll() {
        Department dep1 = new Department("a");
        Department dep2 = new Department("b");
        persist(dep1, dep2);

        Collection<Id<Department>> ids = dao.getMany(query.all(Department.class), Project.<Department>id());

        assertEquals(2, ids.size());
        assertTrue(ids.contains(dep1.getId()));
        assertTrue(ids.contains(dep2.getId()));
    }

    // -------------------------------------------------------------------------
    // finalizeProjectingQuery – result transformation including embeddables
    // -------------------------------------------------------------------------

    @Test
    public void finalizeProjectingQuery_embeddableAttribute_returnsEmbeddable() {
        // Department(Report) constructor sets mandatoryReport
        Report report = new Report(2024);
        Department dep = new Department(report);
        persist(dep);

        // $$$$$ (5 $'s) = Dto(EMBEDDABLE, Report)
        Dto dto = dao.get(query.all(Department.class),
                Dto_.$$$$$(literal(EMBEDDABLE.a), Department_.mandatoryReport));

        assertNotNull(dto.value);
        assertEquals(2024, ((Report) dto.value).getYear().intValue());
    }

    @Test
    public void finalizeProjectingQuery_optionalEmbeddable_present_returnsSome() {
        // Department(Report) sets both mandatoryReport and optionalDepReport
        Report report = new Report(2025);
        Department dep = new Department(report);
        persist(dep);

        // $$$$$$$$$ (9 $'s) = Dto(OPTIONAL_EMBEDDABLE, Option<Report>)
        Dto dto = dao.get(query.all(Department.class),
                Dto_.$$$$$$$$$(literal(OPTIONAL_EMBEDDABLE.a), Cast.optional(Department_.optionalDepReport)));

        Option<?> optVal = (Option<?>) dto.value;
        assertTrue(optVal.isDefined());
        assertEquals(2025, ((Report) optVal.get()).getYear().intValue());
    }

    @Test
    public void finalizeProjectingQuery_optionalEmbeddable_absent_returnsNone() {
        Department dep = new Department();
        persist(dep);

        Dto dto = dao.get(query.all(Department.class),
                Dto_.$$$$$$$$$(literal(OPTIONAL_EMBEDDABLE.a), Cast.optional(Department_.optionalDepReport)));

        assertEquals(None(), dto.value);
    }

    // -------------------------------------------------------------------------
    // performAdditionalQueriesForPlaceholderValues – collection attributes
    // -------------------------------------------------------------------------

    @Test
    public void performAdditionalQueriesForPlaceholderValues_listOfIds_returnsOrderedList() {
        Department dep = new Department();
        Employee emp1 = new Employee("first", dep);
        Employee emp2 = new Employee("second", dep);
        persist(dep, emp1, emp2);

        // $$$$$$$$$$$$$$$$$$ (18 $'s) = Dto(LIST_OF_IDS, ListAttribute)
        Dto dto = dao.get(query.all(Department.class),
                Dto_.$$$$$$$$$$$$$$$$$$(literal(LIST_OF_IDS.a),
                        Related.projection(Department_.employees, Project.id())));

        List<?> ids = (List<?>) dto.value;
        assertEquals(2, ids.size());
        assertTrue(ids.contains(emp1.getId()));
        assertTrue(ids.contains(emp2.getId()));
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_listOfIds_emptyCollection() {
        Department dep = new Department();
        persist(dep);

        Dto dto = dao.get(query.all(Department.class),
                Dto_.$$$$$$$$$$$$$$$$$$(literal(LIST_OF_IDS.a),
                        Related.projection(Department_.employees, Project.id())));

        assertEquals(emptyList(), dto.value);
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_setOfIds_returnsSet() {
        Municipality mun = new Municipality();
        Department dep = new Department();
        Employee emp1 = new Employee("e1", dep, mun);
        Employee emp2 = new Employee("e2", dep, mun);
        persist(mun, dep, emp1, emp2);

        // $$$$$$$$$$$$$$ (14 $'s) = Dto(SET_OF_IDS, SetAttribute)
        Dto dto = dao.get(query.all(Municipality.class),
                Dto_.$$$$$$$$$$$$$$(literal(SET_OF_IDS.a),
                        Related.projection(Municipality_.emps, Project.id())));

        Set<?> ids = (Set<?>) dto.value;
        assertEquals(newSet(emp1.getId(), emp2.getId()), ids);
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_setOfIds_emptyCollection() {
        Municipality mun = new Municipality();
        persist(mun);

        Dto dto = dao.get(query.all(Municipality.class),
                Dto_.$$$$$$$$$$$$$$(literal(SET_OF_IDS.a),
                        Related.projection(Municipality_.emps, Project.id())));

        assertEquals(emptySet(), dto.value);
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_listOfValues_returnsValues() {
        // Department(List<Integer>) constructor sets numbers
        Department dep = new Department(newList(10, 20, 30));
        persist(dep);

        // $$$$$$$$$$$$$$$$$$$ (19 $'s) = Dto(LIST_OF_VALUES, ListAttribute)
        Dto dto = dao.get(query.all(Department.class),
                Dto_.$$$$$$$$$$$$$$$$$$$(literal(LIST_OF_VALUES.a), Department_.numbers));

        List<?> numbers = (List<?>) dto.value;
        assertEquals(3, numbers.size());
        assertTrue(numbers.contains(10));
        assertTrue(numbers.contains(20));
        assertTrue(numbers.contains(30));
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_optionalSingularValue_present_returnsSome() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);

        // $$$$$$$$ (8 $'s) = Dto(OPTIONAL_VALUE, Option<?>)
        // Use Related.value to project the optional municipality id
        Dto dto = dao.get(query.all(Employee.class),
                Dto_.$$$$$$$$(literal(OPTIONAL_VALUE.a),
                        Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.id))));

        assertTrue(((Option<?>) dto.value).isDefined());
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_optionalSingularValue_absent_returnsNone() {
        Department dep = new Department();
        Employee emp = new Employee("", dep);
        persist(dep, emp);

        Dto dto = dao.get(query.all(Employee.class),
                Dto_.$$$$$$$$(literal(OPTIONAL_VALUE.a),
                        Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.id))));

        assertEquals(None(), dto.value);
    }

    @Test
    public void performAdditionalQueriesForPlaceholderValues_multipleSourceEntities_eachGetsOwnCollection() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        Employee emp1 = new Employee("a", dep1);
        Employee emp2 = new Employee("b", dep1);
        Employee emp3 = new Employee("c", dep2);
        persist(dep1, dep2, emp1, emp2, emp3);

        Collection<Dto> dtos = dao.getMany(query.all(Department.class),
                Dto_.$$$$$$$$$$$$$$$$$$(literal(LIST_OF_IDS.a),
                        Related.projection(Department_.employees, Project.id())));

        assertEquals(2, dtos.size());
        int totalEmployees = 0;
        for (Dto dto : dtos) {
            totalEmployees += ((List<?>) dto.value).size();
        }
        assertEquals(3, totalEmployees);
    }
}
