package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Money;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.Report;
import fi.solita.utils.query.generation.Cast;

public class ProjectionResultUtilTest extends QueryTestBase {

    @Autowired
    private fi.solita.utils.query.generation.JpaCriteriaQuery query;

    // -------------------------------------------------------------------------
    // isNoneOrNull
    // -------------------------------------------------------------------------

    @Test
    public void isNoneOrNull_null() {
        assertTrue(ProjectionResultUtil.isNoneOrNull(null));
    }

    @Test
    public void isNoneOrNull_none() {
        assertTrue(ProjectionResultUtil.isNoneOrNull(None()));
    }

    @Test
    public void isNoneOrNull_some() {
        assertFalse(ProjectionResultUtil.isNoneOrNull(Some("value")));
    }

    @Test
    public void isNoneOrNull_nonOptionValue() {
        assertFalse(ProjectionResultUtil.isNoneOrNull("hello"));
        assertFalse(ProjectionResultUtil.isNoneOrNull(42));
    }

    // -------------------------------------------------------------------------
    // optionGet
    // -------------------------------------------------------------------------

    @Test
    public void optionGet_some() {
        assertEquals("value", ProjectionResultUtil.optionGet(Some("value")));
    }

    @Test
    public void optionGet_nonOption() {
        assertEquals("plain", ProjectionResultUtil.optionGet("plain"));
        assertEquals(Integer.valueOf(7), ProjectionResultUtil.optionGet(7));
    }

    // -------------------------------------------------------------------------
    // wrapNullsToOptionsWhereAppropriate
    // -------------------------------------------------------------------------

    @Test
    public void wrapNullsToOptionsWhereAppropriate_optionalAttribute_null_becomesNone() {
        // Cast.optional() marks the attribute as optional for the query framework → null becomes None
        Object result = ProjectionResultUtil.wrapNullsToOptionsWhereAppropriate(Cast.optional(Employee_.optionalSalary), null);
        assertEquals(None(), result);
    }

    @Test
    public void wrapNullsToOptionsWhereAppropriate_optionalAttribute_value_becomesSome() {
        // Cast.optional() marks the attribute as optional for the query framework → value becomes Some
        Money salary = new Money(100);
        Object result = ProjectionResultUtil.wrapNullsToOptionsWhereAppropriate(Cast.optional(Employee_.optionalSalary), salary);
        assertEquals(Some(salary), result);
    }

    @Test
    public void wrapNullsToOptionsWhereAppropriate_optionTypedAttribute_null_becomesNone() {
        // Employee.optionAge is an Option<Integer> field → recognized as optional → null becomes None
        Object result = ProjectionResultUtil.wrapNullsToOptionsWhereAppropriate(Employee_.optionAge, null);
        assertEquals(None(), result);
    }

    @Test
    public void wrapNullsToOptionsWhereAppropriate_requiredAttribute_valuePassedThrough() {
        // Employee.mandatoryName is required → value should pass through unchanged
        Object result = ProjectionResultUtil.wrapNullsToOptionsWhereAppropriate(Employee_.mandatoryName, "test");
        assertEquals("test", result);
    }

    @Test
    public void wrapNullsToOptionsWhereAppropriate_requiredAttribute_null_passedThrough() {
        // For required attributes the value is not wrapped; null passes through
        Object result = ProjectionResultUtil.wrapNullsToOptionsWhereAppropriate(Employee_.mandatoryName, null);
        assertNull(result);
    }

    // -------------------------------------------------------------------------
    // removeNonesAndSomesFromCollections
    // -------------------------------------------------------------------------

    @Test
    public void removeNonesAndSomesFromCollections_nonCollection_passedThrough() {
        // mandatoryName is not a collection and not Option-wrapped
        Object result = ProjectionResultUtil.removeNonesAndSomesFromCollections(Employee_.mandatoryName, "hello");
        assertEquals("hello", result);
    }

    @Test
    public void removeNonesAndSomesFromCollections_list_removesNones_unwrapsSomes() {
        // Department.employees is a list attribute (plural)
        List<Object> input = new ArrayList<>();
        input.add(Some("a"));
        input.add(None());
        input.add(Some("b"));
        input.add(null);

        Object result = ProjectionResultUtil.removeNonesAndSomesFromCollections(Department_.employees, input);
        assertEquals(newList("a", "b"), result);
    }

    @Test
    public void removeNonesAndSomesFromCollections_set_removesNones_unwrapsSomes() {
        // Department.employees is a list; use numbers as a different collection type
        // Use a plain Set input to exercise the Set branch
        Set<Object> input = newSet((Object)Some("x"), None(), Some("y"));

        Object result = ProjectionResultUtil.removeNonesAndSomesFromCollections(Department_.employees, input);
        assertTrue(result instanceof Set);
        assertEquals(newSet("x", "y"), result);
    }

    @Test
    public void removeNonesAndSomesFromCollections_sortedSet_keepsComparableValues() {
        // SomeImpl is not Comparable, so a SortedSet holding Some instances would fail on insertion.
        // In practice the DB returns raw Comparable values in sorted sets; nulls are filtered out.
        SortedSet<Object> input = new TreeSet<>();
        input.add(1);
        input.add(3);

        Object result = ProjectionResultUtil.removeNonesAndSomesFromCollections(Department_.employees, input);
        assertTrue(result instanceof SortedSet);
        SortedSet<?> sortedResult = (SortedSet<?>) result;
        assertEquals(2, sortedResult.size());
        assertTrue(sortedResult.contains(1));
        assertTrue(sortedResult.contains(3));
    }

    // -------------------------------------------------------------------------
    // convertNullsToEmbeddableWhereRequired
    // -------------------------------------------------------------------------

    @Test
    public void convertNullsToEmbeddableWhereRequired_nonEmbeddable_nullPassedThrough() {
        // Employee.mandatoryName is not an embeddable → null passes through
        Object result = ProjectionResultUtil.convertNullsToEmbeddableWhereRequired(Employee_.mandatoryName, null);
        assertNull(result);
    }

    @Test
    public void convertNullsToEmbeddableWhereRequired_nonEmbeddable_valuePassedThrough() {
        Object result = ProjectionResultUtil.convertNullsToEmbeddableWhereRequired(Employee_.mandatoryName, "value");
        assertEquals("value", result);
    }

    @Test
    public void convertNullsToEmbeddableWhereRequired_requiredEmbeddable_null_instantiatesEmpty() {
        // Department.mandatoryReport is a required @Embedded → null should be replaced by an empty Report instance
        Object result = ProjectionResultUtil.convertNullsToEmbeddableWhereRequired(Department_.mandatoryReport, null);
        assertNotNull("required embeddable null should be replaced with empty instance", result);
        assertTrue(result instanceof Report);
    }

    @Test
    public void convertNullsToEmbeddableWhereRequired_requiredEmbeddable_nonNull_passedThrough() {
        Report report = new Report(2024);
        Object result = ProjectionResultUtil.convertNullsToEmbeddableWhereRequired(Department_.mandatoryReport, report);
        assertEquals(report, result);
    }

    @Test
    public void convertNullsToEmbeddableWhereRequired_optionalEmbeddable_null_passedThrough() {
        // Department.optionalDepReport is optional @Embedded → null passes through without instantiation
        Object result = ProjectionResultUtil.convertNullsToEmbeddableWhereRequired(Department_.optionalDepReport, null);
        assertNull("optional embeddable null should remain null", result);
    }

    // -------------------------------------------------------------------------
    // postProcessValue
    // -------------------------------------------------------------------------

    @Test
    public void postProcessValue_nullAttribute_passedThrough() {
        // null attr is used as placeholder; value passes through unchanged
        Object result = ProjectionResultUtil.postProcessValue(null, "rawValue");
        assertEquals("rawValue", result);
    }

    @Test
    public void postProcessValue_requiredStringAttribute_valuePassedThrough() {
        Object result = ProjectionResultUtil.postProcessValue(Employee_.mandatoryName, "Alice");
        assertEquals("Alice", result);
    }

    @Test
    public void postProcessValue_optionalAttribute_null_becomesNone() {
        // Cast.optional() makes the attribute recognized as optional → null becomes None
        Object result = ProjectionResultUtil.postProcessValue(Cast.optional(Employee_.optionalSalary), null);
        assertEquals(None(), result);
    }

    @Test
    public void postProcessValue_optionalAttribute_value_becomesSome() {
        // Cast.optional() makes the attribute recognized as optional → value becomes Some
        Money salary = new Money(42);
        Object result = ProjectionResultUtil.postProcessValue(Cast.optional(Employee_.optionalSalary), salary);
        assertEquals(Some(salary), result);
    }

    // -------------------------------------------------------------------------
    // postProcessResult
    // -------------------------------------------------------------------------

    @Test
    public void postProcessResult_singularRequired_singleElement() {
        List<Object> val = newList((Object)"hello");
        Object result = ProjectionResultUtil.postProcessResult(String.class, Employee_.mandatoryName, val);
        assertEquals("hello", result);
    }

    @Test
    public void postProcessResult_singularOptional_empty_returnsNull() {
        // optional singular with empty list → null (which will later be wrapped to None by wrapNulls)
        List<Object> val = emptyList();
        Object result = ProjectionResultUtil.postProcessResult(Money.class, Cast.optional(Employee_.optionalSalary), val);
        assertNull(result);
    }

    @Test
    public void postProcessResult_pluralList_returnsAsList() {
        List<Object> val = newList((Object)1, 2, 3);
        Object result = ProjectionResultUtil.postProcessResult(List.class, Department_.employees, val);
        assertTrue(result instanceof List);
        assertEquals(val, result);
    }

    @Test
    public void postProcessResult_pluralSet_returnsAsSet() {
        List<Object> val = newList((Object)"a", "b");
        Object result = ProjectionResultUtil.postProcessResult(Set.class, Department_.employees, val);
        assertTrue(result instanceof Set);
        assertEquals(newSet("a", "b"), result);
    }

    @Test
    public void postProcessResult_pluralArrayList_returnsAsArrayList() {
        List<Object> val = newList((Object)"x");
        Object result = ProjectionResultUtil.postProcessResult(ArrayList.class, Department_.employees, val);
        assertTrue(result instanceof ArrayList);
        assertEquals(1, ((Collection<?>)result).size());
    }

    @Test
    public void postProcessResult_pluralSortedSet_returnsAsSortedSet() {
        List<Object> val = newList((Object)3, 1, 2);
        Object result = ProjectionResultUtil.postProcessResult(SortedSet.class, Department_.numbers, val);
        assertTrue(result instanceof SortedSet);
        assertEquals(3, ((SortedSet<?>)result).size());
    }

    @Test
    public void postProcessResult_pluralTreeSet_returnsAsTreeSet() {
        List<Object> val = newList((Object)3, 1, 2);
        Object result = ProjectionResultUtil.postProcessResult(TreeSet.class, Department_.numbers, val);
        assertTrue(result instanceof TreeSet);
        assertEquals(3, ((TreeSet<?>)result).size());
    }

    @Test
    public void postProcessResult_pluralCollection_returnsAsCollection() {
        List<Object> val = newList((Object)"a", "b");
        Object result = ProjectionResultUtil.postProcessResult(Collection.class, Department_.employees, val);
        assertTrue(result instanceof Collection);
        assertEquals(2, ((Collection<?>)result).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void postProcessResult_singularRequired_multipleElements_throws() {
        List<Object> val = newList((Object)"a", "b");
        ProjectionResultUtil.postProcessResult(String.class, Employee_.mandatoryName, val);
    }
}