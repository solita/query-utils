package fi.solita.utils.query.projection;


import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

public abstract class Related {

    public static <E, E1, E2, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return JoiningAttribute.Constructors.singular(a1, a2);
    }
    
    public static <E, E1, E2, E3, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3);
    }
    
    public static <E, E1, E2, E3, E4, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4);
    }
    
    public static <E, E1, E2, E3, E4, E5, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, E5> a4, SingularAttribute<? super E5, R> a5) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4, a5);
    }
    
    
    
    public static <E,Y,R, A extends Attribute<E,?> & Bindable<Y>>
    CollectionAttribute<E, R> value(A a1, CollectionAttribute<? super Y,R> a2) {
        return JoiningAttribute.Constructors.collection(a1, a2);
    }
    
    public static <E,Y, Y2,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    CollectionAttribute<E, R> value(A1 a1, A2 a2, CollectionAttribute<? super Y2,R> a3) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3);
    }
    
    public static <E,Y, Y2, Y3,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    CollectionAttribute<E, R> value(A1 a1, A2 a2, A3 a3, CollectionAttribute<? super Y3,R> a4) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4);
    }
    
    
    
    public static <E,Y,R, A extends Attribute<E,?> & Bindable<Y>>
    SetAttribute<E, R> value(A a1, SetAttribute<? super Y, R> a2) {
        return JoiningAttribute.Constructors.set(a1, a2);
    }
    
    public static <E,Y, Y2,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    SetAttribute<E, R> value(A1 a1, A2 a2, SetAttribute<? super Y2,R> a3) {
        return JoiningAttribute.Constructors.set(a1, a2, a3);
    }
    
    public static <E,Y, Y2, Y3,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, SetAttribute<? super Y3,R> a4) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4);
    }
    
    
    
    public static <E,Y,R, A extends Attribute<E,?> & Bindable<Y>>
    ListAttribute<E, R> value(A a1, ListAttribute<? super Y,R> a2) {
        return JoiningAttribute.Constructors.list(a1, a2);
    }
    
    public static <E,Y, Y2,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    ListAttribute<E, R> value(A1 a1, A2 a2, ListAttribute<? super Y2,R> a3) {
        return JoiningAttribute.Constructors.list(a1, a2, a3);
    }
    
    public static <E,Y, Y2,Y3,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    ListAttribute<E, R> value(A1 a1, A2 a2, A3 a3, ListAttribute<? super Y3,R> a4) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4);
    }

    
    
    public static <E,Y,R, A extends Attribute<? super Y,?> & Bindable<R>>
    CollectionAttribute<E, R> value(CollectionAttribute<E, Y> a1, A a2) {
        return JoiningAttribute.Constructors.collection(a1, a2);
    }
    
    public static <E, E2, E3, R>
    CollectionAttribute<E, R> value(CollectionAttribute<E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return projection(a1, Constructors.value(Related.value(a2, a3)));
    }
    
    public static <E, E2, E3, E4, R>
    CollectionAttribute<E, R> value(CollectionAttribute<E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return projection(a1, Constructors.value(Related.value(a2, a3, a4)));
    }
    
    

    public static <E, E2, R>
    SetAttribute<E, R> value(SetAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return projection(a1, Constructors.value(a2));
    }
    
    public static <E, E2, E3, R>
    SetAttribute<E, R> value(SetAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return projection(a1, Constructors.value(Related.value(a2, a3)));
    }
    
    public static <E, E2, E3, E4, R>
    SetAttribute<E, R> value(SetAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return projection(a1, Constructors.value(Related.value(a2, a3, a4)));
    }
    
    

    public static <E, E2, R>
    ListAttribute<E, R> value(ListAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return projection(a1, Constructors.value(a2));
    }
    
    public static <E, E2, E3, R>
    ListAttribute<E, R> value(ListAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return projection(a1, Constructors.value(Related.value(a2, a3)));
    }
    
    public static <E, E2, E3, E4, R>
    ListAttribute<E, R> value(ListAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return projection(a1, Constructors.value(Related.value(a2, a3, a4)));
    }
    
    
    
    public static <E, E2, R>
    SingularAttribute<E, R> projection(SingularAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }
    
    public static <E, E2, R>
    CollectionAttribute<E, R> projection(CollectionAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }

    public static <E, E2, R>
    SetAttribute<E, R> projection(SetAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }
    
    public static <E, E2, R>
    ListAttribute<E, R> projection(ListAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> constructor) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, constructor);
    }
}
