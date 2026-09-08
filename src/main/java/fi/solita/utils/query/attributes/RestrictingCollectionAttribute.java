package fi.solita.utils.query.attributes;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.PluralAttribute;

class RestrictingCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends CollectionAttributeProxy<E,R,A> implements RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingCollectionAttribute(PluralAttribute<E,Collection<R>,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, attribute.getElementType());
        attributes = restrictionChain;
    }
    
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
