package fi.solita.utils.query.attributes;

import java.util.Set;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.meta.MetaJpaConstructor;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.Type;

class RelationSetAttribute<E, R, A extends Attribute<E, Set<R>> & Bindable<R>> extends SetAttributeProxy<E, R, A> implements AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity<?>, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity<?>> RelationSetAttribute(SetAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((A)(Object)attribute, (Type<R>)(Object)attribute.getElementType());
        this.constructor = (MetaJpaConstructor<? extends IEntity<?>, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity<?>, R, ?> getConstructor() {
        return constructor;
    }
}