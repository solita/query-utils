package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;

import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;

@SuppressWarnings("unchecked")
class JoiningSetAttribute<E, R> extends PluralAttributeProxy<E,Set<R>,R> implements SetAttribute<E,R>, JoiningAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    JoiningSetAttribute(Iterable<Attribute<?,?>> attrs) {
        super((PluralAttribute<E, Set<R>, R>) last(attrs));
        attributes = newList(attrs);
    }
    
    public List<? extends Attribute<?, ?>> getAttributes() {
        return attributes;
    }
    
    @Override
    public ManagedType<E> getDeclaringType() {
        return (ManagedType<E>) head(attributes).getDeclaringType();
    }
    
    @Override
    public CollectionType getCollectionType() {
        return CollectionType.SET;
    }
    
    @Override
    public Class<Set<R>> getJavaType() {
        return (Class<Set<R>>)(Object)Set.class;
    }
    
    @Override
    public boolean isCollection() {
        return true;
    }
    
    @Override
    public String toString() {
        return JoiningAttribute.Constructors.joiningAttributeToString(this);
    }
}
