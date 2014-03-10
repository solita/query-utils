package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.QueryUtils.applyOrder;
import static fi.solita.utils.query.QueryUtils.checkOrdering;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;

public class JpaCriteriaQueries {

    private final JpaCriteriaQueryExecutor queryExecutor;
    private final Function0<EntityManager> em;

    public JpaCriteriaQueries(Function0<EntityManager> em, JpaCriteriaQueryExecutor queryExecutor) {
        this.em = em;
        this.queryExecutor = queryExecutor;
    }

    public long count(CriteriaQuery<?> query) {
        CriteriaQuery<Long> q = em.apply().getCriteriaBuilder().createQuery(Long.class);
        JpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.apply().getCriteriaBuilder());
        Selection<?> selection = resolveSelection(query);
        q.select(em.apply().getCriteriaBuilder().count((Expression<?>) (selection.isCompoundSelection() ? head(selection.getCompoundSelectionItems()) : selection)));
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
        applyOrder(query, resolveSelection(query), em.apply().getCriteriaBuilder());
        checkOrdering(query, page);
        return queryExecutor.getMany(query, page);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering) {
        return getMany(query, Page.NoPaging, ordering);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering) {
        return queryExecutor.getMany(applyOrder(query, resolveSelectionPath(query), ordering, em.apply().getCriteriaBuilder()), page);
    }
}
