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

    public <E extends IEntity & Removable> void remove(Id<E> id) {
        jpaBasicQueries.remove(id);
    }

    public <E extends IEntity & Identifiable<? extends Id<E>> & Removable> void removeAll(CriteriaQuery<E> query) {
        jpaBasicQueries.removeAll(query);
    }

    public <E extends IEntity> E get(Id<E> id) {
        return jpaBasicQueries.get(id);
    }

    public <E extends IEntity> E getProxy(Id<E> id) {
        return jpaBasicQueries.getProxy(id);
    }

    public <E extends IEntity> Option<E> find(Id<E> id) {
        return jpaBasicQueries.find(id);
    }
    
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T> T getProxy(E entity, SingularAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxy(entity, relation);
    }
    
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T extends IEntity> Collection<T> getProxies(E entity, CollectionAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }
    
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T extends IEntity> Set<T> getProxies(E entity, SetAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }
    
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T extends IEntity> List<T> getProxies(E entity, ListAttribute<? super E, T> relation) {
        return jpaBasicQueries.getProxies(entity, relation);
    }

    public <E extends IEntity> Option<E> getIfDefined(Option<? extends Id<E>> idOption) {
        return jpaBasicQueries.getIfDefined(idOption);
    }

    public <E extends IEntity> Option<E> getProxyIfDefined(Option<? extends Id<E>> idOption) {
        return jpaBasicQueries.getProxyIfDefined(idOption);
    }



    public long count(CriteriaQuery<?> query) {
        return jpaCriteriaQueries.count(query);
    }

    public boolean exists(CriteriaQuery<?> query) {
        return jpaCriteriaQueries.exists(query);
    }

    public <T> T get(CriteriaQuery<T> query) throws NoResultException, NonUniqueResultException {
        return jpaCriteriaQueries.get(query);
    }

    public <T> Option<T> find(CriteriaQuery<T> query) throws NonUniqueResultException {
        return jpaCriteriaQueries.find(query);
    }

    public <T> Option<T> findFirst(CriteriaQuery<T> query) {
        return jpaCriteriaQueries.findFirst(query);
    }

    public <E extends IEntity> Option<E> findFirst(CriteriaQuery<E> query, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaCriteriaQueries.findFirst(query, ordering);
    }

    public <T> Collection<T> getMany(CriteriaQuery<T> query) {
        return jpaCriteriaQueries.getMany(query);
    }

    public <T> List<T> getMany(CriteriaQuery<T> query, Page page) throws NoOrderingSpecifiedException {
        return jpaCriteriaQueries.getMany(query, page);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Iterable<? extends Order<? super E, ?>> ordering) {
        return jpaCriteriaQueries.getMany(query, ordering);
    }

    public <E extends IEntity> List<E> getMany(CriteriaQuery<E> query, Page page, Iterable<? extends Order<? super E, ?>> ordering) {
        return jpaCriteriaQueries.getMany(query, page, ordering);
    }



    public <E extends IEntity, R> R get(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor) throws NoResultException, NonUniqueResultException {
        return jpaProjectionQueries.get(query, constructor);
    }

    public <E extends IEntity, R> Option<R> find(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor) throws NonUniqueResultException {
        return jpaProjectionQueries.find(query, constructor);
    }

    public <E extends IEntity,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor) {
        return jpaProjectionQueries.findFirst(query, constructor);
    }

    public <E extends IEntity,R> Option<R> findFirst(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaProjectionQueries.findFirst(query, constructor, ordering);
    }

    public <E extends IEntity,R> Collection<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor) {
        return jpaProjectionQueries.getMany(query, constructor);
    }

    public <E extends IEntity,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor, Page page) throws NoOrderingSpecifiedException {
        return jpaProjectionQueries.getMany(query, constructor, page);
    }

    public <E extends IEntity,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaProjectionQueries.getMany(query, constructor, ordering);
    }

    public <E extends IEntity,R> List<R> getMany(CriteriaQuery<E> query, MetaJpaConstructor<? super E,R, ?> constructor, Page page, Iterable<? extends Order<? super E,?>> ordering) {
        return jpaProjectionQueries.getMany(query, constructor, page, ordering);
    }



    public long count(NativeQuery<?> query) {
        return nativeQueries.count(query);
    }

    public boolean exists(NativeQuery<?> query) {
        return nativeQueries.exists(query);
    }

    public <T> T get(NativeQuery<T> query) {
        return nativeQueries.get(query);
    }

    public <T, P> P get(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.get(query, constructor);
    }

    public <T> Option<T> find(NativeQuery<T> query) {
        return nativeQueries.find(query);
    }

    public <T, P> Option<P> find(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.find(query, constructor);
    }

    public <T> Option<T> findFirst(NativeQuery<T> query) {
        return nativeQueries.findFirst(query);
    }

    public <T, P> Option<P> findFirst(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.findFirst(query, constructor);
    }

    public <T> List<T> getMany(NativeQuery<T> query) {
        return nativeQueries.getMany(query);
    }

    public <T, P> List<P> getMany(NativeQuery<T> query, Apply<T, P> constructor) {
        return nativeQueries.getMany(query, constructor);
    }

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

    public <T> T get(QLQuery<T> query) {
        return qlQueries.get(query);
    }

    public <T, P> P get(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.get(query, constructor);
    }

    public <T> Option<T> find(QLQuery<T> query) {
        return qlQueries.find(query);
    }

    public <T, P> Option<P> find(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.find(query, constructor);
    }

    public <T> Option<T> findFirst(QLQuery<T> query) {
        return qlQueries.findFirst(query);
    }

    public <T, P> Option<P> findFirst(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.find(query, constructor);
    }

    public <T> List<T> getMany(QLQuery<T> query) {
        return qlQueries.getMany(query);
    }

    public <T, P> List<P> getMany(QLQuery<T> query, Apply<T, P> constructor) {
        return qlQueries.getMany(query, constructor);
    }

    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        return qlQueries.getMany(query, page);
    }

    public <T, P> List<P> getMany(QLQuery<T> query, Page page, Apply<T, P> constructor) {
        return qlQueries.getMany(query, page, constructor);
    }
}
