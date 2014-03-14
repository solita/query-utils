package fi.solita.utils.query.generation;

import static fi.solita.utils.query.QueryUtils.id;
import static fi.solita.utils.query.QueryUtils.inExpr;
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
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Numeric;

public class Restrict {

    private final Function0<EntityManager> em;
    
    public Restrict(Function0<EntityManager> em) {
        this.em = em;
    }
    
    protected CriteriaBuilder cb() {
        return em.apply().getCriteriaBuilder();
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
    public <E, A> CriteriaQuery<E> in(SingularAttribute<? super E, A> attribute, Iterable<A> values, CriteriaQuery<E> query) {
        Path<A> path = resolveSelectionPath(query).get(attribute);
        Predicate predicate = inExpr(query, path, values, em.apply().getCriteriaBuilder());
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> excluding(Id<E> idToExclude, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        Path<?> idPath = selectionPath.get(id(selectionPath.getJavaType(), em.apply()));
        Predicate predicate = cb().notEqual(idPath, idToExclude);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E> CriteriaQuery<E> excluding(Iterable<? extends Id<E>> idsToExclude, CriteriaQuery<E> query) {
        Path<E> selectionPath = resolveSelectionPath(query);
        Path<Id<E>> idPath = selectionPath.get(id(selectionPath.getJavaType(), em.apply()));
        Predicate predicate = cb().not(inExpr(query, idPath, idsToExclude, em.apply().getCriteriaBuilder()));
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
        Predicate predicate = inExpr(query, path.type(), classes, cb());
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E, T extends Number> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().lt(selection.get(attribute), value);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Numeric> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().lt((Path<Number>)(Object)selection.get(attribute), cb().literal(value.toNumber()));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Number> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().le(selection.get(attribute), value);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Numeric> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().le((Path<Number>)(Object)selection.get(attribute), cb().literal(value.toNumber()));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Number> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().gt(selection.get(attribute), value);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Numeric> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().gt((Path<Number>)(Object)selection.get(attribute), cb().literal(value.toNumber()));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Number> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().ge(selection.get(attribute), value);
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E, T extends Numeric> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().ge((Path<Number>)(Object)selection.get(attribute), cb().literal(value.toNumber()));
        return query.getRestriction() != null ? query.where(query.getRestriction(), predicate) : query.where(predicate);
    };
}
