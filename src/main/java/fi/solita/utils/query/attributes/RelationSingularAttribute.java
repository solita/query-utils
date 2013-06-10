package fi.solita.utils.query.attributes;

import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.ConstructorMeta_;

class RelationSingularAttribute<E extends IEntity, R> extends SingularAttributeProxy<E, R> implements RelationAttribute {
    private final ConstructorMeta_<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationSingularAttribute(SingularAttribute<? super E, ? super E2> attribute, ConstructorMeta_<? super E2, R, ?> constructor) {
        super((SingularAttribute<E, R>) attribute);
        this.constructor = (ConstructorMeta_<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public ConstructorMeta_<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}