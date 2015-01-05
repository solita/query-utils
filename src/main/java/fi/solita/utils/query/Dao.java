package fi.solita.utils.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public <E extends IEntity & Identifiable<? extends Id<? super E>>> Id<E> persist(E entity) {
        return jpaBasicQueries.persist(entity);
    }

    public boolean isManaged(IEntity entity) {
        return jpaBasicQueries.isManaged(entity);
    }

    /**
     * Removes the entity corresponding to <i>id</i> from the database. Fails if not found.
     */
    public <E extends IEntity & Removable> void remove(Id<E> id) {
        jpaBasicQueries.remove(id);
    }

    public <E extends IEntity & Identifiable<? extends Id<E>> & Removable> void removeAll(CriteriaQuery<E> query) {
        jpaBasicQueries.removeAll(query);
    }

    /**
     * Get the entity corresponding to <i>id</i>. Fails if not found.
     */
    public <E extends IEntity> E get(Id<E> id) {
        return jpaBasicQueries.get(id);
    }

    /**
     * Convert <i>id</i> to a proxy instance without hitting the database
     */
    public <E extends IEntity> E toProxy(Id<E> id) {
        return jpaBasicQueries.toProxy(id);
    }
    
    /**
     * Convert <i>ids</i> to a proxy instances without hitting the database
     */
    public <E extends IEntity> Iterable<E> toProxies(Iterable<? extends Id<E>> ids) {
        return jpaBasicQueries.toProxies(ids);
    }

    public <E extends IEntity> Option<E> find(Id<E> id) {
        return jpaBasicQueries.find(id);
    }
    
    /**
     * Get a proxy of <i>relation</i> from <i>entity</i> without hitting the database 
     */
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T> T toProxy(E entity, SingularAttribute<? super E, T> relation) {
        return jpaBasicQueries.toProxy(entity, relation);
    }
    
    /**
     * Get proxies of <i>relation</i> from <i>entity</i>. THIS HITS THE DATABASE. 
     */
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T extends IEntity> Collection<T> getProxies(E entity, CollectionAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }
    
    /**
     * Get proxies of <i>relation</i> from <i>entity</i>. THIS HITS THE DATABASE. 
     */
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T extends IEntity> Set<T> getProxies(E entity, SetAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }
    
    /**
     * Get proxies of <i>relation</i> from <i>entity</i>. THIS HITS THE DATABASE. 
     */
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T extends IEntity> List<T> getProxies(E entity, ListAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }

    

    public long count(CriteriaQuery<?> query) {
        return jpaCriteriaQueries.count(query);
    }

    public boolean exists(CriteriaQuery<?> query) {
        return jpaCriteriaQueries.exists(query);
    }

    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(CriteriaQuery<T> query) throws NoResultException, NonUniqueResultException {
        return jpaCriteriaQueries.get(query);
    }

    /**
     * Get the only row of <i>query</i>, if any. Fails if multiple rows found.
     */
    public <T> Option<T> find(CriteriaQuery<T> query) throws NonUniqueResultException {
        return jpaCriteriaQueries.find(query);
    }

    /**
     * Get the first row of <i>query</i>, if any. Requires <i>query</i> to have ordering.
     */
    public <T> Option<T> findFirst(CriteriaQuery<T> query) throws NoOrderingSpecifiedException {
        return jpaCriteriaQueries.findFirst(query);
    }

    /**
     * Get the first row of <i>query</i>, if any
     */
    public <E extends IEntity> Option<E> findFirst(CriteriaQuery<E> query, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaCriteriaQueries.findFirst(query, ordering);
    }

    public <T> Collection<T> getMany(CriteriaQuery<T> query) {
        return jpaCriteriaQueries.getMany(query);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>. Requires <i>query</i> to have ordering.
     */
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page) throws NoOrderingSpecifiedException {
        return jpaCriteriaQueries.getMany(query, page);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering) {
        return jpaCriteriaQueries.getMany(query, ordering);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>
     */
    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering) {
        return jpaCriteriaQueries.getMany(query, page, ordering);
    }



    /**
     * Get the single row of <i>query</i>, projecting the result. Fails if multiple or no rows found.
     */
    public <E extends IEntity, R> R get(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection) throws NoResultException, NonUniqueResultException {
        return jpaProjectionQueries.get(query, projection);
    }

    /**
     * Get the only row of <i>query</i>, if any, projecting the result. Fails if multiple or no rows found.
     */
    public <E extends IEntity, R> Option<R> find(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection) throws NonUniqueResultException {
        return jpaProjectionQueries.find(query, projection);
    }

    /**
     * Get the first row of <i>query</i>, if any, projecting the result. Requires <i>query</i> to have ordering.
     */
    public <E extends IEntity,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection) {
        return jpaProjectionQueries.findFirst(query, projection);
    }

    /**
     * Get the first row of <i>query</i>, if any, projecting the result
     */
    public <E extends IEntity,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaProjectionQueries.findFirst(query, projection, ordering);
    }

    /**
     * Get all rows of <i>query</i>, projecting the results
     */
    public <E extends IEntity,R> Collection<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection) {
        return jpaProjectionQueries.getMany(query, projection);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>, projecting the results. Requires <i>query</i> to have ordering.
     */
    public <E extends IEntity,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection, Page page) throws NoOrderingSpecifiedException {
        return jpaProjectionQueries.getMany(query, projection, page);
    }

    /**
     * Get all rows of <i>query</i>, projecting the results
     */
    public <E extends IEntity,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaProjectionQueries.getMany(query, projection, ordering);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>, projecting the results
     */
    public <E extends IEntity,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> projection, Page page, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaProjectionQueries.getMany(query, projection, page, ordering);
    }



    public long count(NativeQuery<?> query) {
        return nativeQueries.count(query);
    }

    public boolean exists(NativeQuery<?> query) {
        return nativeQueries.exists(query);
    }

    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(NativeQuery<T> query) {
        return nativeQueries.get(query);
    }
    
    public <T, P> P get(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.get(query, constructor);
    }

    /**
     * Get the only row of <i>query</i>, if any. Fails if multiple rows found.
     */
    public <T> Option<T> find(NativeQuery<T> query) {
        return nativeQueries.find(query);
    }
    
    public <T, P> Option<P> find(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.find(query, constructor);
    }

    /**
     * Get the first row of <i>query</i>, if any. Remember that the query should have an ordering.
     */
    public <T> Option<T> findFirst(NativeQuery<T> query) {
        return nativeQueries.findFirst(query);
    }
    
    public <T, P> Option<P> findFirst(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.findFirst(query, constructor);
    }

    /**
     * Get all rows of <i>query</i>
     */
    public <T> Collection<T> getMany(NativeQuery<T> query) {
        return nativeQueries.getMany(query);
    }

    public <T, P> List<P> getMany(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.getMany(query, constructor);
    }
    
    /**
     * Get rows of <i>query</i> considering <i>page</i>. Remember that the query should have an ordering.
     */
    public <T> List<T> getMany(NativeQuery<T> query, Page page) {
        return nativeQueries.getMany(query, page);
    }

    public <T, P> List<P> getMany(NativeQuery<T> query, Page page, Apply<T, P> constructor) {
        return nativeQueries.getMany(query, page, constructor);
    }


    public long count(QLQuery<?> query) {
        return qlQueries.count(query);
    }

    public boolean exists(QLQuery<?> query) {
        return qlQueries.exists(query);
    }

    /**
     * Get the single row of <i>query</i>. Fails if multiple or no rows found.
     */
    public <T> T get(QLQuery<T> query) {
        return qlQueries.get(query);
    }
    
    public <T, P> P get(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.get(query, constructor);
    }

    /**
     * Get the only row of <i>query</i>, if any. Fails if multiple rows found.
     */
    public <T> Option<T> find(QLQuery<T> query) {
        return qlQueries.find(query);
    }
    
    public <T, P> Option<P> find(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.find(query, constructor);
    }

    /**
     * Get the first row of <i>query</i>, if any. Remember that the query should have an ordering.
     */
    public <T> Option<T> findFirst(QLQuery<T> query) {
        return qlQueries.findFirst(query);
    }
    
    public <T, P> Option<P> findFirst(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.find(query, constructor);
    }

    /**
     * Get all rows of <i>query</i>
     */
    public <T> Collection<T> getMany(QLQuery<T> query) {
        return qlQueries.getMany(query);
    }
    
    public <T, P> List<P> getMany(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.getMany(query, constructor);
    }

    /**
     * Get rows of <i>query</i> considering <i>page</i>. Remember that the query should have an ordering.
     */
    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        return qlQueries.getMany(query, page);
    }
    
    public <T, P> List<P> getMany(QLQuery<T> query, Page page, Apply<T, P> constructor) {
        return qlQueries.getMany(query, page, constructor);
    }
}
