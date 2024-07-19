package fi.solita.utils.query;

import fi.solita.utils.query.backend.hibernate.HibernateJpaCriteriaCopy;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

public class JpaCriteriaCopy {
    public static <T> CriteriaQuery<T> copyCriteria(CriteriaQuery<T> q) {
        for (CriteriaQuery<T> ret: HibernateJpaCriteriaCopy.copyCriteria(q)) {
            return ret;
        }
        throw new RuntimeException("Not implemented for: " + q.getClass().getName());
    }
    
    public static Order createListIndexSortOrder(Path<?> listAttributePath) {
        for (Order ret : HibernateJpaCriteriaCopy.createListIndexSortOrder(listAttributePath)) {
            return ret;
        }
        throw new RuntimeException("Not implemented for: " + listAttributePath.getClass().getName());
    }
}
