package fi.solita.utils.query;

import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.lastOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.sort;
import static fi.solita.utils.query.QueryUtils.addListAttributeOrdering;
import static fi.solita.utils.query.QueryUtils.resolveOrderColumn;

import fi.solita.utils.functional.Option;
import jakarta.persistence.TupleElement;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.FetchParent;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

public class JpaCriteriaCopy {
    
    private final Configuration config;

    public JpaCriteriaCopy(Configuration config) {
        this.config = config;
    }
    
    static final String elemTypeName(TupleElement<?> e) {
        return e.getJavaType().getName();
    }
    
    static final String fetchTypeName(Fetch<?,?> f) {
        return f.getAttribute().getJavaType().getName();
    }
    
    static final int hash(Object e) {
        return e.hashCode();
    }
    
    public void copyCriteriaWithoutSelect(CriteriaQuery<?> from, CriteriaQuery<?> to, CriteriaBuilder cb) {
        int counter = findLatestCustomAlias(from).getOrElse(0);
        for (Root<?> root : from.getRoots()) {
            Root<?> r = to.from(root.getJavaType());
            copyAlias(root, r, ++counter);
            counter = copyJoins(root, r, ++counter);
        }
        to.distinct(from.isDistinct());
        if (from.getGroupList() != null) {
            to.groupBy(from.getGroupList());
        }
        if (from.getGroupRestriction() != null) {
            to.having(from.getGroupRestriction());
        }
        if (from.getRestriction() != null) {
            to.where(from.getRestriction());
        }
    
        if (from.getOrderList() != null && !from.getOrderList().isEmpty()) {
            to.orderBy(from.getOrderList());
        } else {
            Selection<?> selection = QueryUtils.resolveSelection(from, to);
            if (selection.isCompoundSelection()) {
                for (Selection<?> s: selection.getCompoundSelectionItems()) {
                    if (s instanceof Path<?> && ((Path<?>)s).getModel() instanceof ListAttribute) {
                        addListAttributeOrdering(to, (Expression<?>) s, resolveOrderColumn((ListAttribute<?,?>)((Path<?>)s).getModel()), cb);
                    }
                }
            } else if (selection instanceof Path<?> && ((Path<?>)selection).getModel() instanceof ListAttribute) {
                addListAttributeOrdering(to, (Expression<?>) selection, resolveOrderColumn((ListAttribute<?,?>)((Path<?>)selection).getModel()), cb);
            }
        }
    }

    public void createMissingAliases(CriteriaQuery<?> query) {
        int counter = findLatestCustomAlias(query).getOrElse(0);
        for (Root<?> root : query.getRoots()) {
            getOrCreateAlias(root, ++counter);
            counter = createMissingAliases(root, ++counter);
        }
    }
    
    private Option<Integer> findLatestCustomAlias(CriteriaQuery<?> query) {
        Iterable<String> allAliases = flatMap(JpaCriteriaCopy_.getAliases, query.getRoots());
        Iterable<Integer> allCustomAliases = map(JpaCriteriaCopy_.toInt.ap(this), filter(JpaCriteriaCopy_.isCustomAlias.ap(this), allAliases));
        return lastOption(sort(allCustomAliases));
    }
    
    static Iterable<String> getAliases(Root<?> root) {
        return cons(root.getAlias(), flatMap(JpaCriteriaCopy_.getAliases1, root.getJoins()));
    }
    
    static Iterable<String> getAliases(Join<?,?> join) {
        return cons(join.getAlias(), flatMap(JpaCriteriaCopy_.getAliases1, join.getJoins()));
    }
    
    int toInt(String customAlias) {
        return Integer.parseInt(customAlias.substring(config.getAliasPrefix().length()));
    }
    
    boolean isCustomAlias(String alias) {
        return alias != null && alias.startsWith(config.getAliasPrefix());
    }
    
    static String alias(Join<?,?> join) {
        return join.getAlias();
    }
    
    static String alias(Selection<?> selection) {
        return selection.getAlias();
    }
    
    /**
     * @return last possibly used alias
     */
    private int createMissingAliases(From<?, ?> from, int counter) {
        for (Join<?, ?> join : from.getJoins()) {
            getOrCreateAlias(join, ++counter);
            counter = createMissingAliases(join, ++counter);
        }
        return counter;
    }
    
    /**
     * @return last possibly used alias
     */
    private int copyJoins(From<?, ?> from, From<?, ?> to, int counter) {
        for (Join<?, ?> join : from.getJoins()) {
            Attribute<?, ?> attr = join.getAttribute();
            // Hibern fails with String-bases api; Join.join(String, JoinType)
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Join<Object, Object> j = attr instanceof SingularAttribute ? to.join((SingularAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof CollectionAttribute ? to.join((CollectionAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof SetAttribute ? to.join((SetAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof ListAttribute ? to.join((ListAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof MapAttribute ? to.join((MapAttribute) join.getAttribute(), join.getJoinType()) :
                to.join((CollectionAttribute) join.getAttribute(), join.getJoinType());
            copyAlias(join, j, ++counter);
            counter = copyJoins(join, j, ++counter);
        }
        copyFetches(from, to);
        return counter;
    }

    private static void copyFetches(FetchParent<?, ?> from, FetchParent<?, ?> to) {
        for (Fetch<?, ?> fetch : from.getFetches()) {
            Attribute<?, ?> attr = fetch.getAttribute();
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Fetch<?, ?> f = attr instanceof SingularAttribute ? to.fetch((SingularAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof CollectionAttribute ? to.fetch((CollectionAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof SetAttribute ? to.fetch((SetAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof ListAttribute ? to.fetch((ListAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof MapAttribute ? to.fetch((MapAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                to.fetch((CollectionAttribute) fetch.getAttribute(), fetch.getJoinType());
            copyFetches(fetch, f);
        }
    }

    private void copyAlias(Selection<?> from, Selection<?> to, int counter) {
        to.alias(getOrCreateAlias(from, counter));
    }
    
    private <T> String getOrCreateAlias(Selection<T> selection, int counter) {
        String alias = selection.getAlias();
        if (alias == null) {
            alias = config.getAliasPrefix() + counter;
            selection.alias(alias);
        }
        return alias;
    }
}
