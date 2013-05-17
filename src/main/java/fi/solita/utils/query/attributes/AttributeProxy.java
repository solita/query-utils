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

/**
 * Proxy classes for different kinds of Attributes
 *
 */
public abstract class AttributeProxy<X,Y,T extends Attribute<X, Y>> implements Attribute<X, Y>, Serializable {

    @SuppressWarnings("unchecked")
    public static <T> Option<T> unwrap(Attribute<?,?> attribute, Class<T> type) {
        if (type.isInstance(attribute)) {
            return Some((T)attribute);
        }
        if (attribute instanceof AttributeProxy) {
            if (((AttributeProxy<?,?,?>) attribute).proxyTarget == null) {
                return None();
            }
            return unwrap(((AttributeProxy<?,?,?>) attribute).proxyTarget, type);
        }
        return None();
    }

    protected final T proxyTarget;

    AttributeProxy(T proxyTarget) {
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

}

abstract class BindableAttributeProxy<X,Y,R,T extends Attribute<X, Y> & Bindable<R>> extends AttributeProxy<X,Y,T> implements Bindable<R> {

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

