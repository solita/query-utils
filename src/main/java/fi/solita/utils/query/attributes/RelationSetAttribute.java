package fi.solita.utils.query.attributes;

import java.util.Set;

import javax.persistence.metamodel.SetAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.ConstructorMeta_;

public final class RelationSetAttribute<E extends IEntity, R> extends PluralAttributeProxy<E, Set<R>, R> implements SetAttribute<E,R>, RelationAttribute<E,Set<R>, R> {
    private final ConstructorMeta_<? extends IEntity, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity> RelationSetAttribute(SetAttribute<? super E, ? super E2> attribute, ConstructorMeta_<? super E2, R, ?> constructor) {
        super((SetAttribute<E, R>) attribute);
        this.constructor = (ConstructorMeta_<? extends IEntity, R, ?>) constructor;
    }

    @Override
    public ConstructorMeta_<? extends IEntity, R, ?> getConstructor() {
        return constructor;
    }
}