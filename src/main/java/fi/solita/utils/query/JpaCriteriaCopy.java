package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.it;

import fi.solita.utils.query.backend.hibernate.HibernateJpaCriteriaCopy;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Order;

public class JpaCriteriaCopy {
    public static <T> CriteriaQuery<T> copyCriteria(CriteriaQuery<T> q) {
        for (CriteriaQuery<T> ret: it(HibernateJpaCriteriaCopy.copyCriteria(q))) {
            return ret;
        }
        throw new RuntimeException("Not implemented for: " + q.getClass().getName());
    }
    
    public static Order createListIndexSortOrder(Path<?> listAttributePath) {
        for (Order ret : it(HibernateJpaCriteriaCopy.createListIndexSortOrder(listAttributePath))) {
            return ret;
        }
        throw new RuntimeException("Not implemented for: " + listAttributePath.getClass().getName());
    }

}
