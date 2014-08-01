package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;

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
        public static <E,R,A extends Attribute<? super R, ?> & Bindable<? extends IEntity>> SingularAttribute<E,R> singular(SingularAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingSingularAttribute<E, R>(attribute, newList(restrictionChain));
        }
    
        public static <E,R,A extends Attribute<? super R, ?> & Bindable<? extends IEntity>> SetAttribute<E,R> set(SetAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingSetAttribute<E,R>(attribute, newList(restrictionChain));
        }
        
        public static <E,R,A extends Attribute<? super R, ?> & Bindable<? extends IEntity>> ListAttribute<E,R> list(ListAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingListAttribute<E,R>(attribute, newList(restrictionChain));
        }
        
        public static <E,R,A extends Attribute<? super R, ?> & Bindable<? extends IEntity>> CollectionAttribute<E,R> collection(CollectionAttribute<E,R> attribute, Attribute<?,?>... restrictionChain) {
            return new RestrictingCollectionAttribute<E,R>(attribute, newList(restrictionChain));
        }
    }
}
