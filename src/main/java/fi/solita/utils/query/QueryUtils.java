package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.forAll;
import static fi.solita.utils.functional.Functional.grouped;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.isEmpty;
import static fi.solita.utils.functional.Functional.map;
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
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
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
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;

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
        String specifiedValue = ((AnnotatedElement)attr.getJavaMember()).getAnnotation(OrderColumn.class).name();
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

    public static final <T> javax.persistence.criteria.Predicate inExpr(Path<? super T> path, Iterable<T> values, CriteriaBuilder cb) {
        javax.persistence.criteria.Predicate pred = cb.disjunction();
        // oracle fails if more than 1000 parameters
        List<List<T>> groups = newList(grouped(values, 1000));
        for (List<T> g: groups) {
            pred = cb.or(pred, path.in(newList(g)));
        }
        return pred;
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
            return forAll(((JoiningAttribute) param).getAttributes(), QueryUtils_.isRequiredByMetamodel);
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
            ret &= forAll(((AdditionalQueryPerformingAttribute)param).getConstructor().getParameters(), QueryUtils_.isRequiredByMetamodel);
        }
        
        return ret;
    }
    
    public static boolean isRequiredByQueryAttribute(Attribute<?,?> param) {
        if (param == null || unwrap(PseudoAttribute.class, param).isDefined()) {
            return true;
        }
        
        boolean ret = !unwrap(OptionalAttribute.class, param).isDefined();
        
        if (param instanceof AdditionalQueryPerformingAttribute && param instanceof SingularAttribute) {
            ret &= forAll(((AdditionalQueryPerformingAttribute)param).getConstructor().getParameters(), QueryUtils_.isRequiredByQueryAttribute);
        }
        
        return ret;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Join<?,?> join(From<?, ?> join, Attribute<?,?> attr, JoinType type) {
        return attr instanceof SingularAttribute ? join.join((SingularAttribute) attr, type) :
               attr instanceof SetAttribute ? join.join((SetAttribute) attr, type) :
               attr instanceof ListAttribute ? join.join((ListAttribute) attr, type) :
               attr instanceof MapAttribute ? join.join((MapAttribute) attr, type) :
                                              join.join((CollectionAttribute) attr, type);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Expression<?> get(Path<?> path, Attribute<?,?> attr) {
        return attr instanceof SingularAttribute ? path.get((SingularAttribute) attr) :
               attr instanceof SetAttribute ? path.get((SetAttribute) attr) :
               attr instanceof ListAttribute ? path.get((ListAttribute) attr) :
               attr instanceof MapAttribute ? path.get((PluralAttribute) attr) :
                                               path.get((CollectionAttribute) attr);
    }
}
