package fi.solita.utils.query.attributes;

import java.util.Collection;
import java.util.List;

import org.hibernate.metamodel.model.domain.BagPersistentAttribute;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.PluralAttribute;

class RestrictingCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends PluralAttributeProxy<E,Collection<R>,R,A> implements CollectionAttribute<E,R>, RestrictingAttribute, BagPersistentAttribute<E,R> {
    
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
