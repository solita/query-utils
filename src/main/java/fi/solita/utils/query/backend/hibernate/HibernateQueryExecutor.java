package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.map;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.transform.ResultTransformer;

import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Transformers;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.backend.NativeQueryExecutor;
import fi.solita.utils.query.backend.QLQueryExecutor;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.QLQuery;

public class HibernateQueryExecutor implements JpaCriteriaQueryExecutor, NativeQueryExecutor, QLQueryExecutor {

    private final Function0<EntityManager> em;
    private final TypeProvider typeProvider;
    private final JpaCriteriaCopy jpaCriteriaCopy;
    
    public HibernateQueryExecutor(Function0<EntityManager> em, TypeProvider typeProvider, Configuration config) {
        this.em = em;
        this.typeProvider = typeProvider;
        this.jpaCriteriaCopy = new JpaCriteriaCopy(config);
    }
    
    @Override
    public <T> T get(CriteriaQuery<T> query, LockModeType lock) {
        jpaCriteriaCopy.createMissingAliases(query);
        return replaceProxy(em.apply().createQuery(query).setLockMode(lock).getSingleResult());
    }

    @Override
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page, LockModeType lock) {
        jpaCriteriaCopy.createMissingAliases(query);
        
        TypedQuery<T> q = em.apply().createQuery(query).setLockMode(lock);
        int originalFirstResult = q.getFirstResult();
        int originalMaxResults = q.getMaxResults();
        
        if (page != Page.NoPaging) {
            if (page.getFirstResult() != 0) {
                q.setFirstResult(page.getFirstResult());
            }
            if (page.getMaxResults() != Integer.MAX_VALUE) {
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
        SQLQuery q = em.apply().unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(NativeQuery<? extends T> query) {
        SQLQuery q = em.apply().unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return Option.of(replaceProxy((T)q.uniqueResult()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(NativeQuery<? extends T> query, Page page) {
        SQLQuery q = em.apply().unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return newList(map(HibernateQueryExecutor_.replaceProxy(), applyPaging(q, page).list()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(QLQuery<T> query) {
        Query q = em.apply().unwrap(Session.class).createQuery(query.query);
        q = bindParams(q, query.params);
        return Option.of(replaceProxy((T)q.uniqueResult()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        Query q = em.apply().unwrap(Session.class).createQuery(query.query);
        q = bindParams(q, query.params);
        return newList(map(HibernateQueryExecutor_.replaceProxy(), applyPaging(q, page).list()));
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

    private final SQLQuery bindReturnValues(SQLQuery q, List<Pair<String, Option<Type<?>>>> retvals) {
        for (Entry<String, Option<Type<?>>> param: retvals) {
            if (param.getValue().isDefined()) {
                Type<?> type = param.getValue().get();
                if (type instanceof Type.Optional) {
                    type = ((Type.Optional<?>)type).type;
                }
                org.hibernate.type.Type t = ((HibernateTypeProvider.HibernateType<?>)type).type;
                if (NativeQuery.ENTITY_RETURN_VALUE.equals(param.getKey())) {
                    q.addEntity(t.getReturnedClass());
                } else if (t.isEntityType()) {
                    q.addEntity(param.getKey(), t.getReturnedClass());
                } else {
                    q.addScalar(param.getKey(), t);
                }
            } else {
                // try to find a type
                org.hibernate.type.Type t = ((HibernateTypeProvider.HibernateType<?>)typeProvider.type(param.getKey().getClass())).type;
                q.addScalar(param.getKey(), t);
            }
        }
        return q;
    }

    private static final SQLQuery bindTransformer(SQLQuery q, NativeQuery<?> query) {
        String[] retvals = newArray(String.class, map(Transformers.<String>left(), query.retvals));
        final OptionResultTransformer resultTransformer = new OptionResultTransformer(query.retvals);
        if (query instanceof NativeQuery.NativeQuerySingleEntity ||
            query instanceof NativeQuery.NativeQueryT1 ||
            query instanceof NativeQuery.NativeQueryVoid) {
            q.setResultTransformer(resultTransformer);
        } else {
            final TupleResultTransformer tupleResultTransformer = new TupleResultTransformer(retvals);
            q.setResultTransformer(new ResultTransformer() {
                @Override
                public Object transformTuple(Object[] tuple, String[] aliases) {
                    Object[] ret = (Object[]) resultTransformer.transformTuple(tuple, aliases);
                    return tupleResultTransformer.transformTuple(ret, aliases);
                }
                @SuppressWarnings("rawtypes")
                @Override
                public List transformList(List collection) {
                    return collection;
                }
            });
        }
        return q;
    }

    private final <T extends Query> T bindParams(T q, Map<String, Pair<?, Option<Type<?>>>> params) {
        for (Entry<String, Pair<?, Option<Type<?>>>> param: params.entrySet()) {
            if (param.getValue()._1 instanceof Collection) {
                Collection<?> col = (Collection<?>)param.getValue()._1;
                if (param.getValue()._2.isDefined()) {
                    q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1, ((HibernateTypeProvider.HibernateType<?>)param.getValue()._2.get()).type);
                } else if (!col.isEmpty()) {
                    // try to find a type
                    org.hibernate.type.Type t = ((HibernateTypeProvider.HibernateType<?>)typeProvider.type(head(col).getClass())).type;
                    q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1, t);
                } else {
                    // fallback to hibernate heuristics when empty collection
                    q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1);
                }
            } else {
                if (param.getValue()._2.isDefined()) {
                    q.setParameter(param.getKey(), param.getValue()._1, ((HibernateTypeProvider.HibernateType<?>)param.getValue()._2.get()).type);
                } else {
                    // try to find a type
                    org.hibernate.type.Type t = ((HibernateTypeProvider.HibernateType<?>)typeProvider.type(param.getValue()._1.getClass())).type;
                    q.setParameter(param.getKey(), param.getValue()._1, t);
                }
            }
        }
        return q;
    }

    private static final Query applyPaging(Query q, Page page) {
        if (page != Page.NoPaging) {
            q.setFirstResult(page.getFirstResult())
             .setMaxResults(page.getMaxResults());
        }
        return q;
    }
}
