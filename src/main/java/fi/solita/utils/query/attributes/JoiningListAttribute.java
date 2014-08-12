package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

@SuppressWarnings("unchecked")
class JoiningListAttribute<E, R> extends PluralAttributeProxy<E,List<R>,R> implements ListAttribute<E,R>, JoiningAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    JoiningListAttribute(Iterable<Attribute<?,?>> attrs) {
        super((PluralAttribute<E, List<R>, R>) last(attrs));
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
        return CollectionType.LIST;
    }
    
    @Override
    public Class<List<R>> getJavaType() {
        return (Class<List<R>>)(Object)List.class;
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
