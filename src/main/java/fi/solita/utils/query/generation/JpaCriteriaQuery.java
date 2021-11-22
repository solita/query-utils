package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Functional.init;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.query.QueryUtils.resolveSelection;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.backend.TypeProvider;

public class JpaCriteriaQuery {

    private final ApplyZero<EntityManager> em;
    private final TypeProvider typeProvider;
    private final JpaCriteriaCopy jpaCriteriaCopy;
    private final QueryUtils queryUtils;
    
    public JpaCriteriaQuery(ApplyZero<EntityManager> em, TypeProvider typeProvider, Configuration config) {
        this.em = em;
        this.typeProvider = typeProvider;
        this.jpaCriteriaCopy = new JpaCriteriaCopy(config);
        this.queryUtils = new QueryUtils(config);
    }

    public <E extends IEntity<?>> CriteriaQuery<E> single(Id<E> id) {
        CriteriaQuery<E> query = em.get().getCriteriaBuilder().createQuery(id.getOwningClass());
        Root<E> root = query.from(id.getOwningClass());
        return query.where(em.get().getCriteriaBuilder().equal(root.get(QueryUtils.id(id.getOwningClass(), em.get())), id));
    }

    public <E extends IEntity<?>> CriteriaQuery<E> all(Class<E> entityClass) {
        CriteriaQuery<E> query = em.get().getCriteriaBuilder().createQuery(entityClass);
        return query.select(query.from(entityClass));
    }
    
    public <E, A> CriteriaQuery<E> matching(SingularAttribute<? super E,? super A> first, Id<A> firstId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId));
        return query;
    }
    
    public <E, A> CriteriaQuery<E> matchingOption(SingularAttribute<? super E,? extends Option<? super A>> first, Id<A> firstId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId));
        return query;
    }
    
    public <E, A> CriteriaQuery<E> matching(SingularAttribute<? super E,? super A> relation, A value) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) relation.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(relation), value));
        return query;
    }
    
    public <E, A> CriteriaQuery<E> matchingOption(SingularAttribute<? super E,? extends Option<? super A>> relation, A value) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) relation.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(relation), value));
        return query;
    }
    
    public <E, A, B> CriteriaQuery<E> matching(SingularAttribute<? super E,? super A> first, SingularAttribute<? super E,? super B> second, Id<A> firstId, Id<B> secondId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId),
                    cb.equal(root.get(second).get(QueryUtils.id(second.getBindableJavaType(), em.get())), secondId));
        return query;
    }
    
    public <E, A, B> CriteriaQuery<E> matchingOption(SingularAttribute<? super E,? extends Option<? super A>> first, SingularAttribute<? super E,? extends Option<? super B>> second, Id<A> firstId, Id<B> secondId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId),
                    cb.equal(root.get(second).get(QueryUtils.id(second.getBindableJavaType(), em.get())), secondId));
        return query;
    }
    
    public <E, A, B> CriteriaQuery<E> matching(SingularAttribute<? super E,? super A> first, SingularAttribute<? super E,? super B> second, A firstValue, B secondValue) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first), firstValue),
                    cb.equal(root.get(second), secondValue));
        return query;
    }
    
    public <E, A, B> CriteriaQuery<E> matchingOption(SingularAttribute<? super E,? extends Option<? super A>> first, SingularAttribute<? super E,? extends Option<? super B>> second, A firstValue, B secondValue) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first), firstValue),
                    cb.equal(root.get(second), secondValue));
        return query;
    }
    
    public <E, A, B, C> CriteriaQuery<E> matching(SingularAttribute<? super E,? super A> first, SingularAttribute<? super E,? super B> second, SingularAttribute<? super E,? super C> third, Id<A> firstId, Id<B> secondId, Id<C> thirdId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId),
                    cb.equal(root.get(second).get(QueryUtils.id(second.getBindableJavaType(), em.get())), secondId),
                    cb.equal(root.get(third).get(QueryUtils.id(third.getBindableJavaType(), em.get())), thirdId));
        return query;
    }
    
    public <E, A, B, C> CriteriaQuery<E> matchingOption(SingularAttribute<? super E,? extends Option<? super A>> first, SingularAttribute<? super E,? extends Option<? super B>> second, SingularAttribute<? super E,? extends Option<? super C>> third, Id<A> firstId, Id<B> secondId, Id<C> thirdId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId),
                    cb.equal(root.get(second).get(QueryUtils.id(second.getBindableJavaType(), em.get())), secondId),
                    cb.equal(root.get(third).get(QueryUtils.id(third.getBindableJavaType(), em.get())), thirdId));
        return query;
    }
    
    public <E, A, B, C> CriteriaQuery<E> matching(SingularAttribute<? super E,? super A> first, SingularAttribute<? super E,? super B> second, SingularAttribute<? super E,? super C> third, A firstValue, B secondValue, C thirdValue) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first), firstValue),
                    cb.equal(root.get(second), secondValue),
                    cb.equal(root.get(third), thirdValue));
        return query;
    }
    
    public <E, A, B, C> CriteriaQuery<E> matchingOption(SingularAttribute<? super E,? extends Option<? super A>> first, SingularAttribute<? super E,? extends Option<? super B>> second, SingularAttribute<? super E,? extends Option<? super C>> third, A firstValue, B secondValue, C thirdValue) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<E> rootType = (Class<E>) first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first), firstValue),
                    cb.equal(root.get(second), secondValue),
                    cb.equal(root.get(third), thirdValue));
        return query;
    }

    public <E> CriteriaQuery<E> ofIds(Set<? extends Id<? super E>> ids, Class<E> entityClass) {
        CriteriaQuery<E> query = em.get().getCriteriaBuilder().createQuery(entityClass);
        if (ids.iterator().hasNext()) {
            Root<E> root = query.from(entityClass);
            Path<Id<E>> idPath = root.get(QueryUtils.<E,Id<E>>id(entityClass, em.get()));
            query.where(queryUtils.inExpr(idPath, ids, em.get().getCriteriaBuilder()));
            query.select(root);
            return query;
        } else {
            query.where(em.get().getCriteriaBuilder().or());
            Root<E> root = query.from(entityClass);
            query.select(root);
            return query;
        }
    }

    public <E extends IEntity<?> & Identifiable<?>, R1 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>>
            CriteriaQuery<R1> related(E entity, A1 r1) {
        return doRelated(entity, r1);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
            CriteriaQuery<R2> related(E entity, A1 r1, A2 r2) {
        return doRelated(entity, r1, r2);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2, R3 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
            CriteriaQuery<R3> related(E entity, A1 r1, A2 r2, A3 r3) {
        return doRelated(entity, r1, r2, r3);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2, R3, R4 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
            CriteriaQuery<R4> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4) {
        return doRelated(entity, r1, r2, r3, r4);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2, R3, R4, R5 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
            CriteriaQuery<R5> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5) {
        return doRelated(entity, r1, r2, r3, r4, r5);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2, R3, R4, R5, R6 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
            CriteriaQuery<R6> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6) {
        return doRelated(entity, r1, r2, r3, r4, r5, r6);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2, R3, R4, R5, R6, R7 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
            CriteriaQuery<R7> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7) {
        return doRelated(entity, r1, r2, r3, r4, r5, r6, r7);
    }

    public <E extends IEntity<?> & Identifiable<?>, R1, R2, R3, R4, R5, R6, R7, R8 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>, A8 extends Attribute<? super R7, ?> & Bindable<R8>>
            CriteriaQuery<R8> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, A8 r8) {
        return doRelated(entity, r1, r2, r3, r4, r5, r6, r7, r8);
    }


    @SuppressWarnings("unchecked")
    private <E extends IEntity<?> & Identifiable<?>, R> CriteriaQuery<R> doRelated(E entity, Attribute<?, ?>... attributes) {
        CriteriaQuery<Object> query = (CriteriaQuery<Object>) em.get().getCriteriaBuilder().createQuery(last(attributes).getJavaType());
        Root<E> root = (Root<E>) query.from(typeProvider.getEntityClass(entity));
        query.where(em.get().getCriteriaBuilder().equal(root.get(QueryUtils.id(root.getJavaType(), em.get())), entity.getId()));
        From<?, ?> join = root;
        for (Attribute<?, ?> attr : attributes) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }

        return (CriteriaQuery<R>) query.select(join);
    }

    public <E, A1 extends Attribute<? super E, ?> & Bindable<R1>, R1 extends IEntity<?>>
    CriteriaQuery<R1> related(A1 r1, CriteriaQuery<E> query) {
        return doRelated(query, r1);
    }

    public <E extends Identifiable<?>, R1, R2 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
    CriteriaQuery<R2> related(A1 r1, A2 r2, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2);
    }

    public <E extends Identifiable<?>, R1, R2, R3 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
    CriteriaQuery<R3> related(A1 r1, A2 r2, A3 r3, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3);
    }
    
    public <E extends Identifiable<?>, R1, R2, R3, R4 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
    CriteriaQuery<R4> related(A1 r1, A2 r2, A3 r3, A4 r4, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4);
    }
    
    public <E extends Identifiable<?>, R1, R2, R3, R4, R5 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    CriteriaQuery<R5> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5);
    }
    
    public <E extends Identifiable<?>, R1, R2, R3, R4, R5, R6 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    CriteriaQuery<R6> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5, r6);
    }
    
    public <E extends Identifiable<?>, R1, R2, R3, R4, R5, R6, R7 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    CriteriaQuery<R7> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5, r6, r7);
    }
    
    public <E extends Identifiable<?>, R1, R2, R3, R4, R5, R6, R7, R8 extends IEntity<?>, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>, A8 extends Attribute<? super R7, ?> & Bindable<R8>>
    CriteriaQuery<R8> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, A8 r8, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5, r6, r7, r8);
    }

    @SuppressWarnings("unchecked")
    private <E, R>
    CriteriaQuery<R> doRelated(CriteriaQuery<E> query, Attribute<?,?>... attributes) {
        CriteriaQuery<Object> q = (CriteriaQuery<Object>) em.get().getCriteriaBuilder().createQuery(last(attributes).getJavaType());

        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.get().getCriteriaBuilder());
        From<?,?> join = resolveSelection(query, q);
        for (Attribute<?, ?> attr : attributes) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }

        CriteriaQuery<R> ret = (CriteriaQuery<R>) q.select(join);
        return ret;
    }

    public <R,E extends Identifiable<?>, R1, A1 extends Attribute<? super E, ?> & Bindable<R1>>
    CriteriaQuery<Pair<R,R1>> relatedSingular(SingularAttribute<E,R> key, A1 r1, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
    CriteriaQuery<Pair<R,R2>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, R3, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
    CriteriaQuery<Pair<R,R3>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, A3 r3, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2, r3);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, R3, R4, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
    CriteriaQuery<Pair<R,R4>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, A3 r3, A4 r4, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2, r3, r4);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, R3, R4, R5, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    CriteriaQuery<Pair<R,R5>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2, r3, r4, r5);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, R3, R4, R5, R6, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    CriteriaQuery<Pair<R,R6>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2, r3, r4, r5, r6);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, R3, R4, R5, R6, R7, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    CriteriaQuery<Pair<R,R7>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2, r3, r4, r5, r6, r7);
    }
    
    public <R,E extends Identifiable<?>, R1, R2, R3, R4, R5, R6, R7, R8, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>, A8 extends Attribute<? super R7, ?> & Bindable<R8>>
    CriteriaQuery<Pair<R,R8>> relatedSingular(SingularAttribute<E,R> key, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, A8 r8, CriteriaQuery<E> query) {
        return doRelatedSingular(key, query, r1, r2, r3, r4, r5, r6, r7, r8);
    }
    
    @SuppressWarnings("unchecked")
    private <E, K, V>
    CriteriaQuery<Pair<K,V>> doRelatedSingular(SingularAttribute<E,K> key, CriteriaQuery<E> query, Attribute<?,?>... attributes) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        CriteriaQuery<Pair<K, V>> q = (CriteriaQuery<Pair<K, V>>)(Object)cb.createQuery(Pair.class);
        
        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, cb);
        From<?,E> selection = resolveSelection(query, q);
        
        Subquery<V> subq = (Subquery<V>) q.subquery(last(attributes).getJavaType());
        Root<E> from = subq.from(key.getDeclaringType().getJavaType());
        From<?,?> join = from;
        for (Attribute<?, ?> attr : init(attributes)) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }
        subq.where(cb.equal(cb.function("rownum", long.class), 1),
                   cb.equal(from.get(key), selection.get(key)));
        
        return q.multiselect(selection.get(key), subq.select((Expression<V>)QueryUtils.get(join, last(attributes))));
    }
}
