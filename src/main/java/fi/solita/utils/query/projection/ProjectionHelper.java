package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMultimap;
import static fi.solita.utils.functional.Collections.newMutableListOfSize;
import static fi.solita.utils.functional.Collections.newMutableMap;
import static fi.solita.utils.query.attributes.AttributeProxy.*;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Collections.newSortedSet;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.grouped;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.max;
import static fi.solita.utils.functional.Functional.repeat;
import static fi.solita.utils.functional.Functional.size;
import static fi.solita.utils.functional.Functional.tail;
import static fi.solita.utils.functional.Functional.transpose;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.FunctionalM.find;
import static fi.solita.utils.functional.FunctionalS.range;
import static fi.solita.utils.functional.Predicates.greaterThanOrEqualTo;
import static fi.solita.utils.functional.Predicates.lessThanOrEqualTo;
import static fi.solita.utils.functional.Predicates.not;
import static fi.solita.utils.query.QueryUtils.addListAttributeOrdering;
import static fi.solita.utils.query.QueryUtils.checkOptionalAttributes;
import static fi.solita.utils.query.QueryUtils.id;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Attribute.PersistentAttributeType;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Apply3;
import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Either;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Predicates;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.MultiColumnId;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.QueryUtils.Optimization;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.backend.JpaCriteriaQueryExecutor;
import fi.solita.utils.query.meta.MetaJpaConstructor;
import fi.solita.utils.query.projection.Constructors.ExpressionProjection;
import fi.solita.utils.query.projection.Constructors.IdProjection;

public class ProjectionHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectionHelper.class);

    private final ApplyZero<EntityManagerFactory> emf;
    private final JpaCriteriaQueryExecutor queryExecutor;
    private final Configuration config;
    private final QueryUtils queryUtils;
    
    public ProjectionHelper(ApplyZero<EntityManagerFactory> emf, JpaCriteriaQueryExecutor queryExecutor, Configuration config) {
        this.emf = emf;
        this.queryExecutor = queryExecutor;
        this.config = config;
        this.queryUtils = new QueryUtils(config);
    }

    public <E> List<Selection<?>> prepareProjectingQuery(MetaJpaConstructor<E,?,?> projection, From<?,? extends E> selection) {
        logger.debug("prepareProjectingQuery({},{})", projection, selection);
        
        List<Selection<?>> ret;
        if (projection instanceof IdProjection) {
            logger.debug("IdProjection. Replacing selection {} with just Id.", selection);
            ret = Collections.<Selection<?>>newList(selection.get(QueryUtils.<E,Object>id(selection.getJavaType(), emf.get())));
        } else {
            ret = newMutableListOfSize(projection.getParameters().size());
            for (Tuple3<Integer, Attribute<?,?>, Class<?>> t: zip(range(0), projection.getParameters(), projection.getConstructorParameterTypes())) {
                int index = t._1;
                Attribute<?,?> param = t._2;
                Class<?> constuctorParameterType = t._3;
                ret.add(transformSelectionForQuery(param, isId(constuctorParameterType) || isWrapperOfIds(projection, index), selection, projection));
            }
        }
        
        logger.debug("prepareProjectingQuery -> {}", ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public <R> List<R> finalizeProjectingQuery(MetaJpaConstructor<?,? extends R,?> projection, Iterable<? extends Iterable<Object>> rows) {
        logger.debug("finalizeProjectingQuery({},{})", projection, rows);
        Iterable<Iterable<Object>> columns = transpose(rows);
        columns = newList(map((Apply3<Integer, Attribute<?, ?>, Iterable<Object>, Iterable<Object>>)performAdditionalQueriesForPlaceholderValues.ap(this).ap(projection), zip(range(0), projection.getParameters(), columns)));
        List<? extends R> ret = newList(transformAllRows(projection, transpose(columns)));
        logger.debug("finalizeProjectingQuery -> {}", ret);
        return (List<R>) ret;
    }
    
    @SuppressWarnings("unchecked")
    private <T> Selection<?> transformSelectionForQuery(Attribute<?,?> param, boolean constructorExpectsId, From<?,T> selection, MetaJpaConstructor<?,?,?> projection) {
        logger.debug("transformSelectionForQuery({},{})", param, selection);
        // This check should actually have already occurred, but just in case we are missing it somewhere...
        checkOptionalAttributes(param);
        
        for (PseudoAttribute pseudo: unwrap(PseudoAttribute.class, param)) {
            for (@SuppressWarnings("unused") JoiningAttribute a: unwrap(JoiningAttribute.class, param)) {
                // use left join here, since were are modifying the existing selection which should still return all the rows
                selection = (From<?, T>) doJoins(selection, param, JoinType.LEFT)._2;
            }
            
            logger.debug("PseudoAttribute detected: {}", pseudo);
            Expression<?> s = pseudo.getSelectionForQuery(emf.get().getCriteriaBuilder(), selection);
            doRestrictions(selection, param); // to restrict e.g. SelfAttribute, if so wanted.
            return unwrap(AdditionalQueryPerformingAttribute.class, param).isDefined() || constructorExpectsId && IEntity.class.isAssignableFrom(s.getJavaType()) ? ((Path<IEntity<?>>)s).get(id((Class<IEntity<?>>)s.getJavaType(), emf.get())) : s;
        }
        
        if (unwrap(AdditionalQueryPerformingAttribute.class, param).isDefined()) {
            SingularAttribute<T,Id<T>> replacement = id(selection.getJavaType(), emf.get());
            logger.debug("AdditionalQueryPerformingAttribute detected. Replacing selection {} with source Id {}", selection.getJavaType().getSimpleName(), replacement.getName());
            return selection.get(replacement);
        }

        if (unwrap(PluralAttribute.class, param).isDefined()) {
            SingularAttribute<T,Id<T>> replacement = id(selection.getJavaType(), emf.get());
            logger.debug("PluralAttribute detected. Replacing parameter with source Id.");
            return selection.get(replacement);
        }
        
        for (@SuppressWarnings("unused") JoiningAttribute a: unwrap(JoiningAttribute.class, param)) {
            // use left join here, since were are modifying the existing selection which should still return all the rows
            selection = (From<?, T>) doJoins(selection, param, JoinType.LEFT)._2;
        }
        
        for (SingularAttribute<T,?> attr: unwrap(SingularAttribute.class, param)) {
            if (IEntity.class.isAssignableFrom(attr.getJavaType())) {
                if (constructorExpectsId) {
                    logger.debug("Singular Entity attribute detected, but constructor expects Id. Replacing parameter with Id.");
                    return selection.get(attr).get(id(attr.getBindableJavaType(), emf.get()));
                } else {
                    logger.debug("Singular Entity attribute detected. Performing left join.");
                    return doRestrictions(selection.join(attr, JoinType.LEFT), attr);
                }
            } else if (projection instanceof ExpressionProjection) {
                logger.debug("Expression detected. Wrapping singularattribute to the expression.");
                return ((ExpressionProjection<Object>) projection).getExpression(emf.get().getCriteriaBuilder(), (Expression<Object>)selection.get(attr));
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
            List<Id<IEntity<?>>> ids = (List<Id<IEntity<?>>>)(Object)newList(values);
            if (!ids.isEmpty()) {
                logger.debug("Preforming additional query for Attribute: {}", attr);
                Class<?> projectionType = projection.getConstructorParameterTypes().get(index);
                List<Object> r = doAdditionalQuery(projectionType, (Attribute<IEntity<?>,?>)attr, isId(projectionType), isWrapperOfIds(projection, index), isDistinctable(projection, index), ids);
                ret = r;
                if (r.size() != ids.size()) {
                    throw new RuntimeException("Whoops, a bug");
                }
            }
        }
        
        logger.debug("performAdditionalQueriesForPlaceholderValues -> {}", ret);
        return ret;
    }

    private <SOURCE extends IEntity<?>> List<Object> doAdditionalQuery(Class<?> projectionType, Attribute<SOURCE, ?> attr, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, List<Id<SOURCE>> sourceIdsToQuery) {
        logger.debug("doAdditionalQuery({},{},{},{},{},{})", new Object[] {projectionType, attr, isId, isWrapperOfIds, isDistinctable, sourceIdsToQuery});
        final Map<?, List<Object>> targetQueryResults = queryTargetsOfSources(attr, isId, isWrapperOfIds, isDistinctable, newSet(sourceIdsToQuery));

        Iterable<List<Object>> results = map(new Transformer<Object,List<Object>>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<Object> transform(Object source) {
                return find(source, (Map<Object,List<Object>>)targetQueryResults).getOrElse(emptyList());
            }
        }, sourceIdsToQuery);
        List<Object>ret = newList(map(ProjectionResultUtil_.postProcessResult.ap(projectionType, attr), results));
        logger.debug("doAdditionalQuery -> {}", ret);
        return ret;
    }
    
    private <SOURCE extends IEntity<?>> Map<Id<SOURCE>,List<Object>> queryTargetsOfSources(final Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Set<Id<SOURCE>> sourceIds) {
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
            logger.debug("Target is a collection of Embeddables. Picking embeddable parts manually.");
            result = map(EmbeddableUtil_.collectEmbeddableFromParts.ap(emf.get().getMetamodel(), (Bindable<?>)target), actualResultRows);
        } else {
            // for AdditionalQueryPerformingAttribute, replace the result object array with the actual object, performing additional queries if needed
            Option<AdditionalQueryPerformingAttribute> rel = unwrap(AdditionalQueryPerformingAttribute.class, target);
            if (rel.isDefined()) {
                logger.debug("Target is AdditionalQueryPerformingAttribute. Finalizing: {}", target);
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
    
    public static Class<?> javaType(Attribute<?,?> a) {
        return a instanceof Bindable ? ((Bindable<?>)a).getBindableJavaType() : a.getJavaType();
    }
    
    private static final Collection<Object[]> RETRY_IN_PARTS = new ArrayList<Object[]>() {
        @Override
        public Iterator<Object[]> iterator() {
            throw new RuntimeException("Should not be here!");
        };
    };
    
    private <SOURCE extends IEntity<?>, SOURCE_ID> Collection<Object[]> queryTargets(Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Set<SOURCE_ID> sourceIds) {
        Collection<Object[]> ret = queryTargets(target, isId, isWrapperOfIds, isDistinctable, sourceIds, true);
        if (ret == RETRY_IN_PARTS) {
            SortedSet<Integer> amounts = config.getInClauseValuesAmounts();
            if (head(sourceIds) instanceof MultiColumnId) {
                // if there are multiple columns (usually just two), leave out amounts that more than half of the original max value.
                amounts = newSortedSet(filter(lessThanOrEqualTo(amounts.last() / 2), amounts));
            }
            int maxInClauseSize = max(amounts).get();
            if (size(sourceIds) > maxInClauseSize) {
                // more than max amount of ids
                // -> perform multiple queries instead of or:ring to get rid of ridiculous (multi-minute) parse times
                Iterable<Object[]> results = emptyList();
                for (List<SOURCE_ID> grp: grouped(maxInClauseSize, sourceIds)) {
                    Set<SOURCE_ID> group = newSet(grp);
                    if (group.size() < maxInClauseSize) {
                        // pad in-list to the next specified size repeating the last value, to avoid excessive hard parsing
                        int targetSize = head(filter(greaterThanOrEqualTo(group.size()), amounts));
                        group = newSet(concat(group, repeat(last(group), targetSize-group.size())));
                    }
                    results = concat(results, queryTargets(target, isId, isWrapperOfIds, isDistinctable, group, false));
                }
                ret = newList(results);
            } else {
                ret = queryTargets(target, isId, isWrapperOfIds, isDistinctable, sourceIds, false);
            }
        }
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private <SOURCE extends IEntity<?>, SOURCE_ID> Collection<Object[]> queryTargets(Attribute<SOURCE, ?> target, boolean isId, boolean isWrapperOfIds, boolean isDistinctable, Set<SOURCE_ID> sourceIds, boolean firstRun) {
        logger.debug("queryTargets({},{},{},{},{})", new Object[] {sourceIds, target, isId, isWrapperOfIds, isDistinctable});
        Class<SOURCE> sourceClass = target.getDeclaringType() != null ? target.getDeclaringType().getJavaType() : ((Id<SOURCE>)head(sourceIds)).getOwningClass();
        CriteriaQuery<Object[]> query = emf.get().getCriteriaBuilder().createQuery(Object[].class);
        Root<SOURCE> source = query.from(sourceClass);
        Path<SOURCE_ID> sourceId = source.get(QueryUtils.<SOURCE,SOURCE_ID>id(sourceClass, emf.get()));
        
        Either<From<SOURCE,Object>,Attribute<?,?>> relationOrAdditionalGet;
        Map<Attribute<?, ?>, From<?, ?>> actualJoins = newMutableMap();
        From<?,?> last;
        if (!canUnwrap(PseudoAttribute.class, target)) {
            logger.debug("Inner joining from {} to {}", source, target);
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
            logger.debug("Query is for a PseudoAttribute");
            actualJoins.put(target, source);
            relationOrAdditionalGet = Either.left((From<SOURCE, Object>)(Object)source);
            last = source;
        }
        
        for (From<?,?> r: relationOrAdditionalGet.left) {
            ProjectionUtil.doRestrictions(r, target);
        }
        
        Iterable<Class<?>> allEntities = filter(not(Predicates.isNull()), map(ProjectionHelper_.javaType, actualJoins.keySet()));
        if (logger.isDebugEnabled()) {
            allEntities = newList(allEntities);
            logger.debug("All entities: {}", allEntities);
        }
        boolean enableInClauseOptimizations = !exists(QueryUtils.ImplementsProjectWithRegularInClause, allEntities);
        logger.debug("Enable in-clause optimizations: {}", enableInClauseOptimizations);
        
        // execute in parts only if optimizations are not enabled or would not be used
        if (firstRun && (!enableInClauseOptimizations || !queryUtils.wouldUseInClauseOptimizations(sourceIds))) {
            return RETRY_IN_PARTS;
        }
        
        query.where(queryUtils.inExpr(sourceId, sourceIds, emf.get().getCriteriaBuilder(), enableInClauseOptimizations ? Optimization.ENABLED : Optimization.DISABLED));

        // Would this provide any benefit? Maybe only overhead...
        if (isDistinctable && config.makeProjectionQueriesDistinct()) {
            logger.debug("Query is distinctable.");
            query.distinct(true);
        }

        setListAttributeOrderings(target, query, actualJoins);
        
        Option<AdditionalQueryPerformingAttribute> rel = unwrap(AdditionalQueryPerformingAttribute.class, target);
        if (isCollectionOfEmbeddables(target)) {
            // Must handle collections of embeddables separately, since hibern seems to include only
            // parentid (of the embeddable) in the select clause, but tries to read
            // all fields from the resultset
            logger.debug("Target is a collection of embeddables. Breaking embeddable fields manually for the query.");
            query.multiselect(newList(cons(sourceId, breakEmbeddableToParts(emf.get().getMetamodel(), (Bindable<?>)target, relationOrAdditionalGet.left.get()))));
        } else if (rel.isDefined()) {
            logger.debug("Target is AdditionalQueryPerformingAttribute. Preparing.");
            List<Selection<?>> selections = prepareProjectingQuery((MetaJpaConstructor<Object,?,?>)rel.get().getConstructor(), relationOrAdditionalGet.left.get());
            if ((isId || isWrapperOfIds) && !Id.class.isAssignableFrom(head(selections).getJavaType())) {
                logger.debug("Constructor expects an Id (or Ids) but the query was not for Ids. Projection to Id.");
                if (selections.size() != 1) {
                    throw new RuntimeException("whoops");
                }
                Path<Object> sel = ((Path<Object>)head(selections));
                query.multiselect(sourceId, sel.get(id(sel.getJavaType(), emf.get())));
            } else {
                query.multiselect(newList(cons(sourceId, selections)));
            }
            if (selections.size() == 1 && rel.get().getConstructor() instanceof ExpressionProjection) {
                logger.debug("single value expression projection -> adding explicit group-by for sourceId to make it a legal query");
                query.groupBy(sourceId);
            }
        } else {
            Path<Object> r = (Path<Object>) (relationOrAdditionalGet.isRight() ? QueryUtils.get(last, relationOrAdditionalGet.right.get()) : relationOrAdditionalGet.left.get());
            if ((isId || isWrapperOfIds) && !isId(r.getJavaType())) {
                logger.debug("Constructor expects Ids but the query was not for Ids. Projection to Ids.");
                query.multiselect(sourceId, r.get(id(r.getJavaType(), emf.get())));
            } else {
                query.multiselect(sourceId, r);
            }
        }

        Collection<Object[]> ret = queryExecutor.getMany(query, Page.NoPaging, LockModeType.NONE);
        if (logger.isDebugEnabled()) {
            logger.debug("queryTargets -> {}", newList(map(new Transformer<Object[],String>() {
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
            logger.debug("Adding orderings based on ListAttributes");
            for (Attribute<?, ?> a: ((JoiningAttribute) target).getAttributes()) {
                setListAttributeOrderings(a, query, actualJoins);
            }
        } else if (target instanceof ListAttribute) {
            addListAttributeOrdering(query, actualJoins.get(target), resolveOrderColumn((ListAttribute<?,?>)target), emf.get().getCriteriaBuilder());
        }
    }
    
}
