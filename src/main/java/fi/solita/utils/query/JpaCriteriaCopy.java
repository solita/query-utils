package fi.solita.utils.query;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

public abstract class JpaCriteriaCopy {

    static final AtomicInteger aliasCounter = new AtomicInteger();
    
    public static void copyCriteriaWithoutSelect(CriteriaQuery<?> from, CriteriaQuery<?> to, CriteriaBuilder cb) {
        if (aliasCounter.get() > 100000) {
            aliasCounter.set(0);
        }
        for (Root<?> root : from.getRoots()) {
            Root<?> r = to.from(root.getJavaType());
            JpaCriteriaCopy.copyAlias(root, r);
            JpaCriteriaCopy.copyJoins(root, r);
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
                        QueryUtils.addListAttributeOrdering(to, (Expression<?>) s, cb);
                    }
                }
            } else if (selection instanceof Path<?> && ((Path<?>)selection).getModel() instanceof ListAttribute) {
                QueryUtils.addListAttributeOrdering(to, (Expression<?>) selection, cb);
            }
        }
    }

    private static void copyJoins(From<?, ?> from, From<?, ?> to) {
        for (Join<?, ?> join : from.getJoins()) {
            Attribute<?, ?> attr = join.getAttribute();
            // Hibernate fails with String-bases api; Join.join(String, JoinType)
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Join<Object, Object> j = attr instanceof SingularAttribute ? to.join((SingularAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof SetAttribute ? to.join((SetAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof ListAttribute ? to.join((ListAttribute) join.getAttribute(), join.getJoinType()) :
                attr instanceof MapAttribute ? to.join((MapAttribute) join.getAttribute(), join.getJoinType()) :
                to.join((CollectionAttribute) join.getAttribute(), join.getJoinType());
            copyAlias(join, j);
            copyJoins(join, j);
        }
        JpaCriteriaCopy.copyFetches(from, to);
    }
    
    public static void createMissingAliases(CriteriaQuery<?> query) {
        for (Root<?> root : query.getRoots()) {
            getOrCreateAlias(root);
            createMissingAliases(root);
        }
    }
    
    private static void createMissingAliases(From<?, ?> from) {
        for (Join<?, ?> join : from.getJoins()) {
            getOrCreateAlias(join);
            createMissingAliases(join);
        }
    }

    private static void copyFetches(FetchParent<?, ?> from, FetchParent<?, ?> to) {
        for (Fetch<?, ?> fetch : from.getFetches()) {
            Attribute<?, ?> attr = fetch.getAttribute();
            // Hibernate fails with String-bases api; Join.join(String, JoinType)
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Fetch<?, ?> f = attr instanceof SingularAttribute ? to.fetch((SingularAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof SetAttribute ? to.fetch((SetAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof ListAttribute ? to.fetch((ListAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                attr instanceof MapAttribute ? to.fetch((MapAttribute) fetch.getAttribute(), fetch.getJoinType()) :
                to.fetch((CollectionAttribute) fetch.getAttribute(), fetch.getJoinType());
            copyFetches(fetch, f);
        }
    }

    private static void copyAlias(Selection<?> from, Selection<?> to) {
        to.alias(getOrCreateAlias(from));
    }
    
    private static <T> String getOrCreateAlias(Selection<T> selection) {
        String alias = selection.getAlias();
        if (alias == null) {
            alias = "a_" + aliasCounter.getAndIncrement();
            selection.alias(alias);
        }
        return alias;
    
    }

}
