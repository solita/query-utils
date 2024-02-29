package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.io.Serializable;
import java.lang.reflect.Member;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.AttributeClassification;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.metamodel.model.domain.DomainType;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.PersistentAttribute;
import org.hibernate.metamodel.model.domain.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.SimpleDomainType;
import org.hibernate.metamodel.model.domain.SingularPersistentAttribute;
import org.hibernate.query.BindableType;
import org.hibernate.query.hql.spi.SqmCreationState;
import org.hibernate.query.sqm.SqmExpressible;
import org.hibernate.query.sqm.SqmPathSource;
import org.hibernate.query.sqm.tree.SqmExpressibleAccessor;
import org.hibernate.query.sqm.tree.SqmJoinType;
import org.hibernate.query.sqm.tree.domain.SqmPath;
import org.hibernate.query.sqm.tree.from.SqmFrom;
import org.hibernate.query.sqm.tree.from.SqmJoin;
import org.hibernate.type.descriptor.java.JavaType;

import fi.solita.utils.functional.Option;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

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

abstract class AbstractAttributeProxy<X,Y,T extends Attribute<X, Y>> implements Attribute<X, Y>, Serializable, PersistentAttribute<X, Y> {
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
    public ManagedDomainType<X> getDeclaringType() {
        return proxyTarget == null ? null :  (ManagedDomainType<X>)proxyTarget.getDeclaringType();
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

    @SuppressWarnings("unchecked")
    @Override
    public JavaType<Y> getAttributeJavaType() {
        return proxyTarget == null ? null :  ((PersistentAttribute<X,Y>) proxyTarget).getAttributeJavaType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AttributeClassification getAttributeClassification() {
        return proxyTarget == null ? null :  ((PersistentAttribute<X,Y>) proxyTarget).getAttributeClassification();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DomainType<?> getValueGraphType() {
        return proxyTarget == null ? null :  ((PersistentAttribute<X,Y>) proxyTarget).getValueGraphType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SimpleDomainType<?> getKeyGraphType() {
        return proxyTarget == null ? null :  ((PersistentAttribute<X,Y>) proxyTarget).getKeyGraphType();
    }
}

abstract class BindableAttributeProxy<X,Y,R,T extends Attribute<X, Y> & Bindable<R>> extends AbstractAttributeProxy<X,Y,T> implements Bindable<R>, BindableType<R>, SqmExpressibleAccessor<R>, SqmExpressible<R> {
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

    @SuppressWarnings("unchecked")
    @Override
    public SqmExpressible<R> resolveExpressible(SessionFactoryImplementor sessionFactory) {
        return proxyTarget == null ? null :  ((org.hibernate.query.BindableType<R>)proxyTarget).resolveExpressible(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmExpressible<R> getExpressible() {
        return proxyTarget == null ? null :  ((SqmExpressibleAccessor<R>)proxyTarget).getExpressible();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JavaType<R> getExpressibleJavaType() {
        return proxyTarget == null ? null :  ((SqmExpressible<R>)proxyTarget).getExpressibleJavaType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DomainType<R> getSqmType() {
        return proxyTarget == null ? null :  ((SqmExpressible<R>)proxyTarget).getSqmType();
    }
}


abstract class SingularAttributeProxy<X,Y> extends BindableAttributeProxy<X,Y,Y,SingularAttribute<X,Y>> implements SingularAttribute<X,Y>, SingularPersistentAttribute<X, Y> {

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
    public SimpleDomainType<Y> getType() {
        return proxyTarget == null ? null : (SimpleDomainType<Y>)proxyTarget.getType();
    }
    
    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getPathName() {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).getPathName();
    }

    @Override
    public SqmPathSource<?> findSubPathSource(String name) {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).findSubPathSource(name);
    }

    @Override
    public SqmPath<Y> createSqmPath(SqmPath<?> lhs, SqmPathSource<?> intermediatePathSource) {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).createSqmPath(lhs, intermediatePathSource);
    }

    @Override
    public JavaType<Y> getExpressibleJavaType() {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).getExpressibleJavaType();
    }

    @Override
    public SqmJoin<X, Y> createSqmJoin(SqmFrom<?, X> lhs, SqmJoinType joinType, String alias, boolean fetched, SqmCreationState creationState) {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).createSqmJoin(lhs, joinType, alias, fetched, creationState);
    }

    @Override
    public DomainType<Y> getSqmPathType() {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).getSqmPathType();
    }

    @Override
    public SqmPathSource<Y> getPathSource() {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).getPathSource();
    }

    @Override
    public DomainType<Y> getSqmType() {
        return proxyTarget == null ? null :  ((SingularPersistentAttribute<X,Y>) proxyTarget).getSqmType();
    }
}

abstract class PluralAttributeProxy<X,C,Y,A extends Attribute<X,C> & Bindable<Y>> extends BindableAttributeProxy<X,C,Y,A> implements PluralAttribute<X,C,Y>, PluralPersistentAttribute<X,C,Y> {

    private final CollectionType ct;
    private final SimpleDomainType<Y> et;

    PluralAttributeProxy(A proxyTarget, CollectionType ct, Type<Y> et) {
        super(proxyTarget);
        this.ct = ct;
        this.et = (SimpleDomainType<Y>)et;
    }
    
    @Override
    public CollectionType getCollectionType() {
        return ct;
    }

    @Override
    public SimpleDomainType<Y> getElementType() {
        return et;
    }

    @Override
    public boolean isCollection() {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SimpleDomainType<Y> getValueGraphType() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getValueGraphType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getPathName() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getPathName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DomainType<Y> getSqmPathType() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getSqmPathType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> findSubPathSource(String name) {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).findSubPathSource(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPath<Y> createSqmPath(SqmPath<?> lhs, SqmPathSource<?> intermediatePathSource) {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).createSqmPath(lhs, intermediatePathSource);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JavaType<Y> getExpressibleJavaType() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getExpressibleJavaType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmJoin<X, Y> createSqmJoin(SqmFrom<?, X> lhs, SqmJoinType joinType, String alias, boolean fetched, SqmCreationState creationState) {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).createSqmJoin(lhs, joinType, alias, fetched, creationState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CollectionClassification getCollectionClassification() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getCollectionClassification();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<Y> getElementPathSource() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getElementPathSource();
    }
}
