package fi.solita.utils.query.generation;

import static fi.solita.utils.query.QueryUtils.resolveSelection;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;

import fi.solita.utils.functional.Function0;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.backend.TypeProvider;

public class JpaCriteriaQuery {

    private final Function0<EntityManager> em;
    private final TypeProvider typeProvider;
    
    public JpaCriteriaQuery(Function0<EntityManager> em, TypeProvider typeProvider) {
        this.em = em;
        this.typeProvider = typeProvider;
    }

    public <E extends IEntity> CriteriaQuery<E> single(Id<E> id) {
        CriteriaQuery<E> query = em.apply().getCriteriaBuilder().createQuery(id.getOwningClass());
        Root<E> root = query.from(id.getOwningClass());
        return query.where(em.apply().getCriteriaBuilder().equal(root.get(QueryUtils.id(id.getOwningClass(), em.apply())), id));
    }

    public <E extends IEntity> CriteriaQuery<E> all(Class<E> entityClass) {
        CriteriaQuery<E> query = em.apply().getCriteriaBuilder().createQuery(entityClass);
        return query.select(query.from(entityClass));
    }

    @SuppressWarnings("unchecked")
    public <E extends IEntity> CriteriaQuery<E> ofIds(Iterable<? extends Id<? super E>> ids, Class<E> entityClass) {
        CriteriaQuery<Object> query = em.apply().getCriteriaBuilder().createQuery();
        if (ids.iterator().hasNext()) {
            Root<E> root = query.from(entityClass);
            Path<Id<E>> idPath = root.get(QueryUtils.id(entityClass, em.apply()));
            query.where(QueryUtils.inExpr(query, idPath, (Iterable<Id<E>>)ids, em.apply().getCriteriaBuilder()));
            query.select(root);
            return (CriteriaQuery<E>)(Object)query;
        } else {
            query.where(em.apply().getCriteriaBuilder().or());
            Root<E> root = query.from(entityClass);
            query.select(root);
            return (CriteriaQuery<E>)(Object)query;
        }
    }

    public <E extends IEntity & Identifiable<?>, R1, A1 extends Attribute<? super E, ?> & Bindable<R1>>
            CriteriaQuery<R1> related(E entity, A1 r1) {
        return doRelated(entity, r1);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
            CriteriaQuery<R2> related(E entity, A1 r1, A2 r2) {
        return doRelated(entity, r1, r2);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
            CriteriaQuery<R3> related(E entity, A1 r1, A2 r2, A3 r3) {
        return doRelated(entity, r1, r2, r3);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
            CriteriaQuery<R4> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4) {
        return doRelated(entity, r1, r2, r3, r4);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
            CriteriaQuery<R5> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5) {
        return doRelated(entity, r1, r2, r3, r4, r5);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5 extends IEntity, R6, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
            CriteriaQuery<R6> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6) {
        return doRelated(entity, r1, r2, r3, r4, r5, r6);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5 extends IEntity, R6 extends IEntity, R7, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
            CriteriaQuery<R7> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7) {
        return doRelated(entity, r1, r2, r3, r4, r5, r6, r7);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5 extends IEntity, R6 extends IEntity, R7 extends IEntity, R8, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>, A8 extends Attribute<? super R7, ?> & Bindable<R8>>
            CriteriaQuery<R8> related(E entity, A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, A8 r8) {
        return doRelated(entity, r1, r2, r3, r4, r5, r6, r7, r8);
    }


    private <E extends IEntity & Identifiable<?>, R> CriteriaQuery<R> doRelated(E entity, Attribute<?, ?>... attributes) {
        CriteriaQuery<Object> query = em.apply().getCriteriaBuilder().createQuery();
        @SuppressWarnings("unchecked")
        Root<E> root = (Root<E>) query.from(typeProvider.getEntityClass(entity));
        query.where(em.apply().getCriteriaBuilder().equal(root.get(QueryUtils.id(root.getJavaType(), em.apply())), entity.getId()));
        From<?, ?> join = root;
        for (Attribute<?, ?> attr : attributes) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }

        @SuppressWarnings("unchecked")
        CriteriaQuery<R> ret = (CriteriaQuery<R>) query.select(join);
        return ret;
    }

    public <E extends IEntity, A1 extends Attribute<? super E, ?> & Bindable<R1>, R1>
    CriteriaQuery<R1> related(A1 r1, CriteriaQuery<E> query) {
        return doRelated(query, r1);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>>
    CriteriaQuery<R2> related(A1 r1, A2 r2, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2);
    }

    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>>
    CriteriaQuery<R3> related(A1 r1, A2 r2, A3 r3, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3);
    }
    
    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>>
    CriteriaQuery<R4> related(A1 r1, A2 r2, A3 r3, A4 r4, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4);
    }
    
    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>>
    CriteriaQuery<R5> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5);
    }
    
    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5 extends IEntity, R6, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>>
    CriteriaQuery<R6> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5, r6);
    }
    
    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5 extends IEntity, R6 extends IEntity, R7, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>>
    CriteriaQuery<R7> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5, r6, r7);
    }
    
    public <E extends IEntity & Identifiable<?>, R1 extends IEntity, R2 extends IEntity, R3 extends IEntity, R4 extends IEntity, R5 extends IEntity, R6 extends IEntity, R7 extends IEntity, R8, A1 extends Attribute<? super E, ?> & Bindable<R1>, A2 extends Attribute<? super R1, ?> & Bindable<R2>, A3 extends Attribute<? super R2, ?> & Bindable<R3>, A4 extends Attribute<? super R3, ?> & Bindable<R4>, A5 extends Attribute<? super R4, ?> & Bindable<R5>, A6 extends Attribute<? super R5, ?> & Bindable<R6>, A7 extends Attribute<? super R6, ?> & Bindable<R7>, A8 extends Attribute<? super R7, ?> & Bindable<R8>>
    CriteriaQuery<R8> related(A1 r1, A2 r2, A3 r3, A4 r4, A5 r5, A6 r6, A7 r7, A8 r8, CriteriaQuery<E> query) {
        return doRelated(query, r1, r2, r3, r4, r5, r6, r7, r8);
    }

    private <E extends IEntity, R>
    CriteriaQuery<R> doRelated(CriteriaQuery<E> query, Attribute<?,?>... attributes) {
        CriteriaQuery<Object> q = em.apply().getCriteriaBuilder().createQuery();

        JpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.apply().getCriteriaBuilder());
        From<?,?> join = resolveSelection(query, q);
        for (Attribute<?, ?> attr : attributes) {
            join = QueryUtils.join(join, attr, JoinType.INNER);
        }

        @SuppressWarnings("unchecked")
        CriteriaQuery<R> ret = (CriteriaQuery<R>) q.select(join);
        return ret;
    }
}
