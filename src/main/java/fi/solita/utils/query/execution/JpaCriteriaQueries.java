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
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;

public class JpaCriteriaQueries {

    private final JpaCriteriaQueryExecutor queryExecutor;
    private final ApplyZero<EntityManager> em;
    private final JpaCriteriaCopy jpaCriteriaCopy;

    public JpaCriteriaQueries(ApplyZero<EntityManager> em, JpaCriteriaQueryExecutor queryExecutor, Configuration config) {
        this.em = em;
        this.queryExecutor = queryExecutor;
        this.jpaCriteriaCopy = new JpaCriteriaCopy(config);
    }

    public long count(CriteriaQuery<?> query, LockModeType lock) {
        CriteriaQuery<Long> q = em.get().getCriteriaBuilder().createQuery(Long.class);
        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.get().getCriteriaBuilder());
        Selection<?> selection = resolveSelection(query);
        q.select(em.get().getCriteriaBuilder().count((Expression<?>) (selection.isCompoundSelection() ? head(selection.getCompoundSelectionItems()) : selection)));
        return get(q, lock);
    }

    public boolean exists(CriteriaQuery<?> query, LockModeType lock) {
        // XXX: optimize?
        return count(query, lock) > 0;
    }

    public <T> T get(CriteriaQuery<T> query, LockModeType lock) throws NoResultException, NonUniqueResultException {
        return queryExecutor.get(query, lock);
    }
    
    public <T> Option<T> find(CriteriaQuery<T> query, LockModeType lock) throws NonUniqueResultException {
        try {
            return Some(get(query, lock));
        } catch (NoResultException e) {
            return None();
        }
    }

    public <T> Option<T> findFirst(CriteriaQuery<T> query, LockModeType lock) throws NoOrderingSpecifiedException {
        return headOption(getMany(query, Page.FIRST.withSize(1), lock));
    }

    public <E> Option<E> findFirst(CriteriaQuery<E> query, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return headOption(getMany(query, Page.FIRST.withSize(1), ordering, lock));
    }

    public <T> Collection<T> getMany(CriteriaQuery<T> query, LockModeType lock) throws NoOrderingSpecifiedException {
        return getMany(query, Page.NoPaging, lock);
    }

    public <T> List<T> getMany(CriteriaQuery<T> query, Page page, LockModeType lock) throws NoOrderingSpecifiedException {
        applyOrder(query, resolveSelection(query), em.get().getCriteriaBuilder());
        checkOrdering(query, page);
        return queryExecutor.getMany(query, page, lock);
    }

    public <E> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering, LockModeType lock) {
        return getMany(query, Page.NoPaging, ordering, lock);
    }
    
    public <E> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering, LockModeType lock) {
        return queryExecutor.getMany(applyOrder(query, resolveSelectionPath(query), ordering, em.get().getCriteriaBuilder()), page, lock);
    }
}
