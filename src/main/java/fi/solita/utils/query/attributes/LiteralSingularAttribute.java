package fi.solita.utils.query.attributes;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

class LiteralSingularAttribute<X, T> extends SingularAttributeProxy<X, T> implements PseudoAttribute {
    private final T value;

    public LiteralSingularAttribute(T value) {
        super(null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getJavaType() {
        return (Class<T>) (value == null ? void.class : value.getClass());
    }
    
    @Override
    public Expression<?> getSelectionForQuery(EntityManager em, Path<?> currentSelection) {
        return em.getCriteriaBuilder().literal(PseudoAttribute.QUERY_PLACEHOLDER);
    }
    
    @Override
    public Object getValueToReplaceResult(Object resultFromDb) {
        if (!PseudoAttribute.QUERY_PLACEHOLDER.equals(resultFromDb)) {
            throw new IllegalStateException("Literal attribute placeholder expected, but was: " + resultFromDb);
        }
        return value;
    }
}