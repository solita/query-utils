package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.metamodel.Attribute;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public class ConstructorsTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;

    // =========================================================================
    // id()
    // =========================================================================

    @Test
    public void id_getParameters_returnsListWithOneNullElement() {
        MetaJpaConstructor<Department, Id<Department>, Id<Department>> proj = Constructors.id();
        List<Attribute<?, ?>> params = proj.getParameters();
        assertEquals(1, params.size());
        assertNull(params.get(0));
    }

    @Test
    public void id_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        MetaJpaConstructor<Department, Id<Department>, Id<Department>> proj = Constructors.id();
        assertEquals(emptyList(), proj.getIndexesOfIdWrappingParameters());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void id_getMember_throws() {
        Constructors.id().getMember();
    }

    @Test
    public void id_apply_returnsIdentity() {
        Department dep = new Department();
        persist(dep);
        Id<Department> id = dep.getId();
        MetaJpaConstructor<Department, Id<Department>, Id<Department>> proj = Constructors.id();
        assertSame(id, proj.apply(id));
    }

    @Test
    public void id_toString_containsClassName() {
        String s = Constructors.id().toString();
        assertNotNull(s);
        assertTrue(s.contains("IdProjection"));
    }

    @Test
    public void id_viaDao_returnsEntityId() {
        Department dep = new Department();
        persist(dep);
        Id<?> result = dao.get(query.all(Department.class), Project.id());
        assertEquals(dep.getId(), result);
    }

    // =========================================================================
    // value(SingularAttribute) — ValueAttributeProjection / TransparentProjection
    // =========================================================================

    @Test
    public void value_singular_getParameters_returnsAttribute() {
        MetaJpaConstructor<Department, String, String> proj = Constructors.value(Department_.mandatoryDepName);
        assertEquals(newList((Attribute<?, ?>) Department_.mandatoryDepName), proj.getParameters());
    }

    @Test
    public void value_singular_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        MetaJpaConstructor<Department, String, String> proj = Constructors.value(Department_.mandatoryDepName);
        assertEquals(emptyList(), proj.getIndexesOfIdWrappingParameters());
    }

    @Test
    public void value_singular_getConstructorParameterTypes_returnsAttributeJavaType() {
        MetaJpaConstructor<Department, String, String> proj = Constructors.value(Department_.mandatoryDepName);
        assertEquals(newList((Class<?>) String.class), proj.getConstructorParameterTypes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void value_singular_getMember_throws() {
        Constructors.value(Department_.mandatoryDepName).getMember();
    }

    @Test
    public void value_singular_apply_returnsIdentity() {
        MetaJpaConstructor<Department, String, String> proj = Constructors.value(Department_.mandatoryDepName);
        assertEquals("hello", proj.apply("hello"));
    }

    @Test
    public void value_singular_toString_containsTypeName() {
        String s = Constructors.value(Department_.mandatoryDepName).toString();
        assertNotNull(s);
        assertTrue(s.contains("ValueAttributeProjection") || s.contains("String"));
    }

    @Test
    public void value_singular_isTransparentProjection() {
        MetaJpaConstructor<Department, String, String> proj = Constructors.value(Department_.mandatoryDepName);
        assertTrue(proj instanceof Constructors.TransparentProjection);
        assertSame(Department_.mandatoryDepName, ((Constructors.TransparentProjection) proj).getWrapped());
    }

    @Test
    public void value_singular_viaDao_returnsValue() {
        Department dep = new Department("hello");
        persist(dep);
        String name = dao.get(query.all(Department.class), Project.value(Department_.mandatoryDepName));
        assertEquals("hello", name);
    }

    // =========================================================================
    // value(PluralAttribute) — ValueAttributeProjection / TransparentProjection
    // =========================================================================

    @Test
    public void value_plural_getParameters_returnsAttribute() {
        MetaJpaConstructor<Department, List<Integer>, List<Integer>> proj = Constructors.value(Department_.numbers);
        assertEquals(newList((Attribute<?, ?>) Department_.numbers), proj.getParameters());
    }

    @Test
    public void value_plural_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        MetaJpaConstructor<Department, List<Integer>, List<Integer>> proj = Constructors.value(Department_.numbers);
        assertEquals(emptyList(), proj.getIndexesOfIdWrappingParameters());
    }

    @Test
    public void value_plural_isTransparentProjection() {
        MetaJpaConstructor<Department, List<Integer>, List<Integer>> proj = Constructors.value(Department_.numbers);
        assertTrue(proj instanceof Constructors.TransparentProjection);
        assertSame(Department_.numbers, ((Constructors.TransparentProjection) proj).getWrapped());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void value_plural_getMember_throws() {
        Constructors.value(Department_.numbers).getMember();
    }

    // =========================================================================
    // pair()  — PairProjection
    // =========================================================================

    @Test
    public void pair_getParameters_returnsBothAttributes() {
        MetaJpaConstructor<Department, Pair<String, Integer>, ?> proj =
            Constructors.pair(Department_.mandatoryDepName, Department_.mandatoryNumber);
        List<Attribute<?, ?>> params = proj.getParameters();
        assertEquals(2, params.size());
        assertSame(Department_.mandatoryDepName, params.get(0));
        assertSame(Department_.mandatoryNumber, params.get(1));
    }

    @Test
    public void pair_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        MetaJpaConstructor<Department, Pair<String, Integer>, ?> proj =
            Constructors.pair(Department_.mandatoryDepName, Department_.mandatoryNumber);
        assertEquals(emptyList(), proj.getIndexesOfIdWrappingParameters());
    }

    @Test
    public void pair_getConstructorParameterTypes_returnsTwoObjectClasses() {
        MetaJpaConstructor<Department, Pair<String, Integer>, ?> proj =
            Constructors.pair(Department_.mandatoryDepName, Department_.mandatoryNumber);
        assertEquals(newList((Class<?>) Object.class, Object.class), proj.getConstructorParameterTypes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pair_getMember_throws() {
        Constructors.pair(Department_.mandatoryDepName, Department_.mandatoryNumber).getMember();
    }

    @Test
    public void pair_toString_containsTypeName() {
        String s = Constructors.pair(Department_.mandatoryDepName, Department_.mandatoryNumber).toString();
        assertNotNull(s);
        assertTrue(s.contains("PairProjection") || s.contains("Object"));
    }

    @Test
    public void pair_viaDao_returnsPair() {
        Department dep = new Department("foo", 7);
        persist(dep);
        Pair<String, Integer> result = dao.get(query.all(Department.class),
            Project.pair(Department_.mandatoryDepName, Department_.mandatoryNumber));
        assertEquals(Pair.of("foo", 7), result);
    }

    // =========================================================================
    // tuple() — TupleProjection
    // =========================================================================

    @Test
    public void tuple_getParameters_returnsAllAttributes() {
        MetaJpaConstructor<Department, ?, ?> proj =
            Constructors.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber);
        List<Attribute<?, ?>> params = proj.getParameters();
        assertEquals(2, params.size());
        assertSame(Department_.mandatoryDepName, params.get(0));
        assertSame(Department_.mandatoryNumber, params.get(1));
    }

    @Test
    public void tuple_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        MetaJpaConstructor<Department, ?, ?> proj =
            Constructors.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber);
        assertEquals(emptyList(), proj.getIndexesOfIdWrappingParameters());
    }

    @Test
    public void tuple_getConstructorParameterTypes_returnsObjectClassForEachAttribute() {
        MetaJpaConstructor<Department, ?, ?> proj =
            Constructors.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber);
        assertEquals(newList((Class<?>) Object.class, Object.class), proj.getConstructorParameterTypes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tuple_getMember_throws() {
        Constructors.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber).getMember();
    }

    @Test
    public void tuple_toString_containsTypeName() {
        String s = Constructors.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber).toString();
        assertNotNull(s);
        assertTrue(s.contains("TupleProjection") || s.contains("Object"));
    }

    @Test
    public void tuple_viaDao_returnsTuple2() {
        Department dep = new Department("bar", 42);
        persist(dep);
        Tuple2<String, Integer> result = dao.get(query.all(Department.class),
            Project.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber));
        assertEquals(Pair.of("bar", 42), result);
    }

    @Test
    public void tuple_viaDao_returnsTuple3() {
        Department dep = new Department("baz", 3);
        persist(dep);
        Tuple3<String, Integer, String> result = dao.get(query.all(Department.class),
            Project.tuple(Department_.mandatoryDepName, Department_.mandatoryNumber, Department_.mandatoryDepName));
        assertEquals(Pair.of("baz", 3, "baz"), result);
    }

    // =========================================================================
    // sum() — SumAttributeProjection / ExpressionProjection
    // =========================================================================

    @Test
    public void sum_getParameters_returnsOptionalWrappedAttribute() {
        MetaJpaConstructor<Department, Option<Integer>, Option<Integer>> proj = Constructors.sum(Department_.mandatoryNumber);
        List<Attribute<?, ?>> params = proj.getParameters();
        assertEquals(1, params.size());
        assertNotNull(params.get(0));
    }

    @Test
    public void sum_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        assertEquals(emptyList(), Constructors.sum(Department_.mandatoryNumber).getIndexesOfIdWrappingParameters());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void sum_getMember_throws() {
        Constructors.sum(Department_.mandatoryNumber).getMember();
    }

    @Test
    public void sum_isExpressionProjection() {
        MetaJpaConstructor<Department, Option<Integer>, Option<Integer>> proj = Constructors.sum(Department_.mandatoryNumber);
        assertTrue(proj instanceof Constructors.ExpressionProjection);
        assertTrue(proj instanceof Constructors.TransparentProjection);
    }

    @Test
    public void sum_getExpression_returnsCbSum() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Constructors.ExpressionProjection<Integer> proj =
            (Constructors.ExpressionProjection<Integer>) Constructors.sum(Department_.mandatoryNumber);
        Expression<Integer> e = cb.literal(5);
        Expression<Integer> result = proj.getExpression(cb, e);
        assertNotNull(result);
    }

    @Test
    public void sum_viaDao_returnsSum() {
        persist(new Department("a", 3), new Department("b", 4));
        assertEquals(Some(7), dao.get(query.all(Department.class), Project.sum(Department_.mandatoryNumber)));
    }

    @Test
    public void sum_viaDao_emptyTable_returnsNone() {
        assertEquals(None(), dao.get(query.all(Department.class), Project.sum(Department_.mandatoryNumber)));
    }

    // =========================================================================
    // max() — MaxAttributeProjection / ExpressionProjection
    // =========================================================================

    @Test
    public void max_getParameters_returnsOptionalWrappedAttribute() {
        List<Attribute<?, ?>> params = Constructors.max(Department_.mandatoryNumber).getParameters();
        assertEquals(1, params.size());
        assertNotNull(params.get(0));
    }

    @Test
    public void max_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        assertEquals(emptyList(), Constructors.max(Department_.mandatoryNumber).getIndexesOfIdWrappingParameters());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void max_getMember_throws() {
        Constructors.max(Department_.mandatoryNumber).getMember();
    }

    @Test
    public void max_isExpressionProjection() {
        assertTrue(Constructors.max(Department_.mandatoryNumber) instanceof Constructors.ExpressionProjection);
    }

    @Test
    public void max_getExpression_returnsCbMax() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Constructors.ExpressionProjection<Integer> proj =
            (Constructors.ExpressionProjection<Integer>) Constructors.max(Department_.mandatoryNumber);
        assertNotNull(proj.getExpression(cb, cb.literal(5)));
    }

    @Test
    public void max_viaDao_returnsMax() {
        persist(new Department("a", 1), new Department("b", 9));
        assertEquals(Some(9), dao.get(query.all(Department.class), Project.max(Department_.mandatoryNumber)));
    }

    @Test
    public void max_viaDao_emptyTable_returnsNone() {
        assertEquals(None(), dao.get(query.all(Department.class), Project.max(Department_.mandatoryNumber)));
    }

    // =========================================================================
    // min() — MinAttributeProjection / ExpressionProjection
    // =========================================================================

    @Test
    public void min_getParameters_returnsOptionalWrappedAttribute() {
        List<Attribute<?, ?>> params = Constructors.min(Department_.mandatoryNumber).getParameters();
        assertEquals(1, params.size());
        assertNotNull(params.get(0));
    }

    @Test
    public void min_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        assertEquals(emptyList(), Constructors.min(Department_.mandatoryNumber).getIndexesOfIdWrappingParameters());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void min_getMember_throws() {
        Constructors.min(Department_.mandatoryNumber).getMember();
    }

    @Test
    public void min_isExpressionProjection() {
        assertTrue(Constructors.min(Department_.mandatoryNumber) instanceof Constructors.ExpressionProjection);
    }

    @Test
    public void min_getExpression_returnsCbMin() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Constructors.ExpressionProjection<Integer> proj =
            (Constructors.ExpressionProjection<Integer>) Constructors.min(Department_.mandatoryNumber);
        assertNotNull(proj.getExpression(cb, cb.literal(5)));
    }

    @Test
    public void min_viaDao_returnsMin() {
        persist(new Department("a", 1), new Department("b", 9));
        assertEquals(Some(1), dao.get(query.all(Department.class), Project.min(Department_.mandatoryNumber)));
    }

    @Test
    public void min_viaDao_emptyTable_returnsNone() {
        assertEquals(None(), dao.get(query.all(Department.class), Project.min(Department_.mandatoryNumber)));
    }

    // =========================================================================
    // greatest() — GreatestAttributeProjection / ExpressionProjection
    // =========================================================================

    @Test
    public void greatest_getParameters_returnsOptionalWrappedAttribute() {
        List<Attribute<?, ?>> params = Constructors.greatest(Department_.mandatoryDepName).getParameters();
        assertEquals(1, params.size());
        assertNotNull(params.get(0));
    }

    @Test
    public void greatest_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        assertEquals(emptyList(), Constructors.greatest(Department_.mandatoryDepName).getIndexesOfIdWrappingParameters());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void greatest_getMember_throws() {
        Constructors.greatest(Department_.mandatoryDepName).getMember();
    }

    @Test
    public void greatest_isExpressionProjection() {
        assertTrue(Constructors.greatest(Department_.mandatoryDepName) instanceof Constructors.ExpressionProjection);
    }

    @Test
    public void greatest_getExpression_returnsCbGreatest() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Constructors.ExpressionProjection<String> proj =
            (Constructors.ExpressionProjection<String>) Constructors.greatest(Department_.mandatoryDepName);
        assertNotNull(proj.getExpression(cb, cb.literal("x")));
    }

    @Test
    public void greatest_viaDao_returnsGreatest() {
        persist(new Department("aaa"), new Department("zzz"));
        assertEquals(Some("zzz"), dao.get(query.all(Department.class), Project.greatest(Department_.mandatoryDepName)));
    }

    @Test
    public void greatest_viaDao_emptyTable_returnsNone() {
        assertEquals(None(), dao.get(query.all(Department.class), Project.greatest(Department_.mandatoryDepName)));
    }

    // =========================================================================
    // least() — LeastAttributeProjection / ExpressionProjection
    // =========================================================================

    @Test
    public void least_getParameters_returnsOptionalWrappedAttribute() {
        List<Attribute<?, ?>> params = Constructors.least(Department_.mandatoryDepName).getParameters();
        assertEquals(1, params.size());
        assertNotNull(params.get(0));
    }

    @Test
    public void least_getIndexesOfIdWrappingParameters_returnsEmptyList() {
        assertEquals(emptyList(), Constructors.least(Department_.mandatoryDepName).getIndexesOfIdWrappingParameters());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void least_getMember_throws() {
        Constructors.least(Department_.mandatoryDepName).getMember();
    }

    @Test
    public void least_isExpressionProjection() {
        assertTrue(Constructors.least(Department_.mandatoryDepName) instanceof Constructors.ExpressionProjection);
    }

    @Test
    public void least_getExpression_returnsCbLeast() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Constructors.ExpressionProjection<String> proj =
            (Constructors.ExpressionProjection<String>) Constructors.least(Department_.mandatoryDepName);
        assertNotNull(proj.getExpression(cb, cb.literal("x")));
    }

    @Test
    public void least_viaDao_returnsLeast() {
        persist(new Department("aaa"), new Department("zzz"));
        assertEquals(Some("aaa"), dao.get(query.all(Department.class), Project.least(Department_.mandatoryDepName)));
    }

    @Test
    public void least_viaDao_emptyTable_returnsNone() {
        assertEquals(None(), dao.get(query.all(Department.class), Project.least(Department_.mandatoryDepName)));
    }

    // =========================================================================
    // aggregate apply() — identity pass-through for Option wrappers
    // =========================================================================

    @Test
    public void sum_apply_returnsIdentity() {
        Option<Integer> val = Some(42);
        MetaJpaConstructor<Department, Option<Integer>, Option<Integer>> proj = Constructors.sum(Department_.mandatoryNumber);
        assertSame(val, proj.apply(val));
    }

    @Test
    public void max_apply_returnsIdentity() {
        Option<Integer> val = Some(7);
        MetaJpaConstructor<Department, Option<Integer>, Option<Integer>> proj = Constructors.max(Department_.mandatoryNumber);
        assertSame(val, proj.apply(val));
    }

    @Test
    public void min_apply_returnsIdentity() {
        Option<Integer> val = None();
        MetaJpaConstructor<Department, Option<Integer>, Option<Integer>> proj = Constructors.min(Department_.mandatoryNumber);
        assertSame(val, proj.apply(val));
    }

    @Test
    public void greatest_apply_returnsIdentity() {
        Option<String> val = Some("x");
        MetaJpaConstructor<Department, Option<String>, Option<String>> proj = Constructors.greatest(Department_.mandatoryDepName);
        assertSame(val, proj.apply(val));
    }

    @Test
    public void least_apply_returnsIdentity() {
        Option<String> val = None();
        MetaJpaConstructor<Department, Option<String>, Option<String>> proj = Constructors.least(Department_.mandatoryDepName);
        assertSame(val, proj.apply(val));
    }

    // =========================================================================
    // ExpressionProjection also implements TransparentProjection (getWrapped)
    // =========================================================================

    @Test
    public void sum_getWrapped_isOptionalAttribute() {
        Constructors.TransparentProjection proj =
            (Constructors.TransparentProjection) Constructors.sum(Department_.mandatoryNumber);
        // Cast.optional wraps the attribute; the wrapped attribute must be non-null
        assertNotNull(proj.getWrapped());
    }

    @Test
    public void max_getWrapped_isOptionalAttribute() {
        Constructors.TransparentProjection proj =
            (Constructors.TransparentProjection) Constructors.max(Department_.mandatoryNumber);
        assertNotNull(proj.getWrapped());
    }

    // =========================================================================
    // Employee aggregate on optional salary (Money — optional field)
    // Aggregates require Number; use Department_.mandatoryNumber with employees persisted
    // to exercise the path where rows exist.
    // =========================================================================

    @Test
    public void sum_optionalField_viaDao_withData_returnsSome() {
        persist(new Department("a", 10), new Department("b", 20));
        assertEquals(Some(30), dao.get(query.all(Department.class), Project.sum(Department_.mandatoryNumber)));
    }

    @Test
    public void max_optionalField_viaDao_withData_returnsSome() {
        persist(new Department("a", 5), new Department("b", 15));
        assertEquals(Some(15), dao.get(query.all(Department.class), Project.max(Department_.mandatoryNumber)));
    }

    @Test
    public void min_optionalField_viaDao_withData_returnsSome() {
        persist(new Department("a", 5), new Department("b", 15));
        assertEquals(Some(5), dao.get(query.all(Department.class), Project.min(Department_.mandatoryNumber)));
    }
}
