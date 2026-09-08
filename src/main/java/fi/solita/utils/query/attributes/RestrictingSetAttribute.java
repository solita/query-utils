package fi.solita.utils.query.attributes;

import java.util.List;
import java.util.Set;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.SetAttribute;

class RestrictingSetAttribute<E, R, A extends Attribute<E, Set<R>> & Bindable<R>> extends SetAttributeProxy<E, R, A> implements RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingSetAttribute(SetAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, attribute.getElementType());
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
