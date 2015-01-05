package fi.solita.utils.query.attributes;

import java.util.Collection;
import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.PluralAttribute;

class RestrictingCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends PluralAttributeProxy<E,Collection<R>,R,A> implements CollectionAttribute<E,R>, RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingCollectionAttribute(PluralAttribute<E,Collection<R>,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, CollectionType.COLLECTION, attribute.getElementType());
        attributes = restrictionChain;
    }
    
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
