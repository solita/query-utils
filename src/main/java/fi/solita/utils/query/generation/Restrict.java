package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.query.QueryUtils.id;
import static fi.solita.utils.query.QueryUtils.join;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.RestrictingAttribute;

public class Restrict {
    
    private final Function0<EntityManager> em;

    private final Configuration config;
    private final QueryUtils queryUtils;
    
    public Restrict(Function0<EntityManager> em, Configuration config) {
        this.em = em;
        this.config = config;
        this.queryUtils = new QueryUtils(config);
    }
    
    public static <E, R, R1, A1 extends Attribute<? super R, ?> & Bindable<R1>>
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1) {
        return RestrictingAttribute.Constructors.singular(attribute, r1);
    }
    
    public static <E, R, R1, R2, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>> 
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1, A2 r2) {
        return RestrictingAttribute.Constructors.singular(attribute, r1, r2);
    }
    
    public static <E, R, R1, R2, R3, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>> 
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3) {
        return RestrictingAttribute.Constructors.singular(attribute, r1, r2, r3);
    }
    
    public static <E, R, R1, R2, R3, R4, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>> 
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4) {
        return RestrictingAttribute.Constructors.singular(attribute, r1, r2, r3, r4);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5) {
        return RestrictingAttribute.Constructors.singular(attribute, r1, r2, r3, r4, r5);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6) {
        return RestrictingAttribute.Constructors.singular(attribute, r1, r2, r3, r4, r5, r6);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, R7, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    SingularAttribute<E,R> innerJoin(SingularAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7) {
        return RestrictingAttribute.Constructors.singular(attribute, r1, r2, r3, r4, r5, r6, r7);
    }
    
    
    
    public static <E, R, R1, A1 extends Attribute<? super R, ?> & Bindable<R1>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1) {
        return RestrictingAttribute.Constructors.collection(attribute, r1);
    }
    
    public static <E, R, R1, R2, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1, A2 r2) {
        return RestrictingAttribute.Constructors.collection(attribute, r1, r2);
    }
    
    public static <E, R, R1, R2, R3, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3) {
        return RestrictingAttribute.Constructors.collection(attribute, r1, r2, r3);
    }
    
    public static <E, R, R1, R2, R3, R4, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4) {
        return RestrictingAttribute.Constructors.collection(attribute, r1, r2, r3, r4);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5) {
        return RestrictingAttribute.Constructors.collection(attribute, r1, r2, r3, r4, r5);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6) {
        return RestrictingAttribute.Constructors.collection(attribute, r1, r2, r3, r4, r5, r6);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, R7, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    CollectionAttribute<E,R> innerJoin(CollectionAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7) {
        return RestrictingAttribute.Constructors.collection(attribute, r1, r2, r3, r4, r5, r6, r7);
    }
    
    
    
    public static <E, R, R1, A1 extends Attribute<? super R, ?> & Bindable<R1>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1) {
        return RestrictingAttribute.Constructors.set(attribute, r1);
    }
    
    public static <E, R, R1, R2, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1, A2 r2) {
        return RestrictingAttribute.Constructors.set(attribute, r1, r2);
    }
    
    public static <E, R, R1, R2, R3, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3) {
        return RestrictingAttribute.Constructors.set(attribute, r1, r2, r3);
    }
    
    public static <E, R, R1, R2, R3, R4, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4) {
        return RestrictingAttribute.Constructors.set(attribute, r1, r2, r3, r4);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5) {
        return RestrictingAttribute.Constructors.set(attribute, r1, r2, r3, r4, r5);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6) {
        return RestrictingAttribute.Constructors.set(attribute, r1, r2, r3, r4, r5, r6);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, R7, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    SetAttribute<E,R> innerJoin(SetAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7) {
        return RestrictingAttribute.Constructors.set(attribute, r1, r2, r3, r4, r5, r6, r7);
    }
    
    
    
    public static <E, R, R1, A1 extends Attribute<? super R, ?> & Bindable<R1>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1) {
        return RestrictingAttribute.Constructors.list(attribute, r1);
    }
    
    public static <E, R, R1, R2, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1, A2 r2) {
        return RestrictingAttribute.Constructors.list(attribute, r1, r2);
    }
    
    public static <E, R, R1, R2, R3, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3) {
        return RestrictingAttribute.Constructors.list(attribute, r1, r2, r3);
    }
    
    public static <E, R, R1, R2, R3, R4, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4) {
        return RestrictingAttribute.Constructors.list(attribute, r1, r2, r3, r4);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5) {
        return RestrictingAttribute.Constructors.list(attribute, r1, r2, r3, r4, r5);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6) {
        return RestrictingAttribute.Constructors.list(attribute, r1, r2, r3, r4, r5, r6);
    }
    
    public static <E, R, R1, R2, R3, R4, R5, R6, R7, A1 extends Attribute<? super R, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    ListAttribute<E,R> innerJoin(ListAttribute<E,R> attribute, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7) {
        return RestrictingAttribute.Constructors.list(attribute, r1, r2, r3, r4, r5, r6, r7);
    }
    
    
    
    protected CriteriaBuilder cb() {
        return em.apply().getCriteriaBuilder();
    }
    
    @SuppressWarnings("unchecked")
    private <T> Expression<T> wrap(T value) {
        if (value instanceof Number) {
            for (String unwrappingFunctionName: config.wrapComparedNumbersWithFunction()) {
                return (Expression<T>) cb().function(unwrappingFunctionName, value.getClass(), cb().literal(value.toString())); 
            }
        }
        return cb().literal(value);
    }

    /**
     * Modifies existing query!
     */
    public <E, T1, A1 extends Attribute<? super E, ?> & Bindable<T1>> CriteriaQuery<E>
    innerJoin(A1 attribute, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(from, attribute, JoinType.INNER);
        return query;
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T1, T2, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>> CriteriaQuery<E>
    innerJoin(A1 attribute1, A2 attribute2, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(join(from, attribute1, JoinType.INNER),
                        attribute2, JoinType.INNER);
        return query;
    }

    /**
     * Modifies existing query!
     */
    public <E, T1, T2, T3, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>, A3 extends Attribute<? super T2, ?> & Bindable<T3>> CriteriaQuery<E> 
    innerJoin(A1 attribute1, A2 attribute2, A3 attribute3, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(join(join(from, attribute1, JoinType.INNER),
                             attribute2, JoinType.INNER),
                             attribute3, JoinType.INNER);
        return query;
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T1, T2, T3, T4, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>, A3 extends Attribute<? super T2, ?> & Bindable<T3>, A4 extends Attribute<? super T3, ?> & Bindable<T4>> CriteriaQuery<E> 
    innerJoin(A1 attribute1, A2 attribute2, A3 attribute3, A4 attribute4, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(join(join(join(from, attribute1, JoinType.INNER),
                                  attribute2, JoinType.INNER),
                                  attribute3, JoinType.INNER),
                                  attribute4, JoinType.INNER);
        return query;
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T1, T2, T3, T4, T5, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>, A3 extends Attribute<? super T2, ?> & Bindable<T3>, A4 extends Attribute<? super T3, ?> & Bindable<T4>, A5 extends Attribute<? super T4, ?> & Bindable<T5>> CriteriaQuery<E> 
    innerJoin(A1 attribute1, A2 attribute2, A3 attribute3, A4 attribute4, A5 attribute5, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(join(join(join(join(from, attribute1, JoinType.INNER),
                                       attribute2, JoinType.INNER),
                                       attribute3, JoinType.INNER),
                                       attribute4, JoinType.INNER),
                                       attribute5, JoinType.INNER);
        return query;
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T1, T2, T3, T4, T5, T6, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>, A3 extends Attribute<? super T2, ?> & Bindable<T3>, A4 extends Attribute<? super T3, ?> & Bindable<T4>, A5 extends Attribute<? super T4, ?> & Bindable<T5>, A6 extends Attribute<? super T5, ?> & Bindable<T6>> CriteriaQuery<E> 
    innerJoin(A1 attribute1, A2 attribute2, A3 attribute3, A4 attribute4, A5 attribute5, A6 attribute6, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(join(join(join(join(join(from, attribute1, JoinType.INNER),
                                            attribute2, JoinType.INNER),
                                            attribute3, JoinType.INNER),
                                            attribute4, JoinType.INNER),
                                            attribute5, JoinType.INNER),
                                            attribute6, JoinType.INNER);
        return query;
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T1, T2, T3, T4, T5, T6, T7, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>, A3 extends Attribute<? super T2, ?> & Bindable<T3>, A4 extends Attribute<? super T3, ?> & Bindable<T4>, A5 extends Attribute<? super T4, ?> & Bindable<T5>, A6 extends Attribute<? super T5, ?> & Bindable<T6>, A7 extends Attribute<? super T6, ?> & Bindable<T7>> CriteriaQuery<E> 
    innerJoin(A1 attribute1, A2 attribute2, A3 attribute3, A4 attribute4, A5 attribute5, A6 attribute6, A7 attribute7, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(join(join(join(join(join(join(from, attribute1, JoinType.INNER),
                                                 attribute2, JoinType.INNER),
                                                 attribute3, JoinType.INNER),
                                                 attribute4, JoinType.INNER),
                                                 attribute5, JoinType.INNER),
                                                 attribute6, JoinType.INNER),
                                                 attribute7, JoinType.INNER);
        return query;
    }

    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> equals(SingularAttribute<? super E, T> attribute, Option<T> value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate;
        if (value.isDefined()) {
            predicate = cb().equal(selection.get(attribute), value.get());
        } else {
            predicate = cb().isNull(selection.get(attribute));
        }
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> notEquals(SingularAttribute<? super E, T> attribute, Option<T> value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate;
        if (value.isDefined()) {
            predicate = cb().notEqual(selection.get(attribute), value.get());
        } else {
            predicate = cb().isNotNull(selection.get(attribute));
        }
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> equalsIgnoreCase(SingularAttribute<? super E, String> attribute, Option<String> value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate;
        if (value.isDefined()) {
            predicate = cb().equal(cb().lower(selection.get(attribute)), value.get().toLowerCase());
        } else {
            predicate = cb().isNull(selection.get(attribute));
        }
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> containsIgnoreCase(SingularAttribute<? super E, String> attribute, String value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        
        Expression<Integer> locateExpr = cb().locate(cb().lower(selection.get(attribute)), value.toLowerCase());
        Predicate predicate = cb().not(cb().equal(locateExpr, 0));
        
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> startsWithIgnoreCase(SingularAttribute<? super E, String> attribute, String value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        
        Expression<Integer> locateExpr = cb().locate(cb().lower(selection.get(attribute)), value.toLowerCase());
        Predicate predicate = cb().equal(locateExpr, 1);
        
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> in(SingularAttribute<? super E, A> attribute, Iterable<? super A> values, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        boolean enableInClauseOptimizations = !exists(QueryUtils.ImplementsProjectWithRegularInClause, newList(attribute.getJavaType(), attribute.getDeclaringType().getJavaType()));
        Path<A> path = selectionPath.get(attribute);
        Predicate predicate = queryUtils.inExpr(path, values, em.apply().getCriteriaBuilder(), enableInClauseOptimizations);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Like {@link #in(SingularAttribute, Iterable, CriteriaQuery)}
     * but always explodes arguments to an ordinary in-clause. So doesn't use table/collection optimizations.
     * 
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> in_regularForm(SingularAttribute<? super E, A> attribute, Iterable<? super A> values, CriteriaQuery<E> query) {
        Path<A> path = resolveSelectionPath(query).get(attribute);
        Predicate predicate = queryUtils.inExpr(path, values, em.apply().getCriteriaBuilder(), false);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> inIds(SingularAttribute<? super E, A> attribute, Iterable<? extends Id<A>> values, CriteriaQuery<E> query) {
        Path<A> path = resolveSelectionPath(query).get(attribute);
        Predicate predicate = queryUtils.inExpr(path.get(id(path.getJavaType(), em.apply())), values, em.apply().getCriteriaBuilder());
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> excluding(Id<? super E> idToExclude, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        Path<?> idPath = selectionPath.get(id(selectionPath.getJavaType(), em.apply()));
        Predicate predicate = cb().notEqual(idPath, idToExclude);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> excluding(Iterable<? extends Id<? super E>> idsToExclude, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        Path<Id<E>> idPath = selectionPath.get(QueryUtils.<E,Id<E>>id(selectionPath.getJavaType(), em.apply()));
        Predicate predicate = cb().not(queryUtils.inExpr(idPath, idsToExclude, em.apply().getCriteriaBuilder()));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> including(Id<? super E> idToInclude, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        Path<?> idPath = selectionPath.get(id(selectionPath.getJavaType(), em.apply()));
        Predicate predicate = cb().equal(idPath, idToInclude);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> including(Iterable<? extends Id<? super E>> idsToInclude, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        Path<Id<E>> idPath = selectionPath.get(QueryUtils.<E,Id<E>>id(selectionPath.getJavaType(), em.apply()));
        Predicate predicate = queryUtils.inExpr(idPath, idsToInclude, em.apply().getCriteriaBuilder());
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaQuery<E> typeIs(Class<E> type, CriteriaQuery<? super E> query) {
        Path<? super E> path = resolveSelectionPath(query);
        Predicate predicate = cb().equal(path.type(), type);
        return (CriteriaQuery<E>) (query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate));
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> typeIn(Set<Class<? extends E>> classes, CriteriaQuery<E> query) {
        Path<E> path = resolveSelectionPath(query);
        Predicate predicate = path.type().in(classes);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().lessThan(selection.get(attribute), wrap(value));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThan(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().lessThan(wrap(value), selection.get(attribute));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().lessThanOrEqualTo(selection.get(attribute), wrap(value));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThanOrEqual(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().lessThanOrEqualTo(wrap(value), selection.get(attribute));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().greaterThan(selection.get(attribute), wrap(value));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThan(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().greaterThan(wrap(value), selection.get(attribute));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().greaterThanOrEqualTo(selection.get(attribute), wrap(value));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThanOrEqual(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().greaterThanOrEqualTo(wrap(value), selection.get(attribute));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> between(T value, SingularAttribute<? super E, T> a1, SingularAttribute<? super E, T> a2, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().between(wrap(value), selection.get(a1), selection.get(a2));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> between(SingularAttribute<? super E, T> a, T value1, T value2, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().between(selection.get(a), wrap(value1), wrap(value2));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
}
