package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.emptyList;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMultimap;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.find;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.range;
import static fi.solita.utils.functional.Functional.sort;
import static fi.solita.utils.functional.Functional.transpose;
import static fi.solita.utils.functional.Functional.transpose2;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;
import static fi.solita.utils.query.projection.ProjectionSupport_.fixCollectionOfEmbeddables;
import static fi.solita.utils.query.projection.ProjectionSupport_.objectToObjectArray;
import static fi.solita.utils.query.projection.ProjectionSupport_.performAdditionalQueriesForPlaceholderValues;
import static fi.solita.utils.query.projection.ProjectionSupport_.postProcessResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import fi.solita.utils.functional.Collections_;
import fi.solita.utils.functional.Functional_;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.NotDistinctable;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.LiteralAttribute;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.attributes.RelationAttribute;
import fi.solita.utils.query.attributes.SelfAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.projection.Constructors.IdProjection;

public class ProjectionSupport {

    @PersistenceContext
    private EntityManager em;

    public static final class NullValueButNonOptionConstructorArgumentException extends RuntimeException {
        private final Class<?> argumentType;
        private final int argumentIndex;
        private final Constructor<?> constructor;

        public NullValueButNonOptionConstructorArgumentException(Constructor<?> constructor, Class<?> argumentType, int argumentIndex) {
            this.constructor = constructor;
            this.argumentType = argumentType;
            this.argumentIndex = argumentIndex;
        }

        @Override
        public String getMessage() {
            return "Constructor " + constructor + " for class " + constructor.getDeclaringClass().getName() + " had a non-Option argument of type " + argumentType.getName() + " at position " + argumentIndex + " which was tried to supply with a null";
        }
    }

    public <R> List<R> performAdditionalQueriesAndTransformResults(List<? extends Object> results, ConstructorMeta_<?,R,?> constructor_) {
        List<Object[]> rows = newList(map(results, objectToObjectArray));
        Iterable<Iterable<Object>> columns = map(zip(range(0), constructor_.getParameters(), transpose2(rows)), performAdditionalQueriesForPlaceholderValues.apply(this).applyPartial(constructor_));
        return newList(transformResults(transpose(columns), constructor_));
    }
    
    
    @SuppressWarnings("unchecked")
    Iterable<Object> performAdditionalQueriesForPlaceholderValues(ConstructorMeta_<?,?,?> constructor_, int index, Attribute<?,?> attr, Iterable<Object> values) {
        if (shouldPerformAdditionalQuery(attr)) {
            return query(constructor_.getConstructorParameterTypes().get(index), (Attribute<IEntity,?>)attr, isWrapperOfIds(constructor_, index), isDistinctable(constructor_, index), (Iterable<Id<IEntity>>)(Object)values);
        }
        return values;
    }

    static <R> Iterable<R> transformResults(Iterable<Iterable<Object>> results, ConstructorMeta_<?, R, ?> constructor_) {
        return map(results, ProjectionResultSupport_.<R>transformResults().applyPartial(constructor_));
    }

    static boolean shouldPerformAdditionalQuery(Attribute<?, ?> param) {
        return unwrap(param, RelationAttribute.class).isDefined() ||
               (unwrap(param, PluralAttribute.class).isDefined() && !unwrap(param, LiteralAttribute.class).isDefined());
    }
    
    static Object[] objectToObjectArray(Object obj) {
        return obj instanceof Object[] ? (Object[]) obj : new Object[] { obj };
    }

    static void assertSize(int expected, Collection<?> c) {
        if (c.size() != expected) {
            throw new IllegalArgumentException("Collection expected to be of size " + expected + " but was: " + c);
        }
    }

    private <SOURCE extends IEntity> List<Object> query(final Class<?> constructorParameterType, final Attribute<SOURCE, ?> attr, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIdsToQuery) {
        // Query for (sourceId,targetData) pairs
        final Map<Id<SOURCE>, List<Object>> targetQueryResults = queryTargetsOfSources(attr, isWrapperOfIds, isDistinctable, sourceIdsToQuery);

        Iterable<List<Object>> results = map(sourceIdsToQuery, new Transformer<Id<SOURCE>,List<Object>>() {
            @Override
            public List<Object> transform(Id<SOURCE> source) {
                return find(targetQueryResults, source).getOrElse(emptyList());
            }
        });
        
        return newList(map(results, postProcessResult.applyPartial(constructorParameterType).applyPartial(attr)));
    }
    
    static Object postProcessResult(Class<?> constructorParameterType, final Attribute<?, ?> attr, List<Object> val) {
        Object v;
        if (attr instanceof SingularAttribute) {
            if (unwrap(attr, OptionalAttribute.class).isDefined()) {
                if (val.isEmpty()) {
                    v = None();
                } else {
                    assertSize(1, val);
                    v = headOption(val);
                }
            } else {
                assertSize(1, val);
                v = head(val);
            }
        } else {
            if (List.class.isAssignableFrom(constructorParameterType)) {
                v = newList(val);
            } else if (SortedSet.class.isAssignableFrom(constructorParameterType)) {
                v = new TreeSet<Object>(val);
            } else {
                v = newSet(val);
            }
        }
        return v;
    }

    private <SOURCE extends IEntity> Map<Id<SOURCE>,List<Object>> queryTargetsOfSources(final Attribute<SOURCE, ?> target, boolean isWrapperOfIds, boolean isDistinctable, Iterable<Id<SOURCE>> sourceIds) {
        List<Object[]> results = queryTargets(sourceIds, target, isWrapperOfIds, isDistinctable);
        
        @SuppressWarnings("unchecked")
        Iterable<Id<SOURCE>> ids = (Iterable<Id<SOURCE>>)(Object)map(results, Functional_.head());
        Iterable<Object[]> targetCols = map(results, Functional_.tail().andThen(Collections_.newArray16().curried().apply(Object.class)));

        Iterable<? extends Object> result;
        if (isCollectionOfEmbeddables(target)) {
            result = map(targetCols, fixCollectionOfEmbeddables.applyPartial(em.getMetamodel()).applyPartial((Bindable<?>)target));
        } else {
            result = targetCols;
        }
        
        // for RelationAttributes, replace the result object array with the actual object, performing nested subqueries if present
        for (RelationAttribute<?,?,Object> rel: unwrap(target, RelationAttribute.class)) {
            result = performAdditionalQueriesAndTransformResults(newList(targetCols), rel.getConstructor());
        }
        
        return newMultimap(zip(ids, result));
    }
    
    static Object fixCollectionOfEmbeddables(Metamodel metamodel, Bindable<?> attr, Object[] cols) {
        List<? extends Attribute<?, ?>> embeddableAttributes = getEmbeddableAttributes(attr, metamodel);
        if (embeddableAttributes.size() != cols.length) {
            throw new IllegalStateException("Expected same size for: " + embeddableAttributes + ", " + Arrays.toString(cols));
        }
        Class<?> embeddableClass = getEmbeddableType(attr, metamodel).getJavaType();
        try {
            Constructor<?> constructor = embeddableClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object embeddable = constructor.newInstance();
            for (Tuple2<? extends Attribute<?, ?>, Object> a: zip(embeddableAttributes, cols)) {
                Member member = a._1.getJavaMember();
                if (member instanceof Field) {
                    Field f = (Field)member;
                    f.setAccessible(true);
                    f.set(embeddable, a._2);
                } else {
                    Method m = (Method)member;
                    if (m.getParameterTypes().length == 1 && head(m.getParameterTypes()).isAssignableFrom(a._1.getJavaType())) {
                        m.setAccessible(true);
                        m.invoke(embeddable, a._2);
                    } else {
                        throw new UnsupportedOperationException("not implemented. Run, Forrest, run!");
                    }
                }
            }
            return embeddable;
        } catch (Exception e)  {
            throw new RuntimeException(e);
        }
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
            List<Selection<?>> params = newList(cons(sourceId, transformParametersForQuery(unwrap(target, RelationAttribute.class).get().getConstructor(), relation)));
            query.multiselect(params);
        } else if (isWrapperOfIds) {
            // if the constructor wanted only a wrapper of ids, project to the id
            query.multiselect(sourceId, relation.get(QueryUtils.id(relation.getJavaType(), em)));
        } else if (isCollectionOfEmbeddables(target)) {
            // Must handle collections of embeddables separately, since hibernate seems to include only
            // parentid (of the embeddable) in the select clause, but tries to read
            // all fields from the resultset
            Iterable<Selection<?>> selections = map(getEmbeddableAttributes((Bindable<?>)target, em.getMetamodel()), new Transformer<Attribute<?,?>,Selection<?>>() {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                @Override
                public Selection<?> transform(Attribute<?, ?> attribute) {
                    return attribute instanceof SingularAttribute ? relation.get((SingularAttribute) attribute) :
                           attribute instanceof SetAttribute ?      relation.get((SetAttribute) attribute) :
                           attribute instanceof ListAttribute ?     relation.get((ListAttribute) attribute) :
                                                                    relation.get((PluralAttribute) attribute);
                }
            });
            query.multiselect(newList(cons(sourceId, selections)));
        } else {
            query.multiselect(sourceId, relation);
        }

        return em.createQuery(query).getResultList();
    }

    private static List<? extends Attribute<?,?>> getEmbeddableAttributes(Bindable<?> attribute, Metamodel metamodel) {
        return newList(sort(getEmbeddableType(attribute, metamodel).getAttributes(), attributeByName));
    }
    
    private static final Comparator<Attribute<?,?>> attributeByName = new Comparator<Attribute<?,?>>() {
        @Override
        public int compare(Attribute<?, ?> o1, Attribute<?, ?> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private static <T> EmbeddableType<T> getEmbeddableType(Bindable<T> attribute, Metamodel metamodel) {
        return metamodel.embeddable(attribute.getBindableJavaType());
    }

    private static boolean isCollectionOfEmbeddables(Attribute<?, ?> attribute) {
        return attribute.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION &&
               attribute instanceof PluralAttribute &&
               ((PluralAttribute<?,?,?>)attribute).getElementType().getPersistenceType() == PersistenceType.EMBEDDABLE;
    }

    /**
     * Transform the given selection to a suitable projection
     */
    public <E> List<Selection<?>> transformParametersForQuery(ConstructorMeta_<E,?,?> constructor_, From<?,? extends E> selection) {
        if (constructor_ instanceof IdProjection) {
            // special handling for Id projections
            return Arrays.<Selection<?>>asList(selection.get(QueryUtils.<E>id(selection.getJavaType(), em)));
        }
        
        List<Selection<?>> selections = newList();
        for (Tuple3<Integer, Attribute<?,?>, Class<?>> t: zip(range(0), constructor_.getParameters(), constructor_.getConstructorParameterTypes())) {
            int index = t._1;
            Attribute<?,?> param = t._2;
            Class<?> constuctorParameterType = t._3;
            selections.add(transformSelectionForQuery(param, isId(constuctorParameterType) || isWrapperOfIds(constructor_, index), selection));
        }
        return selections;
    }
    
    /**
     * Transform the given selection to a suitable format considering projections
     */
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


    private static boolean isId(Class<?> clazz) {
        return Id.class.isAssignableFrom(clazz);
    }

    // whether constructor expects a collections of Ids (instead of a collection of entities)
    private static boolean isWrapperOfIds(ConstructorMeta_<?, ?, ?> constructor_, int columnIndex) {
        return constructor_.getIndexesOfIdWrappingParameters().contains(columnIndex);
    }

    private static boolean isDistinctable(ConstructorMeta_<?, ?, ?> constructor_, int columnIndex) {
        return Set.class.isAssignableFrom(constructor_.getConstructorParameterTypes().get(columnIndex)) && !NotDistinctable.class.isAssignableFrom(((Bindable<?>)constructor_.getParameters().get(columnIndex)).getBindableJavaType());
    }
}
