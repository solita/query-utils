package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Option.*;

import java.lang.reflect.Field;

import org.hibernate.query.sqm.internal.SimpleSqmCopyContext;
import org.hibernate.query.sqm.tree.domain.SqmListJoin;
import org.hibernate.query.sqm.tree.select.AbstractSqmSelectQuery;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.query.sqm.tree.select.SqmSortSpecification;

import fi.solita.utils.functional.Option;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

public class HibernateJpaCriteriaCopy {
    public static <T> Option<CriteriaQuery<T>> copyCriteria(CriteriaQuery<T> q) {
        if (q instanceof SqmSelectStatement) {
            SqmSelectStatement<T> ret = ((SqmSelectStatement<T>) q).copy(new SimpleSqmCopyContext());
            try {
                Field resultType = AbstractSqmSelectQuery.class.getDeclaredField("resultType");
                resultType.setAccessible(true);
                resultType.set(ret, null);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return Some((CriteriaQuery<T>)ret);
        }
        return None();
    }
    
    public static Option<Order> createListIndexSortOrder(Path<?> listAttributePath) {
        if (listAttributePath instanceof SqmListJoin) {
            return Some((Order)new SqmSortSpecification(((SqmListJoin<?,?>)listAttributePath).index()));
        }
        return None();
    }
}
