package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMutableList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.query.QueryUtils.applyOrder;
import static fi.solita.utils.query.QueryUtils.resolveSelection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.meta.MetaJpaConstructor;
import fi.solita.utils.query.projection.ProjectionHelper;
import fi.solita.utils.query.projection.ProjectionUtil;
import fi.solita.utils.query.projection.ProjectionUtil_;

public class JpaProjectionQueries {

    private final ProjectionHelper projectionSupport;
    private final ApplyZero<EntityManager> em;
    private final JpaCriteriaQueryExecutor queryExecutor;
    private final JpaCriteriaCopy jpaCriteriaCopy;

    public JpaProjectionQueries(ApplyZero<EntityManager> em, ProjectionHelper projectionSupport, JpaCriteriaQueryExecutor queryExecutor, Configuration config) {
        this.em = em;
        this.projectionSupport = projectionSupport;
        this.queryExecutor = queryExecutor;
        this.jpaCriteriaCopy = new JpaCriteriaCopy(config);
    }
    
    public <E, R> R get(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, LockModeType lock) throws NoResultException, NonUniqueResultException {
        CriteriaQuery<Object> q = em.get().getCriteriaBuilder().createQuery();
        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.get().getCriteriaBuilder());
        From<?,E> selection = QueryUtils.resolveSelection(query, q);
        q.multiselect(projectionSupport.prepareProjectingQuery(constructor, selection));
        
        List<Iterable<Object>> res = newMutableList();
        res.add(ProjectionUtil.objectToObjectList(queryExecutor.get(q, lock)));
        return head(projectionSupport.finalizeProjectingQuery(constructor, res));
    }
    
    public <E, R> Option<R> find(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, LockModeType lock) throws NonUniqueResultException {
        try {
            return Some(get(query, constructor, lock));
        } catch (NoResultException e) {
            return None();
        }
    }
    
    public <E,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, LockModeType lock) throws NoOrderingSpecifiedException {
        return headOption(getMany(query, constructor, Page.FIRST.withSize(1), lock));
    }

    public <E,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return headOption(getMany(query, constructor, Page.FIRST.withSize(1), ordering, lock));
    }

    public <E,R> Collection<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, LockModeType lock) throws NoOrderingSpecifiedException {
        return getMany(query, constructor, Page.NoPaging, lock);
    }

    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Page page, LockModeType lock) throws NoOrderingSpecifiedException {
        QueryUtils.applyOrder(query, resolveSelection(query), em.get().getCriteriaBuilder());
        QueryUtils.checkOrdering(query, page);
        List<Order<? super E,?>> noOrdering = Collections.emptyList();
        return getMany(query, constructor, page, noOrdering, lock);
    }

    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return getMany(query, constructor, Page.NoPaging, ordering, lock);
    }
    
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Page page, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        CriteriaQuery<Object> q = em.get().getCriteriaBuilder().createQuery();
        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.get().getCriteriaBuilder());
        From<?,E> selection = resolveSelection(query, q);

        @SuppressWarnings("unchecked")
        CriteriaQuery<Object> ordered = (CriteriaQuery<Object>)(Object)applyOrder((CriteriaQuery<E>)(Object)q, selection, ordering, em.get().getCriteriaBuilder());

        q.multiselect(projectionSupport.prepareProjectingQuery(constructor, selection));
        
        List<Object> results = queryExecutor.getMany(ordered, page, lock);
        return projectionSupport.finalizeProjectingQuery(constructor, map(ProjectionUtil_.objectToObjectList, results));
    }
}
