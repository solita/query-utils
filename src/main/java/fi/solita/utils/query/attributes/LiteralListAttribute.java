package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ListAttribute;


class LiteralListAttribute<X, Y, A extends Attribute<X, List<Y>> & Bindable<Y>> extends PluralAttributeProxy<X, List<Y>, Y, A> implements ListAttribute<X, Y>, PseudoAttribute {

    private final List<Y> value;

    public LiteralListAttribute(List<Y> value) {
        super(null, CollectionType.LIST, null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<Y>> getJavaType() {
        return (Class<List<Y>>)(Object)List.class;
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