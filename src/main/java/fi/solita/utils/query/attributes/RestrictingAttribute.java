package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

/**
 * 
 * Do not use this class directly.
 * 
 * An attribute, which contains related attributes that are to be inner joined
 * to the attribute itself, to restrict the resulting rows based on the inner joins.
 *
 */
public interface RestrictingAttribute {
    public abstract List<? extends Attribute<?, ?>> getRestrictionChain();
    
    public static class Constructors {
        public static <E,R,A extends Attribute<E, ?> & Bindable<R>> SingularAttribute<E,R> singular(SingularAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingSingularAttribute<E, R>(attribute, newList(restrictionChain));
        }
    
        public static <E,R,A extends Attribute<E, Set<R>> & Bindable<R>> SetAttribute<E,R> set(SetAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingSetAttribute<E,R,A>(attribute, newList(restrictionChain));
        }
        
        public static <E,R,A extends Attribute<E, List<R>> & Bindable<R>> ListAttribute<E,R> list(ListAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingListAttribute<E,R,A>(attribute, newList(restrictionChain));
        }
        
        public static <E,R,A extends Attribute<E, Collection<R>> & Bindable<R>> CollectionAttribute<E,R> collection(CollectionAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingCollectionAttribute<E,R,A>(attribute, newList(restrictionChain));
        }
    }
}
