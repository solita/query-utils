package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.forAll;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.sort;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.Functional.zipWithIndex;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
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

import fi.solita.utils.query.attributes.AttributeProxy;
import fi.solita.utils.query.attributes.LiteralAttribute;
import fi.solita.utils.query.attributes.LiteralListAttribute;
import fi.solita.utils.query.attributes.LiteralSetAttribute;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.attributes.RelationAttribute;
import fi.solita.utils.query.attributes.SelfAttribute;
import fi.solita.utils.query.attributes.RelationSingularAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.projection.Constructors.IdProjection;
import fi.solita.utils.query.projection.Constructors.PairProjection;
import fi.solita.utils.query.projection.Constructors.TupleProjection;
import fi.solita.utils.query.projection.Constructors.ValueAttributeProjection;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.NotDistinctable;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Predicate;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Transformers;
import fi.solita.utils.functional.Tuple1;

public class ProjectionSupport {

    @PersistenceContext
    private EntityManager em;

    private static final Id<?> RegularQueryId = new Id<Object>() {
        @Override
        public String toString() {
            return "Id<Dummy for a 'regular' query>";
        }
        @Override
        public Class<Object> getOwningClass() {
            throw new UnsupportedOperationException();
        };
    };

    private static final boolean NOT_A_WRAPPER_OF_IDS = false;

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

    @SuppressWarnings("unchecked")
    <R,PARAMS,ID> Map<ID, List<R>> replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(Map<ID, List<Object[]>> results, ConstructorMeta_<?,R,PARAMS> constructor_) {
        Iterable<Object[]> allResultRows = flatMap(results.entrySet(), Transformers.<List<Object[]>>values());
        // perform subquery for each relevant parameter separately
        for (Map.Entry<Integer,? extends Attribute<?, ?>> paramAndIndex: zipWithIndex(constructor_.getParameters())) {
            final int index = paramAndIndex.getKey();
            Attribute<?, ?> param = paramAndIndex.getValue();

            // only relevant for PluralAttribute or a RelationAttribute (singular or plural) but not literalAttribute
            if ( (!(unwrap(param, LiteralSetAttribute.class).isDefined() || unwrap(param, LiteralListAttribute.class).isDefined()) && unwrap(param, PluralAttribute.class).isDefined()) ||
                  unwrap(param, RelationAttribute.class).isDefined() ) {
                    Attribute<? extends IEntity, ?> attr = (Attribute<? extends IEntity, ?>)param;
                    handleColumn(constructor_, allResultRows, index, attr);
            }
        }

        final ProjectionResultTransformer<R,PARAMS> resultTransformer = new ProjectionSupport.ProjectionResultTransformer<R,PARAMS>(constructor_);
        return map(results, new Transformer<Map.Entry<ID,List<Object[]>>, Map.Entry<ID,List<R>>>() {
            @Override
            public Map.Entry<ID, List<R>> transform(Map.Entry<ID, List<Object[]>> source) {
                return Pair.of(source.getKey(), newList(map(source.getValue(), resultTransformer)));
            }
        });
    }

    public <R> List<R> replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(List<Object> results, ConstructorMeta_<?,R,?> constructor_) {
        List<Object[]> rows = new LinkedList<Object[]>(newList(map(results, new Transformer<Object,Object[]>() {
            @Override
            public Object[] transform(Object source) {
                return source instanceof Object[] ? (Object[]) source : new Object[] { source };
            }
        })));
        Map<Id<?>, List<R>> processed = replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(fi.solita.utils.functional.Collections.<Id<?>,List<Object[]>>newMap(Pair.of(RegularQueryId, rows)), constructor_);
        if (processed.size() != 1) {
            throw new IllegalStateException("size should have been 1 but was: " + processed.size());
        }
        return head(processed.values());
    }

    private void handleColumn(ConstructorMeta_<?, ?, ?> constructor_, Iterable<Object[]> allResultRows, final int columnIndex, Attribute<? extends IEntity, ?> param) {
        Class<?> constructorArgumentType = constructor_.getMember().getParameterTypes()[columnIndex];

        // Ids of all parent entities to include. parentId was assigned as a placeholder to the query result.
        Iterable<Id<?>> parentIdsToQuery = map(allResultRows, new Transformer<Object[],Id<?>>() {
            @Override
            public Id<?> transform(Object[] source) {
                return (Id<?>) source[columnIndex];
            }
        });

        // Query for (parentId,childAttributes) pairs. The resulting object array will have parentId as the first
        // element and remaining fields (entity attributes / projected attributes) as subsequent columns
        List<Object[]> subQueryResults = queryRelationWithParentId(param, parentIdsToQuery, isWrapperOfIds(constructor_, columnIndex), isDistinctable(constructor_, columnIndex));

        // convert [parentId,childAttributes...] arrays to a Map<parentId,childAttributes>
        Map<Id<?>, List<Object>> parentChildren = newMap(map(parentIdsToQuery, new Transformer<Id<?>, Map.Entry<Id<?>, List<Object>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Map.Entry<Id<?>, List<Object>> transform(Id<?> source) {
                return (Map.Entry<Id<?>, List<Object>>)(Object)Pair.of(source, newList());
            }
        }));

        for (Object[] cols: subQueryResults) {
            Id<?> parentId = (Id<?>) cols[0];
            List<Object> children = parentChildren.get(parentId);

            Object[] childCols = Arrays.copyOfRange(cols, 1, cols.length);
            boolean allNulls = forAll(childCols, new Predicate<Object>() {
                @Override
                public boolean accept(Object candidate) {
                    // count literal placeholders as null. Using cb.nullLiteral instead of a string placeholder seems to not work...
                    return candidate == null || candidate.equals(LiteralAttribute.QUERY_PLACEHOLDER);
                }
            });

            if (unwrap(param, OptionalAttribute.class).isDefined() && allNulls) {
                // OK, skipping optional with all nulls (either a null column, or left-joining to a nullable relation causes an all-nulls result)
            } else if (unwrap(param, PluralAttribute.class).isDefined() && allNulls) {
                // include a None if the PluralAttribute contains a single Optional null value (whooh...)
                for (RelationAttribute<?,?,?> pr: unwrap(param, RelationAttribute.class)) {
                    if (pr.getConstructor() instanceof ValueAttributeProjection) {
                        if (unwrap(((ValueAttributeProjection<?,?>)pr.getConstructor()).getAttribute(), OptionalAttribute.class).isDefined()) {
                            if (childCols.length != 1) {
                                throw new IllegalStateException("Length should have been 1 but was: " + childCols.length);
                            }
                            children.add(null);
                        }
                    }
                }
                // OK, skipping an empty set (left-joining to an empty plural causes an all-nulls result)
            } else {
                if (allNulls) {
                    throw new IllegalStateException("Whoops, a bug! Tried to add an all-nulls child array to parent: " + parentId);
                }
                Object childColumns = extractChildColumns(param, childCols, em.getMetamodel());
                if (childColumns == null) {
                    throw new IllegalStateException("Whoops, a bug! Tried to add a null-child to parent: " + parentId);
                }
                children.add(childColumns);
            }
        }

        // for RelationAttributes, replace the result object array with the actual object, performing nested subqueries if present
        for (RelationAttribute<?,?,?> rel: unwrap(param, RelationAttribute.class)) {
            // modify children so that a single Object is always inside an Object[]
            // TODO: should probably make this a bit more obvious...
            Map<?, List<Object[]>> p2 = map(parentChildren, new Transformer<Map.Entry<Id<?>,List<Object>>, Map.Entry<Id<?>,List<Object[]>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Map.Entry<Id<?>, List<Object[]>> transform(Entry<Id<?>, List<Object>> source) {
                    return (Map.Entry<Id<?>, List<Object[]>>)(Object)Pair.of(source.getKey(), new LinkedList<Object[]>(newList(map(source.getValue(), new Transformer<Object, Object[]>() {
                        @Override
                        public Object[] transform(Object source) {
                            return source instanceof Object[] ? (Object[])source : new Object[]{source};
                        }
                    }))));
                }
            });
            @SuppressWarnings("unchecked")
            Map<Id<?>, List<Object>> m2 = (Map<Id<?>, List<Object>>)(Object)replaceRelatedProjectionPlaceholdersWithResultsFromSubquery(p2, rel.getConstructor());
            parentChildren = m2;
        }

        // replace placeholder-parentId from the original query result with the produced collection of child objects
        Iterator<Object[]> it = allResultRows.iterator();
        while (it.hasNext()) {
            Object[] row = it.next();
            List<Object> values = parentChildren.get(row[columnIndex]);
            if (unwrap(param, OptionalAttribute.class).isDefined() && values.isEmpty()) {
                row[columnIndex] = null;
            } else if (unwrap(param, RelationSingularAttribute.class).isDefined()) {
                if (values.isEmpty()) {
                    it.remove();
                } else {
                    if (values.size() != 1) {
                        throw new IllegalStateException("Size should have been 1 but was: " + values.size());
                    }
                    row[columnIndex] = head(values);
                }
            } else if (unwrap(param, PluralAttribute.class).isDefined()) {
                if (isCollectionOfEmbeddables(param) && values == null) {
                    // empty resultset due to inner join -> OK
                    values = Collections.emptyList();
                }
                if (List.class.isAssignableFrom(constructorArgumentType)) {
                    row[columnIndex] = newList(values);
                } else {
                    row[columnIndex] = newSet(values);
                }
            } else {
                throw new RuntimeException("Should not happen");
            }
        }
    }

    private static Object extractChildColumns(Attribute<? extends IEntity, ?> param, Object[] childCols, Metamodel metamodel) {
        if (unwrap(param, RelationSingularAttribute.class).isDefined()) {
            return childCols;
        } else if (unwrap(param, RelationAttribute.class).isDefined()) {
            return childCols;
        } else if (isCollectionOfEmbeddables(param)) {
            List<? extends Attribute<?, ?>> embeddableAttributes = getEmbeddableAttributes(param, metamodel);
            if (embeddableAttributes.size() != childCols.length) {
                throw new IllegalStateException("Expected same size for: " + embeddableAttributes + ", " + Arrays.toString(childCols));
            }
            Class<?> embeddableClass = getEmbeddableType(param, metamodel).getJavaType();
            try {
                Constructor<?> constructor = embeddableClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object embeddable = constructor.newInstance();
                for (int i = 0; i < embeddableAttributes.size(); ++i) {
                    Attribute<?,?> a = embeddableAttributes.get(i);
                    if (a.getJavaMember() instanceof Field) {
                        Field f = (Field) a.getJavaMember();
                        f.setAccessible(true);
                        f.set(embeddable, childCols[i]);
                    } else {
                        throw new UnsupportedOperationException("Method setter access not implemented");
                    }
                }
                return embeddable;
            } catch (Exception e)  {
                throw new RuntimeException(e);
            }
        } else {
            // the query was for either Entities or Ids, so Hibernate has already provided us with a constructed single object
            if (childCols.length != 1) {
                throw new IllegalStateException("Length should have been 1 but was: " + childCols.length);
            }
            return childCols[0];
        }
    }

    private <R> List<Object[]> queryRelationWithParentId(Attribute<? extends IEntity, ?> joinTarget, Iterable<Id<?>> parentIdsToQuery, boolean isWrapperOfIds, boolean isDistinctable) {
        CriteriaQuery<Object> q = em.getCriteriaBuilder().createQuery();
        Root<? extends IEntity> parent = q.from(joinTarget.getDeclaringType().getJavaType());
        @SuppressWarnings("unchecked")
        Path<Id<?>> parentId = (Path<Id<?>>)(Object)parent.get(QueryUtils.id(joinTarget.getDeclaringType().getJavaType(), em));
        final Path<Object> relation = parent.join(joinTarget.getName(), JoinType.LEFT /* Should this be LEFT for some cases? No? */);
        q.where(QueryUtils.inExpr(parentId, parentIdsToQuery, em.getCriteriaBuilder()));

        if (isDistinctable) {
            // Target type is a Set so we can use distinct
            q.distinct(true);
        }

        if (joinTarget instanceof ListAttribute) {
            QueryUtils.addListAttributeOrdering(q, relation, em.getCriteriaBuilder());
        }

        if (unwrap(joinTarget, RelationAttribute.class).isDefined()) {
            List<Selection<?>> selection = newList();
            selection.add(parentId);
            selection.addAll(transformParametersForQuery(unwrap(joinTarget, RelationAttribute.class).get().getConstructor(), (From<?, ?>) relation));
            q.multiselect(selection);
        } else if (isWrapperOfIds) {
            // if the constructor wanted only a wrapper of ids, project to the id
            q.multiselect(parentId, relation.get(QueryUtils.id(relation.getJavaType(), em)));
        } else if (isCollectionOfEmbeddables(joinTarget)) {
            // Must handle collections of embeddables separately, since hibernate seems to include only
            // parentid (of the embeddable) in the select clause, but tries to read
            // all fields from the resultset
            @SuppressWarnings("rawtypes")
            Iterable<Selection> selections = map(getEmbeddableAttributes(joinTarget, em.getMetamodel()), new Transformer<Attribute<?,?>,Selection>() {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                @Override
                public Selection transform(Attribute<?, ?> attribute) {
                    return attribute instanceof SingularAttribute ? relation.get((SingularAttribute) attribute) :
                           attribute instanceof SetAttribute ?      relation.get((SetAttribute) attribute) :
                           attribute instanceof ListAttribute ?     relation.get((ListAttribute) attribute) :
                                                                    relation.get((PluralAttribute) attribute);
                }
            });
            q.multiselect(newArray(concat(new Selection[]{parentId}, selections), Selection.class));
        } else {
            q.multiselect(parentId, relation);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> subResults = (List<Object[]>)(Object)em.createQuery(q).getResultList();
        return subResults;
    }

    private static List<? extends Attribute<?,?>> getEmbeddableAttributes(Attribute<? extends IEntity, ?> attribute, Metamodel metamodel) {
        return newList(sort(getEmbeddableType(attribute, metamodel).getAttributes(), new Comparator<Attribute<?,?>>() {
            @Override
            public int compare(Attribute<?, ?> o1, Attribute<?, ?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }));
    }

    private static EmbeddableType<?> getEmbeddableType(Attribute<? extends IEntity, ?> attribute, Metamodel metamodel) {
        if (attribute instanceof PluralAttribute<?,?,?>) {
            return metamodel.embeddable(((PluralAttribute<?,?,?>)attribute).getBindableJavaType());
        } else {
            return metamodel.embeddable(attribute.getJavaType());
        }
    }

    private static boolean isCollectionOfEmbeddables(Attribute<? extends IEntity, ?> attribute) {
        return attribute.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION && attribute instanceof PluralAttribute && ((PluralAttribute<?,?,?>)attribute).getElementType().getPersistenceType() == PersistenceType.EMBEDDABLE;
    }

    /**
     * Transform the given selection to a suitable projection
     */
    public <E> List<Selection<?>> transformParametersForQuery(ConstructorMeta_<?,?,?> constructor_, From<?,E> selection) {
        List<Selection<?>> selections = newList();
        if (constructor_ instanceof IdProjection) {
            selections.add(selection.get(QueryUtils.<E>id(selection.getJavaType(), em)));
        } else if (constructor_ instanceof ValueAttributeProjection) {
            Attribute<?, ?> attr = ((ValueAttributeProjection<?,?>) constructor_).getAttribute();
            selections.add(transformSelectionForQuery(attr.getJavaType(), attr, selection, NOT_A_WRAPPER_OF_IDS));
        } else if (constructor_ instanceof PairProjection) {
            PairProjection<?,?,?> proj = (PairProjection<?,?,?>) constructor_;
            selections.add(transformSelectionForQuery(proj.getLeft().getJavaType(), proj.getLeft(), selection, NOT_A_WRAPPER_OF_IDS));
            selections.add(transformSelectionForQuery(proj.getRight().getJavaType(), proj.getRight(), selection, NOT_A_WRAPPER_OF_IDS));
        } else if (constructor_ instanceof TupleProjection) {
            TupleProjection<?,?> proj = (TupleProjection<?,?>) constructor_;
            for (Attribute<?,?> a: proj.getAttributes()) {
                selections.add(transformSelectionForQuery(a.getJavaType(), a, selection, NOT_A_WRAPPER_OF_IDS));
            }
        } else {
            // more complicated projection
            for (Map.Entry<Integer,? extends Map.Entry<? extends Attribute<?,?>,Class<?>>> paramAndType: zipWithIndex(zip(constructor_.getParameters(), constructor_.getMember().getParameterTypes()))) {
                selections.add(transformSelectionForQuery(paramAndType.getValue().getValue(), paramAndType.getValue().getKey(), selection, isWrapperOfIds(constructor_, paramAndType.getKey())));
            }
        }
        return selections;
    }

    /**
     * Transform the given selection to a suitable format considering projections
     */
    private <T> Selection<?> transformSelectionForQuery(Class<?> contructorArgumentType, Attribute<?,?> param, From<?,T> selection, boolean isWrapperOfIds) {
        if (unwrap(param, LiteralAttribute.class).isDefined()) {
            // just add some placeholder literal to the query. Actual literal value is inserted after retrieving the results form the db.
            return em.getCriteriaBuilder().literal(LiteralAttribute.QUERY_PLACEHOLDER);
        }

        if (unwrap(param, SelfAttribute.class).isDefined()) {
            return selection;
        }

        // This check should actually have already occurred, but just in case we are missing it somewhere...
        QueryUtils.checkOptionalAttributes((Attribute<?, ?>) param);

        if (unwrap(param, RelationAttribute.class).isDefined() || unwrap(param, PluralAttribute.class).isDefined()) {
            // add entity id as a placeholder to the query, to be replaced later with the actual projection.
            return selection.get(QueryUtils.id(selection.getJavaType(), em));
        } else if (unwrap(param, SingularAttribute.class).isDefined()) {
            @SuppressWarnings("unchecked")
            SingularAttribute<T,? extends IEntity> attr = unwrap(param, SingularAttribute.class).get();
            if (IEntity.class.isAssignableFrom(attr.getJavaType())) {
                if (Id.class.isAssignableFrom(contructorArgumentType) || isWrapperOfIds) {
                    // target class constructor expects an Id but the selection is for an entity, so get just the Id instead of the whole thing
                    @SuppressWarnings("unchecked")
                    Path<Object> at = (Path<Object>)(Object)selection.get(attr);
                    return at.get(QueryUtils.<Object>id(at.getJavaType(), em));
                } else {
                    return selection.join(attr, JoinType.LEFT);
                }
            } else {
                return selection.get(attr);
            }
        } else {
            throw new IllegalArgumentException("Selection transformation for parameter type " + param.getClass().getName() + " not implemented. Should it be?");
        }
    }

    /**
     * Transforms query results to actual instance of a projection target class, based on the given constructor metadata
     */
    private static final class ProjectionResultTransformer<T,PARAMS> extends Transformer<Object, T> {
        private final ConstructorMeta_<?,T,PARAMS> constructor_;

        public ProjectionResultTransformer(ConstructorMeta_<?,T,PARAMS> constructor) {
            this.constructor_ = constructor;
        }

        @Override
        public T transform(Object source) {
            if (source == null) {
                throw new IllegalArgumentException("source was null");
            }

            Object[] columns = (source instanceof Object[] ? (Object[]) source : new Object[] { source });

            if (constructor_ instanceof IdProjection) {
                if (columns.length != 1) {
                    throw new IllegalArgumentException("Length should have been 1 but was: " + columns.length);
                }
                @SuppressWarnings("unchecked")
                T ret = (T) columns[0];
                if (ret == null) {
                    throw new IllegalArgumentException("ret was null");
                }
                return ret;
            }

            transformResults(columns, constructor_);

            T instance = constructor_.apply(asParams(columns));
            @SuppressWarnings("unchecked")
            T t = (T) (instance instanceof ValueAttributeProjection.Wrapper ? ((ValueAttributeProjection.Wrapper)instance).getWrapped() : instance);
            return t;
        }

        @SuppressWarnings("unchecked")
        private PARAMS asParams(Object[] columns) {
            if (columns.length == 1) {
                return (PARAMS) columns[0];
            } else if (columns.length == 2) {
                return (PARAMS) Tuple1.of(columns[0], columns[1]);
            } else if (columns.length == 3) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2]);
            } else if (columns.length == 4) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3]);
            } else if (columns.length == 5) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4]);
            } else if (columns.length == 6) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5]);
            } else if (columns.length == 7) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6]);
            } else if (columns.length == 8) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7]);
            } else if (columns.length == 9) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8]);
            } else if (columns.length == 10) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9]);
            } else if (columns.length == 11) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10]);
            } else if (columns.length == 12) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11]);
            } else if (columns.length == 13) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12]);
            } else if (columns.length == 14) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13]);
            } else if (columns.length == 15) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14]);
            } else if (columns.length == 16) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15]);
            } else if (columns.length == 17) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15], columns[16]);
            } else if (columns.length == 18) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15], columns[16], columns[17]);
            } else if (columns.length == 19) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15], columns[16], columns[17], columns[18]);
            } else if (columns.length == 20) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15], columns[16], columns[17], columns[18], columns[19]);
            } else if (columns.length == 21) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15], columns[16], columns[17], columns[18], columns[19], columns[20]);
            } else if (columns.length == 22) {
                return (PARAMS) Tuple1.of(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15], columns[16], columns[17], columns[18], columns[19], columns[20], columns[21]);
            }
            throw new UnsupportedOperationException("not implemented");
        }
    }

    /**
     * Performs some transformations to actual query results
     */
    private static void transformResults(Object[] columns, ConstructorMeta_<?, ?, ?> constructor_) {
        Class<?>[] constructorParameterTypes = constructor_.getMember().getParameterTypes();

        for (Map.Entry<Integer,? extends Attribute<?,?>> paramAndIndex: zipWithIndex(constructor_.getParameters())) {
            int column = paramAndIndex.getKey();

            for (LiteralAttribute<?,?> lit: AttributeProxy.unwrap(paramAndIndex.getValue(), LiteralAttribute.class)) {
                // Replace literal placeholder values with the actual values given by the user
                if (!LiteralAttribute.QUERY_PLACEHOLDER.equals(columns[column])) {
                    throw new IllegalStateException("Literal attribute placeholder expected, but was: " + columns[column]);
                }
                columns[column] = lit.getValue();
            }

            // Wrap values to Some and nulls to None for optional parameters
            if (unwrap(paramAndIndex.getValue(), OptionalAttribute.class).isDefined()) {
                columns[column] = Option.of(columns[column]);
            }

            if (columns[column] == null) {
                throw new NullValueButNonOptionConstructorArgumentException(constructor_.getMember(), constructorParameterTypes[column], column);
            }
        }
    }

    // whether constructor expects a collections of Ids (instead of a collection of entities)
    private static boolean isWrapperOfIds(ConstructorMeta_<?, ?, ?> constructor_, int columnIndex) {
        return constructor_.getIndexesOfIdWrappingParameters().contains(columnIndex);
    }

    private static boolean isDistinctable(ConstructorMeta_<?, ?, ?> constructor_, int columnIndex) {
        return Set.class.isAssignableFrom(constructor_.getMember().getParameterTypes()[columnIndex]) && !NotDistinctable.class.isAssignableFrom(((Bindable<?>)constructor_.getParameters().get(columnIndex)).getBindableJavaType());
    }
}
