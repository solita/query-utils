package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.last;

import java.util.List;

import javax.persistence.metamodel.SingularAttribute;

class JoiningSingularAttribute<E,R> extends SingularAttributeProxy<E,R> implements JoiningAttribute {
    
    private final List<? extends SingularAttribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    public JoiningSingularAttribute(SingularAttribute<?,?>... attrs) {
        super((SingularAttribute<E, R>) last(attrs));
        attributes = newList(attrs);
    }

    public List<? extends SingularAttribute<?, ?>> getAttributes() {
        return attributes;
    }
    
}