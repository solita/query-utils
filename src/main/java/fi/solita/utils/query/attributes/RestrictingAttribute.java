package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

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
