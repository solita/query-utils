package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newListOfSize;
import static fi.solita.utils.functional.Collections.newMultimap;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.transpose;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.FunctionalImpl.find;
import static fi.solita.utils.functional.FunctionalImpl.flatMap;
import static fi.solita.utils.functional.FunctionalImpl.map;
import static fi.solita.utils.functional.FunctionalS.range;
import static fi.solita.utils.query.QueryUtils.addListAttributeOrdering;
import static fi.solita.utils.query.QueryUtils.checkOptionalAttributes;
import static fi.solita.utils.query.QueryUtils.id;
import static fi.solita.utils.query.QueryUtils.inExpr;
import static fi.solita.utils.query.QueryUtils.join;
import static fi.solita.utils.query.QueryUtils.resolveOrderColumn;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;
import static fi.solita.utils.query.projection.EmbeddableUtil.breakEmbeddableToParts;
import static fi.solita.utils.query.projection.EmbeddableUtil.isCollectionOfEmbeddables;
import static fi.solita.utils.query.projection.ProjectionHelper_.performAdditionalQueriesForPlaceholderValues;
import static fi.solita.utils.query.projection.ProjectionResultUtil.transformAllRows;
import static fi.solita.utils.query.projection.ProjectionUtil.doJoins;
import static fi.solita.utils.query.projection.ProjectionUtil.isDistinctable;
import static fi.solita.utils.query.projection.ProjectionUtil.isId;
import static fi.solita.utils.query.projection.ProjectionUtil.isWrapperOfIds;
import static fi.solita.utils.query.projection.ProjectionUtil.shouldPerformAdditionalQuery;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.FunctionalA_;
import fi.solita.utils.functional.Functional_;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.codegen.MetaJpaConstructor;
import fi.solita.utils.query.projection.Constructors.IdProjection;

public class ProjectionHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectionHelper.class);

    private final Function0<EntityManager> em;
    private final JpaCriteriaQueryExecutor queryExecutor;
    
    public ProjectionHelper(Function0<EntityManager> em, JpaCriteriaQueryExecutor queryExecutor) {
        this.em = em;
        this.queryExecutor = queryExecutor;
    }

    public <E> List<Selection<?>> prepareProjectingQuery(MetaJpaConstructor<E,?,?> projection, From<?,? extends E> selection) {
        logger.info("prepareProjectingQuery({},{})", projection, selection);
        
        List<Selection<?>> ret;
        if (projection instanceof IdProjection) {
            logger.info("IdProjection. Replacing selection {} with just Id.", selection);
            ret = Collections.<Selection<?>>newList(selection.get(QueryUtils.<E>id(selection.getJavaType(), em.apply())));
        } else {
            ret = newListOfSize(projection.getParameters().size());
            for (Tuple3<Integer, Attribute<?,?>, Class<?>> t: zip(range(0), projection.getParameters(), projection.getConstructorParameterTypes())) {
                int index = t._1;
                Attribute<?,?> param = t._2;
                Class<?> constuctorParameterType = t._3;
                ret.add(transformSelectionForQuery(param, isId(constuctorParameterType) || isWrapperOfIds(projection, index), selection));
            }
        }
        
        logger.debug("prepareProjectingQuery -> {}", ret);
        return ret;
    }
    
    public <R> List<R> finalizeProjectingQuery(MetaJpaConstructor<?,R,?> projection, Iterable<? extends Iterable<Object>> rows) {
        logger.info("finalizeProjectingQuery({},{})", projection, rows);
        Iterable<Iterable<Object>> columns = transpose(rows);
        columns = newList(map(zip(range(0), projection.getParameters(), columns), performAdditionalQueriesForPlaceholderValues.ap(this).ap(projection)));
        List<R> ret = newList(transformAllRows(projection, transpose(columns)));
        logger.debug("finalizeProjectingQuery -> {}", ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private <T> Selection<?> transformSelectionForQuery(Attribute<?,?> param, boolean constructorExpectsId, From<?,T> sel) {
        logger.debug("transformSelectionForQuery({},{})", param, sel);
        // This check should actually have already occurred, but just in case we are missing it somewhere...
        checkOptionalAttributes((Attribute<?, ?>) param);
        
        Path<T> selection = sel;
        for (JoiningAttribute a: unwrap(JoiningAttribute.class, param)) {
            logger.info("JoiningAttribute detected. Performing joins: {}", a.getAttributes());
            // use left join here, since were are modifying the existing selection which should still return all the rows
            Pair<? extends Expression<?>, ? extends Attribute<?, ?>> relAndTarget = doJoins(sel, param, JoinType.LEFT);
            selection = (Path<T>) relAndTarget._1;
            param = relAndTarget._2;
        }
        
        for (PseudoAttribute pseudo: unwrap(PseudoAttribute.class, param)) {
            logger.info("PseudoAttribute detected: {}", pseudo);
            Expression<?> s = pseudo.getSelectionForQuery(em.apply(), selection);
            return constructorExpectsId && IEntity.class.isAssignableFrom(s.getJavaType()) ? ((Path<IEntity>)s).get(id((Class<IEntity>)s.getJavaType(), em.apply())) : s;
        }

        if (unwrap(AdditionalQueryPerformingAttribute.class, param).isDefined()) {
            logger.info("AdditionalQueryPerformingAttribute detected. Replacing parameter with source Id.");
            // add entity id as a placeholder to the query, to be replaced later with the actual result.
            return selection.get(id(selection.getJavaType(), em.apply()));
        }
        
        if (unwrap(PluralAttribute.class, param).isDefined()) {
            logger.info("PluralAttribute detected. Replacing parameter with source Id.");
            return selection.get(id(selection.getJavaType(), em.apply()));
        }
        
        for (SingularAttribute<T,?> attr: unwrap(SingularAttribute.class, param)) {
            if (IEntity.class.isAssignableFrom(attr.getJavaType())) {
                if (constructorExpectsId) {
                    logger.info("Singular Entity attribute detected, but constructor expects Id. Replacing parameter with Id.");
                    return selection.get(attr).get(id(attr.getBindableJavaType(), em.apply()));
                } else {
                    logger.info("Singular Entity attribute detected. Performing left join.");
                    return ((From<?, T>) selection).join(attr, JoinType.LEFT);
                }
            } else {
                return selection.get(attr);
            }
        }
        
        throw new IllegalArgumentException("Selection transformation for parameter type " + param.getClass().getName() + " not implemented. Should it be?");
    }

    @SuppressWarnings("unchecked")
    Iterable<Object> performAdditionalQueriesForPlaceholderValues(MetaJpaConstructor<?,?,?> projection, int index, Attribute<?,?> attr, Iterable<Object> values) {
        logger.debug("performAdditionalQueriesForPlaceholderValues({},{},{},{})", new Object[] {projection, index, attr, values});
        
        Iterable<Object> ret = values;
        if (shouldPerformAdditionalQuery(attr)) {
            List<Id<IEntity>> ids = (List<Id<IEntity>>)(Object)newList(values);
            logger.info("Preforming additional query for Attribute: {}", attr);
            Class<?> projectionType = projection.getConstructorParameterTypes().get(index);
            List<Object> r = doAdditionalQuery(projectionType, (Attribute<IEntity,?>)attr, isId(projectionType), isWrapperOfIds(projection, index), isDistinctable(projection, index), ids);
            ret = r;
            if (r.size() != ids.size()) {
                throw new RuntimeException("Whoops, a bug");
            }
        }
        
        logger.debug("performAdditionalQueriesForPlaceholderValues -> {}", ret);
        return ret;
    }

    private <SOURCE extends IEntity> List<Object> doAdditionalQuery(Class<?> projectionType, Attribute<SOURCE, ?> attr, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIdsToQuery) {
        logger.debug("doAdditionalQuery({},{},{},{},{},{})", new Object[] {projectionType, attr, isId, isWrapperOfIds, isDistinctable, sourceIdsToQuery});
        final Map<Id<SOURCE>, List<Object>> targetQueryResults = queryTargetsOfSources(attr, isId, isWrapperOfIds, isDistinctable, sourceIdsToQuery);

        Iterable<List<Object>> results = map(sourceIdsToQuery, new Transformer<Id<SOURCE>,List<Object>>() {
            @Override
            public List<Object> transform(Id<SOURCE> source) {
                return find(targetQueryResults, source).getOrElse(emptyList());
            }
        });
        List<Object>ret = newList(map(results, ProjectionResultUtil_.postProcessResult.ap(projectionType, attr)));
        logger.debug("doAdditionalQuery -> {}", ret);
        return ret;
    }
    
    private <SOURCE extends IEntity> Map<Id<SOURCE>,List<Object>> queryTargetsOfSources(Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIds) {
        logger.debug("queryTargetsOfSources({},{},{},{},{})", new Object[] {target, isId, isWrapperOfIds, isDistinctable, sourceIds});
        List<Object[]> results = queryTargets(target, isId, isWrapperOfIds, isDistinctable, sourceIds);
        
        @SuppressWarnings("unchecked")
        Iterable<Id<SOURCE>> ids = (Iterable<Id<SOURCE>>)(Object)map(results, FunctionalA_.head());
        Iterable<Iterable<Object>> actualResultRows = map(results, FunctionalA_.tail());

        Iterable<? extends Object> result;
        if (isCollectionOfEmbeddables(target)) {
            logger.info("Target is a collection of Embeddables. Picking embeddable parts manually.");
            result = map(actualResultRows, EmbeddableUtil_.collectEmbeddableFromParts.ap(em.apply().getMetamodel(), (Bindable<?>)target));
        } else {
            // for AdditionalQueryPerformingAttribute, replace the result object array with the actual object, performing additional queries if needed
            Option<AdditionalQueryPerformingAttribute> rel = unwrap(AdditionalQueryPerformingAttribute.class, target);
            if (rel.isDefined()) {
                logger.info("Target is AdditionalQueryPerformingAttribute. Finalizing: {}", target);
                result = finalizeProjectingQuery(rel.get().getConstructor(), actualResultRows);
            } else {
                if (!isEmpty(flatMap(actualResultRows, Functional_.tail()))) {
                    throw new RuntimeException("whoops");
                }
                result = map(actualResultRows, Functional_.head().andThen(ProjectionResultUtil_.postProcessValue.ap(target)));
            }
        }
        
        Map<Id<SOURCE>,List<Object>> ret = newMultimap(zip(ids, result));
        logger.debug("queryTargetsOfSources -> {}", ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private <SOURCE extends IEntity,R> List<Object[]> queryTargets(Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIds) {
        logger.debug("queryTargets({},{},{},{},{})", new Object[] {sourceIds, target, isId, isWrapperOfIds, isDistinctable});
        Class<SOURCE> sourceClass = target.getDeclaringType().getJavaType();
        CriteriaQuery<Object[]> query = em.apply().getCriteriaBuilder().createQuery(Object[].class);
        Root<SOURCE> source = query.from(sourceClass);
        Path<Id<SOURCE>> sourceId = source.get(id(sourceClass, em.apply()));
        
        logger.info("Inner joining from {} to {}", source, target);
        final Join<SOURCE,Object> relation = (Join<SOURCE, Object>) join(source, target, JoinType.INNER);
        
        query.where(inExpr(query, sourceId, sourceIds, em.apply().getCriteriaBuilder()));

        if (isDistinctable) {
            logger.info("Query is distinctable.");
            query.distinct(true);
        }

        if (target instanceof ListAttribute) {
            logger.info("Adding ordering based on ListAttribute");
            addListAttributeOrdering(query, relation, resolveOrderColumn((ListAttribute<?,?>)target), em.apply().getCriteriaBuilder());
        }
        
        Option<AdditionalQueryPerformingAttribute> rel = unwrap(AdditionalQueryPerformingAttribute.class, target);
        if (isCollectionOfEmbeddables(target)) {
            // Must handle collections of embeddables separately, since hibernate seems to include only
            // parentid (of the embeddable) in the select clause, but tries to read
            // all fields from the resultset
            logger.info("Target is a collection of embeddables. Breaking embeddable fields manually for the query.");
            query.multiselect(newList(cons(sourceId, breakEmbeddableToParts(em.apply().getMetamodel(), (Bindable<?>)target, relation))));
        } else if (rel.isDefined()) {
            logger.info("Target is AdditionalQueryPerformingAttribute. Preparing.");
            List<Selection<?>> selections = prepareProjectingQuery((MetaJpaConstructor<Object,?,?>)rel.get().getConstructor(), relation);
            if ((isId || isWrapperOfIds) && !Id.class.isAssignableFrom(head(selections).getJavaType())) {
                logger.info("Constructor expects an Id (or Ids) but the query was not for Ids. Projection to Id.");
                if (selections.size() != 1) {
                    throw new RuntimeException("whoops");
                }
                Path<Object> sel = ((Path<Object>)head(selections));
                query.multiselect(sourceId, sel.get(id(sel.getJavaType(), em.apply())));
            } else {
                query.multiselect(newList(cons(sourceId, selections)));
            }
        } else if (isWrapperOfIds) {
            logger.info("Constructor expects Ids but the query was not for Ids. Projection to Ids.");
            query.multiselect(sourceId, relation.get(id(relation.getJavaType(), em.apply())));
        } else {
            query.multiselect(sourceId, relation);
        }

        List<Object[]> ret = queryExecutor.getMany(query, Page.NoPaging);
        logger.info("queryTargets -> {}", ret);
        return ret;
    }
    
}
