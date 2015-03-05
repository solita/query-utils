package fi.solita.utils.query.attributes;

import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.Type;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.meta.MetaJpaConstructor;

class RelationSetAttribute<E, R, A extends Attribute<E, Set<R>> & Bindable<R>> extends PluralAttributeProxy<E, Set<R>, R, A> implements SetAttribute<E,R>, AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity<?>, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity<?>> RelationSetAttribute(SetAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((A)(Object)attribute, CollectionType.SET, (Type<R>)attribute.getElementType());
        this.constructor = (MetaJpaConstructor<? extends IEntity<?>, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity<?>, R, ?> getConstructor() {
        return constructor;
    }
}