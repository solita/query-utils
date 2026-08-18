package fi.solita.utils.query.backend.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple1;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.functional.Tuple4;

public class TupleResultTransformerTest {

    // -------------------------------------------------------------------------
    // constructor
    // -------------------------------------------------------------------------

    @Test
    public void constructor_acceptsTwoAliases() {
        new TupleResultTransformer(new String[]{"a", "b"});
    }

    @Test
    public void constructor_acceptsThreeAliases() {
        new TupleResultTransformer(new String[]{"a", "b", "c"});
    }

    @Test
    public void constructor_acceptsSingleAlias() {
        new TupleResultTransformer(new String[]{"a"});
    }

    // -------------------------------------------------------------------------
    // transformList
    // -------------------------------------------------------------------------

    @Test
    public void transformList_returnsOriginalCollection() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b"});
        List<Object> collection = Arrays.<Object>asList("x", "y");
        List<Object> result = transformer.transformList(collection);
        assertSame(collection, result);
    }

    @Test
    public void transformList_returnsEmptyListUnchanged() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b"});
        List<Object> empty = Collections.<Object>emptyList();
        assertSame(empty, transformer.transformList(empty));
    }

    // -------------------------------------------------------------------------
    // transformTuple - two aliases => Pair
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_returnsPair_whenTwoAliases() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b"});
        Object result = transformer.transformTuple(new Object[]{"left", "right"}, new String[]{"a", "b"});
        assertTrue("Expected Pair", result instanceof Pair);
    }

    @Test
    public void transformTuple_pairHasCorrectValues_whenTwoAliases() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b"});
        Pair<?, ?> pair = (Pair<?, ?>) transformer.transformTuple(
                new Object[]{"left", "right"}, new String[]{"a", "b"});
        assertEquals("left", pair._1);
        assertEquals("right", pair._2);
    }

    @Test
    public void transformTuple_pairRespectesAliasOrder_whenTupleOrderDiffers() {
        // aliases passed to constructor say [a, b], but DB returns them as [b, a]
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b"});
        Pair<?, ?> pair = (Pair<?, ?>) transformer.transformTuple(
                new Object[]{"right", "left"}, new String[]{"b", "a"});
        // a => "left", b => "right"
        assertEquals("left", pair._1);
        assertEquals("right", pair._2);
    }

    @Test
    public void transformTuple_pairSupportsNullValues() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b"});
        Pair<?, ?> pair = (Pair<?, ?>) transformer.transformTuple(
                new Object[]{null, "right"}, new String[]{"a", "b"});
        assertEquals(null, pair._1);
        assertEquals("right", pair._2);
    }

    // -------------------------------------------------------------------------
    // transformTuple - more than two aliases => Tuple
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_returnsTuple_whenThreeAliases() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c"});
        Object result = transformer.transformTuple(
                new Object[]{"v1", "v2", "v3"}, new String[]{"a", "b", "c"});
        assertTrue("Expected Tuple", result instanceof Tuple);
    }

    @Test
    public void transformTuple_tupleHasCorrectValues_whenThreeAliases() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c"});
        Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>) transformer.transformTuple(
                new Object[]{"v1", "v2", "v3"}, new String[]{"a", "b", "c"});
        assertEquals("v1", tuple._1);
        assertEquals("v2", tuple._2);
        assertEquals("v3", tuple._3);
    }

    @Test
    public void transformTuple_tupleRespectesAliasOrder_whenThreeAliasesReordered() {
        // constructor aliases: [a, b, c]; DB returns: [c, a, b]
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c"});
        Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>) transformer.transformTuple(
                new Object[]{"vC", "vA", "vB"}, new String[]{"c", "a", "b"});
        // position 0 should be alias "a" => "vA"
        assertEquals("vA", tuple._1);
        assertEquals("vB", tuple._2);
        assertEquals("vC", tuple._3);
    }

    @Test
    public void transformTuple_tupleSupportsNullValues() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c"});
        Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>) transformer.transformTuple(
                new Object[]{"v1", null, "v3"}, new String[]{"a", "b", "c"});
        assertEquals("v1", tuple._1);
        assertEquals(null, tuple._2);
        assertEquals("v3", tuple._3);
    }

    // -------------------------------------------------------------------------
    // transformTuple - single alias => Tuple1
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_returnsTuple1_whenSingleAlias() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a"});
        Object result = transformer.transformTuple(new Object[]{"only"}, new String[]{"a"});
        assertTrue("Expected Tuple1", result instanceof Tuple1);
    }

    @Test
    public void transformTuple_tuple1HasCorrectValue_whenSingleAlias() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a"});
        Tuple1<?> tuple = (Tuple1<?>) transformer.transformTuple(new Object[]{"only"}, new String[]{"a"});
        assertEquals("only", tuple._1);
    }

    @Test
    public void transformTuple_tuple1SupportsNullValue() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a"});
        Tuple1<?> tuple = (Tuple1<?>) transformer.transformTuple(new Object[]{null}, new String[]{"a"});
        assertEquals(null, tuple._1);
    }

    // -------------------------------------------------------------------------
    // transformTuple - four aliases => Tuple4
    // -------------------------------------------------------------------------

    @Test
    public void transformTuple_returnsTuple4_whenFourAliases() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c", "d"});
        Object result = transformer.transformTuple(
                new Object[]{"v1", "v2", "v3", "v4"}, new String[]{"a", "b", "c", "d"});
        assertTrue("Expected Tuple4", result instanceof Tuple4);
    }

    @Test
    public void transformTuple_tuple4HasCorrectValues_whenFourAliases() {
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c", "d"});
        Tuple4<?, ?, ?, ?> tuple = (Tuple4<?, ?, ?, ?>) transformer.transformTuple(
                new Object[]{"v1", "v2", "v3", "v4"}, new String[]{"a", "b", "c", "d"});
        assertEquals("v1", tuple._1);
        assertEquals("v2", tuple._2);
        assertEquals("v3", tuple._3);
        assertEquals("v4", tuple._4);
    }

    @Test
    public void transformTuple_tuple4RespectesAliasOrder_whenFourAliasesReordered() {
        // constructor aliases: [a, b, c, d]; DB returns: [d, b, a, c]
        TupleResultTransformer transformer = new TupleResultTransformer(new String[]{"a", "b", "c", "d"});
        Tuple4<?, ?, ?, ?> tuple = (Tuple4<?, ?, ?, ?>) transformer.transformTuple(
                new Object[]{"vD", "vB", "vA", "vC"}, new String[]{"d", "b", "a", "c"});
        assertEquals("vA", tuple._1);
        assertEquals("vB", tuple._2);
        assertEquals("vC", tuple._3);
        assertEquals("vD", tuple._4);
    }

    // -------------------------------------------------------------------------
    // getter (static helper)
    // -------------------------------------------------------------------------

    @Test
    public void getter_returnsCorrectValue_forKnownAlias() {
        List<String> aliases = Arrays.asList("x", "y", "z");
        Object[] tuple = new Object[]{"vx", "vy", "vz"};
        Function<String, Object> getter = TupleResultTransformer.getter(aliases, tuple);
        assertEquals("vx", getter.apply("x"));
        assertEquals("vy", getter.apply("y"));
        assertEquals("vz", getter.apply("z"));
    }

    @Test
    public void getter_returnsNull_whenValueIsNull() {
        List<String> aliases = Arrays.asList("a", "b");
        Object[] tuple = new Object[]{null, "v"};
        Function<String, Object> getter = TupleResultTransformer.getter(aliases, tuple);
        assertEquals(null, getter.apply("a"));
    }
}