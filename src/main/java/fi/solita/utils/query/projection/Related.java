package fi.solita.utils.query.projection;


import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;

public abstract class Related {

    public static <E extends IEntity, E2 extends IEntity, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return JoiningAttribute.Constructors.singular(a1, a2);
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3);
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, E4 extends IEntity, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4);
    }
    
    
    
    public static <E extends IEntity, E2 extends IEntity, R>
    SetAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SetAttribute<? super E2, R> a2) {
        return JoiningAttribute.Constructors.set(a1, a2);
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R>
    SetAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SetAttribute<? super E3, R> a3) {
        return JoiningAttribute.Constructors.set(a1, a2, a3);
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, E4 extends IEntity, R>
    SetAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SetAttribute<? super E4, R> a4) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4);
    }
    
    
    
    public static <E extends IEntity, E2 extends IEntity, R>
    ListAttribute<E, R> value(SingularAttribute<? super E, E2> a1, ListAttribute<? super E2, R> a2) {
        return JoiningAttribute.Constructors.list(a1, a2);
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R>
    ListAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, ListAttribute<? super E3, R> a3) {
        return JoiningAttribute.Constructors.list(a1, a2, a3);
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, E4 extends IEntity, R>
    ListAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, ListAttribute<? super E4, R> a4) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4);
    }
    
    

    public static <E extends IEntity, E2 extends IEntity, R>
    SetAttribute<E, R> value(SetAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return projection(a1, Constructors.value(a2));
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R>
    SetAttribute<E, R> value(SetAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return projection(a1, Constructors.value(Related.value(a2, a3)));
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, E4 extends IEntity, R>
    SetAttribute<E, R> value(SetAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return projection(a1, Constructors.value(Related.value(a2, a3, a4)));
    }
    
    

    public static <E extends IEntity, E2 extends IEntity, R>
    ListAttribute<E, R> value(ListAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return projection(a1, Constructors.value(a2));
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, R>
    ListAttribute<E, R> value(ListAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return projection(a1, Constructors.value(Related.value(a2, a3)));
    }
    
    public static <E extends IEntity, E2 extends IEntity, E3 extends IEntity, E4 extends IEntity, R>
    ListAttribute<E, R> value(ListAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return projection(a1, Constructors.value(Related.value(a2, a3, a4)));
    }
    
    
    
    public static <E extends IEntity, E2 extends IEntity, R>
    SingularAttribute<E, R> projection(SingularAttribute<? super E, E2> relation, ConstructorMeta_<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }

    public static <E extends IEntity, E2 extends IEntity, R>
    SetAttribute<E, R> projection(SetAttribute<? super E, E2> relation, ConstructorMeta_<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }
    
    public static <E extends IEntity, E2 extends IEntity, R>
    ListAttribute<E, R> projection(ListAttribute<? super E, E2> relation, ConstructorMeta_<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }
}
