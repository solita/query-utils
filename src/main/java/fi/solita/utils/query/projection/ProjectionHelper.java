package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newListOfSize;
import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Collections.newMultimap;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.tail;
import static fi.solita.utils.functional.Functional.transpose;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.FunctionalM.find;
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
import static fi.solita.utils.query.projection.ProjectionUtil.doRestrictions;
import static fi.solita.utils.query.projection.ProjectionUtil.isDistinctable;
import static fi.solita.utils.query.projection.ProjectionUtil.isId;
import static fi.solita.utils.query.projection.ProjectionUtil.isWrapperOfIds;
import static fi.solita.utils.query.projection.ProjectionUtil.shouldPerformAdditionalQuery;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Either;
import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.Option;
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
import fi.solita.utils.query.meta.MetaJpaConstructor;
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
            ret = Collections.<Selection<?>>newList(selection.get(QueryUtils.<E,Object>id(selection.getJavaType(), em.apply())));
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
        columns = newList(map(performAdditionalQueriesForPlaceholderValues.ap(this).ap(projection), zip(range(0), projection.getParameters(), columns)));
        List<R> ret = newList(transformAllRows(projection, transpose(columns)));
        logger.debug("finalizeProjectingQuery -> {}", ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private <T> Selection<?> transformSelectionForQuery(Attribute<?,?> param, boolean constructorExpectsId, From<?,T> selection) {
        logger.debug("transformSelectionForQuery({},{})", param, selection);
        // This check should actually have already occurred, but just in case we are missing it somewhere...
        checkOptionalAttributes((Attribute<?, ?>) param);
        
        for (PseudoAttribute pseudo: unwrap(PseudoAttribute.class, param)) {
            for (@SuppressWarnings("unused") JoiningAttribute a: unwrap(JoiningAttribute.class, param)) {
                // use left join here, since were are modifying the existing selection which should still return all the rows
                selection = (From<?, T>) doJoins(selection, param, JoinType.LEFT)._2;
            }
            
            logger.info("PseudoAttribute detected: {}", pseudo);
            Expression<?> s = pseudo.getSelectionForQuery(em.apply(), selection);
            doRestrictions(selection, param); // to restrict e.g. SelfAttribute, if so wanted.
            return unwrap(AdditionalQueryPerformingAttribute.class, param).isDefined() || constructorExpectsId && IEntity.class.isAssignableFrom(s.getJavaType()) ? ((Path<IEntity>)s).get(id((Class<IEntity>)s.getJavaType(), em.apply())) : s;
        }
        
        if (unwrap(AdditionalQueryPerformingAttribute.class, param).isDefined()) {
            SingularAttribute<T,Id<T>> replacement = id(selection.getJavaType(), em.apply());
            logger.info("AdditionalQueryPerformingAttribute detected. Replacing selection {} with source Id {}", selection.getJavaType().getSimpleName(), replacement.getName());
            // add entity id as a placeholder to the query, to be replaced later with the actual result.
            return selection.get(replacement);
        }

        if (unwrap(PluralAttribute.class, param).isDefined()) {
            logger.info("PluralAttribute detected. Replacing parameter with source Id.");
            return selection.get(id(selection.getJavaType(), em.apply()));
        }
        
        for (@SuppressWarnings("unused") JoiningAttribute a: unwrap(JoiningAttribute.class, param)) {
            // use left join here, since were are modifying the existing selection which should still return all the rows
            selection = (From<?, T>) doJoins(selection, param, JoinType.LEFT)._2;
            //param = relAndTarget._2;
        }
        
        for (SingularAttribute<T,?> attr: unwrap(SingularAttribute.class, param)) {
            if (IEntity.class.isAssignableFrom(attr.getJavaType())) {
                if (constructorExpectsId) {
                    logger.info("Singular Entity attribute detected, but constructor expects Id. Replacing parameter with Id.");
                    return doRestrictions(selection.join(attr, JoinType.LEFT), attr).get(id(attr.getBindableJavaType(), em.apply()));
                } else {
                    logger.info("Singular Entity attribute detected. Performing left join.");
                    return doRestrictions(selection.join(attr, JoinType.LEFT), attr);
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
            if (!ids.isEmpty()) {
                logger.info("Preforming additional query for Attribute: {}", attr);
                Class<?> projectionType = projection.getConstructorParameterTypes().get(index);
                List<Object> r = doAdditionalQuery(projectionType, (Attribute<IEntity,?>)attr, isId(projectionType), isWrapperOfIds(projection, index), isDistinctable(projection, index), ids);
                ret = r;
                if (r.size() != ids.size()) {
                    throw new RuntimeException("Whoops, a bug");
                }
            }
        }
        
        logger.debug("performAdditionalQueriesForPlaceholderValues -> {}", ret);
        return ret;
    }

    private <SOURCE extends IEntity> List<Object> doAdditionalQuery(Class<?> projectionType, Attribute<SOURCE, ?> attr, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIdsToQuery) {
        logger.debug("doAdditionalQuery({},{},{},{},{},{})", new Object[] {projectionType, attr, isId, isWrapperOfIds, isDistinctable, sourceIdsToQuery});
        final Map<?, List<Object>> targetQueryResults = queryTargetsOfSources(attr, isId, isWrapperOfIds, isDistinctable, sourceIdsToQuery);

        Iterable<List<Object>> results = map(new Transformer<Object,List<Object>>() {
            @Override
            public List<Object> transform(Object source) {
                return find(source, targetQueryResults).getOrElse(emptyList());
            }
        }, sourceIdsToQuery);
        List<Object>ret = newList(map(ProjectionResultUtil_.postProcessResult.ap(projectionType, attr), results));
        logger.debug("doAdditionalQuery -> {}", ret);
        return ret;
    }
    
    private <SOURCE extends IEntity> Map<Id<SOURCE>,List<Object>> queryTargetsOfSources(final Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIds) {
        logger.debug("queryTargetsOfSources({},{},{},{},{})", new Object[] {target, isId, isWrapperOfIds, isDistinctable, sourceIds});
        Collection<Object[]> results = queryTargets(target, isId, isWrapperOfIds, isDistinctable, sourceIds);
        
        @SuppressWarnings("unchecked")
        Iterable<Id<SOURCE>> ids = (Iterable<Id<SOURCE>>)(Object)map(new Transformer<Object[],Object>() {
            @Override
            public Object transform(Object[] source) {
                return head(source);
            }
        }, results);
        Iterable<Iterable<Object>> actualResultRows = map(new Transformer<Object[],Iterable<Object>>() {
            @Override
            public Iterable<Object> transform(Object[] source) {
                return tail(source);
            }
        }, results);

        Iterable<? extends Object> result;
        if (isCollectionOfEmbeddables(target)) {
            logger.info("Target is a collection of Embeddables. Picking embeddable parts manually.");
            result = map(EmbeddableUtil_.collectEmbeddableFromParts.ap(em.apply().getMetamodel(), (Bindable<?>)target), actualResultRows);
        } else {
            // for AdditionalQueryPerformingAttribute, replace the result object array with the actual object, performing additional queries if needed
            Option<AdditionalQueryPerformingAttribute> rel = unwrap(AdditionalQueryPerformingAttribute.class, target);
            if (rel.isDefined()) {
                logger.info("Target is AdditionalQueryPerformingAttribute. Finalizing: {}", target);
                result = finalizeProjectingQuery(rel.get().getConstructor(), actualResultRows);
            } else {
                if (!isEmpty(flatMap(new Transformer<Iterable<Object>,Iterable<Object>>() {
                    @Override
                    public Iterable<Object> transform(Iterable<Object> source) {
                        return tail(source);
                    }
                }, actualResultRows))) {
                    throw new RuntimeException("whoops");
                }
                result = map(new Transformer<Iterable<Object>,Object>() {
                    @Override
                    public Object transform(Iterable<Object> source) {
                        return ProjectionResultUtil.postProcessValue(target, head(source));
                    }
                }, actualResultRows);
            }
        }
        
        Map<Id<SOURCE>,List<Object>> ret = newMultimap(zip(ids, result));
        logger.debug("queryTargetsOfSources -> {}", ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private <SOURCE extends IEntity, SOURCE_ID,R> Collection<Object[]> queryTargets(Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Iterable<SOURCE_ID> sourceIds) {
        logger.debug("queryTargets({},{},{},{},{})", new Object[] {sourceIds, target, isId, isWrapperOfIds, isDistinctable});
        Class<SOURCE> sourceClass = target.getDeclaringType() != null ? target.getDeclaringType().getJavaType() : ((Id<SOURCE>)head(sourceIds)).getOwningClass();
        CriteriaQuery<Object[]> query = em.apply().getCriteriaBuilder().createQuery(Object[].class);
        Root<SOURCE> source = query.from(sourceClass);
        Path<SOURCE_ID> sourceId = source.get(QueryUtils.<SOURCE,SOURCE_ID>id(sourceClass, em.apply()));
        
        Either<From<SOURCE,Object>,Attribute<?,?>> relationOrAdditionalGet;
        Map<Attribute<?, ?>, From<?, ?>> actualJoins = newMap();
        From<?,?> last;
        if (!unwrap(PseudoAttribute.class, target).isDefined()) { 
            logger.info("Inner joining from {} to {}", source, target);
            Tuple3<Map<Attribute<?, ?>, From<?, ?>>, From<?, ?>, Attribute<?, ?>> joined = doJoins(source, target, JoinType.INNER);
            actualJoins = joined._1;
            if (joined._3.getPersistentAttributeType() != PersistentAttributeType.BASIC) {
                From<SOURCE,Object> r = (From<SOURCE, Object>) join(joined._2, joined._3, JoinType.INNER);
                relationOrAdditionalGet = Either.left(r);
                actualJoins.put(joined._3, r);
                last = r;
            } else {
                relationOrAdditionalGet = (Either<From<SOURCE,Object>,Attribute<?,?>>)(Object)Either.right(joined._3);
                last = joined._2;
            }
        } else {
            logger.info("Query is for a PseudoAttribute");
            actualJoins.put(target, source);
            relationOrAdditionalGet = Either.left((From<SOURCE, Object>)(Object)source);
            last = source;
        }
        
        for (From<?,?> r: relationOrAdditionalGet.left) {
            ProjectionUtil.doRestrictions(r, target);
        }
        
        query.where(inExpr(query, sourceId, sourceIds, em.apply().getCriteriaBuilder()));

        if (isDistinctable) {
            logger.info("Query is distinctable.");
            query.distinct(true);
        }

        setListAttributeOrderings(target, query, actualJoins);
        
        Option<AdditionalQueryPerformingAttribute> rel = unwrap(AdditionalQueryPerformingAttribute.class, target);
        if (isCollectionOfEmbeddables(target)) {
            // Must handle collections of embeddables separately, since hibernate seems to include only
            // parentid (of the embeddable) in the select clause, but tries to read
            // all fields from the resultset
            logger.info("Target is a collection of embeddables. Breaking embeddable fields manually for the query.");
            query.multiselect(newList(cons(sourceId, breakEmbeddableToParts(em.apply().getMetamodel(), (Bindable<?>)target, relationOrAdditionalGet.left.get()))));
        } else if (rel.isDefined()) {
            logger.info("Target is AdditionalQueryPerformingAttribute. Preparing.");
            List<Selection<?>> selections = prepareProjectingQuery((MetaJpaConstructor<Object,?,?>)rel.get().getConstructor(), relationOrAdditionalGet.left.get());
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
        } else {
            Path<Object> r = (Path<Object>) (relationOrAdditionalGet.isRight() ? QueryUtils.get(last, relationOrAdditionalGet.right.get()) : relationOrAdditionalGet.left.get());
            if ((isId || isWrapperOfIds) && !isId(r.getJavaType())) {
                logger.info("Constructor expects Ids but the query was not for Ids. Projection to Ids.");
                query.multiselect(sourceId, r.get(id(r.getJavaType(), em.apply())));
            } else {
                query.multiselect(sourceId, r);
            }
        }

        Collection<Object[]> ret = queryExecutor.getMany(query, Page.NoPaging);
        if (logger.isInfoEnabled()) {
            logger.info("queryTargets -> {}", newList(map(new Transformer<Object[],String>() {
                @Override
                public String transform(Object[] source) {
                    return Arrays.toString(source);
                }
            }, ret)));
        }
        return ret;
    }

    private void setListAttributeOrderings(Attribute<?, ?> target, CriteriaQuery<Object[]> query, Map<Attribute<?, ?>, From<?, ?>> actualJoins) {
        if (target instanceof JoiningAttribute) {
            logger.info("Adding orderings based on ListAttributes");
            for (Attribute<?, ?> a: ((JoiningAttribute) target).getAttributes()) {
                setListAttributeOrderings(a, query, actualJoins);
            }
        } else if (target instanceof ListAttribute) {
            addListAttributeOrdering(query, actualJoins.get(target), resolveOrderColumn((ListAttribute<?,?>)target), em.apply().getCriteriaBuilder());
        }
    }
    
}
