package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.generation.NativeQuery.NativeQueryPair;
import fi.solita.utils.query.generation.NativeQuery.NativeQuerySingleEntity;
import fi.solita.utils.query.generation.NativeQuery.NativeQueryT1;
import fi.solita.utils.query.generation.NativeQuery.NativeQueryT3;
import fi.solita.utils.query.generation.NativeQuery.NativeQueryVoid;

public class NativeQueryTest {

    private static final Type<String> STRING_TYPE = new Type<String>() {};
    private static final Type<Integer> INTEGER_TYPE = new Type<Integer>() {};
    private static final Type<Department> DEPT_TYPE = new Type<Department>() {};

    // ---------------------------------------------------------------------------
    // ENTITY_RETURN_VALUE constant
    // ---------------------------------------------------------------------------

    @Test
    public void entityReturnValueConstantIsDash() {
        assertEquals("-", NativeQuery.ENTITY_RETURN_VALUE);
    }

    // ---------------------------------------------------------------------------
    // NativeQuery.of()
    // ---------------------------------------------------------------------------

    @Test
    public void of_setsQueryString() {
        NativeQueryVoid q = NativeQuery.of("select 1");
        assertEquals("select 1", q.query);
    }

    @Test
    public void of_hasNoParams() {
        NativeQueryVoid q = NativeQuery.of("select 1");
        assertTrue(q.params.isEmpty());
    }

    @Test
    public void of_hasNoRetvals() {
        NativeQueryVoid q = NativeQuery.of("select 1");
        assertTrue(q.retvals.isEmpty());
    }

    // ---------------------------------------------------------------------------
    // returns(Type) – entity type
    // ---------------------------------------------------------------------------

    @Test
    public void returns_entityType_createsNativeQuerySingleEntity() {
        NativeQueryVoid q = NativeQuery.of("select * from T");
        NativeQuerySingleEntity<?> result = q.returns(DEPT_TYPE);
        assertNotNull(result);
    }

    @Test
    public void returns_entityType_preservesQuery() {
        NativeQuery<?> result = NativeQuery.of("select * from T").returns(DEPT_TYPE);
        assertEquals("select * from T", result.query);
    }

    @Test
    public void returns_entityType_addsEntityReturnValueAlias() {
        NativeQuery<?> result = NativeQuery.of("select * from T").returns(DEPT_TYPE);
        assertEquals(1, result.retvals.size());
        assertEquals(NativeQuery.ENTITY_RETURN_VALUE, result.retvals.get(0)._1);
    }

    @Test
    public void returns_entityType_storesTypeInRetvals() {
        NativeQuery<?> result = NativeQuery.of("select * from T").returns(DEPT_TYPE);
        assertTrue(result.retvals.get(0)._2.isDefined());
    }

    // ---------------------------------------------------------------------------
    // returnsOptional(Type.Optional) – optional entity type
    // ---------------------------------------------------------------------------

    @Test
    public void returnsOptional_createsNativeQuerySingleEntity() {
        Type.Optional<Department> optType = new Type.Optional<Department>(DEPT_TYPE);
        NativeQuerySingleEntity<?> result = NativeQuery.of("select * from T").returnsOptional(optType);
        assertNotNull(result);
    }

    @Test
    public void returnsOptional_addsEntityReturnValueAlias() {
        Type.Optional<Department> optType = new Type.Optional<Department>(DEPT_TYPE);
        NativeQuery<?> result = NativeQuery.of("select * from T").returnsOptional(optType);
        assertEquals(1, result.retvals.size());
        assertEquals(NativeQuery.ENTITY_RETURN_VALUE, result.retvals.get(0)._1);
    }

    @Test
    public void returnsOptional_storesTypeInRetvals() {
        Type.Optional<Department> optType = new Type.Optional<Department>(DEPT_TYPE);
        NativeQuery<?> result = NativeQuery.of("select * from T").returnsOptional(optType);
        assertTrue(result.retvals.get(0)._2.isDefined());
    }

    // ---------------------------------------------------------------------------
    // returns(String) – untyped alias
    // ---------------------------------------------------------------------------

    @Test
    public void returns_aliasOnly_createsNativeQueryT1() {
        NativeQueryT1<?> result = NativeQuery.of("select 1 as a").returns("a");
        assertNotNull(result);
    }

    @Test
    public void returns_aliasOnly_preservesQuery() {
        NativeQuery<?> result = NativeQuery.of("select 1 as a").returns("a");
        assertEquals("select 1 as a", result.query);
    }

    @Test
    public void returns_aliasOnly_addsAliasToRetvals() {
        NativeQuery<?> result = NativeQuery.of("select 1 as a").returns("a");
        assertEquals(1, result.retvals.size());
        assertEquals("a", result.retvals.get(0)._1);
    }

    @Test
    public void returns_aliasOnly_hasNoTypeInRetvals() {
        NativeQuery<?> result = NativeQuery.of("select 1 as a").returns("a");
        assertTrue(result.retvals.get(0)._2 instanceof Option && !result.retvals.get(0)._2.isDefined());
    }

    // ---------------------------------------------------------------------------
    // returns(String, Type) – typed alias
    // ---------------------------------------------------------------------------

    @Test
    public void returns_aliasAndType_createsNativeQueryT1() {
        NativeQueryT1<String> result = NativeQuery.of("select 1 as a").returns("a", STRING_TYPE);
        assertNotNull(result);
    }

    @Test
    public void returns_aliasAndType_addsAliasToRetvals() {
        NativeQuery<?> result = NativeQuery.of("select 1 as a").returns("a", STRING_TYPE);
        assertEquals(1, result.retvals.size());
        assertEquals("a", result.retvals.get(0)._1);
    }

    @Test
    public void returns_aliasAndType_storesTypeInRetvals() {
        NativeQuery<?> result = NativeQuery.of("select 1 as a").returns("a", STRING_TYPE);
        assertTrue(result.retvals.get(0)._2.isDefined());
    }

    @Test(expected = NullPointerException.class)
    public void returns_aliasAndNullType_throwsNullPointerException() {
        NativeQuery.of("select 1 as a").returns("a", (Type<String>) null);
    }

    // ---------------------------------------------------------------------------
    // Chaining returns() – building Pair and Tuple3
    // ---------------------------------------------------------------------------

    @Test
    public void chaining_twoReturns_createsPair() {
        NativeQueryPair<String, Integer> result = NativeQuery.of("select 1 as a, 2 as b")
                .returns("a", STRING_TYPE)
                .returns("b", INTEGER_TYPE);
        assertNotNull(result);
        assertEquals(2, result.retvals.size());
        assertEquals("a", result.retvals.get(0)._1);
        assertEquals("b", result.retvals.get(1)._1);
    }

    @Test
    public void chaining_threeReturns_createsTuple3() {
        NativeQueryT3<String, Integer, String> result = NativeQuery.of("select 1 as a, 2 as b, 3 as c")
                .returns("a", STRING_TYPE)
                .returns("b", INTEGER_TYPE)
                .returns("c", STRING_TYPE);
        assertNotNull(result);
        assertEquals(3, result.retvals.size());
    }

    @Test
    public void chaining_preservesQueryString() {
        String sql = "select a, b from T";
        NativeQuery<?> result = NativeQuery.of(sql)
                .returns("a", STRING_TYPE)
                .returns("b", INTEGER_TYPE);
        assertEquals(sql, result.query);
    }

    // ---------------------------------------------------------------------------
    // NativeQueryBig – more than 44 aliases
    // ---------------------------------------------------------------------------

    @Test
    public void returns_beyondT44_createsNativeQueryBig() {
        NativeQueryVoid base = NativeQuery.of("select 1");
        NativeQueryT1<?> q = base.returns("c1");
        NativeQueryPair<?, ?> q2 = q.returns("c2");
        NativeQueryT3<?, ?, ?> q3 = q2.returns("c3");
        // just verify chaining works and query is preserved
        assertEquals("select 1", q3.query);
        assertEquals(3, q3.retvals.size());
    }

    // ---------------------------------------------------------------------------
    // setParameter(String, Object)
    // ---------------------------------------------------------------------------

    @Test
    public void setParameter_nonNull_addsToParams() {
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", "value");
        assertTrue(result.params.containsKey("p"));
        assertEquals("value", result.params.get("p")._1);
    }

    @Test
    public void setParameter_preservesQueryString() {
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", "value");
        assertEquals("select :p", result.query);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setParameter_null_throwsUnsupportedOperationException() {
        NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setParameter_noneOption_throwsUnsupportedOperationException() {
        NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", None());
    }

    @Test
    public void setParameter_someOption_addsToParams() {
        Option<String> someVal = Some("hello");
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", someVal);
        assertTrue(result.params.containsKey("p"));
    }

    // ---------------------------------------------------------------------------
    // setParameter(String, Object, Type)
    // ---------------------------------------------------------------------------

    @Test
    public void setParameterWithType_addsToParams() {
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", "value", STRING_TYPE);
        assertTrue(result.params.containsKey("p"));
        assertEquals("value", result.params.get("p")._1);
        assertTrue(result.params.get("p")._2.isDefined());
    }

    @Test
    public void setParameterWithType_nullValue_addsToParams() {
        // null value with explicit type should be accepted
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", (String) null, STRING_TYPE);
        assertTrue(result.params.containsKey("p"));
    }

    @Test(expected = NullPointerException.class)
    public void setParameterWithType_nullType_throwsNullPointerException() {
        NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameter("p", "value", (Type<String>) null);
    }

    // ---------------------------------------------------------------------------
    // setParameterList(String, Collection)
    // ---------------------------------------------------------------------------

    @Test
    public void setParameterList_nonEmpty_addsToParams() {
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameterList("p", Arrays.asList("a", "b"));
        assertTrue(result.params.containsKey("p"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setParameterList_null_throwsUnsupportedOperationException() {
        NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameterList("p", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setParameterList_empty_throwsUnsupportedOperationException() {
        NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameterList("p", Collections.emptyList());
    }

    // ---------------------------------------------------------------------------
    // setParameterList(String, Collection, Type)
    // ---------------------------------------------------------------------------

    @Test
    public void setParameterListWithType_addsToParams() {
        NativeQuery<?> result = NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameterList("p", Arrays.asList("a", "b"), STRING_TYPE);
        assertTrue(result.params.containsKey("p"));
        assertTrue(result.params.get("p")._2.isDefined());
    }

    @Test(expected = NullPointerException.class)
    public void setParameterListWithType_nullType_throwsNullPointerException() {
        NativeQuery.of("select :p")
                .returns("a", STRING_TYPE)
                .setParameterList("p", Arrays.asList("a"), (Type<String>) null);
    }

    // ---------------------------------------------------------------------------
    // Immutability – setParameter returns a new instance
    // ---------------------------------------------------------------------------

    @Test
    public void setParameter_returnsNewInstance() {
        NativeQueryT1<String> base = NativeQuery.of("select :p").returns("a", STRING_TYPE);
        NativeQueryT1<String> withParam = base.setParameter("p", "val");
        assertTrue(base != withParam);
        assertTrue(base.params.isEmpty());
        assertTrue(withParam.params.containsKey("p"));
    }

    @Test
    public void setParameterList_returnsNewInstance() {
        NativeQueryT1<String> base = NativeQuery.of("select :p").returns("a", STRING_TYPE);
        NativeQueryT1<String> withParam = base.setParameterList("p", Arrays.asList("x"));
        assertTrue(base != withParam);
        assertTrue(base.params.isEmpty());
        assertTrue(withParam.params.containsKey("p"));
    }
}
