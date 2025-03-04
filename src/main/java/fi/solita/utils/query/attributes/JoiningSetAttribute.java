package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;

import java.util.List;
import java.util.Set;

import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.SetPersistentAttribute;

import fi.solita.utils.query.QueryUtils;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.SetAttribute;

@SuppressWarnings("unchecked")
class JoiningSetAttribute<E, R, A extends Attribute<E, Set<R>> & Bindable<R>> extends PluralAttributeProxy<E,Set<R>,R,A> implements SetAttribute<E,R>, JoiningAttribute, SetPersistentAttribute<E,R> {
    
    private final List<? extends Attribute<?, ?>> attributes;

    JoiningSetAttribute(Iterable<Attribute<?,?>> attrs) {
        super((A) last(attrs), CollectionType.SET, QueryUtils.<R>getElementType(last(attrs)));
        if (exists(JoiningAttribute.illegalContainedAttribute, attrs)) {
            throw new IllegalArgumentException("Cannot use attributes of types defined in JoiningAttribute.illegalContainedAttribute within JoiningAttributes!");
        }
        attributes = newList(attrs);
    }
    
    public List<? extends Attribute<?, ?>> getAttributes() {
        return attributes;
    }
    
    @Override
    public ManagedDomainType<E> getDeclaringType() {
        return (ManagedDomainType<E>) head(attributes).getDeclaringType();
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
