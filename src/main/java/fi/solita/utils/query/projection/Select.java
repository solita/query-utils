package fi.solita.utils.query.projection;

import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.attributes.PseudoAttribute;

public abstract class Select {

    /**
     * Include the whole entity itself in the projection (instead of an attribute of it)
     */
    public static <T extends IEntity> SingularAttribute<T, T> self() {
        return PseudoAttribute.Constructors.self();
    }

    public static <E, T> SingularAttribute<E, T> literal(T value) {
        return PseudoAttribute.Constructors.literal(value);
    }

    public static <E, T> SetAttribute<E, T> literal(Set<T> value) {
        return PseudoAttribute.Constructors.literal(value);
    }

    public static <E, T> ListAttribute<E, T> literal(List<T> value) {
        return PseudoAttribute.Constructors.literal(value);
    }

}
