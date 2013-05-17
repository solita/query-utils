package fi.solita.utils.query.execution;

import static fi.solita.utils.query.QueryUtils.copyCriteriaWithoutSelect;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;
import static fi.solita.utils.functional.Option.None;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Hibernate;

import fi.solita.utils.query.Id;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.ProjectionSupport;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.Removable;
import fi.solita.utils.functional.Option;

public class JpaBasicQueries {

    @PersistenceContext
    private EntityManager em;

    private final ProjectionSupport projectionSupport;

    public JpaBasicQueries(ProjectionSupport projectionSupport) {
        this.projectionSupport = projectionSupport;
    }

    public <ID extends Id<?>, E extends IEntity & Identifiable<ID>> ID persist(E entity) {
        em.persist(entity);
        return entity.getId();
    }

    public boolean isManaged(IEntity entity) {
        return em.contains(entity);
    }

    public <E extends IEntity & Removable> void remove(Id<E> id) {
        em.remove(getProxy(id));
    }

    public <E extends IEntity & Identifiable<? extends Id<E>> & Removable> void removeAll(CriteriaQuery<E> query) {
        CriteriaQuery<Object> q = em.getCriteriaBuilder().createQuery();
        copyCriteriaWithoutSelect(query, q, em.getCriteriaBuilder());
        @SuppressWarnings("unchecked")
        From<?,E> selection = (From<?, E>) resolveSelection(query, q);
        q.select(selection);

        q.multiselect(projectionSupport.transformParametersForQuery(Project.<E>id(), selection));
        List<Object> results = em.createQuery(q).getResultList();

        Collection<Id<E>> idList = projectionSupport.replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(results, Project.<E>id());
        if (!idList.isEmpty()) {
            em.createQuery("delete from " + resolveSelectionPath(query).getJavaType().getName() + " e where e.id in(:idList)").setParameter("idList", idList).executeUpdate();
        }
    }

    public <E extends IEntity> E get(Id<E> id) {
        E ret = em.find(id.getOwningClass(), id);
        if (ret == null) {
            throw new EntityNotFoundException("Entity of type " + id.getOwningClass().getName() + " with id " + id + " not found.");
        }
        return ret;
    }

    public <E extends IEntity> E getProxy(Id<E> id) {
        return em.getReference(id.getOwningClass(), id);
    }

    public <E extends IEntity> Option<E> find(Id<E> id) {
        return Option.of(em.find(id.getOwningClass(), id));
    }

    /**
     * Get an Entity proxy from <i>entity</i> without accessing database
     * (<i>entity</i> already has an ID for a ToOne relation, so this is possible)
     */
    @SuppressWarnings("unchecked")
    public <E extends IEntity, T extends IEntity> Option<T> getProxy(E entity, SingularAttribute<? super E, T> relation) {
        if (!Hibernate.isInitialized(entity)) {
            Hibernate.initialize(entity);
        }
        Field field = (Field)relation.getJavaMember();
        field.setAccessible(true);
        try {
            return Option.of((T) field.get(entity));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <E extends IEntity> Option<E> getIfDefined(Option<? extends Id<E>> idOption) {
        for (Id<E> id: idOption) {
            return Option.Some(get(id));
        }
        return None();
    }

    public <E extends IEntity> Option<E> getProxyIfDefined(Option<? extends Id<E>> idOption) {
        for (Id<E> id: idOption) {
            return Option.Some(getProxy(id));
        }
        return None();
    }
}
