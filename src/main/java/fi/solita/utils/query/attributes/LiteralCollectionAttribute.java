package fi.solita.utils.query.attributes;

import java.util.Collection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;


class LiteralCollectionAttribute<X, Y, A extends Attribute<X, Collection<Y>> & Bindable<Y>> extends PluralAttributeProxy<X, Collection<Y>, Y, A> implements CollectionAttribute<X, Y>, PseudoAttribute {

    private final Collection<Y> value;

    public LiteralCollectionAttribute(Collection<Y> value) {
        super(null, CollectionType.COLLECTION, null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Collection<Y>> getJavaType() {
        return (Class<Collection<Y>>)(Object)Collection.class;
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