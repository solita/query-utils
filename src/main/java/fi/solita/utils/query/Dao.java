package fi.solita.utils.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.QueryUtils.NoOrderingSpecifiedException;
import fi.solita.utils.query.execution.JpaBasicQueries;
import fi.solita.utils.query.execution.JpaCriteriaQueries;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.execution.NativeQueries;
import fi.solita.utils.query.execution.QLQueries;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.QLQuery;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public class Dao {

    private final JpaBasicQueries jpaBasicQueries;

    private final JpaCriteriaQueries jpaCriteriaQueries;

    private final JpaProjectionQueries jpaProjectionQueries;

    private final NativeQueries nativeQueries;

    private final QLQueries qlQueries;

    public Dao(JpaBasicQueries jpaBasicQueries, JpaCriteriaQueries jpaCriteriaQueries, JpaProjectionQueries jpaProjectionQueries, NativeQueries nativeQueries, QLQueries qlQueries) {
        this.jpaBasicQueries = jpaBasicQueries;
        this.jpaCriteriaQueries = jpaCriteriaQueries;
        this.jpaProjectionQueries = jpaProjectionQueries;
        this.nativeQueries = nativeQueries;
        this.qlQueries = qlQueries;
    }

    public <E extends IEntity<?> & Identifiable<? extends Id<?>>> Id<E> persist(E entity) {
        return jpaBasicQueries.persist(entity);
    }

    public boolean isManaged(IEntity<?> entity) {
        return jpaBasicQueries.isManaged(entity);
    }

    /**
     * Removes the entity corresponding to <i>id</i> from the database. Fails if not found.
     */
    public <E extends IEntity<?> & Removable> void remove(Id<E> id) {
        jpaBasicQueries.remove(id);
    }

    /**
     * Removes all entities returned by the query from the database.
     */
    public <E extends IEntity<?> & Identifiable<? extends Id<E>> & Removable> void removeAll(CriteriaQuery<E> query) {
        jpaBasicQueries.removeAll(query);
    }

    /**
     * Get the entity corresponding to <i>id</i>. Fails with EntityNotFoundException if not found.
     */
    public <E extends IEntity<?>> E get(Id<E> id) throws EntityNotFoundException {
        return jpaBasicQueries.get(id);
    }
    
    /**
     * Get the entity corresponding to <i>id</i>, if one exists.
     */
    public <E extends IEntity<?>> Option<E> find(Id<E> id) {
        return jpaBasicQueries.find(id);
    }

    /**
     * Convert <i>id</i> to a proxy instance without hitting the database
     */
    public <E extends IEntity<?>> E toProxy(Id<E> id) {
        return jpaBasicQueries.toProxy(id);
    }
    
    /**
     * Convert <i>id</i> to a proxy instance without hitting the database.
     * Use this if E has inheritance and you get an exception trying to cast the proxy to E.
     */
    public <E extends IEntity<?>> E toProxy(Class<E> clazz, Id<? super E> id) {
        return jpaBasicQueries.toProxy(clazz, id);
    }
    
    /**
     * Convert <i>ids</i> to a proxy instances without hitting the database
     */
    public <E extends IEntity<?>> Iterable<E> toProxies(Iterable<? extends Id<E>> ids) {
        return jpaBasicQueries.toProxies(ids);
    }
    
    /**
     * Convert <i>ids</i> to a proxy instances without hitting the database.
     * Use this if E has inheritance and you get an exception trying to cast the proxy to E.
     */
    public <E extends IEntity<?>> Iterable<E> toProxies(Class<E> clazz, Iterable<? extends Id<? super E>> ids) {
        return jpaBasicQueries.toProxies(clazz, ids);
    }

    /**
     * Get a proxy of <i>relation</i> from <i>entity</i> without hitting the database 
     */
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T> T toProxy(E entity, SingularAttribute<? super E, T> relation) {
        return jpaBasicQueries.toProxy(entity, relation);
    }
    
    /**
     * Get proxies of <i>relation</i> from <i>entity</i>. THIS HITS THE DATABASE. 
     */
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>> Collection<T> getProxies(E entity, CollectionAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }
    
    /**
     * Get proxies of <i>relation</i> from <i>entity</i>. THIS HITS THE DATABASE. 
     */
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>> Set<T> getProxies(E entity, SetAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }
    
    /**
     * Get proxies of <i>relation</i> from <i>entity</i>. THIS HITS THE DATABASE. 
     */
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>> List<T> getProxies(E entity, ListAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }

    /**
     * Get the amount of rows that would be returned by <i>query</i>.
     */
    public long count(CriteriaQuery<?> query) {
        return count(query, LockModeType.NONE);
    }

    /**
     * Get the amount of rows that would be returned by <i>query</i>.
     */
    public long count(CriteriaQuery<?> query, LockModeType lock) {
        return jpaCriteriaQueries.count(query, lock);
    }

    /**
     * Get whether <i>query</i> would return any rows.
     */
    public boolean exists(CriteriaQuery<?> query) {
        return exists(query, LockModeType.NONE);
    }
    
    /**
     * Get whether <i>query</i> would return any rows.
     */
    public boolean exists(CriteriaQuery<?> query, LockModeType lock) {
        return jpaCriteriaQueries.exists(query, lock);
    }

    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(CriteriaQuery<T> query) throws NoResultException, NonUniqueResultException {
        return get(query, LockModeType.NONE);
    }
    
    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(CriteriaQuery<T> query, LockModeType lock) throws NoResultException, NonUniqueResultException {
        return jpaCriteriaQueries.get(query, lock);
    }

    /**
     * Get the only row of <i>query</i>, if any. Fails with NonUniqueResultException if multiple rows found.
     */
    public <T> Option<T> find(CriteriaQuery<T> query) throws NonUniqueResultException {
        return find(query, LockModeType.NONE);
    }
    
    /**
     * Get the only row of <i>query</i>, if any. Fails with NonUniqueResultException if multiple rows found.
     */
    public <T> Option<T> find(CriteriaQuery<T> query, LockModeType lock) throws NonUniqueResultException {
        return jpaCriteriaQueries.find(query, lock);
    }

    /**
     * Get the first row of <i>query</i>, if any. Requires <i>query</i> to have ordering defined.
     */
    public <T> Option<T> findFirst(CriteriaQuery<T> query) throws NoOrderingSpecifiedException {
        return findFirst(query, LockModeType.NONE);
    }
    
    /**
     * Get the first row of <i>query</i>, if any. Requires <i>query</i> to have ordering defined.
     */
    public <T> Option<T> findFirst(CriteriaQuery<T> query, LockModeType lock) throws NoOrderingSpecifiedException {
        return jpaCriteriaQueries.findFirst(query, lock);
    }

    /**
     * Get the first row of <i>query</i>, if any
     */
    public <E> Option<E> findFirst(CriteriaQuery<E> query, Iterable<? extends Order<? super E,?>> ordering) {
        return findFirst(query, ordering, LockModeType.NONE);
    }
    
    /**
     * Get the first row of <i>query</i>, if any
     */
    public <E> Option<E> findFirst(CriteriaQuery<E> query, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return jpaCriteriaQueries.findFirst(query, ordering, lock);
    }

    /**
     * Get all rows returned by <i>query</i>
     */
    public <T> Collection<T> getMany(CriteriaQuery<T> query) {
        return getMany(query, Page.NoPaging, LockModeType.NONE);
    }
    
    /**
     * Get all rows returned by <i>query</i>
     */
    public <T> Collection<T> getMany(CriteriaQuery<T> query, LockModeType lock) {
        return jpaCriteriaQueries.getMany(query, lock);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>. Requires <i>query</i> to have ordering.
     */
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page) throws NoOrderingSpecifiedException {
        return getMany(query, page, LockModeType.NONE);
    }
    
    /**
     * Get rows of <i>query</i> considering <i>page</i>. Requires <i>query</i> to have ordering.
     */
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page, LockModeType lock) throws NoOrderingSpecifiedException {
        return jpaCriteriaQueries.getMany(query, page, lock);
    }

    /**
     * Get rows of <i>query</i> ordered by <i>ordering</i>
     */
    public <E> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering) {
        return getMany(query, ordering, LockModeType.NONE);
    }
    
    /**
     * Get rows of <i>query</i> ordered by <i>ordering</i>
     */
    public <E> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering, LockModeType lock) {
        return jpaCriteriaQueries.getMany(query, ordering, lock);
    }

    /**
     * Get rows of <i>query</i> ordered by <i>ordering</i> considering <i>page</i>
     */
    public <E> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering) {
        return getMany(query, page, ordering, LockModeType.NONE);
    }
    
    /**
     * Get rows of <i>query</i> ordered by <i>ordering</i> considering <i>page</i>
     */
    public <E> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering, LockModeType lock) {
        return jpaCriteriaQueries.getMany(query, page, ordering, lock);
    }



    /**
     * Get the single row of <i>query</i>, projecting the result. Fails if multiple or no rows found.
     */
    public <E, R> R get(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor) throws NoResultException, NonUniqueResultException {
        return get(query, constructor, LockModeType.NONE);
    }
    
    /**
     * Get the single row of <i>query</i>, projecting the result. Fails if multiple or no rows found.
     */
    public <E, R> R get(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, LockModeType lock) throws NoResultException, NonUniqueResultException {
        return jpaProjectionQueries.get(query, projection, lock);
    }

    /**
     * Get the only row of <i>query</i>, if any, projecting the result. Fails with NonUniqueResultException if multiple rows found.
     */
    public <E, R> Option<R> find(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor) throws NonUniqueResultException {
        return find(query, constructor, LockModeType.NONE);
    }
    
    /**
     * Get the only row of <i>query</i>, if any, projecting the result. Fails NonUniqueResultException if multiple rows found.
     */
    public <E, R> Option<R> find(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, LockModeType lock) throws NonUniqueResultException {
        return jpaProjectionQueries.find(query, projection, lock);
    }

    /**
     * Get the first row of <i>query</i>, if any, projecting the result. Requires <i>query</i> to have ordering.
     */
    public <E,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor) throws NoOrderingSpecifiedException {
        return findFirst(query, constructor, LockModeType.NONE);
    }
    
    /**
     * Get the first row of <i>query</i>, if any, projecting the result. Requires <i>query</i> to have ordering.
     */
    public <E,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, LockModeType lock) throws NoOrderingSpecifiedException {
        return jpaProjectionQueries.findFirst(query, projection, lock);
    }

    /**
     * Get the first row of <i>query</i>, if any, projecting the result
     */
    public <E,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering) {
        return findFirst(query, constructor, ordering, LockModeType.NONE);
    }
    
    /**
     * Get the first row of <i>query</i>, if any, projecting the result
     */
    public <E,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return jpaProjectionQueries.findFirst(query, projection, ordering, lock);
    }

    /**
     * Get all rows of <i>query</i>, projecting the results
     */
    public <E,R> Collection<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor) {
        return getMany(query, constructor, LockModeType.NONE);
    }
    
    /**
     * Get all rows of <i>query</i>, projecting the results
     */
    public <E,R> Collection<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, LockModeType lock) {
        return jpaProjectionQueries.getMany(query, projection, lock);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>, projecting the results. Requires <i>query</i> to have ordering.
     */
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Page page) throws NoOrderingSpecifiedException {
        return getMany(query, constructor, page, LockModeType.NONE);
    }
    
    /**
     * Get rows of <i>query</i> considering <i>page</i>, projecting the results. Requires <i>query</i> to have ordering.
     */
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, Page page, LockModeType lock) throws NoOrderingSpecifiedException {
        return jpaProjectionQueries.getMany(query, projection, page, lock);
    }

    /**
     * Get all rows of <i>query</i> ordered by <i>ordering</i> projecting the results
     */
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering) {
        return getMany(query, constructor, ordering, LockModeType.NONE);
    }
    
    /**
     * Get all rows of <i>query</i> ordered by <i>ordering</i> projecting the results
     */
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return jpaProjectionQueries.getMany(query, projection, ordering, lock);
    }

    /**
     * Get rows of <i>query</i> ordered by <i>ordering</i> considering <i>page</i>, projecting the results
     */
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> constructor, Page page, Iterable<? extends Order<? super E,?>> ordering) {
        return getMany(query, constructor, page, ordering, LockModeType.NONE);
    }
    
    /**
     * Get rows of <i>query</i> ordered by <i>ordering</i> considering <i>page</i>, projecting the results
     */
    public <E,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,? extends R, ?> projection, Page page, Iterable<? extends Order<? super E,?>> ordering, LockModeType lock) {
        return jpaProjectionQueries.getMany(query, projection, page, ordering, lock);
    }


    
    /**
     * Executes <i>query</i>, returning the number of entities updated or deleted
     */
    public int execute(NativeQuery<Void> query) {
        return nativeQueries.execute(query);
    }

    /**
     * Get the amount of rows that would be returned by <i>query</i>.
     */
    public long count(NativeQuery<?> query) {
        return nativeQueries.count(query);
    }

    /**
     * Get whether <i>query</i> would return any rows.
     */
    public boolean exists(NativeQuery<?> query) {
        return nativeQueries.exists(query);
    }

    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(NativeQuery<T> query) {
        return nativeQueries.get(query);
    }
    
    /**
     * Get the single row of <i>query</i>, projecting the result. Fails if multiple or no rows found.
     */
    public <T, P> P get(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return nativeQueries.get(query, constructor);
    }

    /**
     * Get the only row of <i>query</i>, if any. Fails if multiple rows found.
     */
    public <T> Option<T> find(NativeQuery<T> query) {
        return nativeQueries.find(query);
    }
    
    /**
     * Get the only row of <i>query</i>, if any, projecting the result. Fails if multiple rows found.
     */
    public <T, P> Option<P> find(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return nativeQueries.find(query, constructor);
    }

    /**
     * Get the first row of <i>query</i>, if any. Remember that the query should have an ordering, although this is not checked.
     */
    public <T> Option<T> findFirst(NativeQuery<T> query) {
        return nativeQueries.findFirst(query);
    }
    
    /**
     * Get the first row of <i>query</i>, if any, projecting the result. Remember that the query should have an ordering, although this is not checked.
     */
    public <T, P> Option<P> findFirst(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return nativeQueries.findFirst(query, constructor);
    }

    /**
     * Get all rows of <i>query</i>
     */
    public <T> Collection<T> getMany(NativeQuery<T> query) {
        return nativeQueries.getMany(query);
    }

    /**
     * Get all rows of <i>query</i>, projecting the result.
     */
    public <T, P> Collection<P> getMany(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return nativeQueries.getMany(query, constructor);
    }
    
    /**
     * Get rows of <i>query</i> considering <i>page</i>. Remember that the query should have an ordering, although this is not checked.
     */
    public <T> List<T> getMany(NativeQuery<T> query, Page page) {
        return nativeQueries.getMany(query, page);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>, projecting the result.. Remember that the query should have an ordering, although this is not checked.
     */
    public <T, P> List<P> getMany(NativeQuery<? extends T> query, Page page, Apply<T, P> constructor) {
        return nativeQueries.getMany(query, page, constructor);
    }


    /**
     * Get the amount of rows that would be returned by <i>query</i>.
     */
    public long count(QLQuery<?> query) {
        return qlQueries.count(query);
    }

    /**
     * Get whether <i>query</i> would return any rows.
     */
    public boolean exists(QLQuery<?> query) {
        return qlQueries.exists(query);
    }

    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(QLQuery<T> query) {
        return qlQueries.get(query);
    }
    
    /**
     * Get the single row of <i>query</i>, projecting the result. Fails if multiple or no rows found.
     */
    public <T, P> P get(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.get(query, constructor);
    }

    /**
     * Get the only row of <i>query</i>, if any. Fails if multiple rows found.
     */
    public <T> Option<T> find(QLQuery<T> query) {
        return qlQueries.find(query);
    }
    
    /**
     * Get the only row of <i>query</i>, if any, projecting the result. Fails if multiple rows found.
     */
    public <T, P> Option<P> find(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.find(query, constructor);
    }

    /**
     * Get the first row of <i>query</i>, if any. Remember that the query should have an ordering, although this is not checked.
     */
    public <T> Option<T> findFirst(QLQuery<T> query) {
        return qlQueries.findFirst(query);
    }
    
    /**
     * Get the first row of <i>query</i>, if any, projecting the result. Remember that the query should have an ordering, although this is not checked.
     */
    public <T, P> Option<P> findFirst(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.find(query, constructor);
    }

    /**
     * Get all rows of <i>query</i>
     */
    public <T> Collection<T> getMany(QLQuery<T> query) {
        return qlQueries.getMany(query);
    }
    
    /**
     * Get all rows of <i>query</i>, projecting the result
     */
    public <T, P> Collection<P> getMany(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.getMany(query, constructor);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>. Remember that the query should have an ordering, although this is not checked.
     */
    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        return qlQueries.getMany(query, page);
    }
    
    /**
     * Get rows of <i>query</i> considering <i>page</i>, projecting the result. Remember that the query should have an ordering, although this is not checked.
     */
    public <T, P> List<P> getMany(QLQuery<T> query, Page page, Apply<T, P> constructor) {
        return qlQueries.getMany(query, page, constructor);
    }
}
