package fi.solita.utils.query.attributes;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.metamodel.AttributeClassification;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.metamodel.model.domain.DomainType;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.PathSource;
import org.hibernate.metamodel.model.domain.PersistentAttribute;
import org.hibernate.metamodel.model.domain.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.SimpleDomainType;
import org.hibernate.metamodel.model.domain.SingularPersistentAttribute;
import org.hibernate.query.hql.spi.SqmCreationState;
import org.hibernate.query.sqm.SqmBindableType;
import org.hibernate.query.sqm.SqmExpressible;
import org.hibernate.query.sqm.SqmPathSource;
import org.hibernate.query.sqm.tree.SqmExpressibleAccessor;
import org.hibernate.query.sqm.tree.SqmJoinType;
import org.hibernate.query.sqm.tree.domain.SqmBagPersistentAttribute;
import org.hibernate.query.sqm.tree.domain.SqmDomainType;
import org.hibernate.query.sqm.tree.domain.SqmListPersistentAttribute;
import org.hibernate.query.sqm.tree.domain.SqmPath;
import org.hibernate.query.sqm.tree.domain.SqmPluralPersistentAttribute;
import org.hibernate.query.sqm.tree.domain.SqmSetPersistentAttribute;
import org.hibernate.query.sqm.tree.domain.SqmSingularPersistentAttribute;
import org.hibernate.query.sqm.tree.from.SqmFrom;
import org.hibernate.query.sqm.tree.from.SqmJoin;
import org.hibernate.spi.NavigablePath;
import org.hibernate.type.BindableType;
import org.hibernate.type.BindingContext;
import org.hibernate.type.descriptor.java.JavaType;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

public abstract class AttributeProxy {
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> unwrap(Class<T> type, Attribute<?,?> attribute) {
        if (type.isInstance(attribute)) {
            return Optional.of((T)attribute);
        }
        if (attribute instanceof AbstractAttributeProxy) {
            if (((AbstractAttributeProxy<?,?,?>) attribute).proxyTarget == null) {
                return Optional.empty();
            }
            return unwrap(type, ((AbstractAttributeProxy<?,?,?>) attribute).proxyTarget);
        }
        return Optional.empty();
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

abstract class BindableAttributeProxy<X,R,Y,T extends Attribute<X, R> & Bindable<Y>> extends AbstractAttributeProxy<X,R,T> implements Bindable<Y>, BindableType<R>, SqmExpressibleAccessor<Y>, SqmExpressible<Y> {
    BindableAttributeProxy(T proxyTarget) {
        super(proxyTarget);
    }

    @Override
    public BindableType getBindableType() {
        return proxyTarget == null ? null :  proxyTarget.getBindableType();
    }

    @Override
    public Class<Y> getBindableJavaType() {
        return proxyTarget == null ? null :  proxyTarget.getBindableJavaType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmBindableType<R> resolveExpressible(BindingContext sessionFactory) {
        return proxyTarget == null ? null :  ((org.hibernate.type.BindableType<R>)proxyTarget).resolveExpressible(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmBindableType<Y> getExpressible() {
        return proxyTarget == null ? null :  ((SqmExpressibleAccessor<Y>)proxyTarget).getExpressible();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JavaType<Y> getExpressibleJavaType() {
        return proxyTarget == null ? null :  ((SqmExpressible<Y>)proxyTarget).getExpressibleJavaType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmDomainType<Y> getSqmType() {
        return proxyTarget == null ? null :  ((SqmExpressible<Y>)proxyTarget).getSqmType();
    }

    @Override
    public PersistenceType getPersistenceType() {
        return proxyTarget == null ? null : ((Type<?>) proxyTarget).getPersistenceType();
    }
}


abstract class SingularAttributeProxy<X,Y> extends BindableAttributeProxy<X,Y,Y,SingularAttribute<X,Y>> implements SingularAttribute<X,Y>, SqmSingularPersistentAttribute<X,Y> {

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
        return proxyTarget == null ? null :  (SqmPathSource<?>) ((SingularPersistentAttribute<X,Y>) proxyTarget).findSubPathSource(name);
    }
    
    @Override
    public DomainType<Y> getValueGraphType() {
        return getType();
    }

    @Override
    public SqmDomainType<Y> getPathType() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getPathType();
    }

    @Override
    public SqmPathSource<?> findSubPathSource(String name, boolean includeSubtypes) {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).findSubPathSource(name, includeSubtypes);
    }
    
    @Override
    public SqmPath<Y> createSqmPath(SqmPath<?> lhs, SqmPathSource<?> intermediatePathSource) {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).createSqmPath(lhs, intermediatePathSource);
    }

    @Override
    public JavaType<Y> getExpressibleJavaType() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getExpressibleJavaType();
    }

    @Override
    public SqmJoin<X, Y> createSqmJoin(SqmFrom<?, X> lhs, SqmJoinType joinType, String alias, boolean fetched, SqmCreationState creationState) {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).createSqmJoin(lhs, joinType, alias, fetched, creationState);
    }

    @Override
    public SqmDomainType<Y> getSqmType() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getSqmType();
    }
    
    @Override
    public JavaType<Y> getNodeJavaType() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getNodeJavaType();
    }
    
    @Override
    public String getTypeName() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getTypeName();
    }
    
    @Override
    public JavaType<?> getRelationalJavaType() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getRelationalJavaType();
    }

    @Override
    public SqmPathSource<Y> getSqmPathSource() {
        return proxyTarget == null ? null :  ((SqmSingularPersistentAttribute<X,Y>) proxyTarget).getSqmPathSource();
    }
}

abstract class PluralAttributeProxy<X,C,Y,A extends Attribute<X,C> & Bindable<Y>> extends BindableAttributeProxy<X,C,Y,A> implements PluralAttribute<X,C,Y>, SqmPluralPersistentAttribute<X,C,Y> {

    private final CollectionType ct;
    private final SimpleDomainType<Y> et;

    PluralAttributeProxy(A proxyTarget, CollectionType ct, Type<Y> et) {
        super(proxyTarget);
        this.ct = ct;
        this.et = (SimpleDomainType<Y>)et;
    }
    
    @Override
    public SimpleDomainType<Y> getElementType() {
        return et;
    }
    
    @Override
    public CollectionType getCollectionType() {
        return ct;
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
    public SqmPathSource<?> findSubPathSource(String name) {
        return proxyTarget == null ? null :  (SqmPathSource<?>) ((PluralPersistentAttribute<X,C,Y>) proxyTarget).findSubPathSource(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CollectionClassification getCollectionClassification() {
        return proxyTarget == null ? null :  ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getCollectionClassification();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<Y> getElementPathSource() {
        return proxyTarget == null ? null :  (SqmPathSource<Y>) ((PluralPersistentAttribute<X,C,Y>) proxyTarget).getElementPathSource();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmDomainType<Y> getPathType() {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).getPathType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> findSubPathSource(String name, boolean includeSubtypes) {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).findSubPathSource(name, includeSubtypes);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPath<Y> createSqmPath(SqmPath<?> lhs, SqmPathSource<?> intermediatePathSource) {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).createSqmPath(lhs, intermediatePathSource);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JavaType<Y> getExpressibleJavaType() {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).getExpressibleJavaType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmJoin<X, Y> createSqmJoin(SqmFrom<?, X> lhs, SqmJoinType joinType, String alias, boolean fetched, SqmCreationState creationState) {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).createSqmJoin(lhs, joinType, alias, fetched, creationState);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public JavaType<Y> getNodeJavaType() {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).getNodeJavaType();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String getTypeName() {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).getTypeName();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public JavaType<?> getRelationalJavaType() {
        return proxyTarget == null ? null :  ((SqmPluralPersistentAttribute<X,C,Y>) proxyTarget).getRelationalJavaType();
    }
}

abstract class CollectionAttributeProxy<X,Y,A extends Attribute<X,Collection<Y>> & Bindable<Y>> extends PluralAttributeProxy<X,Collection<Y>,Y,A> implements SqmBagPersistentAttribute<X,Y> {

    CollectionAttributeProxy(A proxyTarget, Type<Y> et) {
        super(proxyTarget, PluralAttribute.CollectionType.COLLECTION, et);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public PathSource<?> getIndexPathSource() {
        return proxyTarget == null ? null :  ((SqmBagPersistentAttribute<X,Y>) proxyTarget).getIndexPathSource();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean isGeneric() {
        return proxyTarget == null ? null :  ((SqmBagPersistentAttribute<X,Y>) proxyTarget).isGeneric();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public NavigablePath createNavigablePath(SqmPath<?> parent, String alias) {
        return proxyTarget == null ? null :  ((SqmBagPersistentAttribute<X,Y>) proxyTarget).createNavigablePath(parent, alias);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getIntermediatePathSource(SqmPathSource<?> pathSource) {
        return proxyTarget == null ? null :  ((SqmBagPersistentAttribute<X,Y>) proxyTarget).getIntermediatePathSource(pathSource);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getSubPathSource(String name) {
        return proxyTarget == null ? null :  ((SqmBagPersistentAttribute<X,Y>) proxyTarget).getSubPathSource(name);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getSubPathSource(String name, boolean subtypes) {
        return proxyTarget == null ? null :  ((SqmBagPersistentAttribute<X,Y>) proxyTarget).getSubPathSource(name, subtypes);
    }
}

abstract class SetAttributeProxy<X,Y,A extends Attribute<X,Set<Y>> & Bindable<Y>> extends PluralAttributeProxy<X,Set<Y>,Y,A> implements SqmSetPersistentAttribute<X,Y> {

    SetAttributeProxy(A proxyTarget, Type<Y> et) {
        super(proxyTarget, PluralAttribute.CollectionType.SET, et);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public PathSource<?> getIndexPathSource() {
        return proxyTarget == null ? null :  ((SqmSetPersistentAttribute<X,Y>) proxyTarget).getIndexPathSource();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean isGeneric() {
        return proxyTarget == null ? null :  ((SqmSetPersistentAttribute<X,Y>) proxyTarget).isGeneric();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public NavigablePath createNavigablePath(SqmPath<?> parent, String alias) {
        return proxyTarget == null ? null :  ((SqmSetPersistentAttribute<X,Y>) proxyTarget).createNavigablePath(parent, alias);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getIntermediatePathSource(SqmPathSource<?> pathSource) {
        return proxyTarget == null ? null :  ((SqmSetPersistentAttribute<X,Y>) proxyTarget).getIntermediatePathSource(pathSource);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getSubPathSource(String name) {
        return proxyTarget == null ? null :  ((SqmSetPersistentAttribute<X,Y>) proxyTarget).getSubPathSource(name);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getSubPathSource(String name, boolean subtypes) {
        return proxyTarget == null ? null :  ((SqmSetPersistentAttribute<X,Y>) proxyTarget).getSubPathSource(name, subtypes);
    }
}

abstract class ListAttributeProxy<X,Y,A extends Attribute<X,List<Y>> & Bindable<Y>> extends PluralAttributeProxy<X,List<Y>,Y,A> implements SqmListPersistentAttribute<X,Y> {

    ListAttributeProxy(A proxyTarget, Type<Y> et) {
        super(proxyTarget, PluralAttribute.CollectionType.LIST, et);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean isGeneric() {
        return proxyTarget == null ? null :  ((SqmListPersistentAttribute<X,Y>) proxyTarget).isGeneric();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public NavigablePath createNavigablePath(SqmPath<?> parent, String alias) {
        return proxyTarget == null ? null :  ((SqmListPersistentAttribute<X,Y>) proxyTarget).createNavigablePath(parent, alias);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getIntermediatePathSource(SqmPathSource<?> pathSource) {
        return proxyTarget == null ? null :  ((SqmListPersistentAttribute<X,Y>) proxyTarget).getIntermediatePathSource(pathSource);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getSubPathSource(String name) {
        return proxyTarget == null ? null :  ((SqmListPersistentAttribute<X,Y>) proxyTarget).getSubPathSource(name);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<?> getSubPathSource(String name, boolean subtypes) {
        return proxyTarget == null ? null :  ((SqmListPersistentAttribute<X,Y>) proxyTarget).getSubPathSource(name, subtypes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PathSource<Integer> getIndexPathSource() {
        return proxyTarget == null ? null :  ((SqmListPersistentAttribute<X,Y>) proxyTarget).getIndexPathSource();
    }
}
