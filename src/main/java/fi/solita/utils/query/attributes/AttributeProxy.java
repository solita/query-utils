package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.io.Serializable;
import java.lang.reflect.Member;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import fi.solita.utils.functional.Option;

public abstract class AttributeProxy {
    @SuppressWarnings("unchecked")
    public static <T> Option<T> unwrap(Class<T> type, Attribute<?,?> attribute) {
        if (type.isInstance(attribute)) {
            return Some((T)attribute);
        }
        if (attribute instanceof AbstractAttributeProxy) {
            if (((AbstractAttributeProxy<?,?,?>) attribute).proxyTarget == null) {
                return None();
            }
            return unwrap(type, ((AbstractAttributeProxy<?,?,?>) attribute).proxyTarget);
        }
        return None();
    }
}

abstract class AbstractAttributeProxy<X,Y,T extends Attribute<X, Y>> implements Attribute<X, Y>, Serializable {
    protected final T proxyTarget;

    AbstractAttributeProxy(T proxyTarget) {
        this.proxyTarget = proxyTarget;
    }

    @Override
    public String getName() {
        return proxyTarget.getName();
    }

    @Override
    public PersistentAttributeType getPersistentAttributeType() {
        return proxyTarget.getPersistentAttributeType();
    }

    @Override
    public ManagedType<X> getDeclaringType() {
        return proxyTarget.getDeclaringType();
    }

    @Override
    public Class<Y> getJavaType() {
        return proxyTarget.getJavaType();
    }

    @Override
    public Member getJavaMember() {
        return proxyTarget.getJavaMember();
    }

    @Override
    public boolean isAssociation() {
        return proxyTarget.isAssociation();
    }

    @Override
    public boolean isCollection() {
        return proxyTarget.isCollection();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (proxyTarget == null ? "?" : getDeclaringType().getJavaType().getSimpleName() + "." + getName()) + ")";
    }
}

abstract class BindableAttributeProxy<X,Y,R,T extends Attribute<X, Y> & Bindable<R>> extends AbstractAttributeProxy<X,Y,T> implements Bindable<R> {
    BindableAttributeProxy(T proxyTarget) {
        super(proxyTarget);
    }

    @Override
    public BindableType getBindableType() {
        return proxyTarget.getBindableType();
    }

    @Override
    public Class<R> getBindableJavaType() {
        return proxyTarget.getBindableJavaType();
    }
}


abstract class SingularAttributeProxy<X,Y> extends BindableAttributeProxy<X,Y,Y,SingularAttribute<X,Y>> implements SingularAttribute<X,Y> {

    SingularAttributeProxy(SingularAttribute<X,Y> proxyTarget) {
        super(proxyTarget);
    }

    @Override
    public boolean isId() {
        return proxyTarget.isId();
    }

    @Override
    public boolean isVersion() {
        return proxyTarget.isVersion();
    }

    @Override
    public boolean isOptional() {
        return proxyTarget.isOptional();
    }

    @Override
    public Type<Y> getType() {
        return proxyTarget.getType();
    }
    
    @Override
    public boolean isCollection() {
        return false;
    }

}

abstract class PluralAttributeProxy<X,C,Y> extends BindableAttributeProxy<X,C,Y,PluralAttribute<X,C,Y>> implements PluralAttribute<X,C,Y> {

    PluralAttributeProxy(PluralAttribute<X,C,Y> proxyTarget) {
        super(proxyTarget);
    }

    @Override
    public CollectionType getCollectionType() {
        return proxyTarget.getCollectionType();
    }

    @Override
    public Type<Y> getElementType() {
        return proxyTarget.getElementType();
    }

    @Override
    public boolean isCollection() {
        return true;
    }
}
