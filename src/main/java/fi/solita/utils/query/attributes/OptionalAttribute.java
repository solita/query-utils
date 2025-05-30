package fi.solita.utils.query.attributes;

import jakarta.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;

public interface OptionalAttribute {
    SingularAttribute<?,?> getAttribute();
    
    public static class Constructors {
        public static <X,Y> SingularAttribute<X,Option<Y>> optional(SingularAttribute<? extends X, Y> attribute) {
            return new OptionalAttributeImpl<X, Y>(attribute);
        }
    }
}

class OptionalAttributeImpl<X,Y> extends SingularAttributeProxy<X,Option<Y>> implements OptionalAttribute {
    @SuppressWarnings("unchecked")
    public OptionalAttributeImpl(SingularAttribute<? extends X, Y> attribute) {
        super((SingularAttribute<X, Option<Y>>) attribute);
    }
    
    @Override
    public SingularAttribute<?, ?> getAttribute() {
        return proxyTarget;
    }
}