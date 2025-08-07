package fi.solita.utils.query.attributes;

import java.util.Set;
import java.util.SortedSet;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.SetAttribute;


class LiteralSetAttribute<X, Y, A extends Attribute<X, Set<Y>> & Bindable<Y>> extends PluralAttributeProxy<X, Set<Y>, Y, A> implements SetAttribute<X, Y>, PseudoAttribute {

    private final Set<Y> value;

    public LiteralSetAttribute(Set<Y> value) {
        super(null, CollectionType.SET, null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Set<Y>> getJavaType() {
        return (Class<Set<Y>>)(Object)SortedSet.class;
    }

    @Override
    public Expression<?> getSelectionForQuery(CriteriaBuilder cb, Path<?> currentSelection) {
        return cb.literal(PseudoAttribute.QUERY_PLACEHOLDER);
    }
    
    @Override
    public Object getValueToReplaceResult(Object resultFromDb) {
        if (!PseudoAttribute.QUERY_PLACEHOLDER.equals(resultFromDb)) {
            throw new IllegalStateException("Literal attribute placeholder expected, but was: " + resultFromDb);
        }
        return value;
    }
}