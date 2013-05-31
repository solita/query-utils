package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.map;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple_;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.backend.NativeQueryExecutor;
import fi.solita.utils.query.backend.QLQueryExecutor;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.QLQuery;

public class HibernateQueryExecutor implements JpaCriteriaQueryExecutor, NativeQueryExecutor, QLQueryExecutor {

    @PersistenceContext
    private EntityManager em;

    @Override
    public <T> T get(CriteriaQuery<T> query) {
        JpaCriteriaCopy.createMissingAliases(query);
        return em.createQuery(query).getSingleResult();
    }

    @Override
    public <T> List<T> getMany(CriteriaQuery<T> query, Page page) {
        JpaCriteriaCopy.createMissingAliases(query);
        
        TypedQuery<T> q = em.createQuery(query);
        int originalFirstResult = q.getFirstResult();
        int originalMaxResults = q.getMaxResults();
        
        if (page != Page.NoPaging) {
            q.setFirstResult(page.getFirstResult());
            q.setMaxResults(page.getMaxResults());
        }
        try {
            return q.getResultList();
        } finally {
            if (page != Page.NoPaging) {
                q.setFirstResult(originalFirstResult);
                q.setMaxResults(originalMaxResults);
            }
        }
    }

    @Override
    public void execute(NativeQuery<Void> query) {
        em.createNativeQuery(query.query).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(NativeQuery<T> query) {
        SQLQuery q = em.unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return Option.of((T)q.uniqueResult());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(NativeQuery<T> query, Page page) {
        SQLQuery q = em.unwrap(Session.class).createSQLQuery(query.query);
        q = bindParams(q, query.params);
        q = bindReturnValues(q, query.retvals);
        q = bindTransformer(q, query);
        return applyPaging(q, page).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Option<T> find(QLQuery<T> query) {
        Query q = em.unwrap(Session.class).createQuery(query.query);
        q = bindParams(q, query.params);
        return Option.of((T)q.uniqueResult());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        Query q = em.unwrap(Session.class).createQuery(query.query);
        q = bindParams(q, query.params);
        return applyPaging(q, page).list();
    }

    private static SQLQuery bindReturnValues(SQLQuery q, List<Pair<String, Option<Type<?>>>> retvals) {
        for (Entry<String, Option<Type<?>>> param: retvals) {
            if (param.getValue().isDefined()) {
                org.hibernate.type.Type t = ((HibernateTypeProvider.HibernateType<?>)param.getValue().get()).type;
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

    private static SQLQuery bindTransformer(SQLQuery q, NativeQuery<?> query) {
        String[] retvals = newArray(String.class, map(query.retvals, Tuple_._1_.<String>get_1()));
        if (query instanceof NativeQuery.NativeQueryPair) {
            q.setResultTransformer(TupleResultTransformers.Tuple2(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT3) {
            q.setResultTransformer(TupleResultTransformers.Tuple3(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT4) {
            q.setResultTransformer(TupleResultTransformers.Tuple4(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT5) {
            q.setResultTransformer(TupleResultTransformers.Tuple5(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT6) {
            q.setResultTransformer(TupleResultTransformers.Tuple6(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT7) {
            q.setResultTransformer(TupleResultTransformers.Tuple7(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT8) {
            q.setResultTransformer(TupleResultTransformers.Tuple8(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT9) {
            q.setResultTransformer(TupleResultTransformers.Tuple9(retvals));
        } else if (query instanceof NativeQuery.NativeQueryT10) {
            q.setResultTransformer(TupleResultTransformers.Tuple10(retvals));
        }
        // TODO:
        return q;
    }

    private static <T extends Query> T bindParams(T q, Map<String, Pair<?, Option<Type<?>>>> params) {
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

    private static Query applyPaging(Query q, Page page) {
        if (page != Page.NoPaging) {
            q.setFirstResult(page.getFirstResult())
             .setMaxResults(page.getMaxResults());
        }
        return q;
    }
}
