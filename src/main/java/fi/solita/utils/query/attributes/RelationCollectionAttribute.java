package fi.solita.utils.query.attributes;

import java.util.Collection;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.meta.MetaJpaConstructor;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.Type;

class RelationCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends CollectionAttributeProxy<E, R, A> implements AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity<?>, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity<?>> RelationCollectionAttribute(CollectionAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((A)(Object)attribute, (Type<R>)(Object)attribute.getElementType());
        this.constructor = (MetaJpaConstructor<? extends IEntity<?>, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity<?>, R, ?> getConstructor() {
        return constructor;
    }
}