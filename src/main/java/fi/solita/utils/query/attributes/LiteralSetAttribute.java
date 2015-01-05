package fi.solita.utils.query.attributes;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SetAttribute;


class LiteralSetAttribute<X, Y, A extends Attribute<X, Set<Y>> & Bindable<Y>> extends PluralAttributeProxy<X, Set<Y>, Y, A> implements SetAttribute<X, Y>, PseudoAttribute {

    private final Set<Y> value;

    public LiteralSetAttribute(Set<Y> value) {
        super(null, CollectionType.SET, null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Set<Y>> getJavaType() {
        return (Class<Set<Y>>) value.getClass();
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