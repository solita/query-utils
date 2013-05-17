package fi.solita.utils.query.execution;

import static fi.solita.utils.query.QueryUtils.NoPaging;
import static fi.solita.utils.query.QueryUtils.copyCriteriaWithoutSelect;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;

import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.Order;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.projection.ProjectionSupport;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.functional.Option;

public class JpaProjectionQueries {

    private final ProjectionSupport projectionSupport;

    @PersistenceContext
    private EntityManager em;

    private final JpaCriteriaQueryExecutor queryExecutor;

    public JpaProjectionQueries(ProjectionSupport projectionSupport, JpaCriteriaQueryExecutor queryExecutor) {
        this.projectionSupport = projectionSupport;
        this.queryExecutor = queryExecutor;
    }

    public <E extends IEntity, R> R get(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor) throws NoResultException, NonUniqueResultException {
        CriteriaQuery<Object> q = em.getCriteriaBuilder().createQuery();
        copyCriteriaWithoutSelect(query, q, em.getCriteriaBuilder());
        From<?,?> selection = QueryUtils.resolveSelection(query, q);
        q.multiselect(projectionSupport.transformParametersForQuery(constructor, selection));
        return head(projectionSupport.replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(newList(queryExecutor.get(q)), constructor));
    }

    public <E extends IEntity, R> Option<R> find(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor) throws NonUniqueResultException {
        try {
            return Some(get(query, constructor));
        } catch (NoResultException e) {
            return None();
        }
    }

    public <E extends IEntity,R> Option<R> findFirst(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor) throws NoOrderingSpecifiedException {
        return headOption(getList(query, constructor, Page.FIRST.withSize(1)));
    }

    public <E extends IEntity,R> Option<R> findFirst(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering) {
        return headOption(getList(query, constructor, Page.FIRST.withSize(1), ordering));
    }

    public <E extends IEntity,R> Collection<R> getList(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor) throws NoOrderingSpecifiedException {
        return getList(query, constructor, NoPaging);
    }

    public <E extends IEntity,R> List<R> getList(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor, Page page) throws NoOrderingSpecifiedException {
        QueryUtils.applyOrder(query, resolveSelectionPath(query), em.getCriteriaBuilder());
        QueryUtils.checkOrdering(query, page);
        List<Order<? super E,?>> noOrdering = Collections.emptyList();
        return getList(query, constructor, page, noOrdering);
    }

    public <E extends IEntity,R> List<R> getList(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering) {
        return getList(query, constructor, NoPaging, ordering);
    }

    public <E extends IEntity,R> List<R> getList(CriteriaQuery<E> query, ConstructorMeta_<? super E,R, ?> constructor, Page page, Iterable<? extends Order<? super E,?>> ordering) {
        CriteriaQuery<Object> q = em.getCriteriaBuilder().createQuery();
        copyCriteriaWithoutSelect(query, q, em.getCriteriaBuilder());
        @SuppressWarnings("unchecked")
        From<?,E> selection = (From<?, E>) resolveSelection(query, q);
        q.select(selection);

        @SuppressWarnings("unchecked")
        CriteriaQuery<Object> ordered = (CriteriaQuery<Object>)(Object)JpaCriteriaQueries.applyOrder((CriteriaQuery<E>)(Object)q, selection, ordering, em.getCriteriaBuilder());

        q.multiselect(projectionSupport.transformParametersForQuery(constructor, selection));
        List<Object> results = queryExecutor.getMany(ordered, page);
        return projectionSupport.replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(results, constructor);
    }
}
