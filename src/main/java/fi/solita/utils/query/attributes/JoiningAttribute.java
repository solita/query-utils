package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;

/**
 * 
 * Do not use this class directly.
 *
 * An attribute containing other attributes to be inner joined to the
 * attribute itself, thus changing the resulting type of the original attribute.
 */
public interface JoiningAttribute {
    public abstract List<? extends Attribute<?, ?>> getAttributes();
    
    public static class Constructors {
        public static <E extends IEntity,R> SingularAttribute<E,R> singular(SingularAttribute<?,?>... attrs) {
            return new JoiningSingularAttribute<E, R>(attrs);
        }
    
        
        public static <E extends IEntity,Y extends IEntity,R> CollectionAttribute<E,R> Collection(SingularAttribute<? super E,Y> attr1, CollectionAttribute<? super Y,R> attr2) {
            return new JoiningCollectionAttribute<E,R>(newList(attr1, attr2));
        }
        
        public static <E extends IEntity,Y extends IEntity, Y2 extends IEntity,R> CollectionAttribute<E,R> Collection(SingularAttribute<? super E,Y> attr1, SingularAttribute<? super Y,Y2> attr2, CollectionAttribute<? super Y2,R> attr3) {
            return new JoiningCollectionAttribute<E,R>(newList(attr1, attr2, attr3));
        }
        
        public static <E extends IEntity,Y extends IEntity, Y2 extends IEntity, Y3 extends IEntity,R> CollectionAttribute<E,R> Collection(SingularAttribute<? super E,Y> attr1, SingularAttribute<? super Y,Y2> attr2, SingularAttribute<? super Y2,Y3> attr3, CollectionAttribute<? super Y3,R> attr4) {
            return new JoiningCollectionAttribute<E,R>(newList(attr1, attr2, attr3, attr4));
        }
        
    
        public static <E extends IEntity,Y extends IEntity,R> SetAttribute<E,R> set(SingularAttribute<? super E,Y> attr1, SetAttribute<? super Y,R> attr2) {
            return new JoiningSetAttribute<E,R>(newList(attr1, attr2));
        }
        
        public static <E extends IEntity,Y extends IEntity, Y2 extends IEntity,R> SetAttribute<E,R> set(SingularAttribute<? super E,Y> attr1, SingularAttribute<? super Y,Y2> attr2, SetAttribute<? super Y2,R> attr3) {
            return new JoiningSetAttribute<E,R>(newList(attr1, attr2, attr3));
        }
        
        public static <E extends IEntity,Y extends IEntity, Y2 extends IEntity, Y3 extends IEntity,R> SetAttribute<E,R> set(SingularAttribute<? super E,Y> attr1, SingularAttribute<? super Y,Y2> attr2, SingularAttribute<? super Y2,Y3> attr3, SetAttribute<? super Y3,R> attr4) {
            return new JoiningSetAttribute<E,R>(newList(attr1, attr2, attr3, attr4));
        }
        
        
        public static <E extends IEntity,Y extends IEntity,R> ListAttribute<E,R> list(SingularAttribute<? super E,Y> attr1, ListAttribute<? super Y,R> attr2) {
            return new JoiningListAttribute<E,R>(newList(attr1, attr2));
        }
        
        public static <E extends IEntity,Y extends IEntity, Y2 extends IEntity,R> ListAttribute<E,R> list(SingularAttribute<? super E,Y> attr1, SingularAttribute<? super Y,Y2> attr2, ListAttribute<? super Y2,R> attr3) {
            return new JoiningListAttribute<E,R>(newList(attr1, attr2, attr3));
        }
        
        public static <E extends IEntity,Y extends IEntity, Y2 extends IEntity, Y3 extends IEntity,R> ListAttribute<E,R> list(SingularAttribute<? super E,Y> attr1, SingularAttribute<? super Y,Y2> attr2, SingularAttribute<? super Y2,Y3> attr3, ListAttribute<? super Y3,R> attr4) {
            return new JoiningListAttribute<E,R>(newList(attr1, attr2, attr3, attr4));
        }
    }
}
