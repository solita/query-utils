package fi.solita.utils.query.backend.hibernate;

import java.lang.reflect.Field;
import java.util.Optional;

import org.hibernate.query.sqm.internal.SimpleSqmCopyContext;
import org.hibernate.query.sqm.tree.domain.SqmListJoin;
import org.hibernate.query.sqm.tree.select.AbstractSqmSelectQuery;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.query.sqm.tree.select.SqmSortSpecification;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

public class HibernateJpaCriteriaCopy {
    public static <T> Optional<CriteriaQuery<T>> copyCriteria(CriteriaQuery<T> q) {
        if (q instanceof SqmSelectStatement) {
            SqmSelectStatement<T> ret = ((SqmSelectStatement<T>) q).copy(new SimpleSqmCopyContext());
            try {
                Field resultType = AbstractSqmSelectQuery.class.getDeclaredField("resultType");
                resultType.setAccessible(true);
                resultType.set(ret, Object.class); // was null for Hibernate 6, but needs to be Object.class for 7... Semantics for both is "we don't know yet, so determine later"
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return Optional.of((CriteriaQuery<T>)ret);
        }
        return Optional.empty();
    }
    
    public static Optional<Order> createListIndexSortOrder(Path<?> listAttributePath) {
        if (listAttributePath instanceof SqmListJoin) {
            return Optional.of((Order)new SqmSortSpecification(((SqmListJoin<?,?>)listAttributePath).index()));
        }
        return Optional.empty();
    }
}
