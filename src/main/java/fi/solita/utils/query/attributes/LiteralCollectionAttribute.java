package fi.solita.utils.query.attributes;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.CollectionAttribute;


class LiteralCollectionAttribute<X, Y> extends PluralAttributeProxy<X, Collection<Y>, Y> implements CollectionAttribute<X, Y>, PseudoAttribute {

    private final Collection<Y> value;

    public LiteralCollectionAttribute(Collection<Y> value) {
        super(null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Collection<Y>> getJavaType() {
        return (Class<Collection<Y>>) value.getClass();
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