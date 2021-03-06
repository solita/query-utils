package fi.solita.utils.query.projection;



import static fi.solita.utils.query.QueryUtils.checkOptionalAttributes;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Function;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple1;
import fi.solita.utils.functional.Tuple10;
import fi.solita.utils.functional.Tuple11;
import fi.solita.utils.functional.Tuple12;
import fi.solita.utils.functional.Tuple13;
import fi.solita.utils.functional.Tuple14;
import fi.solita.utils.functional.Tuple15;
import fi.solita.utils.functional.Tuple16;
import fi.solita.utils.functional.Tuple17;
import fi.solita.utils.functional.Tuple18;
import fi.solita.utils.functional.Tuple19;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.functional.Tuple20;
import fi.solita.utils.functional.Tuple21;
import fi.solita.utils.functional.Tuple22;
import fi.solita.utils.functional.Tuple23;
import fi.solita.utils.functional.Tuple24;
import fi.solita.utils.functional.Tuple25;
import fi.solita.utils.functional.Tuple26;
import fi.solita.utils.functional.Tuple27;
import fi.solita.utils.functional.Tuple28;
import fi.solita.utils.functional.Tuple29;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.functional.Tuple30;
import fi.solita.utils.functional.Tuple31;
import fi.solita.utils.functional.Tuple32;
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.functional.Tuple5;
import fi.solita.utils.functional.Tuple6;
import fi.solita.utils.functional.Tuple7;
import fi.solita.utils.functional.Tuple8;
import fi.solita.utils.functional.Tuple9;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public class Project {
    
    public static final <S,T,P> MetaJpaConstructor<S, T, P> recurse(final ApplyZero<? extends MetaJpaConstructor<S, T, P>> f) {
        return new MetaJpaConstructor<S, T, P>() {
            private final ApplyZero<? extends MetaJpaConstructor<S, T, P>> c = Function.memoize(f);
            
            @Override
            public List<Class<?>> getConstructorParameterTypes() {
                return c.get().getConstructorParameterTypes();
            }

            @Override
            public Constructor<T> getMember() {
                return c.get().getMember();
            }

            @Override
            public T apply(P t) {
                return c.get().apply(t);
            }

            @Override
            public List<Attribute<?, ?>> getParameters() {
                return c.get().getParameters();
            }
            @Override

            public List<Integer> getIndexesOfIdWrappingParameters() {
                return c.get().getIndexesOfIdWrappingParameters();
            }
        };
    }

    public static <E extends IEntity<?> & Identifiable<?>> MetaJpaConstructor<E,Id<E>,Id<E>> id() {
        return Constructors.id();
    }
    
    public static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> max(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.max(attribute);
    }
    
    public static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> min(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.min(attribute);
    }
    
    public static <E, T extends Comparable<? super T>> MetaJpaConstructor<E,Option<T>,Option<T>> greatest(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.greatest(attribute);
    }
    
    public static <E, T extends Comparable<? super T>> MetaJpaConstructor<E,Option<T>,Option<T>> least(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.least(attribute);
    }

    public static <E, T> MetaJpaConstructor<E,T,T> value(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.value(attribute);
    }

    public static <E, T> MetaJpaConstructor<E,T,T> value(PluralAttribute<? super E, T, ?> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.value(attribute);
    }
    
    public static <E, LEFT, RIGHT> MetaJpaConstructor<E,Pair<LEFT,RIGHT>,Map.Entry<? extends LEFT,? extends RIGHT>> pair(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
        checkOptionalAttributes(left);
        checkOptionalAttributes(right);
        return Constructors.pair(left, right);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1> MetaJpaConstructor<E,Tuple1<T1>,Tuple1<T1>> tuple(Attribute<? super E, T1> t1) {
        return makeTuple(t1);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2> MetaJpaConstructor<E,Tuple2<T1, T2>,Tuple2<T1,T2>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2) {
        return makeTuple(t1, t2);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3> MetaJpaConstructor<E,Tuple3<T1, T2, T3>,Tuple3<T1, T2, T3>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3) {
        return makeTuple(t1, t2, t3);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4> MetaJpaConstructor<E,Tuple4<T1, T2, T3, T4>,Tuple4<T1, T2, T3, T4>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4) {
        return makeTuple(t1, t2, t3, t4);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5> MetaJpaConstructor<E,Tuple5<T1, T2, T3, T4, T5>,Tuple5<T1, T2, T3, T4, T5>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5) {
        return makeTuple(t1, t2, t3, t4, t5);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6> MetaJpaConstructor<E,Tuple6<T1, T2, T3, T4, T5, T6>,Tuple6<T1, T2, T3, T4, T5, T6>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6) {
        return makeTuple(t1, t2, t3, t4, t5, t6);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7> MetaJpaConstructor<E,Tuple7<T1, T2, T3, T4, T5, T6, T7>,Tuple7<T1, T2, T3, T4, T5, T6, T7>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8> MetaJpaConstructor<E,Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>,Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9> MetaJpaConstructor<E,Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>,Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MetaJpaConstructor<E,Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>,Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MetaJpaConstructor<E,Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>,Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MetaJpaConstructor<E,Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>,Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MetaJpaConstructor<E,Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>,Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MetaJpaConstructor<E,Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>,Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MetaJpaConstructor<E,Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>,Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MetaJpaConstructor<E,Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>,Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MetaJpaConstructor<E,Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>,Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MetaJpaConstructor<E,Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>,Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MetaJpaConstructor<E,Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>,Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MetaJpaConstructor<E,Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>,Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MetaJpaConstructor<E,Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>,Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MetaJpaConstructor<E,Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>,Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> MetaJpaConstructor<E,Tuple23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23>,Tuple23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24> MetaJpaConstructor<E,Tuple24<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24>,Tuple24<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25> MetaJpaConstructor<E,Tuple25<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25>,Tuple25<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26> MetaJpaConstructor<E,Tuple26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26>,Tuple26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27> MetaJpaConstructor<E,Tuple27<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27>,Tuple27<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26, Attribute<? super E, T27> t27) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28> MetaJpaConstructor<E,Tuple28<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28>,Tuple28<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26, Attribute<? super E, T27> t27, Attribute<? super E, T28> t28) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29> MetaJpaConstructor<E,Tuple29<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29>,Tuple29<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26, Attribute<? super E, T27> t27, Attribute<? super E, T28> t28, Attribute<? super E, T29> t29) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30> MetaJpaConstructor<E,Tuple30<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30>,Tuple30<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26, Attribute<? super E, T27> t27, Attribute<? super E, T28> t28, Attribute<? super E, T29> t29, Attribute<? super E, T30> t30) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31> MetaJpaConstructor<E,Tuple31<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31>,Tuple31<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26, Attribute<? super E, T27> t27, Attribute<? super E, T28> t28, Attribute<? super E, T29> t29, Attribute<? super E, T30> t30, Attribute<? super E, T31> t31) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32> MetaJpaConstructor<E,Tuple32<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32>,Tuple32<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32>> tuple(Attribute<? super E, T1> t1, Attribute<? super E, T2> t2, Attribute<? super E, T3> t3, Attribute<? super E, T4> t4, Attribute<? super E, T5> t5, Attribute<? super E, T6> t6, Attribute<? super E, T7> t7, Attribute<? super E, T8> t8, Attribute<? super E, T9> t9, Attribute<? super E, T10> t10, Attribute<? super E, T11> t11, Attribute<? super E, T12> t12, Attribute<? super E, T13> t13, Attribute<? super E, T14> t14, Attribute<? super E, T15> t15, Attribute<? super E, T16> t16, Attribute<? super E, T17> t17, Attribute<? super E, T18> t18, Attribute<? super E, T19> t19, Attribute<? super E, T20> t20, Attribute<? super E, T21> t21, Attribute<? super E, T22> t22, Attribute<? super E, T23> t23, Attribute<? super E, T24> t24, Attribute<? super E, T25> t25, Attribute<? super E, T26> t26, Attribute<? super E, T27> t27, Attribute<? super E, T28> t28, Attribute<? super E, T29> t29, Attribute<? super E, T30> t30, Attribute<? super E, T31> t31, Attribute<? super E, T32> t32) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32);
    }
    
    private static <E, T extends Tuple> MetaJpaConstructor<E,T,T> makeTuple(Attribute<? super E,?>... attributes) {
        for (Attribute<? super E, ?> a: attributes) {
            checkOptionalAttributes(a);
        }
        return Constructors.tuple(attributes);
    }

}
