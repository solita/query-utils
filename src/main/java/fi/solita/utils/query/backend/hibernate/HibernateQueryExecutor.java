package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.FunctionalImpl.map;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
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
import fi.solita.utils.functional.Pair_;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.backend.NativeQueryExecutor;
import fi.solita.utils.query.backend.QLQueryExecutor;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.QLQuery;

public class HibernateQueryExecutor implements JpaCriteriaQueryExecutor, NativeQueryExecutor, QLQueryExecutor {

    private final Function0<EntityManager> em;
    
    public HibernateQueryExecutor(Function0<EntityManager> em) {
        this.em = em;
    }
    
    @Override
    public <T> T get(CriteriaQuery<T> query) {
        JpaCriteriaCopy.createMissingAliases(query);
        return replaceProxy(em.apply().createQuery(query).getSingleResult());
    }

    @Override
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page) {
        JpaCriteriaCopy.createMissingAliases(query);
        
        TypedQuery<T> q = em.apply().createQuery(query);
        int originalFirstResult = q.getFirstResult();
        int originalMaxResults = q.getMaxResults();
        
        if (page != Page.NoPaging) {
            q.setFirstResult(page.getFirstResult());
            q.setMaxResults(page.getMaxResults());
        }
        try {
            return newList(map(q.getResultList(), HibernateQueryExecutor_.<T>replaceProxy()));
        } finally {
            if (page != Page.NoPaging) {
                q.setFirstResult(originalFirstResult);
                q.setMaxResults(originalMaxResults);
            }
        }
    }

    @Override
    public void execute(NativeQuery<Void> query) {
        em.apply().createNativeQuery(query.query).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(NativeQuery<T> query) {
        SQLQuery q = em.apply().unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return Option.of(replaceProxy((T)q.uniqueResult()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(NativeQuery<T> query, Page page) {
        SQLQuery q = em.apply().unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return newList(map(applyPaging(q, page).list(), HibernateQueryExecutor_.replaceProxy()));
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
        return newList(map(applyPaging(q, page).list(), HibernateQueryExecutor_.replaceProxy()));
    }
    
    /**
     * Sometimes Hibernate returns proxies even for initialized entities. Don't know why.
     */
    @SuppressWarnings("unchecked")
    private static final <T> T replaceProxy(T entityOrProxy) {
        if (entityOrProxy instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)entityOrProxy;
            if (!proxy.getHibernateLazyInitializer().isUninitialized()) {
                return (T) proxy.getHibernateLazyInitializer().getImplementation();
            }
        }
        return entityOrProxy;
    }

    private static final SQLQuery bindReturnValues(SQLQuery q, List<Pair<String, Option<Type<?>>>> retvals) {
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
                q.addScalar(param.getKey());
            }
        }
        return q;
    }

    private static final SQLQuery bindTransformer(SQLQuery q, NativeQuery<?> query) {
        String[] retvals = newArray(String.class, map(query.retvals, Pair_.<String>left()));
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

    private static final <T extends Query> T bindParams(T q, Map<String, Pair<?, Option<Type<?>>>> params) {
        for (Entry<String, Pair<?, Option<Type<?>>>> param: params.entrySet()) {
            if (param.getValue()._1 instanceof Collection) {
                if (param.getValue()._2.isDefined()) {
                    q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1, ((HibernateTypeProvider.HibernateType<?>)param.getValue()._2.get()).type);
                } else {
                    q.setParameterList(param.getKey(), (Collection<?>) param.getValue()._1);
                }
            } else {
                if (param.getValue()._2.isDefined()) {
                    q.setParameter(param.getKey(), param.getValue()._1, ((HibernateTypeProvider.HibernateType<?>)param.getValue()._2.get()).type);
                } else {
                    q.setParameter(param.getKey(), param.getValue()._1);
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
