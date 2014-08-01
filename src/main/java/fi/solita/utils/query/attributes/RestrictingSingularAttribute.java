package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SingularAttribute;

class RestrictingSingularAttribute<E,R> extends SingularAttributeProxy<E,R> implements RestrictingAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    RestrictingSingularAttribute(SingularAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super(attribute);
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }
}