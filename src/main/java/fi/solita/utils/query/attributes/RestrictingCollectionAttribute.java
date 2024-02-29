package fi.solita.utils.query.attributes;

import java.util.Collection;
import java.util.List;


class RestrictingCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends PluralAttributeProxy<E,Collection<R>,R,A> implements CollectionAttribute<E,R>, RestrictingAttribute {
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
    
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
