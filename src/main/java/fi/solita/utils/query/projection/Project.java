package fi.solita.utils.query.projection;



import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.codegen.ConstructorMeta_;
import fi.solita.utils.query.projection.Constructors.*;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple1;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.functional.Tuple5;
import fi.solita.utils.functional.Tuple6;

public class Project {

    public static <E extends IEntity & Identifiable<?>> ConstructorMeta_<E,Id<E>,Id<E>> id() {
        return new IdProjection<E>();
    }

    public static <E extends IEntity, T> ConstructorMeta_<E,T,T> value(SingularAttribute<? super E, T> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    public static <E extends IEntity, T> ConstructorMeta_<E,T,T> value(PluralAttribute<? super E, T, ?> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    public static <E extends IEntity, LEFT, RIGHT> ConstructorMeta_<E,Pair<LEFT,RIGHT>,Tuple2<LEFT,RIGHT>> pair(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
        QueryUtils.checkOptionalAttributes(left);
        QueryUtils.checkOptionalAttributes(right);
        return new PairProjection<E,LEFT,RIGHT>(left, right);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity, T1> ConstructorMeta_<E,Tuple1<T1>,Tuple1<T1>> tuple(Attribute<? super E, T1> t1) {
        return makeTuple(t1);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity, T1, T2> ConstructorMeta_<E,Tuple2<T1, T2>,Tuple2<T1,T2>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2) {
        return makeTuple(t1, t2);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity, T1, T2, T3> ConstructorMeta_<E,Tuple3<T1, T2, T3>,Tuple3<T1, T2, T3>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3) {
        return makeTuple(t1, t2, t3);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity, T1, T2, T3, T4> ConstructorMeta_<E,Tuple4<T1, T2, T3, T4>,Tuple4<T1, T2, T3, T4>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4) {
        return makeTuple(t1, t2, t3, t4);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity, T1, T2, T3, T4, T5> ConstructorMeta_<E,Tuple5<T1, T2, T3, T4, T5>,Tuple5<T1, T2, T3, T4, T5>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5) {
        return makeTuple(t1, t2, t3, t4, t5);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity, T1, T2, T3, T4, T5, T6> ConstructorMeta_<E,Tuple6<T1, T2, T3, T4, T5, T6>,Tuple6<T1, T2, T3, T4, T5, T6>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6) {
        return makeTuple(t1, t2, t3, t4, t5, t6);
    }
    
    private static <E extends IEntity, T extends Tuple> ConstructorMeta_<E,T,T> makeTuple(Attribute<? super E,?>... attributes) {
        for (Attribute<? super E, ?> a: attributes) {
            QueryUtils.checkOptionalAttributes(a);
        }
        return new TupleProjection<E,T>(attributes);
    }

}
