package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.query.QueryUtils.id;
import static fi.solita.utils.query.QueryUtils.inExpr;
import static fi.solita.utils.query.QueryUtils.join;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Numeric;

public class Restrict {

    @PersistenceContext
    private EntityManager em;
    
    private CriteriaBuilder cb() {
        return em.getCriteriaBuilder();
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T> CriteriaQuery<E> innerJoin(Attribute<? super E, T> attribute, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) resolveSelection(query);
        join(from, attribute, JoinType.INNER);
        return query;
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T> CriteriaQuery<E> attributeEquals(SingularAttribute<? super E, T> attribute, Option<T> value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate;
        if (value.isDefined()) {
            predicate = cb().equal(selection.get(attribute), value.get());
        } else {
            predicate = cb().isNull(selection.get(attribute));
        }
        return query.where(existingRestriction, predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, A> CriteriaQuery<E> attributeIn(SingularAttribute<? super E, A> attribute, Iterable<A> values, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<A> path = resolveSelectionPath(query).get(attribute);
        return query.where(existingRestriction,
                           inExpr(path, values, em.getCriteriaBuilder()));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> exclude(Id<E> idToExclude, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selectionPath = resolveSelectionPath(query);
        Path<?> idPath = selectionPath.get(id(selectionPath.getJavaType(), em));
        return query.where(existingRestriction,
                           cb().notEqual(idPath, idToExclude));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> exclude(Iterable<? extends Id<E>> idsToExclude, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selectionPath = resolveSelectionPath(query);
        Path<Id<E>> idPath = selectionPath.get(id(selectionPath.getJavaType(), em));
        return query.where(existingRestriction,
                           cb().not(inExpr(idPath, idsToExclude, em.getCriteriaBuilder())));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> attributeStartsWithIgnoreCase(SingularAttribute<? super E, String> attribute, String value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<String> path = resolveSelectionPath(query).get(attribute);
        return query.where(existingRestriction, cb().like(cb().lower(path), value.toLowerCase() + "%"));
    }

    /**
     * Modifies existing query!
     */
    @SuppressWarnings("unchecked")
    public <E extends IEntity> CriteriaQuery<E> byType(Class<E> type, CriteriaQuery<? super E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<? super E> path = resolveSelectionPath(query);
        return (CriteriaQuery<E>) query.where(existingRestriction, cb().equal(path.type(), type));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> byTypes(Set<Class<? extends E>> classes, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());
        final Path<E> path = resolveSelectionPath(query);

        Iterable<Predicate> typeRestrictions = map(classes, new Transformer<Class<?>, Predicate>() {
            @Override
            public Predicate transform(Class<?> source) {
                return cb().equal(path.type(), source);
            }
        });

        return query.where(existingRestriction, cb().or(newArray(Predicate.class, typeRestrictions)));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().lt(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().lt((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)cb().literal(value));
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().le(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().le((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)cb().literal(value));
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().gt(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().gt((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)cb().literal(value));
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = cb().ge(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(cb().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = cb().ge((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)cb().literal(value));
        return query.where(existingRestriction, predicate);
    };
}
