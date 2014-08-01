package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;

class RestrictingListAttribute<E, R> extends PluralAttributeProxy<E, List<R>, R> implements ListAttribute<E,R>, RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    RestrictingListAttribute(ListAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super(attribute);
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
