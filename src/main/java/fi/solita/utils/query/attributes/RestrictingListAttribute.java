package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ListAttribute;

class RestrictingListAttribute<E, R, A extends Attribute<E, List<R>> & Bindable<R>> extends PluralAttributeProxy<E, List<R>, R, A> implements ListAttribute<E,R>, RestrictingAttribute {
    
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
