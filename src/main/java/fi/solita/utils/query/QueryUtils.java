package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newListOfSize;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.FunctionalImpl.flatMap;
import static fi.solita.utils.functional.FunctionalImpl.forall;
import static fi.solita.utils.functional.FunctionalImpl.grouped;
import static fi.solita.utils.functional.FunctionalImpl.map;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.OrderColumn;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Transformer;
import fi.solita.utils.query.Order.Direction;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.codegen.MetaJpaConstructor;
import fi.solita.utils.query.entities.Table;
import fi.solita.utils.query.entities.Table_;
import fi.solita.utils.query.projection.Constructors.TransparentProjection;

public abstract class QueryUtils {
    
    public static final class NoOrderingSpecifiedException extends RuntimeException {
        public NoOrderingSpecifiedException() {
            super("Paged query requires an Ordering. Either give Order.by:s as parameter (preferred), or use a query which defines ordering itself");
        }
    }

    public static final class RequiredAttributeMustNotHaveOptionTypeException extends RuntimeException {
        public RequiredAttributeMustNotHaveOptionTypeException(Attribute<?,?> attribute) {
            super(attribute.getDeclaringType().getJavaType().getSimpleName() + "." + attribute.getName() + ". Remove Cast.optional() wrapping from a mandatory attribute");
        }
    }

    public static final class OptionalAttributeNeedOptionTypeException extends RuntimeException {
        public OptionalAttributeNeedOptionTypeException(Attribute<?,?> attribute) {
            super(attribute.getDeclaringType().getJavaType().getSimpleName() + "." + attribute.getName() + ". Wrap the optional attribute with Cast.optional()");
        }
    }

    private QueryUtils() {
        //
    }

    @SuppressWarnings("unchecked")
    public static <E> SingularAttribute<E, Id<E>> id(Class<? extends E> entityClass, EntityManager em) {
        EntityType<?> e = em.getMetamodel().entity(entityClass);
        return (SingularAttribute<E, Id<E>>) e.getId(e.getIdType().getJavaType());
    }
    
    public static String resolveOrderColumn(ListAttribute<?,?> attr) {
        OrderColumn annotation = ((AnnotatedElement)attr.getJavaMember()).getAnnotation(OrderColumn.class);
        String specifiedValue = annotation == null ? "" : annotation.name();
        return specifiedValue != "" ? specifiedValue : attr.getName() + "_" + "ORDER";
    }

    public static void addListAttributeOrdering(CriteriaQuery<?> query, Expression<?> listAttributePath, String orderColumn, CriteriaBuilder cb) {
        List<Order> orders = newList();
        if (query.getOrderList() != null) {
            orders.addAll(query.getOrderList());
        }
        // "index" is a "function" in HQL that is used to order by the @Indexcolumn/@OrderColumn
        // Yes, this is Hibernate-specific. Gotta figure out what to do with this...
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
        if (page != Page.NoPaging && query.getOrderList().isEmpty()) {
            throw new NoOrderingSpecifiedException();
        }
    }
    
    public static final <E extends IEntity> CriteriaQuery<E> applyOrder(CriteriaQuery<E> query, final Path<E> selection, Iterable<? extends fi.solita.utils.query.Order<? super E, ?>> orderings, final CriteriaBuilder cb) {
        if (!isEmpty(orderings)) {
            query.orderBy(newList(map(orderings, QueryUtils_.<E>order2jpaOrder().ap(cb, selection))));
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

    public static final Predicate inExpr(CriteriaQuery<?> q, Expression<?> path, Iterable<?> values, CriteriaBuilder cb) {
        if (Table.isSupported(values)) {
            Subquery<Long> tableselect = q.subquery(Long.class);
            Root<Table> root = tableselect.from(Table.class);
            tableselect.select(cb.function("dynamic_sampling", Long.class));
            tableselect.where(cb.equal(cb.literal(new Table.Value(newList(values))), root.get(Table_.commentEndWithBindParameter)));
            return path.in(tableselect);
        } else {
            // oracle fails if more than 1000 parameters
            List<? extends List<?>> groups = newList(grouped(values, 1000));
            List<Predicate> preds = newListOfSize(groups.size());
            for (List<?> g: groups) {
                preds.add(path.in(g));
            }
            if (preds.isEmpty()) {
                return cb.or();
            } else if (preds.size() == 1) {
                return head(preds);
            } else {
                return cb.or(newArray(Predicate.class, preds));
            }
        }
    }
    
    /**
     * Assumes that a function named "column_value" can get the value from the table-subselect.
     * With Oracle and Hibernate this can be achieved by adding the following line to the Dialect:
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
        return flatMap(parent.getJoins(), new Transformer<Join<?,?>,Iterable<Join<?,?>>>() {
            @Override
            public Iterable<Join<?, ?>> transform(Join<?, ?> source) {
                return cons(source, getAllJoins(source));
            }
        });
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
        if (param == null || unwrap(PseudoAttribute.class, param).isDefined()) {
            return true;
        }
        
        boolean ret;
        if (param instanceof OptionalAttribute) {
            return false;
        } else if (param instanceof JoiningAttribute && param instanceof SingularAttribute) {
            return forall(((JoiningAttribute) param).getAttributes(), QueryUtils_.isRequiredByMetamodel);
        } else if (param instanceof JoiningAttribute) {
            // joining set/list attributes return sets/lists and are thus considered always required
            return true;
        } else if (param instanceof SingularAttribute && ((SingularAttribute<?,?>)param).getPersistentAttributeType() == PersistentAttributeType.EMBEDDED) {
            if (!((SingularAttribute<?,?>)param).isOptional()) {
                ret = true;
            } else {
                Member member = ((SingularAttribute<?,?>)param).getJavaMember();
                if (member instanceof AnnotatedElement) {
                    Column column = ((AnnotatedElement)member).getAnnotation(Column.class);
                    Basic basic = ((AnnotatedElement)member).getAnnotation(Basic.class);
                    ret = column != null && column.nullable() == false ||
                          basic != null && basic.optional() == false;
                } else {
                    ret = false;
                }
            }
        } else if (param instanceof SingularAttribute) {
            ret = !((SingularAttribute<?,?>)param).isOptional();
        } else {
            ret = param.isCollection();
        }
        
        if (param instanceof AdditionalQueryPerformingAttribute && param instanceof SingularAttribute) {
            MetaJpaConstructor<?,?,?> c = ((AdditionalQueryPerformingAttribute)param).getConstructor();
            if (c instanceof TransparentProjection) {
                // optionality of a TransparentProjection propagates
                ret &= forall(c.getParameters(), QueryUtils_.isRequiredByMetamodel);
            }
        }
        
        return ret;
    }
    
    public static boolean isRequiredByQueryAttribute(Attribute<?,?> param) {
        if (param == null || unwrap(PseudoAttribute.class, param).isDefined()) {
            return true;
        }
        
        boolean ret = !unwrap(OptionalAttribute.class, param).isDefined();
        
        if (param instanceof AdditionalQueryPerformingAttribute && param instanceof SingularAttribute) {
            ret &= forall(((AdditionalQueryPerformingAttribute)param).getConstructor().getParameters(), QueryUtils_.isRequiredByQueryAttribute);
        }
        
        return ret;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Join<?,?> join(From<?, ?> join, Attribute<?,?> attr, JoinType type) {
        return attr instanceof SingularAttribute ? join.join((SingularAttribute) attr, type) :
               attr instanceof CollectionAttribute ? join.join((CollectionAttribute) attr, type) :
               attr instanceof SetAttribute ? join.join((SetAttribute) attr, type) :
               attr instanceof ListAttribute ? join.join((ListAttribute) attr, type) :
               attr instanceof MapAttribute ? join.join((MapAttribute) attr, type) :
                                              join.join((CollectionAttribute) attr, type);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Expression<?> get(Path<?> path, Attribute<?,?> attr) {
        return attr instanceof SingularAttribute ? path.get((SingularAttribute) attr) :
               attr instanceof CollectionAttribute ? path.get((CollectionAttribute) attr) :
               attr instanceof SetAttribute ? path.get((SetAttribute) attr) :
               attr instanceof ListAttribute ? path.get((ListAttribute) attr) :
               attr instanceof MapAttribute ? path.get((PluralAttribute) attr) :
                                               path.get((CollectionAttribute) attr);
    }
}
