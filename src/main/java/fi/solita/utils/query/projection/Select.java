package fi.solita.utils.query.projection;

import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.attributes.LiteralListAttribute;
import fi.solita.utils.query.attributes.LiteralSetAttribute;
import fi.solita.utils.query.attributes.LiteralSingularAttribute;
import fi.solita.utils.query.attributes.SelfAttribute;

public abstract class Select {

    /**
     * Include the whole entity itself in the projection (instead of an attribute of it)
     */
    public static <T extends IEntity> SingularAttribute<T, T> self() {
        return new SelfAttribute<T, T>();
    }

    public static <E, T> SingularAttribute<E, T> literal(T value) {
        return new LiteralSingularAttribute<E, T>(value);
    }

    public static <E, T> SetAttribute<E, T> literal(Set<T> value) {
        return new LiteralSetAttribute<E, T>(value);
    }

    public static <E, T> ListAttribute<E, T> literal(List<T> value) {
        return new LiteralListAttribute<E, T>(value);
    }

}
