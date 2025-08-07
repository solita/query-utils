package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.QueryUtils.applyOrder;
import static fi.solita.utils.query.QueryUtils.checkOrdering;
import static fi.solita.utils.query.QueryUtils.resolveSelection;

import java.util.Collection;
import java.util.List;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.criteria.CriteriaQuery;

public class JpaCriteriaQueries {

    private final JpaCriteriaQueryExecutor queryExecutor;
    private final ApplyZero<EntityManagerFactory> emf;

    public JpaCriteriaQueries(ApplyZero<EntityManagerFactory> emf, JpaCriteriaQueryExecutor queryExecutor, Configuration config) {
        this.emf = emf;
        this.queryExecutor = queryExecutor;
    }

    public long count(CriteriaQuery<?> query, LockModeType lock) {
        @SuppressWarnings("unchecked")
        CriteriaQuery<Long> q = (CriteriaQuery<Long>) JpaCriteriaCopy.copyCriteria(query);
        q.select(emf.get().getCriteriaBuilder().count(emf.get().getCriteriaBuilder().literal(1)));
        return get(q, lock);
    }
    
    public boolean exists(CriteriaQuery<?> query, LockModeType lock) {
        return !isEmpty(queryExecutor.getMany(query, Page.SINGLE_ROW, lock));
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

    public <T> Collection<T> getMany(CriteriaQuery<T> query, LockModeType lock) {
        return getMany(query, Page.NoPaging, lock);
    }

    public <T> List<T> getMany(CriteriaQuery<T> query, Page page, LockModeType lock) throws NoOrderingSpecifiedException {
        applyOrder(query, resolveSelection(query), emf.get().getCriteriaBuilder());
        checkOrdering(query, page);
        return queryExecutor.getMany(query, page, lock);
    }

    public <E> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering, LockModeType lock) {
        return getMany(query, Page.NoPaging, ordering, lock);
    }
    
    public <E> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering, LockModeType lock) {
        return queryExecutor.getMany(applyOrder(query, resolveSelection(query), ordering, emf.get().getCriteriaBuilder()), page, lock);
    }
}
