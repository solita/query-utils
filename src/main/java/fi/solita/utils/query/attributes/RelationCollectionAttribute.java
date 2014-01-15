package fi.solita.utils.query.attributes;

import java.util.Collection;

import javax.persistence.metamodel.CollectionAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

class RelationCollectionAttribute<E extends IEntity, R> extends PluralAttributeProxy<E, Collection<R>, R> implements CollectionAttribute<E,R>, AdditionalQueryPerformingAttribute {
    private final MetaJpaConstructor<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationCollectionAttribute(CollectionAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((CollectionAttribute<E, R>) attribute);
        this.constructor = (MetaJpaConstructor<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}