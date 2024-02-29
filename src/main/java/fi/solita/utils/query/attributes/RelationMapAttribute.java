package fi.solita.utils.query.attributes;

import java.util.Map;


import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.Type;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.meta.MetaJpaConstructor;

class RelationMapAttribute<E, K, R, A extends Attribute<E, Map<K,R>> & Bindable<R>> extends PluralAttributeProxy<E, Map<K,R>, R, A> implements MapAttribute<E,K,R>, AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity<?>, R, ?> constructor;
    private final Type<K> keyType;
    private final Class<K> keyJavaType;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity<?>> RelationMapAttribute(MapAttribute<? super E, K, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((A)(Object)attribute, CollectionType.MAP, (Type<R>)(Object)attribute.getElementType());
        this.constructor = (MetaJpaConstructor<? extends IEntity<?>, R, ?>) constructor;
        this.keyType = attribute.getKeyType();
        this.keyJavaType = attribute.getKeyJavaType();
    }

    @Override
    public MetaJpaConstructor<? extends IEntity<?>, R, ?> getConstructor() {
        return constructor;
    }

    @Override
    public Class<K> getKeyJavaType() {
        return keyJavaType;
    }

    @Override
    public Type<K> getKeyType() {
        return keyType;
    }
}