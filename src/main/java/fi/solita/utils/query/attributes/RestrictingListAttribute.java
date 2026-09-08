package fi.solita.utils.query.attributes;

import java.util.List;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ListAttribute;

class RestrictingListAttribute<E, R, A extends Attribute<E, List<R>> & Bindable<R>> extends ListAttributeProxy<E, R, A> implements RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingListAttribute(ListAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, attribute.getElementType());
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
