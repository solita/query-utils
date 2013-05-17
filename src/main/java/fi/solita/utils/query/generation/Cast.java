package fi.solita.utils.query.generation;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.OptionalAttribute;

public class Cast {

    public static <E, T> SingularAttribute<E, Option<T>> optional(SingularAttribute<E, T> attribute) throws QueryUtils.RequiredAttributeMustNotHaveOptionTypeException {
        if (!attribute.isOptional()) {
            throw new QueryUtils.RequiredAttributeMustNotHaveOptionTypeException(attribute);
        }
        return new OptionalAttribute<E,T>(attribute);
    }

    public static <E, T> SingularAttribute<E, Option<T>> optionalSubtype(SingularAttribute<? extends E, T> attribute) {
        return new OptionalAttribute<E,T>(attribute) {
            @Override
            public boolean isOptional() {
                // always consider the field optional since it's part of a subtype
                return true;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity & Identifiable<?>, T extends IEntity & Identifiable<?>> SingularAttribute<E, T> castSuper(SingularAttribute<? extends E, T> attribute) {
        return (SingularAttribute<E, T>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity & Identifiable<?>, T extends IEntity> SingularAttribute<E, T> cast(SingularAttribute<? super E, ? super T> attribute) {
        return (SingularAttribute<E, T>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity & Identifiable<?>, T extends IEntity & Identifiable<?>> SetAttribute<E, T> cast(SetAttribute<? super E, ? super T> attribute) {
        return (SetAttribute<E, T>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity & Identifiable<?>, T extends IEntity & Identifiable<?>> ListAttribute<E, T> cast(ListAttribute<? super E, ? super T> attribute) {
        return (ListAttribute<E, T>) attribute;
    }

}
