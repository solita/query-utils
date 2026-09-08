package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;

import java.util.Collection;
import java.util.List;

import org.hibernate.metamodel.model.domain.ManagedDomainType;

import fi.solita.utils.query.QueryUtils;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;

@SuppressWarnings("unchecked")
class JoiningCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends CollectionAttributeProxy<E,R,A> implements JoiningAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    JoiningCollectionAttribute(Iterable<Attribute<?,?>> attrs) {
        super((A) last(attrs), QueryUtils.<R>getElementType(last(attrs)));
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
    public Class<Collection<R>> getJavaType() {
        return (Class<Collection<R>>)(Object)Collection.class;
    }
    
    @Override
    public String toString() {
        return JoiningAttribute.Constructors.joiningAttributeToString(this);
    }
}
