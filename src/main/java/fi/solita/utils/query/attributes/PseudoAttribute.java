package fi.solita.utils.query.attributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

public interface PseudoAttribute {

    /**
     * Do not use these directly. Use class fi.solita.utils.query.projection.Select instead.
     */
    public static class Constructors {
        public static <E, T> SingularAttribute<E, T> literal(T value) {
            return new LiteralSingularAttribute<E, T>(value);
        }
        
        public static <E, T> CollectionAttribute<E, T> literal(Collection<T> value) {
            return new LiteralCollectionAttribute<E, T, CollectionAttribute<E, T>>(value);
        }
    
        public static <E, T> SetAttribute<E, T> literal(Set<T> value) {
            return new LiteralSetAttribute<E, T, SetAttribute<E, T>>(value);
        }
    
        public static <E, T> ListAttribute<E, T> literal(List<T> value) {
            return new LiteralListAttribute<E, T, ListAttribute<E, T>>(value);
        }
        
        public static <E, K, T> MapAttribute<E, K, T> literal(Map<K,T> value) {
            return new LiteralMapAttribute<E, K, T, MapAttribute<E, K, T>>(value);
        }

        public static <T> SingularAttribute<T, T> self() {
            return new SelfAttribute<T, T>();
        }
    }

    public static final String QUERY_PLACEHOLDER = "<>";

    public Expression<?> getSelectionForQuery(CriteriaBuilder cb, Path<?> currentSelection);

    public Object getValueToReplaceResult(Object resultFromDb);
}
