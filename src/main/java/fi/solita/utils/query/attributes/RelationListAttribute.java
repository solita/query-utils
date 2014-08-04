package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.metamodel.ListAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

class RelationListAttribute<E, R> extends PluralAttributeProxy<E, List<R>, R> implements ListAttribute<E,R>, AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationListAttribute(ListAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((ListAttribute<E, R>) attribute);
        this.constructor = (MetaJpaConstructor<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}