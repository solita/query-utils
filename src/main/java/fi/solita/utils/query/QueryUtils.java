package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMutableList;
import static fi.solita.utils.functional.Collections.newMutableListOfSize;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.forall;
import static fi.solita.utils.functional.Functional.grouped;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.repeat;
import static fi.solita.utils.functional.Predicates.greaterThanOrEqualTo;
import static fi.solita.utils.query.attributes.AttributeProxy.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.FetchParent;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Attribute.PersistentAttributeType;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Order.Direction;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.db.TableInClauseOptimization;
import fi.solita.utils.query.entities.Table;
import fi.solita.utils.query.entities.Table_;
import fi.solita.utils.query.meta.MetaJpaConstructor;
import fi.solita.utils.query.projection.Constructors.TransparentProjection;

public class QueryUtils {
    
    static final String MEMBER_OF_CAST = "member_of_cast_";

    public static final class NoOrderingSpecifiedException extends RuntimeException {
        public NoOrderingSpecifiedException() {
            super("Paged query requires an Ordering. Either give Order.by:s as parameter (preferred), or use a query which defines ordering itself");
        }
    }

    public static final class RequiredAttributeMustNotHaveOptionTypeException extends RuntimeException {
        public RequiredAttributeMustNotHaveOptionTypeException(Attribute<?,?> attribute) {
            super(attribute + ". Remove Cast.optional() wrapping from a mandatory attribute");
        }
    }

    public static final class OptionalAttributeNeedOptionTypeException extends RuntimeException {
        public OptionalAttributeNeedOptionTypeException(Attribute<?,?> attribute) {
            super(attribute + ". Wrap the optional attribute with Cast.optional()");
        }
    }
    
    public static final fi.solita.utils.functional.Predicate<Class<?>> ImplementsProjectWithRegularInClause = new fi.solita.utils.functional.Predicate<Class<?>>() {
        @Override
        public boolean accept(Class<?> candidate) {
            return ProjectWithRegularInClause.class.isAssignableFrom(candidate);
        }
    };

    private final Configuration config;
    
    public QueryUtils(Configuration config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public static <E,ID_E> SingularAttribute<E, ID_E> id(Class<? extends E> entityClass, EntityManager em) {
        EntityType<?> e = em.getMetamodel().entity(entityClass);
        return (SingularAttribute<E, ID_E>) e.getId(e.getIdType().getJavaType());
    }
    
    public static String resolveOrderColumn(ListAttribute<?,?> attr) {
        OrderColumn annotation = ((AnnotatedElement)attr.getJavaMember()).getAnnotation(OrderColumn.class);
        String specifiedValue = annotation.name();
        return specifiedValue != "" ? specifiedValue : attr.getName() + "_" + "ORDER";
    }

    public static void addListAttributeOrdering(CriteriaQuery<?> query, Expression<?> listAttributePath, String orderColumn, CriteriaBuilder cb) {
        List<Order> orders = newMutableList();
        if (query.getOrderList() != null) {
            orders.addAll(query.getOrderList());
        }
        // "index" is a "function" in HQL that is used to order by the @Indexcolumn/@OrderColumn
        // Yes, this is Hibern-specific. Gotta figure out what to do with this...
        orders.add(cb.asc(cb.function("index", null, listAttributePath)));
        query.orderBy(orders);
    }

    @SuppressWarnings("unchecked")
    public static <T> Selection<T> resolveSelection(CriteriaQuery<T> query) {
        if (query.getSelection() != null) {
            return query.getSelection();
        }
        Iterator<Root<?>> it = query.getRoots().iterator();
        if (it.hasNext()) {
            Root<?> root = it.next();
            if (!it.hasNext() && root.getJavaType().equals(query.getResultType())) {
                return (Root<T>) root;
            }
        }
        throw new RuntimeException("Could not resolve selection. Either specify entity-result type matching the single query root when constructing the query (cb.createQuery(FooEntity.class);) or do an explicit selection (query.select(fooEntity);)");
    }

    // If the selection targets an entity, it should always be a path. Right?
    public static <E> Path<E> resolveSelectionPath(CriteriaQuery<E> query) {
        return (Path<E>) resolveSelection(query);
    }

    @SuppressWarnings("unchecked")
    public static <T> From<?,T> resolveSelection(CriteriaQuery<T> from, CriteriaQuery<?> to) {
        if (from.getSelection() != null) {
            Selection<?> selection = (Selection<?>) from.getSelection();
            if (selection instanceof Root) {
                for (Root<?> root: to.getRoots()) {
                    if (root.getJavaType().equals(selection.getJavaType())) {
                        return (Root<T>) root;
                    }
                }
            }
            if (selection instanceof Join) {
                for (Root<?> root: to.getRoots()) {
                    for (Join<?,?> join: getAllJoins(root)) {
                        if (join.getAttribute().equals(((Join<?,?>)selection).getAttribute())) {
                            return (From<?,T>) join;
                        }
                    }
                }
            }
        }
        if (to.getRoots().size() == 1) {
            return (From<?, T>)head(to.getRoots());
        }
        throw new RuntimeException("Could not resolve selection.");
    }

    public static <T> void checkOrdering(CriteriaQuery<T> query, Page page) throws NoOrderingSpecifiedException {
        if ((page.getFirstResult() != Page.NoPaging.getFirstResult() || page.getMaxResults() != Page.NoPaging.getMaxResults()) && query.getOrderList().isEmpty()) {
            throw new NoOrderingSpecifiedException();
        }
    }
    
    public static final <E> CriteriaQuery<E> applyOrder(CriteriaQuery<E> query, final Path<E> selection, Iterable<? extends fi.solita.utils.query.Order<? super E, ?>> orderings, final CriteriaBuilder cb) {
        if (!isEmpty(orderings)) {
            query.orderBy(newList(map(QueryUtils_.<E>order2jpaOrder().ap(cb, selection), orderings)));
        } else {
            applyOrder(query, selection, cb);
        }
        return query;
    }
    
    static <E> Order order2jpaOrder(CriteriaBuilder cb, Path<E> selection, fi.solita.utils.query.Order<? super E, ?> o) {
        return o.getDirection() == Direction.ASC ? cb.asc(selection.get(o.getAttribute())) : cb.desc(selection.get(o.getAttribute()));
    }
    
    public static <E> CriteriaQuery<E> applyOrder(CriteriaQuery<E> query, final Selection<E> selection, CriteriaBuilder cb) {
        if (selection instanceof Path<?> && ((Path<?>) selection).getModel() instanceof ListAttribute) {
            addListAttributeOrdering(query, (Path<?>) selection, resolveOrderColumn((ListAttribute<?,?>)((Path<?>) selection).getModel()), cb);
        }
        return query;
    }
    
    public final Predicate inExpr(Expression<?> path, Set<?> values, CriteriaBuilder cb) {
        return inExpr(path, values, cb, true);
    }
    
    public final boolean useTableForInClause(Set<?> vals) {
        return vals.size() > config.getMaxValuesForMemberOfRestriction();
    }
    
    public final boolean useMemberOfForInClause(Set<?> vals) {
        return vals.size() > config.getMinValuesForMemberOfRestriction();
    }
    
    public final boolean wouldUseInClauseOptimizations(Set<?> vals) {
        return config.getTableInClauseProvider().isDefined() && (useTableForInClause(vals) || useMemberOfForInClause(vals));
    }

    @SuppressWarnings("unchecked")
    public final Predicate inExpr(Expression<?> path, Set<?> vals, CriteriaBuilder cb, boolean enableOptimizations) {
        if (vals.size() == 1) {
            return cb.equal(path, head(vals));
        } else if (vals.isEmpty()) {
            return cb.or();
        }
        
        List<? extends List<?>> groups;
        List<Predicate> preds = null;
        
        if (enableOptimizations) {
            for (TableInClauseOptimization provider: config.getTableInClauseProvider()) {
                // only use table-expression for large sets since ora performs better with regular in-clause.
                if (useTableForInClause(vals)) {
                    // use 'table' for huge sets since member-of starts to perform badly
                    preds = newList(path.in(cb.function("table", Collection.class, cb.literal(Table.of(vals)))));
                } else if (useMemberOfForInClause(vals)) {
                    Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> targetType = provider.getSqlTypeAndValues(vals);
                    if (!targetType.isDefined()) {
                        throw new IllegalArgumentException("No tabletype registered (see fi.solita.utils.query.DefaultConfiguration.getRegisteredTableTypes()) for type " + head(vals).getClass());
                    }
                    // use member-of
                    // return type doesn't seem to make a difference, so just set to boolean...
                    preds = newList(cb.<Object,Collection<Object>>isMember(path, (Expression<Collection<Object>>)(Object)cb.function(MEMBER_OF_CAST + targetType.get()._1, Collection.class, cb.literal(Table.of(vals)))));
                }
            }
        }
        
        if (preds == null) {
            // Use regular in-clause.
            if (config.getInClauseValuesAmounts().isEmpty()) {
                groups = Arrays.asList(newList(vals));
            } else {
                groups = newList(grouped(config.getInClauseValuesAmounts().last(), vals));
            }
            preds = newMutableListOfSize(groups.size());
            
            for (List<?> g: groups) {
                preds.add(path.in(padInListIfNeeded(g)));
            }
        }
        
        if (preds.size() == 1) {
            return head(preds);
        } else {
            return cb.or(newArray(Predicate.class, preds));
        }
    }
    
    public <T> List<T> padInListIfNeeded(List<T> list) {
        if (list.isEmpty()) {
            return list;
        } else if (!config.getInClauseValuesAmounts().isEmpty() && list.size() < config.getInClauseValuesAmounts().last()) {
            // pad in-list to the next specified size, to avoid excessive hard parsing
            int targetSize = head(filter(greaterThanOrEqualTo(list.size()), config.getInClauseValuesAmounts()));
            @SuppressWarnings("unchecked")
            T valueToRepeat = (T) config.getInListPadValue(last(list).getClass()).getOrElse(last(list));
            return newList(concat(list, repeat(valueToRepeat, targetSize-list.size())));
        } else {
            return list;
        }
    }
    
    /**
     * Assumes that a function named "column_value" can get the value from the table-subselect.
     * With Ora and Hibern this can be achieved by adding the following line to the Dialect:
     * <code><pre>
     * registerFunction("column_value", new NoArgSQLFunction("column_value", StandardBasicTypes.STRING, false));
     * </pre></code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Subquery<T> table(Subquery<T> tableselect, Expression<? extends Iterable<T>> expr, CriteriaBuilder cb) {
        Root<Table> r = tableselect.from(Table.class);
        tableselect.select(cb.function("column_value", (Class<T>)tableselect.getJavaType()));
        Predicate truthy = cb.equal(cb.literal(0), 0);
        tableselect.where(cb.and(truthy,
                                 cb.or(truthy,
                                       cb.equal(cb.literal(0),
                                                cb.quot(r.get(Table_.star),
                                                        (Expression<? extends Number>)(Object)expr)))));
        return tableselect;
    }
    
    public static Iterable<Join<?,?>> getAllJoins(From<?, ?> parent) {
        return flatMap(new Transformer<Join<?,?>,Iterable<Join<?,?>>>() {
            @Override
            public Iterable<Join<?, ?>> transform(Join<?, ?> source) {
                return cons(source, getAllJoins(source));
            }
        }, parent.getJoins());
    }
    
    public static Iterable<Fetch<?,?>> getAllFetches(FetchParent<?, ?> parent) {
        return flatMap(new Transformer<Fetch<?,?>,Iterable<Fetch<?,?>>>() {
            @Override
            public Iterable<Fetch<?, ?>> transform(Fetch<?, ?> source) {
                return cons(source, getAllFetches(source));
            }
        }, parent.getFetches());
    }
    
    public static void checkOptionalAttributes(Attribute<?,?> param) {
        boolean metaModelAttributeIsRequired = isRequiredByMetamodel(param);
        boolean queryAttributeIsRequired = isRequiredByQueryAttribute(param);
    
        if (!metaModelAttributeIsRequired && queryAttributeIsRequired) {
            throw new OptionalAttributeNeedOptionTypeException(param);
        } else if (metaModelAttributeIsRequired && !queryAttributeIsRequired) {
            throw new RequiredAttributeMustNotHaveOptionTypeException(param);
        }
    }
    
    public static boolean isRequiredByMetamodel(Attribute<?,?> param) {
        if (param == null) {
            return true;
        }
        if (canUnwrap(PseudoAttribute.class, param)) {
            return true;
        }
        
        boolean ret;
        if (param instanceof OptionalAttribute) {
            return false;
        } else if (param instanceof JoiningAttribute && param instanceof SingularAttribute) {
            return forall(QueryUtils_.isRequiredByMetamodel, ((JoiningAttribute) param).getAttributes());
        } else if (param instanceof JoiningAttribute) {
            return true;
        } else if (!(param instanceof AdditionalQueryPerformingAttribute) && param instanceof Bindable && Option.class.isAssignableFrom(((Bindable<?>) param).getBindableJavaType())) {
            return false;
        } else if (param instanceof SingularAttribute && ((SingularAttribute<?,?>)param).getPersistentAttributeType() == PersistentAttributeType.EMBEDDED) {
            if (!((SingularAttribute<?,?>)param).isOptional()) {
                ret = true;
            } else {
                Member member = ((SingularAttribute<?,?>)param).getJavaMember();
                if (member instanceof AnnotatedElement) {
                    ret = memberIsRequired(member);
                } else {
                    ret = false;
                }
            }
        } else if (param instanceof SingularAttribute) {
            if (!((SingularAttribute<?,?>)param).isOptional()) {
                ret = true;
            } else {
                Member member = ((SingularAttribute<?,?>)param).getJavaMember();
                if (member instanceof AnnotatedElement) {
                    ret = memberIsRequired(member);
                } else {
                    ret = false;
                }
            }
        } else {
            ret = param.isCollection();
        }
        
        if (param instanceof AdditionalQueryPerformingAttribute) {
            MetaJpaConstructor<?,?,?> c = ((AdditionalQueryPerformingAttribute)param).getConstructor();
            if (c instanceof TransparentProjection) {
                // optionality of a TransparentProjection propagates
                ret &= forall(QueryUtils_.isRequiredByMetamodel, c.getParameters());
            }
        }
        
        return ret;
    }

    private static boolean memberIsRequired(Member member) {
        Column column = ((AnnotatedElement)member).getAnnotation(Column.class);
        Basic basic = ((AnnotatedElement)member).getAnnotation(Basic.class);
        return column != null && column.nullable() == false ||
               basic != null && basic.optional() == false ||
               member instanceof Field && Option.class.isAssignableFrom(((Field)member).getType()) ||
               member instanceof Method && Option.class.isAssignableFrom(((Method)member).getReturnType());
    }
    
    public static boolean isRequiredByQueryAttribute(Attribute<?,?> param) {
        if (param == null) {
            return true;
        }
        if (canUnwrap(PseudoAttribute.class, param)) {
            return true;
        }
        
        if (param instanceof AdditionalQueryPerformingAttribute && ((AdditionalQueryPerformingAttribute) param).getConstructor() instanceof TransparentProjection) {
            return isRequiredByQueryAttribute(((TransparentProjection)((AdditionalQueryPerformingAttribute) param).getConstructor()).getWrapped());
        }
        
        if (param instanceof JoiningAttribute && param instanceof SingularAttribute) {
            return forall(QueryUtils_.isRequiredByQueryAttribute, ((JoiningAttribute) param).getAttributes());
        } else if (param instanceof JoiningAttribute) {
            return true;
        }
        
        if (canUnwrap(OptionalAttribute.class, param)) {
            return false;
        }
        
        if (param instanceof Bindable && Option.class.isAssignableFrom(((Bindable<?>) param).getBindableJavaType())) {
            return false;
        }
        
        return true;
    }
    
    public static Join<?,?> join(From<?, ?> join, Attribute<?,?> attr, JoinType type) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Join<?,?> ret = attr instanceof SingularAttribute ? join.join((SingularAttribute) attr, type) :
                        attr instanceof CollectionAttribute ? join.join((CollectionAttribute) attr, type) :
                        attr instanceof SetAttribute ? join.join((SetAttribute) attr, type) :
                        attr instanceof ListAttribute ? join.join((ListAttribute) attr, type) :
                        attr instanceof MapAttribute ? join.join((MapAttribute) attr, type) :
                                                       join.join((CollectionAttribute) attr, type);
        return ret;
    }
    
    public static Expression<?> get(Path<?> path, Attribute<?,?> attr) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Expression<?> ret = attr instanceof SingularAttribute ? path.get((SingularAttribute) attr) :
                            attr instanceof CollectionAttribute ? path.get((CollectionAttribute) attr) :
                            attr instanceof SetAttribute ? path.get((SetAttribute) attr) :
                            attr instanceof ListAttribute ? path.get((ListAttribute) attr) :
                            attr instanceof MapAttribute ? path.get((PluralAttribute) attr) :
                                                           path.get((CollectionAttribute) attr);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static <R> Type<R> getElementType(Attribute<?, ?> attr) {
        if (attr instanceof SingularAttribute) {
            return ((SingularAttribute<?,R>) attr).getType();
        } else {
            return ((PluralAttribute<?,?,R>)attr).getElementType();
        }
    }
}
