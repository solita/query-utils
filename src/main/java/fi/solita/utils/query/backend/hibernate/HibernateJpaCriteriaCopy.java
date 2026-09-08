package fi.solita.utils.query.backend.hibernate;

import java.util.Optional;

import org.hibernate.query.sqm.internal.SimpleSqmCopyContext;
import org.hibernate.query.sqm.tree.domain.SqmListJoin;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.query.sqm.tree.select.SqmSortSpecification;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

public class HibernateJpaCriteriaCopy {
    public static <T> Optional<CriteriaQuery<T>> copyCriteria(CriteriaQuery<T> q) {
        if (q instanceof SqmSelectStatement) {
            SqmSelectStatement<T> ret = ((SqmSelectStatement<T>) q).copy(new SimpleSqmCopyContext());
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
