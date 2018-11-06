package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.grouped;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.query.QueryUtils.checkOptionalAttributes;
import static fi.solita.utils.query.QueryUtils.resolveSelection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.Removable;
import fi.solita.utils.query.attributes.AttributeProxy;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.ProjectionHelper;

public class JpaBasicQueries {

    private final ApplyZero<EntityManager> em;

    private final ProjectionHelper projectionSupport;
    private final TypeProvider typeProvider;
    private final JpaCriteriaQueryExecutor queryExecutor;
    
    private final JpaCriteriaCopy jpaCriteriaCopy;
    private final Configuration config;
    
    public JpaBasicQueries(ApplyZero<EntityManager> em, ProjectionHelper projectionSupport, TypeProvider typeProvider, JpaCriteriaQueryExecutor queryExecutor, Configuration config) {
        this.em = em;
        this.projectionSupport = projectionSupport;
        this.typeProvider = typeProvider;
        this.queryExecutor = queryExecutor;
        this.jpaCriteriaCopy = new JpaCriteriaCopy(config);
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public <E extends IEntity<?> & Identifiable<? extends Id<?>>> Id<E> persist(E entity) {
        em.get().persist(entity);
        return (Id<E>) entity.getId();
    }

    public boolean isManaged(IEntity<?> entity) {
        return em.get().contains(entity);
    }

    public <E extends IEntity<?> & Removable> void remove(Id<E> id) {
        em.get().remove(toProxy(id));
    }

    @SuppressWarnings("unchecked")
    public <E extends IEntity<?> & Identifiable<? extends Id<E>> & Removable> void removeAll(CriteriaQuery<E> query) {
        CriteriaQuery<Id<E>> q = (CriteriaQuery<Id<E>>)(Object)em.get().getCriteriaBuilder().createQuery();
        jpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.get().getCriteriaBuilder());
        From<?,E> selection = resolveSelection(query, q);

        q.multiselect(projectionSupport.prepareProjectingQuery(Project.id(), selection));
        Collection<Id<E>> idList = queryExecutor.getMany(q, Page.NoPaging, LockModeType.NONE);

        Iterable<? extends Iterable<Id<E>>> grouped;
        if (!config.getInClauseValuesAmounts().isEmpty()) {
            grouped = grouped(config.getInClauseValuesAmounts().last(), idList);
        } else {
            grouped = Arrays.asList(idList);
        }
        for (Iterable<Id<E>> ids: grouped) {
            em.get().createQuery("delete from " + resolveSelection(query).getJavaType().getName() + " e where e.id in (:ids)").setParameter("ids", newList(ids)).executeUpdate();
        }
    }

    public <E extends IEntity<?>> E get(Id<E> id) {
        Option<E> ret = find(id);
        if (!ret.isDefined()) {
            throw new EntityNotFoundException("Entity of type " + id.getOwningClass().getName() + " with id " + id + " not found.");
        }
        return ret.get();
    }

    public <E extends IEntity<?>> E toProxy(Id<E> id) {
        return em.get().getReference(id.getOwningClass(), id);
    }
    
    public <E extends IEntity<?>> Iterable<E> toProxies(Iterable<? extends Id<E>> ids) {
        return map(JpaBasicQueries_.<E>toProxy().ap(this), ids);
    }

    public <E extends IEntity<?>> Option<E> find(Id<E> id) {
        CriteriaQuery<E> query = em.get().getCriteriaBuilder().createQuery(id.getOwningClass());
        Root<E> root = query.from(id.getOwningClass());
        query.where(em.get().getCriteriaBuilder().equal(root.get(QueryUtils.id(id.getOwningClass(), em.get())), id));
        
        List<E> ret = queryExecutor.getMany(query, Page.NoPaging, LockModeType.NONE);
        if (ret.size() > 1) {
            throw new RuntimeException("Found multiple entities with key: " + id);
        } else {
            return headOption(ret);
        }
        // replaced with a criteria query, since Hibernate 5 started to return base class instances instead of subclasses 
        // when queried with a base class id... 
        //return Option.of(em.get().find(id.getOwningClass(), id));
    }

    /**
     * Get an Entity proxy from <i>entity</i> without accessing database
     * (<i>entity</i> already has an ID for a ToOne relation, so this is possible)
     */
    @SuppressWarnings("unchecked")
    public <E extends Identifiable<? extends Id<? super E>>, T> T toProxy(E entity, SingularAttribute<? super E, T> relation) {
        checkOptionalAttributes(relation);
        
        Field field = (Field)relation.getJavaMember();
        field.setAccessible(true);
        Object ret;
        try {
            ret = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (AttributeProxy.unwrap(OptionalAttribute.class, relation).isDefined()) {
            return (T) Option.of(ret);
        } else {
            if (ret == null) {
                throw new IllegalArgumentException("Failed. Did you try to get an attribute from a proxy?");
            }
            return (T) ret;
        }
    }
    
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>> Collection<T> getProxies(E entity, CollectionAttribute<? super E, T> relation) {
        return newList(getProxiesIt(entity, relation));
    }
    
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>> Set<T> getProxies(E entity, SetAttribute<? super E, T> relation) {
        return newSet(getProxiesIt(entity, relation));
    }
    
    public <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>> List<T> getProxies(E entity, ListAttribute<? super E, T> relation) {
        return newList(getProxiesIt(entity, relation));
    }
    
    @SuppressWarnings("unchecked")
    private <E extends IEntity<?> & Identifiable<? extends Id<? super E>>, T extends IEntity<?>, C extends Collection<T>> Iterable<T> getProxiesIt(E entity, PluralAttribute<? super E, C, T> relation) {
        SingularAttribute<T, Id<T>> id = QueryUtils.id(relation.getBindableJavaType(), em.get());
        CriteriaQuery<Id<T>> query = em.get().getCriteriaBuilder().createQuery(id.getBindableJavaType());
        Root<E> root = (Root<E>) query.from(typeProvider.getEntityClass(entity));
        query.where(em.get().getCriteriaBuilder().equal(root.get(QueryUtils.id(root.getJavaType(), em.get())), entity.getId()));
        query.select(((Join<?,T>)QueryUtils.join((Root<?>)root, (Attribute<?,?>)relation, JoinType.INNER)).get(id));
        
        return map(JpaBasicQueries_.<T>toProxy().ap(this), queryExecutor.getMany(query, Page.NoPaging, LockModeType.NONE));
    }
}
