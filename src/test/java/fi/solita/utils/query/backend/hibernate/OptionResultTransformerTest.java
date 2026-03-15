package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.generation.NativeQuery;

public class OptionResultTransformerTest {

    private static Pair<String, Option<Type<?>>> retval(String col, Option<Type<?>> type) {
        return Pair.of(col, type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Option<Type<?>> optional() {
        return (Option<Type<?>>) (Option) Some(new Type.Optional<>(new DummyType()));
    }

    // -------------------------------------------------------------------------
    // constructor
    // -------------------------------------------------------------------------

    @Test
    public void constructor_acceptsEmptyList() {
        new OptionResultTransformer(Collections.<Pair<String, Option<Type<?>>>>emptyList());
    }

    @Test
    public void constructor_acceptsSingleRetval() {
        new OptionResultTransformer(newList(retval("col", Option.<Type<?>>None())));
    }

    @Test
    public void constructor_acceptsMultipleRetvals() {
        new OptionResultTransformer(newList(
                retval("col1", Option.<Type<?>>None()),
                retval("col2", optional())));
    }

    // -------------------------------------------------------------------------
    // transformList
    // -------------------------------------------------------------------------

    @Test
    public void transformList_returnsOriginalCollection() {
        OptionResultTransformer transformer = new OptionResultTransformer(Collections.<Pair<String, Option<Type<?>>>>emptyList());
        List<Object> collection = Arrays.<Object>asList("a", "b", "c");
        List<Object> result = transformer.transformList(collection);
        assertSame(collection, result);
    }

    @Test
    public void transformList_returnsEmptyListUnchanged() {
        OptionResultTransformer transformer = new OptionResultTransformer(Collections.<Pair<String, Option<Type<?>>>>emptyList());
        List<Object> collection = Collections.<Object>emptyList();
        List<Object> result = transformer.transformList(collection);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // transformTuple - single value, no Optional type
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_returnsSingleValue_whenOnlyOneElement() {
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval("col", Option.<Type<?>>None())));
        Object result = transformer.transformTuple(new Object[]{"value"}, new String[]{"col"});
        assertEquals("value", result);
    }

    @Test
    public void transformTuple_returnsNullValue_whenValueIsNullAndTypeIsNone() {
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval("col", Option.<Type<?>>None())));
        Object result = transformer.transformTuple(new Object[]{null}, new String[]{"col"});
        assertEquals(null, result);
    }

    // -------------------------------------------------------------------------
    // transformTuple - single value, Optional type
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_wrapsValueInSome_whenTypeIsOptional() {
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval("col", optional())));
        Object result = transformer.transformTuple(new Object[]{"value"}, new String[]{"col"});
        assertTrue("Result should be an Option", result instanceof Option);
        Option<?> opt = (Option<?>) result;
        assertTrue("Option should be defined", opt.isDefined());
        assertEquals("value", opt.get());
    }

    @Test
    public void transformTuple_wrapsNullInNone_whenTypeIsOptionalAndValueIsNull() {
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval("col", optional())));
        Object result = transformer.transformTuple(new Object[]{null}, new String[]{"col"});
        assertTrue("Result should be an Option", result instanceof Option);
        Option<?> opt = (Option<?>) result;
        assertFalse("Option should be empty (None) for null value", opt.isDefined());
    }

    // -------------------------------------------------------------------------
    // transformTuple - multiple values
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_returnsArray_whenMultipleElements() {
        OptionResultTransformer transformer = new OptionResultTransformer(newList(
                retval("col1", Option.<Type<?>>None()),
                retval("col2", Option.<Type<?>>None())));
        Object result = transformer.transformTuple(new Object[]{"a", "b"}, new String[]{"col1", "col2"});
        assertTrue("Result should be an Object array", result instanceof Object[]);
        Object[] arr = (Object[]) result;
        assertEquals(2, arr.length);
        assertEquals("a", arr[0]);
        assertEquals("b", arr[1]);
    }

    @Test
    public void transformTuple_wrapsOptionalColumn_inMultiColumnResult() {
        OptionResultTransformer transformer = new OptionResultTransformer(newList(
                retval("col1", Option.<Type<?>>None()),
                retval("col2", optional())));
        Object result = transformer.transformTuple(new Object[]{"a", "b"}, new String[]{"col1", "col2"});
        Object[] arr = (Object[]) result;
        assertEquals("a", arr[0]);
        assertTrue("Second element should be wrapped in Option", arr[1] instanceof Option);
        assertEquals("b", ((Option<?>) arr[1]).get());
    }

    @Test
    public void transformTuple_wrapsNullOptionalColumn_inMultiColumnResult() {
        OptionResultTransformer transformer = new OptionResultTransformer(newList(
                retval("col1", Option.<Type<?>>None()),
                retval("col2", optional())));
        Object result = transformer.transformTuple(new Object[]{"a", null}, new String[]{"col1", "col2"});
        Object[] arr = (Object[]) result;
        assertEquals("a", arr[0]);
        assertTrue("Second element should be an Option", arr[1] instanceof Option);
        assertFalse("Second element should be None for null value", ((Option<?>) arr[1]).isDefined());
    }

    // -------------------------------------------------------------------------
    // transformTuple - ENTITY_RETURN_VALUE special case
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_handlesEntityReturnValue_withOptionalType() {
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval(NativeQuery.ENTITY_RETURN_VALUE, optional())));
        Object result = transformer.transformTuple(new Object[]{"entity"}, new String[]{NativeQuery.ENTITY_RETURN_VALUE});
        assertTrue("Result should be an Option", result instanceof Option);
        assertEquals("entity", ((Option<?>) result).get());
    }

    @Test
    public void transformTuple_handlesEntityReturnValue_withNoneType() {
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval(NativeQuery.ENTITY_RETURN_VALUE, Option.<Type<?>>None())));
        Object result = transformer.transformTuple(new Object[]{"entity"}, new String[]{NativeQuery.ENTITY_RETURN_VALUE});
        assertEquals("entity", result);
    }

    @Test
    public void transformTuple_entityReturnValueAlias_matchesByIdentity() {
        // The special-case uses == (identity) comparison for ENTITY_RETURN_VALUE
        OptionResultTransformer transformer = new OptionResultTransformer(
                newList(retval(NativeQuery.ENTITY_RETURN_VALUE, optional())));
        // Aliases with one entry and retvals with one key matching ENTITY_RETURN_VALUE by identity
        Object result = transformer.transformTuple(new Object[]{"entity"}, new String[]{NativeQuery.ENTITY_RETURN_VALUE});
        assertTrue(result instanceof Option);
        assertTrue(((Option<?>) result).isDefined());
    }

    // -------------------------------------------------------------------------
    // helper
    // -------------------------------------------------------------------------

    private static class DummyType implements Type<String> {}
}
