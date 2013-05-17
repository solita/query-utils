package fi.solita.utils.query.attributes;

import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;

public class OptionalAttribute<X,Y> extends SingularAttributeProxy<X,Option<Y>> {
    @SuppressWarnings("unchecked")
    public OptionalAttribute(SingularAttribute<? extends X, Y> proxyTarget) {
        super((SingularAttribute<X, Option<Y>>) proxyTarget);
    }
}