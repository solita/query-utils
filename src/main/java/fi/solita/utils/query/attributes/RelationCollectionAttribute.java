package fi.solita.utils.query.attributes;

import java.util.Collection;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.Type;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.meta.MetaJpaConstructor;

class RelationCollectionAttribute<E, R, A extends Attribute<E, Collection<R>> & Bindable<R>> extends PluralAttributeProxy<E, Collection<R>, R, A> implements CollectionAttribute<E,R>, AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity<?>, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity<?>> RelationCollectionAttribute(CollectionAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((A)(Object)attribute, CollectionType.COLLECTION, (Type<R>)attribute.getElementType());
        this.constructor = (MetaJpaConstructor<? extends IEntity<?>, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity<?>, R, ?> getConstructor() {
        return constructor;
    }
}