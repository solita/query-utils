package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.query.QueryUtils.resolveSelection;
import static fi.solita.utils.query.QueryUtils.resolveSelectionPath;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.JpaCriteriaCopy;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.Removable;
import fi.solita.utils.query.attributes.AttributeProxy;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.ProjectionHelper;
import fi.solita.utils.query.projection.ProjectionUtil_;

public class JpaBasicQueries {

    @PersistenceContext
    private EntityManager em;

    private final ProjectionHelper projectionSupport;

    public JpaBasicQueries(ProjectionHelper projectionSupport) {
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
        JpaCriteriaCopy.copyCriteriaWithoutSelect(query, q, em.getCriteriaBuilder());
        From<?,E> selection = (From<?, E>) resolveSelection(query, q);

        q.multiselect(projectionSupport.prepareProjectingQuery(Project.<E>id(), selection));
        List<Object> results = em.createQuery(q).getResultList();

        Collection<Id<E>> idList = projectionSupport.finalizeProjectingQuery(Project.<E>id(), map(results, ProjectionUtil_.objectToObjectList));
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
    public <E extends IEntity & Identifiable<? extends Id<? super E>>, T> T getProxy(E entity, SingularAttribute<? super E, T> relation) {
        QueryUtils.checkOptionalAttributes(relation);
        
        Field field = (Field)relation.getJavaMember();
        field.setAccessible(true);
        Object ret;
        try {
            ret = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (AttributeProxy.unwrap(relation, OptionalAttribute.class).isDefined()) {
            return (T) Option.of(ret);
        } else {
            if (ret == null) {
                throw new IllegalArgumentException("Failed. Did you try to get an attribute from a proxy?");
            }
            return (T) ret;
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
