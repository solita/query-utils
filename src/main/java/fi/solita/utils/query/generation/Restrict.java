package fi.solita.utils.query.generation;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.attributes.RestrictingAttribute;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import java.util.Set;

import static fi.solita.utils.query.QueryUtils.join;
import static fi.solita.utils.query.QueryUtils.resolveSelection;

public class Restrict {
    
    private final ApplyZero<EntityManager> em;

    private final Configuration config;
    private final Predicates predicates;
    
    public Restrict(ApplyZero<EntityManager> em, Configuration config, Predicates predicates) {
        this.em = em;
        this.config = config;
        this.predicates = predicates;
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
        return em.get().getCriteriaBuilder();
    }
    
    @SuppressWarnings("unchecked")
    public <T> Expression<T> wrap(T value) {
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
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(from, attribute, JoinType.INNER);
        return query;
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T1, T2, A1 extends Attribute<? super E, ?> & Bindable<T1>, A2 extends Attribute<? super T1, ?> & Bindable<T2>> CriteriaQuery<E>
    innerJoin(A1 attribute1, A2 attribute2, CriteriaQuery<E> query) {
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
    public <E> CriteriaQuery<E> by(Predicate predicate, CriteriaQuery<E> query) {
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> by(Apply<CriteriaQuery<E>,Predicate> predicate, CriteriaQuery<E> query) {
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate.apply(query)) : query.where(predicate.apply(query));
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> isDefined(SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        return by(predicates.isDefined(attribute), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> isNotDefined(SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        return by(predicates.isNotDefined(attribute), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> equals(SingularAttribute<? super E, T> attribute, Option<T> value, CriteriaQuery<E> query) {
        return by(predicates.equals(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> equalsOption(SingularAttribute<? super E, Option<T>> attribute, Option<T> value, CriteriaQuery<E> query) {
        return by(predicates.equalsOption(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> notEquals(SingularAttribute<? super E, T> attribute, Option<T> value, CriteriaQuery<E> query) {
        return by(predicates.notEquals(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, T> CriteriaQuery<E> notEqualsOption(SingularAttribute<? super E, Option<T>> attribute, Option<T> value, CriteriaQuery<E> query) {
        return by(predicates.notEqualsOption(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> equalsIgnoreCase(SingularAttribute<? super E, String> attribute, Option<String> value, CriteriaQuery<E> query) {
        return by(predicates.equalsIgnoreCase(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> containsIgnoreCase(SingularAttribute<? super E, String> attribute, String value, CriteriaQuery<E> query) {
        return by(predicates.containsIgnoreCase(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> startsWithIgnoreCase(SingularAttribute<? super E, String> attribute, String value, CriteriaQuery<E> query) {
        return by(predicates.startsWithIgnoreCase(attribute, value), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> startsWithIgnoreCaseOption(SingularAttribute<? super E, Option<String>> attribute, String value, CriteriaQuery<E> query) {
        return by(predicates.startsWithIgnoreCaseOption(attribute, value), query);
    }

    /**
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> in(SingularAttribute<? super E, A> attribute, Set<? super A> values, CriteriaQuery<E> query) {
        return by(predicates.in(attribute, values), query);
    }
    
    /**
     * Like {@link #in(SingularAttribute, Iterable, CriteriaQuery)}
     * but always explodes arguments to an ordinary in-clause. So doesn't use table/collection optimizations.
     * 
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> in_regularForm(SingularAttribute<? super E, A> attribute, Set<? super A> values, CriteriaQuery<E> query) {
        return by(predicates.in_regularForm(attribute, values), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> notIn(SingularAttribute<? super E, A> attribute, Set<? super A> values, CriteriaQuery<E> query) {
        return by(predicates.notIn(attribute, values), query);
    }
    
    /**
     * Like {@link #notIn(SingularAttribute, Iterable, CriteriaQuery)}
     * but always explodes arguments to an ordinary in-clause. So doesn't use table/collection optimizations.
     * 
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> notIn_regularForm(SingularAttribute<? super E, A> attribute, Set<? super A> values, CriteriaQuery<E> query) {
        return by(predicates.notIn_regularForm(attribute, values), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E, A> CriteriaQuery<E> inIds(SingularAttribute<? super E, A> attribute, Set<? extends Id<? super A>> values, CriteriaQuery<E> query) {
        return by(predicates.inIds(attribute, values), query);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> excluding(Id<? super E> idToExclude, CriteriaQuery<E> query) {
        return by(predicates.excluding(idToExclude), query);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> excluding(Set<? extends Id<? super E>> idsToExclude, CriteriaQuery<E> query) {
        return by(predicates.excluding(idsToExclude), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> including(Id<? super E> idToInclude, CriteriaQuery<E> query) {
        return by(predicates.including(idToInclude), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> including(Set<? extends Id<? super E>> idsToInclude, CriteriaQuery<E> query) {
        return by(predicates.including(idsToInclude), query);
    }

    /**
     * Modifies existing query!
     */
    @SuppressWarnings("unchecked")
    public <S,E extends S> CriteriaQuery<E> typeIs(Class<E> type, CriteriaQuery<S> query) {
        return this.<E>by(predicates.<E,E>typeIs(type), (CriteriaQuery<E>)query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> typeIsNot(Class<? extends E> type, CriteriaQuery<E> query) {
        return by(predicates.typeIsNot(type), query);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> typeIn(Set<Class<? extends E>> classes, CriteriaQuery<E> query) {
        return by(predicates.typeIn(classes), query);
    }
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> typeNotIn(Set<Class<? extends E>> classes, CriteriaQuery<E> query) {
        return by(predicates.typeNotIn(classes), query);
    }

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        return by(predicates.lessThan(attribute, value), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThan(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        return by(predicates.lessThan(value, attribute), query);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        return by(predicates.lessThanOrEqual(attribute, value), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> lessThanOrEqual(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        return by(predicates.lessThanOrEqual(value, attribute), query);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        return by(predicates.greaterThan(attribute, value), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThan(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        return by(predicates.greaterThan(value, attribute), query);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        return by(predicates.greaterThanOrEqual(attribute, value), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> greaterThanOrEqual(T value, SingularAttribute<? super E, T> attribute, CriteriaQuery<E> query) {
        return by(predicates.greaterThanOrEqual(value, attribute), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> between(T value, SingularAttribute<? super E, T> a1, SingularAttribute<? super E, T> a2, CriteriaQuery<E> query) {
        return by(predicates.between(value, a1, a2), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> between(SingularAttribute<? super E, T> a, T value1, T value2, CriteriaQuery<E> query) {
        return by(predicates.between(a, value1, value2), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> notBetween(T value, SingularAttribute<? super E, T> a1, SingularAttribute<? super E, T> a2, CriteriaQuery<E> query) {
        return by(predicates.notBetween(value, a1, a2), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E, T extends Comparable<? super T>> CriteriaQuery<E> notBetween(SingularAttribute<? super E, T> a, T value1, T value2, CriteriaQuery<E> query) {
        return by(predicates.notBetween(a, value1, value2), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> like(SingularAttribute<? super E, String> a, String pattern, CriteriaQuery<E> query) {
        return by(predicates.like(a, pattern), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> notLike(SingularAttribute<? super E, String> a, String pattern, CriteriaQuery<E> query) {
        return by(predicates.notLike(a, pattern), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> likeIgnoreCase(SingularAttribute<? super E, String> a, String pattern, CriteriaQuery<E> query) {
        return by(predicates.likeIgnoreCase(a, pattern), query);
    };
    
    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> notLikeIgnoreCase(SingularAttribute<? super E, String> a, String pattern, CriteriaQuery<E> query) {
        return by(predicates.notLikeIgnoreCase(a, pattern), query);
    };
}
