package fi.solita.utils.query.generation;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.Id;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Numeric;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformer;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;
import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.map;

public class Restrict {

    @PersistenceContext
    private EntityManager em;

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T> CriteriaQuery<E> innerJoin(Attribute<? super E, T> attribute, CriteriaQuery<E> query) {
        @SuppressWarnings("unchecked")
        From<?, E> from = (From<?, E>) QueryUtils.resolveSelection(query);

        if (attribute instanceof SingularAttribute) {
            @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
            From<?, E> f = from.join((SingularAttribute)attribute);
        } else if(attribute instanceof SetAttribute) {
            @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
            From<?, E> f = from.join((SetAttribute)attribute);
        } else if (attribute instanceof ListAttribute) {
            @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
            From<?, E> f = from.join((ListAttribute)attribute);
        } else {
            throw new RuntimeException("Shouldn't be here...");
        }

        return query;
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T> CriteriaQuery<E> attributeEquals(SingularAttribute<? super E, T> attribute, Option<T> value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate;
        if (value.isDefined()) {
            predicate = em.getCriteriaBuilder().equal(selection.get(attribute), value.get());
        } else {
            predicate = em.getCriteriaBuilder().isNull(selection.get(attribute));
        }
        return query.where(existingRestriction, predicate);
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, A> CriteriaQuery<E> attributeIn(SingularAttribute<? super E, A> attribute, Iterable<A> values, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<A> path = resolveSelectionPath(query).get(attribute);
        return query.where(existingRestriction,
                           QueryUtils.inExpr(path, values, em.getCriteriaBuilder()));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> exclude(Id<E> idToExclude, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selectionPath = resolveSelectionPath(query);
        Path<?> idPath = selectionPath.get(QueryUtils.id(selectionPath.getJavaType(), em));
        return query.where(existingRestriction,
                           em.getCriteriaBuilder().notEqual(idPath, idToExclude));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> exclude(Iterable<? extends Id<E>> idsToExclude, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selectionPath = resolveSelectionPath(query);
        Path<Id<E>> idPath = selectionPath.get(QueryUtils.id(selectionPath.getJavaType(), em));
        return query.where(existingRestriction,
                           em.getCriteriaBuilder().not(QueryUtils.inExpr(idPath, idsToExclude, em.getCriteriaBuilder())));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> attributeStartsWithIgnoreCase(SingularAttribute<? super E, String> attribute, String value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<String> path = resolveSelectionPath(query).get(attribute);
        return query.where(existingRestriction, em.getCriteriaBuilder().like(em.getCriteriaBuilder().lower(path), value.toLowerCase() + "%"));
    }

    /**
     * Modifies existing query!
     */
    @SuppressWarnings("unchecked")
    public <E extends IEntity> CriteriaQuery<E> byType(Class<E> type, CriteriaQuery<? super E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<? super E> path = resolveSelectionPath(query);
        return (CriteriaQuery<E>) query.where(existingRestriction,
                           em.getCriteriaBuilder().equal(path.type(), type));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity> CriteriaQuery<E> byTypes(Set<Class<? extends E>> classes, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());
        final Path<E> path = resolveSelectionPath(query);

        Iterable<Predicate> typeRestrictions = map(classes, new Transformer<Class<?>, Predicate>() {
            @Override
            public Predicate transform(Class<?> source) {
                return em.getCriteriaBuilder().equal(path.type(), source);
            }
        });

        return query.where(existingRestriction, em.getCriteriaBuilder().or(newArray(typeRestrictions, Predicate.class)));
    }

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = em.getCriteriaBuilder().lt(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> lessThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = em.getCriteriaBuilder().lt((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)em.getCriteriaBuilder().literal(value));
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = em.getCriteriaBuilder().le(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> lessThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = em.getCriteriaBuilder().le((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)em.getCriteriaBuilder().literal(value));
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = em.getCriteriaBuilder().gt(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> greaterThan(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = em.getCriteriaBuilder().gt((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)em.getCriteriaBuilder().literal(value));
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Number> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        Predicate predicate = em.getCriteriaBuilder().ge(selection.get(attribute), value);
        return query.where(existingRestriction, predicate);
    };

    /**
     * Modifies existing query!
     */
    public <E extends IEntity, T extends Numeric> CriteriaQuery<E> greaterThanOrEqual(SingularAttribute<? super E, T> attribute, T value, CriteriaQuery<E> query) {
        Predicate existingRestriction = Option.of(query.getRestriction()).getOrElse(em.getCriteriaBuilder().and());

        Path<E> selection = resolveSelectionPath(query);
        @SuppressWarnings("unchecked")
        Predicate predicate = em.getCriteriaBuilder().ge((Path<Number>)(Object)selection.get(attribute), (Expression<Number>)(Object)em.getCriteriaBuilder().literal(value));
        return query.where(existingRestriction, predicate);
    };
}
