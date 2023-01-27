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
    
    public static <T> boolean canUnwrap(Class<T> type, Attribute<?,?> attribute) {
        if (type.isInstance(attribute)) {
            return true;
        }
        if (attribute instanceof AbstractAttributeProxy) {
            if (((AbstractAttributeProxy<?,?,?>) attribute).proxyTarget == null) {
                return false;
            }
            return canUnwrap(type, ((AbstractAttributeProxy<?,?,?>) attribute).proxyTarget);
        }
        return false;
    }

}

abstract class AbstractAttributeProxy<X,Y,T extends Attribute<X, Y>> implements Attribute<X, Y>, Serializable {
    protected final T proxyTarget;

    AbstractAttributeProxy(T proxyTarget) {
        this.proxyTarget = proxyTarget;
    }

    @Override
    public String getName() {
        return proxyTarget == null ? null : proxyTarget.getName();
    }

    @Override
    public PersistentAttributeType getPersistentAttributeType() {
        return proxyTarget == null ? null :  proxyTarget.getPersistentAttributeType();
    }

    @Override
    public ManagedType<X> getDeclaringType() {
        return proxyTarget == null ? null :  proxyTarget.getDeclaringType();
    }

    @Override
    public Class<Y> getJavaType() {
        return proxyTarget == null ? null :  proxyTarget.getJavaType();
    }

    @Override
    public Member getJavaMember() {
        return proxyTarget == null ? null :  proxyTarget.getJavaMember();
    }

    @Override
    public boolean isAssociation() {
        return proxyTarget == null ? null :  proxyTarget.isAssociation();
    }

    @Override
    public boolean isCollection() {
        return proxyTarget == null ? null :  proxyTarget.isCollection();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (proxyTarget == null ? "?" : (getDeclaringType() == null ? "?" : getDeclaringType().getJavaType().getSimpleName()) + "." + getName()) + ")";
    }
}

abstract class BindableAttributeProxy<X,Y,R,T extends Attribute<X, Y> & Bindable<R>> extends AbstractAttributeProxy<X,Y,T> implements Bindable<R> {
    BindableAttributeProxy(T proxyTarget) {
        super(proxyTarget);
    }

    @Override
    public BindableType getBindableType() {
        return proxyTarget == null ? null :  proxyTarget.getBindableType();
    }

    @Override
    public Class<R> getBindableJavaType() {
        return proxyTarget == null ? null :  proxyTarget.getBindableJavaType();
    }
}


abstract class SingularAttributeProxy<X,Y> extends BindableAttributeProxy<X,Y,Y,SingularAttribute<X,Y>> implements SingularAttribute<X,Y> {

    SingularAttributeProxy(SingularAttribute<X,Y> proxyTarget) {
        super(proxyTarget);
    }

    @Override
    public boolean isId() {
        return proxyTarget == null ? null :  proxyTarget.isId();
    }

    @Override
    public boolean isVersion() {
        return proxyTarget == null ? null :  proxyTarget.isVersion();
    }

    @Override
    public boolean isOptional() {
        return proxyTarget == null ? null :  proxyTarget.isOptional();
    }

    @Override
    public Type<Y> getType() {
        return proxyTarget == null ? null :  proxyTarget.getType();
    }
    
    @Override
    public boolean isCollection() {
        return false;
    }

}

abstract class PluralAttributeProxy<X,C,Y,A extends Attribute<X,C> & Bindable<Y>> extends BindableAttributeProxy<X,C,Y,A> implements PluralAttribute<X,C,Y> {

    private final CollectionType ct;
    private final Type<Y> et;

    PluralAttributeProxy(A proxyTarget, CollectionType ct, Type<Y> et) {
        super(proxyTarget);
        this.ct = ct;
        this.et = et;
    }

    @Override
    public CollectionType getCollectionType() {
        return ct;
    }

    @Override
    public Type<Y> getElementType() {
        return et;
    }

    @Override
    public boolean isCollection() {
        return true;
    }
}
