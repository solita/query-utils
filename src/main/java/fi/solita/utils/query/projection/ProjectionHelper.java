package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newListOfSize;
import static fi.solita.utils.functional.Collections.newMultimap;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.find;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.range;
import static fi.solita.utils.functional.Functional.transpose;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;
import static fi.solita.utils.query.projection.EmbeddableUtil.breakEmbeddableToParts;
import static fi.solita.utils.query.projection.EmbeddableUtil.isCollectionOfEmbeddables;
import static fi.solita.utils.query.projection.ProjectionHelper_.performAdditionalQueriesForPlaceholderValues;
import static fi.solita.utils.query.projection.ProjectionResultUtil.transformAllRows;
import static fi.solita.utils.query.projection.ProjectionUtil.isDistinctable;
import static fi.solita.utils.query.projection.ProjectionUtil.isId;
import static fi.solita.utils.query.projection.ProjectionUtil.isWrapperOfIds;
import static fi.solita.utils.query.projection.ProjectionUtil.shouldPerformAdditionalQuery;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Functional_;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.LiteralAttribute;
import fi.solita.utils.query.attributes.RelationAttribute;
import fi.solita.utils.query.attributes.SelfAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.projection.Constructors.IdProjection;

public class ProjectionHelper {

    @PersistenceContext
    private EntityManager em;

    public <E> List<Selection<?>> prepareProjectingQuery(ConstructorMeta_<E,?,?> constructor_, From<?,? extends E> selection) {
        if (constructor_ instanceof IdProjection) {
            // special handling for Id projections
            return Collections.<Selection<?>>newList(selection.get(QueryUtils.<E>id(selection.getJavaType(), em)));
        }
        
        List<Selection<?>> selections = newListOfSize(constructor_.getParameters().size());
        for (Tuple3<Integer, Attribute<?,?>, Class<?>> t: zip(range(0), constructor_.getParameters(), constructor_.getConstructorParameterTypes())) {
            int index = t._1;
            Attribute<?,?> param = t._2;
            Class<?> constuctorParameterType = t._3;
            selections.add(transformSelectionForQuery(param, isId(constuctorParameterType) || isWrapperOfIds(constructor_, index), selection));
        }
        return selections;
    }
    public <R> List<R> finalizeProjectingQuery(ConstructorMeta_<?,R,?> constructor_, Iterable<? extends Iterable<Object>> rows) {
        Iterable<Iterable<Object>> columns = transpose(rows);
        columns = newList(map(zip(range(0), constructor_.getParameters(), columns), performAdditionalQueriesForPlaceholderValues.apply(this).ap(constructor_)));
        return newList(transformAllRows(constructor_, transpose(columns)));
    }
    
    private <T> Selection<?> transformSelectionForQuery(Attribute<?,?> param, boolean constructorExpectsId, From<?,T> selection) {
        // This check should actually have already occurred, but just in case we are missing it somewhere...
        QueryUtils.checkOptionalAttributes((Attribute<?, ?>) param);

        if (unwrap(param, SelfAttribute.class).isDefined()) {
            return selection;
        }
        
        if (unwrap(param, LiteralAttribute.class).isDefined()) {
            // just add some placeholder literal to the query. Actual literal value is inserted after retrieving the results form the db.
            return em.getCriteriaBuilder().literal(LiteralAttribute.QUERY_PLACEHOLDER);
        }

        if (unwrap(param, RelationAttribute.class).isDefined()) {
            // add entity id as a placeholder to the query, to be replaced later with the actual projection.
            return selection.get(QueryUtils.id(selection.getJavaType(), em));
        }
        
        if (unwrap(param, PluralAttribute.class).isDefined()) {
            // ...same for plural attributes
            return selection.get(QueryUtils.id(selection.getJavaType(), em));
        }
        
        for (SingularAttribute<T,?> attr: unwrap(param, SingularAttribute.class)) {
            if (IEntity.class.isAssignableFrom(attr.getJavaType())) {
                if (constructorExpectsId) {
                    // target class constructor expects an Id but the selection is for an entity, so get just the Id instead of the whole thing
                    return selection.get(attr).get(QueryUtils.id(attr.getBindableJavaType(), em));
                } else {
                    return selection.join(attr, JoinType.LEFT);
                }
            } else {
                return selection.get(attr);
            }
        }
        
        throw new IllegalArgumentException("Selection transformation for parameter type " + param.getClass().getName() + " not implemented. Should it be?");
    }

    @SuppressWarnings("unchecked")
    Iterable<Object> performAdditionalQueriesForPlaceholderValues(ConstructorMeta_<?,?,?> constructor_, int index, Attribute<?,?> attr, Iterable<Object> values) {
        if (shouldPerformAdditionalQuery(attr)) {
            return doAdditionalQuery(constructor_, index, (Attribute<IEntity,?>)attr, isWrapperOfIds(constructor_, index), isDistinctable(constructor_, index), (Iterable<Id<IEntity>>)(Object)values);
        }
        return values;
    }

    private <SOURCE extends IEntity> List<Object> doAdditionalQuery(ConstructorMeta_<?,?,?> constructor_, int index, Attribute<SOURCE, ?> attr, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIdsToQuery) {
        final Map<Id<SOURCE>, List<Object>> targetQueryResults = queryTargetsOfSources(constructor_, attr, isWrapperOfIds, isDistinctable, sourceIdsToQuery);

        Iterable<List<Object>> results = map(sourceIdsToQuery, new Transformer<Id<SOURCE>,List<Object>>() {
            @Override
            public List<Object> transform(Id<SOURCE> source) {
                return find(targetQueryResults, source).getOrElse(emptyList());
            }
        });
        
        return newList(map(results, ProjectionResultUtil_.postProcessResult.ap(constructor_.getConstructorParameterTypes().get(index), attr)));
    }
    
    private <SOURCE extends IEntity> Map<Id<SOURCE>,List<Object>> queryTargetsOfSources(ConstructorMeta_<?,?,?> constructor_, Attribute<SOURCE, ?> target, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIds) {
        List<Object[]> results = queryTargets(sourceIds, target, isWrapperOfIds, isDistinctable);
        
        @SuppressWarnings("unchecked")
        Iterable<Id<SOURCE>> ids = (Iterable<Id<SOURCE>>)(Object)map(results, Functional_.head());
        Iterable<Iterable<Object>> actualResultRows = map(results, Functional_.tail());

        Iterable<? extends Object> result;
        if (isCollectionOfEmbeddables(target)) {
            result = map(actualResultRows, EmbeddableUtil_.collectEmbeddableFromParts.ap(em.getMetamodel(), (Bindable<?>)target));
        } else {
            // for RelationAttributes, replace the result object array with the actual object, performing hierarchical queries if needed
            @SuppressWarnings("rawtypes")
            Option<RelationAttribute> rel = unwrap(target, RelationAttribute.class);
            if (rel.isDefined()) {
                @SuppressWarnings("unchecked")
                List<Object> r = finalizeProjectingQuery(((RelationAttribute<?,?,Object>)rel.get()).getConstructor(), actualResultRows);
                result = r;
            } else {
                if (!isEmpty(flatMap(actualResultRows, Functional_.tail1()))) {
                    throw new RuntimeException("whoops");
                }
                result = map(actualResultRows, Functional_.head1().andThen(ProjectionResultUtil_.postProcessValue.ap(target)));
            }
        }
        
        return newMultimap(zip(ids, result));
    }
    
    private <SOURCE extends IEntity,R> List<Object[]> queryTargets(Iterable<Id<SOURCE>> sourceIds, Attribute<SOURCE, ?> target, boolean isWrapperOfIds, boolean isDistinctable) {
        Class<SOURCE> sourceClass = target.getDeclaringType().getJavaType();
        CriteriaQuery<Object[]> query = em.getCriteriaBuilder().createQuery(Object[].class);
        Root<SOURCE> source = query.from(sourceClass);
        Path<Id<SOURCE>> sourceId = source.get(QueryUtils.id(sourceClass, em));
        final Join<SOURCE,Object> relation = source.join(target.getName());
        query.where(QueryUtils.inExpr(sourceId, sourceIds, em.getCriteriaBuilder()));

        if (isDistinctable) {
            query.distinct(true);
        }

        if (target instanceof ListAttribute) {
            QueryUtils.addListAttributeOrdering(query, relation, em.getCriteriaBuilder());
        }

        if (unwrap(target, RelationAttribute.class).isDefined()) {
            @SuppressWarnings("unchecked")
            List<Selection<?>> params = newList(cons(sourceId, prepareProjectingQuery(unwrap(target, RelationAttribute.class).get().getConstructor(), relation)));
            query.multiselect(params);
        } else if (isWrapperOfIds) {
            // if the constructor wanted only a wrapper of ids, project to the id
            query.multiselect(sourceId, relation.get(QueryUtils.id(relation.getJavaType(), em)));
        } else if (isCollectionOfEmbeddables(target)) {
            // Must handle collections of embeddables separately, since hibernate seems to include only
            // parentid (of the embeddable) in the select clause, but tries to read
            // all fields from the resultset
            query.multiselect(newList(cons(sourceId, breakEmbeddableToParts(em.getMetamodel(), (Bindable<?>)target, relation))));
        } else {
            query.multiselect(sourceId, relation);
        }

        return em.createQuery(query).getResultList();
    }    
}
