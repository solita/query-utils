package fi.solita.utils.query.attributes;

import java.util.List;


class RestrictingListAttribute<E, R, A extends Attribute<E, List<R>> & Bindable<R>> extends PluralAttributeProxy<E, List<R>, R, A> implements ListAttribute<E,R>, RestrictingAttribute {
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ListAttribute;
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingListAttribute(ListAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, CollectionType.LIST, attribute.getElementType());
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
