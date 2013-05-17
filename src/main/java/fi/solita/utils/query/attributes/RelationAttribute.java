package fi.solita.utils.query.attributes;


import javax.persistence.metamodel.Attribute;

import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.IEntity;

public interface RelationAttribute<X,C,R> extends Attribute<X, C> {
    ConstructorMeta_<? extends IEntity, R, ?> getConstructor();
}