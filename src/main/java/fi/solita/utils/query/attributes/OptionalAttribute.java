package fi.solita.utils.query.attributes;

import java.util.Optional;

import jakarta.persistence.metamodel.SingularAttribute;

public interface OptionalAttribute {
    SingularAttribute<?,?> getAttribute();
    
    public static class Constructors {
        public static <X,Y> SingularAttribute<X,Optional<Y>> optional(SingularAttribute<? extends X, Y> attribute) {
            return new OptionalAttributeImpl<X, Y>(attribute);
        }
    }
}

class OptionalAttributeImpl<X,Y> extends SingularAttributeProxy<X,Optional<Y>> implements OptionalAttribute {
    @SuppressWarnings("unchecked")
    public OptionalAttributeImpl(SingularAttribute<? extends X, Y> attribute) {
        super((SingularAttribute<X, Optional<Y>>) attribute);
    }
    
    @Override
    public SingularAttribute<?, ?> getAttribute() {
        return proxyTarget;
    }
}