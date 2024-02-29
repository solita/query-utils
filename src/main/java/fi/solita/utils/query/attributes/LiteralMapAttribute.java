package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Functional.head;

import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.Type;


class LiteralMapAttribute<X, K, Y, A extends Attribute<X, Map<K,Y>> & Bindable<Y>> extends PluralAttributeProxy<X, Map<K,Y>, Y, A> implements MapAttribute<X, K, Y>, PseudoAttribute {

    private final Map<K,Y> value;

    public LiteralMapAttribute(Map<K,Y> value) {
        super(null, CollectionType.MAP, null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Map<K,Y>> getJavaType() {
        return (Class<Map<K,Y>>)(Object)Map.class;
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<K> getKeyJavaType() {
        return (Class<K>)(value.isEmpty() ? null: head(value.keySet()).getClass());
    }

    @Override
    public Type<K> getKeyType() {
        return null;
    }

}