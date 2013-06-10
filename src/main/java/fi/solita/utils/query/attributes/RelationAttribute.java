package fi.solita.utils.query.attributes;


import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.codegen.ConstructorMeta_;

public interface RelationAttribute {
    ConstructorMeta_<?, ?, ?> getConstructor();
    
    /**
     * Do not use these directly. Use class fi.solita.utils.query.projection.Related instead.
     */
    public static class Constructors {
        public static <E extends IEntity, E2 extends IEntity, R> SingularAttribute<E,R> relation(SingularAttribute<? super E, ? super E2> attribute, ConstructorMeta_<? super E2, R, ?> constructor) {
            return new RelationSingularAttribute<E,R>(attribute, constructor);
        }
        
        public static <E extends IEntity, E2 extends IEntity, R> SetAttribute<E,R> relation(SetAttribute<? super E, ? super E2> attribute, ConstructorMeta_<? super E2, R, ?> constructor) {
            return new RelationSetAttribute<E,R>(attribute, constructor);
        }
        
        public static <E extends IEntity, E2 extends IEntity, R> ListAttribute<E,R> relation(ListAttribute<? super E, ? super E2> attribute, ConstructorMeta_<? super E2, R, ?> constructor) {
            return new RelationListAttribute<E,R>(attribute, constructor);
        }
    }
}