package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.metamodel.ListAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.ConstructorMeta_;

class RelationListAttribute<E extends IEntity, R> extends PluralAttributeProxy<E, List<R>, R> implements ListAttribute<E,R>, RelationAttribute {
    private final ConstructorMeta_<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationListAttribute(ListAttribute<? super E, ? super E2> attribute, ConstructorMeta_<? super E2, R, ?> constructor) {
        super((ListAttribute<E, R>) attribute);
        this.constructor = (ConstructorMeta_<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public ConstructorMeta_<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}