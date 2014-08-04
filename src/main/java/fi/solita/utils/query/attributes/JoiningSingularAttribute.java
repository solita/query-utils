package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.FunctionalA.last;

import java.util.List;

import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Predicate;

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

    @Override
    public boolean isOptional() {
        return exists(new Predicate<SingularAttribute<?,?>>() {
            @Override
            public boolean accept(SingularAttribute<?, ?> candidate) {
                return candidate.isOptional();
            }
        }, attributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ManagedType<E> getDeclaringType() {
        return (ManagedType<E>) head(attributes).getDeclaringType();
    }

    @Override
    public String toString() {
        return JoiningAttribute.Constructors.joiningAttributeToString(this);
    }
}