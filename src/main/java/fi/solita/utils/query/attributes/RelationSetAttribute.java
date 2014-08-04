package fi.solita.utils.query.attributes;

import java.util.Set;

import javax.persistence.metamodel.SetAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

class RelationSetAttribute<E, R> extends PluralAttributeProxy<E, Set<R>, R> implements SetAttribute<E,R>, AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationSetAttribute(SetAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((SetAttribute<E, R>) attribute);
        this.constructor = (MetaJpaConstructor<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}