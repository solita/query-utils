package fi.solita.utils.query.projection;


import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.RelationListAttribute;
import fi.solita.utils.query.attributes.RelationSetAttribute;
import fi.solita.utils.query.attributes.RelationSingularAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.projection.Constructors.ValueAttributeProjection;

public abstract class Related {

    public static <E extends IEntity, E2 extends IEntity, R> SingularAttribute<E, R> value(SingularAttribute<? super E, E2> relation, final SingularAttribute<? super E2, R> attribute) {
        QueryUtils.checkOptionalAttributes(attribute);
        Related.checkValueAttribute(attribute);
        return new RelationSingularAttribute<E,R>(relation, new ValueAttributeProjection<E2, R>(attribute));
    }

    public static <E extends IEntity, E2 extends IEntity, R> SetAttribute<E, R> value(SetAttribute<? super E, E2> relation, final SingularAttribute<? super E2, R> attribute) {
        QueryUtils.checkOptionalAttributes(attribute);
        Related.checkValueAttribute(attribute);
        return new RelationSetAttribute<E,R>(relation, new ValueAttributeProjection<E2, R>(attribute));
    }

    public static <E extends IEntity, E2 extends IEntity, R> ListAttribute<E, R> value(ListAttribute<? super E, E2> relation, final SingularAttribute<? super E2, R> attribute) {
        QueryUtils.checkOptionalAttributes(attribute);
        Related.checkValueAttribute(attribute);
        return new RelationListAttribute<E,R>(relation, new ValueAttributeProjection<E2, R>(attribute));
    }

    public static <E extends IEntity, E2 extends IEntity, R> SingularAttribute<E, R> projection(SingularAttribute<? super E, ? super E2> relation, ConstructorMeta_<? super E2, R, ?> constructor) {
        return new RelationSingularAttribute<E,R>(relation, constructor);
    }

    public static <E extends IEntity, E2 extends IEntity, R> SetAttribute<E, R> projection(SetAttribute<? super E, ? super E2> relation, ConstructorMeta_<? super E2, R, ?> constructor) {
        return new RelationSetAttribute<E,R>(relation, constructor);
    }

    public static <E extends IEntity, E2 extends IEntity, R> RelationListAttribute<E, R> projection(ListAttribute<? super E, ? super E2> relation, ConstructorMeta_<? super E2, R, ?> constructor) {
        return new RelationListAttribute<E,R>(relation, constructor);
    }

    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R> SetAttribute<E, R> projection(SetAttribute<? super E, ? super E2> relation1, SingularAttribute<? super E2, ? super E3> relation2, ConstructorMeta_<? super E3, R, ?> constructor) {
        return projection(relation1, Project.value(projection(relation2, constructor)));
    }

    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R> SetAttribute<E, Set<R>> projection(SetAttribute<? super E, ? super E2> relation1, SetAttribute<? super E2, ? super E3> relation2, ConstructorMeta_<? super E3, R, ?> constructor) {
        return projection(relation1, Project.value(projection(relation2, constructor)));
    }

    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R> SetAttribute<E, List<R>> projection(SetAttribute<? super E, ? super E2> relation1, ListAttribute<? super E2, ? super E3> relation2, ConstructorMeta_<? super E3, R, ?> constructor) {
        return projection(relation1, Project.value(projection(relation2, constructor)));
    }

    private static void checkValueAttribute(Bindable<?> attribute) {
        if (IEntity.class.isAssignableFrom(attribute.getBindableJavaType())) {
            throw new UnsupportedOperationException("Projection can only be made to a value, not a relation");
        }
    }

}
