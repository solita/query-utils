package fi.solita.utils.query.attributes;


import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.meta.MetaJpaConstructor;

public interface AdditionalQueryPerformingAttribute {
    MetaJpaConstructor<?, ?, ?> getConstructor();
    
    /**
     * Do not use these directly. Use class fi.solita.utils.query.projection.Related instead.
     */
    public static class Constructors {
        @SuppressWarnings("unchecked")
        public static <E, E2, R> SingularAttribute<E,R> relation(SingularAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
            return new RelationSingularAttribute<E,R>((SingularAttribute<E, E2>) (Object) attribute, (MetaJpaConstructor<E2, R, ?>) (Object) constructor);
        }
        
        @SuppressWarnings("unchecked")
        public static <E, E2, R> CollectionAttribute<E,R> relation(CollectionAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
            return new RelationCollectionAttribute<E,R,CollectionAttribute<E, R>>((CollectionAttribute<E, E2>) (Object) attribute, (MetaJpaConstructor<E2, R, ?>) (Object) constructor);
        }
        
        @SuppressWarnings("unchecked")
        public static <E, E2, R> SetAttribute<E,R> relation(SetAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
            return new RelationSetAttribute<E,R,SetAttribute<E,R>>((SetAttribute<E, E2>) (Object) attribute, (MetaJpaConstructor<E2, R, ?>) (Object) constructor);
        }
        
        @SuppressWarnings("unchecked")
        public static <E, E2, R> ListAttribute<E,R> relation(ListAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
            return new RelationListAttribute<E,R,ListAttribute<E,R>>((ListAttribute<E, E2>) (Object) attribute, (MetaJpaConstructor<E2, R, ?>) (Object) constructor);
        }
        
        @SuppressWarnings("unchecked")
        public static <E, E2, K, R> MapAttribute<E,K,R> relation(MapAttribute<? super E, K, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
            return new RelationMapAttribute<E,K,R,MapAttribute<E,K,R>>((MapAttribute<E, K, E2>) (Object) attribute, (MetaJpaConstructor<E2, R, ?>) (Object) constructor);
        } 
    }
}