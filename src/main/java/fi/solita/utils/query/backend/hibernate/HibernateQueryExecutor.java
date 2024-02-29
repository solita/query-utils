package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.min;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.query.BindableType;
import org.hibernate.query.Query;
import org.hibernate.query.TupleTransformer;
import org.hibernate.type.BasicTypeReference;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Transformers;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.backend.NativeQueryExecutor;
import fi.solita.utils.query.backend.QLQueryExecutor;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.QLQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;

public class HibernateQueryExecutor implements JpaCriteriaQueryExecutor, NativeQueryExecutor, QLQueryExecutor {
    
    public static final String HINT_FETCH_SIZE = "org.hibernate.fetchSize";
    public static final int MAX_FETCH_SIZE = 500;

    private final ApplyZero<EntityManager> em;
    private final TypeProvider typeProvider;
    
    public HibernateQueryExecutor(ApplyZero<EntityManager> em, TypeProvider typeProvider, Configuration config) {
        this.em = em;
        this.typeProvider = typeProvider;
    }
    
    @Override
    public <T> T get(CriteriaQuery<T> query, LockModeType lock) {
        return replaceProxy(em.get().createQuery(query).setLockMode(lock).setHint(HINT_FETCH_SIZE, 2).getSingleResult());
    }

    @Override
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page, LockModeType lock) {
        TypedQuery<T> q = em.get().createQuery(query).setLockMode(lock);
        int originalFirstResult = q.getFirstResult();
        int originalMaxResults = q.getMaxResults();
        
        if (page != Page.NoPaging) {
            if (page.getFirstResult() != 0) {
                q.setFirstResult(page.getFirstResult());
            }
            if (page.getMaxResults() != Integer.MAX_VALUE) {
                q.setHint(HINT_FETCH_SIZE, page.getFetchSizeHint().getOrElse(min(newList(page.getMaxResults() + 1, MAX_FETCH_SIZE)).get()));
                q.setMaxResults(page.getMaxResults());
            }
        }
        try {
            return newList(map(HibernateQueryExecutor_.<T>replaceProxy(), q.getResultList()));
        } finally {
            if (page != Page.NoPaging) {
                if (page.getFirstResult() != 0) {
                    q.setFirstResult(originalFirstResult);
                }
                if (page.getMaxResults() != Integer.MAX_VALUE) {
                    q.setMaxResults(originalMaxResults);
                }
            }
        }
    }

    @Override
    public int execute(NativeQuery<Void> query) {
        org.hibernate.query.NativeQuery<?> q = em.get().unwrap(Session.class).createNativeQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        q.setFetchSize(2);
        return q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(NativeQuery<? extends T> query) {
        org.hibernate.query.NativeQuery<?> q = em.get().unwrap(Session.class).createNativeQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        q.setFetchSize(2);
        return Option.of(replaceProxy((T)q.uniqueResult()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(NativeQuery<? extends T> query, Page page) {
        org.hibernate.query.NativeQuery<?> q = em.get().unwrap(Session.class).createNativeQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        if (page != Page.NoPaging) {
            q.setFetchSize(page.getFetchSizeHint().getOrElse(min(newList(page.getMaxResults() + 1, MAX_FETCH_SIZE)).get()));
        }
        return (List<T>) newList(map(HibernateQueryExecutor_.replaceProxy(), applyPaging(q, page).list()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(QLQuery<T> query) {
        Query<?> q = em.get().unwrap(Session.class).createQuery(query.query);
        q = bindParams(q, query.params);
        q.setFetchSize(2);
        return Option.of(replaceProxy((T)q.uniqueResult()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        Query<?> q = em.get().unwrap(Session.class).createQuery(query.query);
        q = bindParams(q, query.params);
        if (page != Page.NoPaging) {
            q.setFetchSize(page.getFetchSizeHint().getOrElse(min(newList(page.getMaxResults() + 1, MAX_FETCH_SIZE)).get()));
        }
        return (List<T>) newList(map(HibernateQueryExecutor_.replaceProxy(), applyPaging(q, page).list()));
    }
    
    /**
     * Sometimes Hibernate returns proxies even for initialized entities. Don't know why.
     */
    @SuppressWarnings("unchecked")
    static final <T> T replaceProxy(T entityOrProxy) {
        if (entityOrProxy instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)entityOrProxy;
            if (!proxy.getHibernateLazyInitializer().isUninitialized()) {
                return (T) proxy.getHibernateLazyInitializer().getImplementation();
            }
        }
        return entityOrProxy;
    }

    private final org.hibernate.query.NativeQuery<?> bindReturnValues(org.hibernate.query.NativeQuery<?> q, List<Pair<String, Option<Type<?>>>> retvals) {
        for (Entry<String, Option<Type<?>>> param: retvals) {
            HibernateTypeProvider.HibernateType<?> ht;
            if (param.getValue().isDefined()) {
                Type<?> type = param.getValue().get();
                if (type instanceof Type.Optional) {
                    type = ((Type.Optional<?>)type).type;
                }
                ht = ((HibernateTypeProvider.HibernateType<?>)type);
            } else {
                // try to find a type
                ht = (HibernateTypeProvider.HibernateType<?>)typeProvider.type(param.getKey().getClass());
            }
            if (NativeQuery.ENTITY_RETURN_VALUE.equals(param.getKey())) {
                q.addEntity(head(concat(ht.entity, ht.javaType)));
            } else if (ht.entity.isDefined()) {
                q.addEntity(param.getKey(), ht.entity.get());
            } else if (ht.bindableType.isDefined()) {
                q.addScalar(param.getKey(), (BasicTypeReference<?>) ht.bindableType.get());
            } else {
                q.addScalar(param.getKey(), ht.javaType.get());
            }
        }
        return q;
    }

    private static final org.hibernate.query.NativeQuery<?> bindTransformer(org.hibernate.query.NativeQuery<?> q, NativeQuery<?> query) {
        String[] retvals = newArray(String.class, map(Transformers.<String>left(), query.retvals));
        final OptionResultTransformer resultTransformer = new OptionResultTransformer(query.retvals);
        if (query instanceof NativeQuery.NativeQuerySingleEntity ||
            query instanceof NativeQuery.NativeQueryT1 ||
            query instanceof NativeQuery.NativeQueryVoid) {
            q.setTupleTransformer(resultTransformer);
        } else {
            final TupleResultTransformer tupleResultTransformer = new TupleResultTransformer(retvals);
            q.setTupleTransformer(new TupleTransformer<Object>() {
                @Override
                public Object transformTuple(Object[] tuple, String[] aliases) {
                    Object[] ret = (Object[]) resultTransformer.transformTuple(tuple, aliases);
                    return tupleResultTransformer.transformTuple(ret, aliases);
                }
            });
        }
        return q;
    }

    @SuppressWarnings("unchecked")
    private final <T extends Query<?>> T bindParams(T q, Map<String, Pair<?, Option<Type<?>>>> params) {
        for (Entry<String, Pair<?, Option<Type<?>>>> param: params.entrySet()) {
            if (param.getValue()._1 instanceof Collection) {
                Collection<?> col = (Collection<?>)param.getValue()._1;
                Option<HibernateTypeProvider.HibernateType<Object>> ht;
                if (param.getValue()._2.isDefined()) {
                    ht = Some((HibernateTypeProvider.HibernateType<Object>)param.getValue()._2.get());
                } else if (!col.isEmpty()) {
                    // try to find a type
                    ht = Some((HibernateTypeProvider.HibernateType<Object>)typeProvider.type(head(col).getClass()));
                } else {
                    // fallback to hibernate heuristics when empty collection
                    ht = None();
                    q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1);

                }
                for (HibernateTypeProvider.HibernateType<Object> h: ht) {
                    for (BindableType<Object> s: h.bindableType) {
                        q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1, s);
                    }
                    for (Class<?> s: h.javaType) {
                        q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1, (Class<Object>)s);
                    }
                    for (Class<?> s: h.entity) {
                        q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1, (Class<Object>)s);
                    }
                }
            } else {
                HibernateTypeProvider.HibernateType<Object> ht;
                Object value;
                if (param.getValue()._2.isDefined()) {
                    Type<?> type = param.getValue()._2.get();
                    value = param.getValue()._1;
                    if (type instanceof Type.Optional) {
                        type = ((Type.Optional<?>) type).type;
                        value = ((Option<?>)value).getOrElse(null);
                    }
                    ht = ((HibernateTypeProvider.HibernateType<Object>)type);
                } else {
                    // try to find a type
                    value = param.getValue()._1;
                    ht = (HibernateTypeProvider.HibernateType<Object>)typeProvider.type(param.getValue()._1.getClass());
                }
                for (BindableType<Object> s: ht.bindableType) {
                    q.setParameter(param.getKey(), value, s);
                }
                for (Class<?> s: ht.javaType) {
                    q.setParameter(param.getKey(), value, (Class<Object>)s);
                }
                for (Class<?> s: ht.entity) {
                    q.setParameter(param.getKey(), value, (Class<Object>)s);
                }
            }
        }
        return q;
    }

    private static final Query<?> applyPaging(Query<?> q, Page page) {
        if (page != Page.NoPaging) {
            q.setFirstResult(page.getFirstResult())
             .setMaxResults(page.getMaxResults());
        }
        return q;
    }
}
