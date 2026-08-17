package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.query.projection.Select.literal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Dto.COLLECTION_OF_IDS;
import fi.solita.utils.query.Dto.ID;
import fi.solita.utils.query.Dto.OPTIONAL_ID;
import fi.solita.utils.query.Dto.SET_OF_IDS;
import fi.solita.utils.query.Dto.SET_OF_VALUES;
import fi.solita.utils.query.Dto_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public class ProjectionUtilTest extends QueryTestBase {

    // -------------------------------------------------------------------------
    // objectToObjectList
    // -------------------------------------------------------------------------

    @Test
    public void objectToObjectList_objectArray_returnsList() {
        Object[] arr = new Object[]{"a", "b", "c"};
        Iterable<Object> result = ProjectionUtil.objectToObjectList(arr);
        assertEquals(newList("a", "b", "c"), newList(result));
    }

    @Test
    public void objectToObjectList_singleObject_wrapsInList() {
        Iterable<Object> result = ProjectionUtil.objectToObjectList("hello");
        assertEquals(newList("hello"), newList(result));
    }

    @Test
    public void objectToObjectList_null_wrapsInList() {
        Iterable<Object> result = ProjectionUtil.objectToObjectList(null);
        List<Object> asList = newList(result);
        assertEquals(1, asList.size());
        assertEquals(null, asList.get(0));
    }

    @Test
    public void objectToObjectList_emptyArray_returnsEmptyList() {
        Object[] arr = new Object[]{};
        Iterable<Object> result = ProjectionUtil.objectToObjectList(arr);
        assertEquals(emptyList(), newList(result));
    }

    // -------------------------------------------------------------------------
    // shouldPerformAdditionalQuery
    // -------------------------------------------------------------------------

    @Test
    public void shouldPerformAdditionalQuery_singularAttribute_false() {
        // Plain singular attribute — no additional query needed
        assertFalse(ProjectionUtil.shouldPerformAdditionalQuery(Employee_.mandatoryName));
    }

    @Test
    public void shouldPerformAdditionalQuery_pluralListAttribute_true() {
        // ListAttribute (plural) always needs an additional query
        assertTrue(ProjectionUtil.shouldPerformAdditionalQuery(Department_.employees));
    }

    @Test
    public void shouldPerformAdditionalQuery_pluralNumbersAttribute_true() {
        // Another ListAttribute (plural) always needs an additional query
        assertTrue(ProjectionUtil.shouldPerformAdditionalQuery(Department_.numbers));
    }

    @Test
    public void shouldPerformAdditionalQuery_additionalQueryPerformingAttribute_true() {
        // Related.projection wraps the attribute in AdditionalQueryPerformingAttribute
        assertTrue(ProjectionUtil.shouldPerformAdditionalQuery(
            Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryNumber))));
    }

    @Test
    public void shouldPerformAdditionalQuery_singularPseudoAttribute_false() {
        // A literal pseudo-attribute is singular and pseudo → not an additional query
        assertFalse(ProjectionUtil.shouldPerformAdditionalQuery(literal(42)));
    }

    // -------------------------------------------------------------------------
    // isId
    // -------------------------------------------------------------------------

    @Test
    public void isId_departmentId_true() {
        assertTrue(ProjectionUtil.isId(Department.ID.class));
    }

    @Test
    public void isId_employeeId_true() {
        assertTrue(ProjectionUtil.isId(Employee.ID.class));
    }

    @Test
    public void isId_idInterface_true() {
        assertTrue(ProjectionUtil.isId(Id.class));
    }

    @Test
    public void isId_string_false() {
        assertFalse(ProjectionUtil.isId(String.class));
    }

    @Test
    public void isId_integer_false() {
        assertFalse(ProjectionUtil.isId(Integer.class));
    }

    @Test
    public void isId_entity_false() {
        assertFalse(ProjectionUtil.isId(Department.class));
    }

    // -------------------------------------------------------------------------
    // isWrapperOfIds
    // -------------------------------------------------------------------------

    @Test
    public void isWrapperOfIds_optionalId_index1_true() {
        // Dto_.$$$$$$ (OPTIONAL_ID constructor): getIndexesOfIdWrappingParameters() = [1]
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$
                (literal(OPTIONAL_ID.a), Cast.optional(Department_.id));
        assertTrue(ProjectionUtil.isWrapperOfIds(proj, 1));
    }

    @Test
    public void isWrapperOfIds_optionalId_index0_false() {
        // The enum placeholder at index 0 is not an id-wrapping parameter
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$
                (literal(OPTIONAL_ID.a), Cast.optional(Department_.id));
        assertFalse(ProjectionUtil.isWrapperOfIds(proj, 0));
    }

    @Test
    public void isWrapperOfIds_id_index1_true() {
        // Dto_.$$ (ID constructor): getIndexesOfIdWrappingParameters() = [] but index 1 holds the id
        // Actually $$ returns [] for indexes → index 1 is false
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$(literal(ID.a), Department_.id);
        assertFalse(ProjectionUtil.isWrapperOfIds(proj, 1));
    }

    @Test
    public void isWrapperOfIds_collectionOfIds_index1_true() {
        // COLLECTION_OF_IDS metaconstructor: getIndexesOfIdWrappingParameters() = [1]
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$$$$$(
                literal(COLLECTION_OF_IDS.a), Department_.employees);
        assertTrue(ProjectionUtil.isWrapperOfIds(proj, 1));
    }

    @Test
    public void isWrapperOfIds_setOfIds_index1_true() {
        // SET_OF_IDS metaconstructor: getIndexesOfIdWrappingParameters() = [1]
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$$$$$$$$$
                (literal(SET_OF_IDS.a), Municipality_.emps);
        assertTrue(ProjectionUtil.isWrapperOfIds(proj, 1));
    }

    @Test
    public void isWrapperOfIds_setOfValues_index1_false() {
        // SET_OF_VALUES metaconstructor: getIndexesOfIdWrappingParameters() = []
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$$$$$$$$$$(
                literal(SET_OF_VALUES.a), Municipality_.emps);
        assertFalse(ProjectionUtil.isWrapperOfIds(proj, 1));
    }

    // -------------------------------------------------------------------------
    // isDistinctable
    // -------------------------------------------------------------------------

    @Test
    public void isDistinctable_valueAttribute_false() {
        // Project.value(mandatoryDepName): constructorParameterType is String.class (not Bindable),
        // attribute javaType is String (not NotDistinctable) → returns false
        MetaJpaConstructor<?, ?, ?> proj = Project.value(Department_.mandatoryDepName);
        assertFalse(ProjectionUtil.isDistinctable(proj, 0));
    }

    @Test
    public void isDistinctable_optionalIdAtIndex1_false() {
        // OPTIONAL_ID constructor: constructorParameterType at index 1 is Option.class (not Bindable)
        // and attribute javaType is Optional<Department.ID> (not NotDistinctable) → returns false
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$
                (literal(OPTIONAL_ID.a), Cast.optional(Department_.id));
        assertFalse(ProjectionUtil.isDistinctable(proj, 1));
    }

    @Test
    public void isDistinctable_setOfValues_noException() {
        // SET_OF_VALUES: index 0 = SET_OF_VALUES.class (not Bindable, not NotDistinctable) → false
        // index 1 = Set.class (not Bindable, not NotDistinctable) → false
        MetaJpaConstructor<?, ?, ?> proj = Dto_.$$$$$$$$$$$$$$$(
                literal(SET_OF_VALUES.a), Municipality_.emps);
        assertFalse(ProjectionUtil.isDistinctable(proj, 0));
        assertFalse(ProjectionUtil.isDistinctable(proj, 1));
    }
}