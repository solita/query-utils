package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;

import java.lang.reflect.Member;
import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import fi.solita.utils.query.IEntity;

@SuppressWarnings("unchecked")
class JoiningListAttribute<E extends IEntity, R> implements ListAttribute<E,R>, JoiningAttribute {
    
    private final List<? extends Attribute<?, ?>> attributes;

    <A extends Attribute<?,?> & Bindable<?>> JoiningListAttribute(Iterable<A> attrs) {
        attributes = newList(attrs);
    }
    
    public List<? extends Attribute<?, ?>> getAttributes() {
        return attributes;
    }
    
    @Override
    public ManagedType<E> getDeclaringType() {
        return (ManagedType<E>) last(attributes).getDeclaringType();
    }
    
    @Override
    public Class<R> getBindableJavaType() {
        return ((Bindable<R>)last(attributes)).getBindableJavaType();
    }
    
    @Override
    public PersistentAttributeType getPersistentAttributeType() {
        if (head(attributes) instanceof ListAttribute) {
            return PersistentAttributeType.ONE_TO_MANY;
        }
        return last(attributes).getPersistentAttributeType();
    }
    
    @Override
    public Type<R> getElementType() {
        Attribute<?, ?> last = last(attributes);
        if (last instanceof PluralAttribute) {
            return ((PluralAttribute<?,?,R>) last).getElementType();
        } else {
            return ((SingularAttribute<?,R>)last).getType();
        }
    }

    @Override
    public CollectionType getCollectionType() {
        return CollectionType.LIST;
    }

    @Override
    public String getName() {
        return last(attributes).getName();
    }

    @Override
    public Class<List<R>> getJavaType() {
        return (Class<List<R>>) last(attributes).getJavaType();
    }

    @Override
    public Member getJavaMember() {
        return last(attributes).getJavaMember();
    }

    @Override
    public boolean isAssociation() {
        return true;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public BindableType getBindableType() {
        return BindableType.PLURAL_ATTRIBUTE;
    }
}
