package fi.solita.utils.query.attributes;

import java.util.Collection;
import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.PluralAttribute;

class RestrictingCollectionAttribute<E, R> extends PluralAttributeProxy<E,Collection<R>,R> implements CollectionAttribute<E,R>, RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    RestrictingCollectionAttribute(PluralAttribute<E,Collection<R>,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super(attribute);
        attributes = restrictionChain;
    }
    
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
