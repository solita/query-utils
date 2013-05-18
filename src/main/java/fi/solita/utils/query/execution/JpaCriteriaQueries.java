package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.ListAttribute;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Order.Direction;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;

public class JpaCriteriaQueries {

    private final JpaCriteriaQueryExecutor queryExecutor;

    @PersistenceContext
    private EntityManager em;

    public JpaCriteriaQueries(JpaCriteriaQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public long count(CriteriaQuery<?> query) {
        CriteriaQuery<Long> q = em.getCriteriaBuilder().createQuery(Long.class);
        JpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.getCriteriaBuilder());
        Selection<?> selection = resolveSelection(query);
        q.select(em.getCriteriaBuilder().count((Expression<?>) (selection.isCompoundSelection() ? head(selection.getCompoundSelectionItems()) : selection)));
        return get(q);
    }

    public boolean exists(CriteriaQuery<?> query) {
        // XXX: optimize?
        return count(query) > 0;
    }

    public <T> T get(CriteriaQuery<T> query) throws NoResultException, NonUniqueResultException {
        return queryExecutor.get(query);
    }

    public <T> Option<T> find(CriteriaQuery<T> query) throws NonUniqueResultException {
        try {
            return Some(get(query));
        } catch (NoResultException e) {
            return None();
        }
    }

    public <T> Option<T> findFirst(CriteriaQuery<T> query) throws NoOrderingSpecifiedException {
        return headOption(getMany(query, Page.FIRST.withSize(1)));
    }

    public <E extends IEntity> Option<E> findFirst(CriteriaQuery<E> query, Iterable<? extends Order<? super E,?>> ordering) {
        return headOption(getMany(query, Page.FIRST.withSize(1), ordering));
    }

    public <T> Collection<T> getMany(CriteriaQuery<T> query) throws NoOrderingSpecifiedException {
        return getMany(query, Page.NoPaging);
    }

    public <T> List<T> getMany(CriteriaQuery<T> query, Page page) {
        QueryUtils.applyOrder(query, resolveSelection(query), em.getCriteriaBuilder());
        QueryUtils.checkOrdering(query, page);
        return queryExecutor.getMany(query, page);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering) {
        return getMany(query, Page.NoPaging, ordering);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering) {
        return queryExecutor.getMany(applyOrder(query, resolveSelectionPath(query), ordering, em.getCriteriaBuilder()), page);
    }

    public static final <E extends IEntity> CriteriaQuery<E> applyOrder(CriteriaQuery<E> query, final Path<E> selection, Iterable<? extends Order<? super E, ?>> orderings, final CriteriaBuilder cb) {
        if (!isEmpty(orderings)) {
            query.orderBy(newList(map(orderings, new Transformer<Order<? super E,?>, javax.persistence.criteria.Order>() {
                @Override
                public javax.persistence.criteria.Order transform(Order<? super E, ?> o) {
                    return o.getDirection() == Direction.ASC ? cb.asc(selection.get(o.getAttribute())) : cb.desc(selection.get(o.getAttribute()));
                }
            })));
        } else if (selection.getModel() instanceof ListAttribute) {
            QueryUtils.addListAttributeOrdering(query, selection, cb);
        }
        return query;
    }
}
