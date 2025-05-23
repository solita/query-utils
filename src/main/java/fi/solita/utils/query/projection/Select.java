package fi.solita.utils.query.projection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.attributes.PseudoAttribute;

public abstract class Select {

    /**
     * Include the whole entity itself in the projection (instead of an attribute of it)
     */
    public static <T extends IEntity<?>> SingularAttribute<T, T> self() {
        return PseudoAttribute.Constructors.self();
    }

    /**
     * Select a literal value (will not go through the database).
     */
    public static <E, T> SingularAttribute<E, T> literal(T value) {
        return PseudoAttribute.Constructors.literal(value);
    }
    
    /**
     * Select a literal Collection value (will not go through the database).
     */
    public static <E, T> CollectionAttribute<E, T> literal(Collection<T> value) {
        return PseudoAttribute.Constructors.literal(value);
    }

    /**
     * Select a literal Set value (will not go through the database).
     */
    public static <E, T> SetAttribute<E, T> literal(Set<T> value) {
        return PseudoAttribute.Constructors.literal(value);
    }

    /**
     * Select a literal List value (will not go through the database).
     */
    public static <E, T> ListAttribute<E, T> literal(List<T> value) {
        return PseudoAttribute.Constructors.literal(value);
    }
    
    /**
     * Select a null value.
     */
    public static <E, T> SingularAttribute<E, T> singularNull() {
        return PseudoAttribute.Constructors.literal((T)null);
    }
    
    /**
     * Select a null value.
     */
    public static <E, T> CollectionAttribute<E, T> collectionNull() {
        return PseudoAttribute.Constructors.literal((Collection<T>)null);
    }
    
    /**
     * Select a null value.
     */
    public static <E, T> SetAttribute<E, T> setNull() {
        return PseudoAttribute.Constructors.literal((Set<T>)null);
    }

    /**
     * Select a null value.
     */
    public static <E, T> ListAttribute<E, T> listNull() {
        return PseudoAttribute.Constructors.literal((List<T>)null);
    }
    
    /**
     * Select a null value.
     */
    public static <E, K, T> MapAttribute<E, K, T> mapNull() {
        return PseudoAttribute.Constructors.literal((Map<K,T>)null);
    }
}
