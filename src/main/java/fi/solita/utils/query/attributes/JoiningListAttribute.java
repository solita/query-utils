package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.exists;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;

import java.util.List;

import org.hibernate.metamodel.model.domain.ListPersistentAttribute;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.query.sqm.SqmPathSource;

import fi.solita.utils.query.QueryUtils;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ListAttribute;

@SuppressWarnings("unchecked")
class JoiningListAttribute<E, R, A extends Attribute<E, List<R>> & Bindable<R>> extends PluralAttributeProxy<E,List<R>,R,A> implements ListAttribute<E,R>, JoiningAttribute, ListPersistentAttribute<E,R> {
    
    private final List<? extends Attribute<?, ?>> attributes;

    JoiningListAttribute(Iterable<Attribute<?,?>> attrs) {
        super((A) last(attrs), CollectionType.LIST, QueryUtils.<R>getElementType(last(attrs)));
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
    
    @Override
    public SqmPathSource<Integer> getIndexPathSource() {
        return proxyTarget == null ? null :  ((ListPersistentAttribute<E,E>)(Object)proxyTarget).getIndexPathSource();
    }
}
