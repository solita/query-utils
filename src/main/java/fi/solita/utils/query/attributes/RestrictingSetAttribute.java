package fi.solita.utils.query.attributes;

import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SetAttribute;

class RestrictingSetAttribute<E, R, A extends Attribute<E, Set<R>> & Bindable<R>> extends PluralAttributeProxy<E, Set<R>, R, A> implements SetAttribute<E,R>, RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingSetAttribute(SetAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, CollectionType.SET, attribute.getElementType());
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
