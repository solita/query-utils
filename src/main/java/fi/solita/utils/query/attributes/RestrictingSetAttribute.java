package fi.solita.utils.query.attributes;

import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SetAttribute;

class RestrictingSetAttribute<E, R> extends PluralAttributeProxy<E, Set<R>, R> implements SetAttribute<E,R>, RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    RestrictingSetAttribute(SetAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super(attribute);
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}
