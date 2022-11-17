package fi.solita.utils.query.projection;


import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public abstract class Related {

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E, E2, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, R> a2) {
        return JoiningAttribute.Constructors.singular(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E, E2, E3, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, R> a3) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E, E2, E3, E4, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, R> a4) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E, E2, E3, E4, E5, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, E5> a4, SingularAttribute<? super E5, R> a5) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E, E2, E3, E4, E5, E6, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, E5> a4, SingularAttribute<? super E5, E6> a5, SingularAttribute<? super E6, R> a6) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E, E2, E3, E4, E5, E6, E7, R>
    SingularAttribute<E, R> value(SingularAttribute<? super E, E2> a1, SingularAttribute<? super E2, E3> a2, SingularAttribute<? super E3, E4> a3, SingularAttribute<? super E4, E5> a4, SingularAttribute<? super E5, E6> a5, SingularAttribute<? super E6, E7> a6, SingularAttribute<? super E7, R> a7) {
        return JoiningAttribute.Constructors.singular(a1, a2, a3, a4, a5, a6, a7);
    }
    
    
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,R, A1 extends Attribute<E,?> & Bindable<Y>>
    CollectionAttribute<E, R> collection(A1 a1, CollectionAttribute<? super Y,R> a2) {
        return JoiningAttribute.Constructors.collection(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    CollectionAttribute<E, R> collection(A1 a1, A2 a2, CollectionAttribute<? super Y2,R> a3) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    CollectionAttribute<E, R> collection(A1 a1, A2 a2, A3 a3, CollectionAttribute<? super Y3,R> a4) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    CollectionAttribute<E, R> collection(A1 a1, A2 a2, A3 a3, A4 a4, CollectionAttribute<? super Y4,R> a5) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    CollectionAttribute<E, R> collection(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, CollectionAttribute<? super Y5,R> a6) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    CollectionAttribute<E, R> collection(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, CollectionAttribute<? super Y6,R> a7) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6, a7);
    }
    

    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,R, A extends Attribute<E,?> & Bindable<Y>>
    SetAttribute<E, R> set(A a1, SetAttribute<? super Y, R> a2) {
        return JoiningAttribute.Constructors.set(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    SetAttribute<E, R> set(A1 a1, A2 a2, SetAttribute<? super Y2,R> a3) {
        return JoiningAttribute.Constructors.set(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, SetAttribute<? super Y3,R> a4) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, A4 a4, SetAttribute<? super Y4,R> a5) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, SetAttribute<? super Y5,R> a6) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, SetAttribute<? super Y6,R> a7) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6, a7);
    }
    
    
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,R, A extends Attribute<E,?> & Bindable<Y>>
    ListAttribute<E, R> list(A a1, ListAttribute<? super Y,R> a2) {
        return JoiningAttribute.Constructors.list(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    ListAttribute<E, R> list(A1 a1, A2 a2, ListAttribute<? super Y2,R> a3) {
        return JoiningAttribute.Constructors.list(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    ListAttribute<E, R> list(A1 a1, A2 a2, A3 a3, ListAttribute<? super Y3,R> a4) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    ListAttribute<E, R> list(A1 a1, A2 a2, A3 a3, A4 a4, ListAttribute<? super Y4,R> a5) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    ListAttribute<E, R> list(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, ListAttribute<? super Y5,R> a6) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,R, A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    ListAttribute<E, R> list(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, ListAttribute<? super Y6,R> a7) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6, a7);
    }

    
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,Y2, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    CollectionAttribute<E, Y2> collection(CollectionAttribute<E, Y> a1, A2 a2) {
        return JoiningAttribute.Constructors.collection(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    CollectionAttribute<E, Y3> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    CollectionAttribute<E, Y4> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    CollectionAttribute<E, Y5> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    CollectionAttribute<E, Y6> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>, A7 extends Attribute<? super Y6,?> & Bindable<Y7>>
    CollectionAttribute<E, Y7> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6, a7);
    }
    
    

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,Y2, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    SetAttribute<E, Y2> set(SetAttribute<E, Y> a1, A2 a2) {
        return JoiningAttribute.Constructors.set(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    SetAttribute<E, Y3> set(SetAttribute<E, Y> a1, A2 a2, A3 a3) {
        return JoiningAttribute.Constructors.set(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    SetAttribute<E, Y4> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    SetAttribute<E, Y5> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    SetAttribute<E, Y6> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>, A7 extends Attribute<? super Y6,?> & Bindable<Y7>>
    SetAttribute<E, Y7> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6, a7);
    }
    
    

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,Y2, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    ListAttribute<E, Y2> list(ListAttribute<E, Y> a1, A2 a2) {
        return JoiningAttribute.Constructors.list(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    ListAttribute<E, Y3> list(ListAttribute<E, Y> a1, A2 a2, A3 a3) {
        return JoiningAttribute.Constructors.list(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    ListAttribute<E, Y4> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    ListAttribute<E, Y5> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    ListAttribute<E, Y6> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>, A7 extends Attribute<? super Y6,?> & Bindable<Y7>>
    ListAttribute<E, Y7> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6, a7);
    }
    
    
    
    
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,Y2>
    CollectionAttribute<E, Y2> collection(CollectionAttribute<E, Y> a1, CollectionAttribute<? super Y, Y2> a2) {
        return JoiningAttribute.Constructors.collection(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    CollectionAttribute<E, Y3> collection(CollectionAttribute<E, Y> a1, A2 a2, CollectionAttribute<? super Y2,Y3> a3) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    CollectionAttribute<E, Y4> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, CollectionAttribute<? super Y3,Y4> a4) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    CollectionAttribute<E, Y5> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, CollectionAttribute<? super Y4,Y5> a5) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    CollectionAttribute<E, Y6> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, CollectionAttribute<? super Y5,Y6> a6) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    CollectionAttribute<E, Y7> collection(CollectionAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, CollectionAttribute<? super Y6,Y7> a7) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6, a7);
    }
    
    

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,Y2>
    SetAttribute<E, Y2> set(SetAttribute<E, Y> a1, SetAttribute<? super Y,Y2> a2) {
        return JoiningAttribute.Constructors.set(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    SetAttribute<E, Y3> set(SetAttribute<E, Y> a1, A2 a2, SetAttribute<? super Y2,Y3> a3) {
        return JoiningAttribute.Constructors.set(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    SetAttribute<E, Y4> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, SetAttribute<? super Y3,Y4> a4) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    SetAttribute<E, Y5> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, SetAttribute<? super Y4,Y5> a5) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    SetAttribute<E, Y6> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, SetAttribute<? super Y5,Y6> a6) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    SetAttribute<E, Y7> set(SetAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, SetAttribute<? super Y6,Y7> a7) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6, a7);
    }
    
    

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a2</i>.
     */
    public static <E,Y,Y2>
    ListAttribute<E, Y2> list(ListAttribute<E, Y> a1, ListAttribute<? super Y,Y2> a2) {
        return JoiningAttribute.Constructors.list(a1, a2);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
    ListAttribute<E, Y3> list(ListAttribute<E, Y> a1, A2 a2, ListAttribute<? super Y2,Y3> a3) {
        return JoiningAttribute.Constructors.list(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
    ListAttribute<E, Y4> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, ListAttribute<? super Y3,Y4> a4) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
    ListAttribute<E, Y5> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, ListAttribute<? super Y4,Y5> a5) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
    ListAttribute<E, Y6> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, ListAttribute<? super Y5,Y6> a6) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
    ListAttribute<E, Y7> list(ListAttribute<E, Y> a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, ListAttribute<? super Y6,Y7> a7) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6, a7);
    }

    
    
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3>
    CollectionAttribute<E, Y3> collection(SingularAttribute<E, Y> a1, CollectionAttribute<? super Y,Y2> a2, SingularAttribute<? super Y2,Y3> a3) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4>
    CollectionAttribute<E, Y4> collection(SingularAttribute<E, Y> a1, CollectionAttribute<? super Y,Y2> a2, CollectionAttribute<? super Y2,Y3> a3, SingularAttribute<? super Y3,Y4> a4) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5>
    CollectionAttribute<E, Y5> collection(SingularAttribute<E, Y> a1, CollectionAttribute<? super Y,Y2> a2, CollectionAttribute<? super Y2,Y3> a3, CollectionAttribute<? super Y3,Y4> a4, SingularAttribute<? super Y4,Y5> a5) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6>
    CollectionAttribute<E, Y6> collection(SingularAttribute<E, Y> a1, CollectionAttribute<? super Y,Y2> a2, CollectionAttribute<? super Y2,Y3> a3, CollectionAttribute<? super Y3,Y4> a4, CollectionAttribute<? super Y4,Y5> a5, SingularAttribute<? super Y5,Y6> a6) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7>
    CollectionAttribute<E, Y7> collection(SingularAttribute<E, Y> a1, CollectionAttribute<? super Y,Y2> a2, CollectionAttribute<? super Y2,Y3> a3, CollectionAttribute<? super Y3,Y4> a4, CollectionAttribute<? super Y4,Y5> a5, CollectionAttribute<? super Y5,Y6> a6, SingularAttribute<? super Y6,Y7> a7) {
        return JoiningAttribute.Constructors.collection(a1, a2, a3, a4, a5, a6, a7);
    }
    
    

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3>
    SetAttribute<E, Y3> set(SingularAttribute<E, Y> a1, SetAttribute<? super Y,Y2> a2, SingularAttribute<? super Y2,Y3> a3) {
        return JoiningAttribute.Constructors.set(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4>
    SetAttribute<E, Y4> set(SingularAttribute<E, Y> a1, SetAttribute<? super Y,Y2> a2, SetAttribute<? super Y2,Y3> a3, SingularAttribute<? super Y3,Y4> a4) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5>
    SetAttribute<E, Y5> set(SingularAttribute<E, Y> a1, SetAttribute<? super Y,Y2> a2, SetAttribute<? super Y2,Y3> a3, SetAttribute<? super Y3,Y4> a4, SingularAttribute<? super Y4,Y5> a5) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6>
    SetAttribute<E, Y6> set(SingularAttribute<E, Y> a1, SetAttribute<? super Y,Y2> a2, SetAttribute<? super Y2,Y3> a3, SetAttribute<? super Y3,Y4> a4, SetAttribute<? super Y4,Y5> a5, SingularAttribute<? super Y5,Y6> a6) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7>
    SetAttribute<E, Y7> set(SingularAttribute<E, Y> a1, SetAttribute<? super Y,Y2> a2, SetAttribute<? super Y2,Y3> a3, SetAttribute<? super Y3,Y4> a4, SetAttribute<? super Y4,Y5> a5, SetAttribute<? super Y5,Y6> a6, SingularAttribute<? super Y6,Y7> a7) {
        return JoiningAttribute.Constructors.set(a1, a2, a3, a4, a5, a6, a7);
    }
    
    

    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a3</i>.
     */
    public static <E,Y,Y2,Y3>
    ListAttribute<E, Y3> list(SingularAttribute<E, Y> a1, ListAttribute<? super Y,Y2> a2, SingularAttribute<? super Y2,Y3> a3) {
        return JoiningAttribute.Constructors.list(a1, a2, a3);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a4</i>.
     */
    public static <E,Y,Y2,Y3,Y4>
    ListAttribute<E, Y4> list(SingularAttribute<E, Y> a1, ListAttribute<? super Y,Y2> a2, ListAttribute<? super Y2,Y3> a3, SingularAttribute<? super Y3,Y4> a4) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a5</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5>
    ListAttribute<E, Y5> list(SingularAttribute<E, Y> a1, ListAttribute<? super Y,Y2> a2, ListAttribute<? super Y2,Y3> a3, ListAttribute<? super Y3,Y4> a4, SingularAttribute<? super Y4,Y5> a5) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a6</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6>
    ListAttribute<E, Y6> list(SingularAttribute<E, Y> a1, ListAttribute<? super Y,Y2> a2, ListAttribute<? super Y2,Y3> a3, ListAttribute<? super Y3,Y4> a4, ListAttribute<? super Y4,Y5> a5, SingularAttribute<? super Y5,Y6> a6) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6);
    }
    
    /**
     * Create an attribute, which is the inner join from <i>a1</i> to <i>a7</i>.
     */
    public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7>
    ListAttribute<E, Y7> list(SingularAttribute<E, Y> a1, ListAttribute<? super Y,Y2> a2, ListAttribute<? super Y2,Y3> a3, ListAttribute<? super Y3,Y4> a4, ListAttribute<? super Y4,Y5> a5, ListAttribute<? super Y5,Y6> a6, SingularAttribute<? super Y6,Y7> a7) {
        return JoiningAttribute.Constructors.list(a1, a2, a3, a4, a5, a6, a7);
    }
    
    
    
    
    /**
     * Create an attribute, which is a subquery for <i>relation</i> with its result projected to <i>projection</i>.
     */
    public static <E, E2, R>
    SingularAttribute<E, R> projection(SingularAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> projection) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, projection);
    }
    
    /**
     * Create an attribute, which is a subquery for <i>relation</i> with its result projected to <i>projection</i>.
     */
    public static <E, E2, R>
    CollectionAttribute<E, R> projection(CollectionAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> projection) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, projection);
    }

    /**
     * Create an attribute, which is a subquery for <i>relation</i> with its result projected to <i>projection</i>.
     */
    public static <E, E2, R>
    SetAttribute<E, R> projection(SetAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> projection) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, projection);
    }
    
    /**
     * Create an attribute, which is a subquery for <i>relation</i> with its result projected to <i>projection</i>.
     */
    public static <E, E2, R>
    ListAttribute<E, R> projection(ListAttribute<? super E, E2> relation, MetaJpaConstructor<? super E2, R, ?> projection) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, projection);
    }
    
    /**
     * Create an attribute, which is a subquery for <i>relation</i> with its result projected to <i>projection</i>.
     */
    public static <E, E2, K, R>
    MapAttribute<E, K, R> projection(MapAttribute<? super E, K, E2> relation, MetaJpaConstructor<? super E2, R, ?> projection) {
        return AdditionalQueryPerformingAttribute.Constructors.relation(relation, projection);
    }
}
