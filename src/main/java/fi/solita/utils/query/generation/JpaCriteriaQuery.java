package fi.solita.utils.query.generation;

import static fi.solita.utils.query.QueryUtils.resolveSelection;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.ApplyZero;
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
    
    public <E, A> CriteriaQuery<E> matching(SingularAttribute<E,? super A> first, Id<A> firstId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        Class<E> rootType = first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId));
        return query;
    }
    
    public <E, A> CriteriaQuery<E> matching(SingularAttribute<E,? super A> relation, A value) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        Class<E> rootType = relation.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(relation), value));
        return query;
    }
    
    public <E, A, B> CriteriaQuery<E> matching(SingularAttribute<E,? super A> first, SingularAttribute<E,? super B> second, Id<A> firstId, Id<B> secondId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        Class<E> rootType = first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId),
                    cb.equal(root.get(second).get(QueryUtils.id(second.getBindableJavaType(), em.get())), secondId));
        return query;
    }
    
    public <E, A, B> CriteriaQuery<E> matching(SingularAttribute<E,? super A> first, SingularAttribute<E,? super B> second, A firstValue, B secondValue) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        Class<E> rootType = first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first), firstValue),
                    cb.equal(root.get(second), secondValue));
        return query;
    }
    
    public <E, A, B, C> CriteriaQuery<E> matching(SingularAttribute<E,? super A> first, SingularAttribute<E,? super B> second, SingularAttribute<E,? super C> third, Id<A> firstId, Id<B> secondId, Id<C> thirdId) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        Class<E> rootType = first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first).get(QueryUtils.id(first.getBindableJavaType(), em.get())), firstId),
                    cb.equal(root.get(second).get(QueryUtils.id(second.getBindableJavaType(), em.get())), secondId),
                    cb.equal(root.get(third).get(QueryUtils.id(third.getBindableJavaType(), em.get())), thirdId));
        return query;
    }
    
    public <E, A, B, C> CriteriaQuery<E> matching(SingularAttribute<E,? super A> first, SingularAttribute<E,? super B> second, SingularAttribute<E,? super C> third, A firstValue, B secondValue, C thirdValue) {
        CriteriaBuilder cb = em.get().getCriteriaBuilder();
        Class<E> rootType = first.getDeclaringType().getJavaType();
        CriteriaQuery<E> query = cb.createQuery(rootType);
        Root<E> root = query.from(rootType);
        query.where(cb.equal(root.get(first), firstValue),
                    cb.equal(root.get(second), secondValue),
                    cb.equal(root.get(third), thirdValue));
        return query;
    }

    @SuppressWarnings("unchecked")
    public <E> CriteriaQuery<E> ofIds(Set<? extends Id<? super E>> ids, Class<E> entityClass) {
        CriteriaQuery<Object> query = em.get().getCriteriaBuilder().createQuery();
        if (ids.iterator().hasNext()) {
            Root<E> root = query.from(entityClass);
            Path<Id<E>> idPath = root.get(QueryUtils.<E,Id<E>>id(entityClass, em.get()));
            query.where(queryUtils.inExpr(idPath, (Set<Id<E>>)ids, em.get().getCriteriaBuilder()));
            query.select(root);
            return (CriteriaQuery<E>)(Object)query;
        } else {
            query.where(em.get().getCriteriaBuilder().or());
            Root<E> root = query.from(entityClass);
            query.select(root);
            return (CriteriaQuery<E>)(Object)query;
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


    private <E extends IEntity<?> & Identifiable<?>, R> CriteriaQuery<R> doRelated(E entity, Attribute<?, ?>... attributes) {
        CriteriaQuery<Object> query = em.get().getCriteriaBuilder().createQuery();
        @SuppressWarnings("unchecked")
        Root<E> root = (Root<E>) query.from(typeProvider.getEntityClass(entity));
        query.where(em.get().getCriteriaBuilder().equal(root.get(QueryUtils.id(root.getJavaType(), em.get())), entity.getId()));
        From<?, ?> join = root;
        for (Attribute<?, ?> attr : attributes) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }

        @SuppressWarnings("unchecked")
        CriteriaQuery<R> ret = (CriteriaQuery<R>) query.select(join);
        return ret;
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

    private <E, R>
    CriteriaQuery<R> doRelated(CriteriaQuery<E> query, Attribute<?,?>... attributes) {
        CriteriaQuery<Object> q = em.get().getCriteriaBuilder().createQuery();

        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.get().getCriteriaBuilder());
        From<?,?> join = resolveSelection(query, q);
        for (Attribute<?, ?> attr : attributes) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }

        @SuppressWarnings("unchecked")
        CriteriaQuery<R> ret = (CriteriaQuery<R>) q.select(join);
        return ret;
    }
}
