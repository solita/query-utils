package fi.solita.utils.query.attributes;

import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

class RelationSingularAttribute<E, R> extends SingularAttributeProxy<E, R> implements AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationSingularAttribute(SingularAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((SingularAttribute<E, R>) attribute);
        this.constructor = (MetaJpaConstructor<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}