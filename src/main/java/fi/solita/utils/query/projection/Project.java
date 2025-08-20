package fi.solita.utils.query.projection;



import static fi.solita.utils.query.QueryUtils.checkOptionalAttributes;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

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
import fi.solita.utils.functional.Tuple33;
import fi.solita.utils.functional.Tuple34;
import fi.solita.utils.functional.Tuple35;
import fi.solita.utils.functional.Tuple36;
import fi.solita.utils.functional.Tuple37;
import fi.solita.utils.functional.Tuple38;
import fi.solita.utils.functional.Tuple39;
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.functional.Tuple40;
import fi.solita.utils.functional.Tuple41;
import fi.solita.utils.functional.Tuple42;
import fi.solita.utils.functional.Tuple43;
import fi.solita.utils.functional.Tuple44;
import fi.solita.utils.functional.Tuple5;
import fi.solita.utils.functional.Tuple6;
import fi.solita.utils.functional.Tuple7;
import fi.solita.utils.functional.Tuple8;
import fi.solita.utils.functional.Tuple9;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.meta.MetaJpaConstructor;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

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

    /**
     * Project the query result to the entity identifier.
     */
    public static <E extends IEntity<?> & Identifiable<?>> MetaJpaConstructor<E,Id<E>,Id<E>> id() {
        return Constructors.id();
    }
    
    /**
     * Project the query result to the aggregate sum of <i>attribute</i>.
     */
    public static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> sum(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.sum(attribute);
    }
    
    /**
     * Project the query result to the aggregate max of <i>attribute</i>.
     */
    public static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> max(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.max(attribute);
    }
    
    /**
     * Project the query result to the aggregate min of <i>attribute</i>.
     */
    public static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> min(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.min(attribute);
    }
    
    /**
     * Project the query result to the aggregate max of <i>attribute</i>.
     */
    public static <E, T extends Comparable<? super T>> MetaJpaConstructor<E,Option<T>,Option<T>> greatest(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.greatest(attribute);
    }
    
    /**
     * Project the query result to the aggregate min of <i>attribute</i>.
     */
    public static <E, T extends Comparable<? super T>> MetaJpaConstructor<E,Option<T>,Option<T>> least(SingularAttribute<? super E, T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.least(attribute);
    }

    /**
     * Project the query result to the value of <i>attribute</i>.
     */
    public static <E, T> MetaJpaConstructor<E,T,T> value(SingularAttribute<? super E, ? extends T> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.value(attribute);
    }

    /**
     * Project the query result to the value of <i>attribute</i>.
     */
    public static <E, T> MetaJpaConstructor<E,T,T> value(PluralAttribute<? super E, ? extends T, ?> attribute) {
        checkOptionalAttributes(attribute);
        return Constructors.value(attribute);
    }
    
    /**
     * Project the query result to the pair value of values <i>left</i> and <i>right</i>.
     */
    public static <E, LEFT, RIGHT> MetaJpaConstructor<E,Pair<LEFT,RIGHT>,Map.Entry<? extends LEFT,? extends RIGHT>> pair(Attribute<? super E, ? extends LEFT> left, Attribute<? super E, ? extends RIGHT> right) {
        checkOptionalAttributes(left);
        checkOptionalAttributes(right);
        return Constructors.pair(left, right);
    }

    /**
     * Project the query result to the value of <i>t1</i>.
     */
    @SuppressWarnings("unchecked")
    public static <E, T1> MetaJpaConstructor<E,Tuple1<T1>,Tuple1<T1>> tuple(Attribute<? super E, ? extends T1> t1) {
        return makeTuple(t1);
    }

    /**
     * Project the query result to the values <i>t1</i> and <i>t2</i>.
     */
    @SuppressWarnings("unchecked")
    public static <E, T1, T2> MetaJpaConstructor<E,Tuple2<T1, T2>,Tuple2<T1,T2>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2) {
        return makeTuple(t1, t2);
    }

    /**
     * Project the query result to the values <i>t1</i> and <i>t2</i> and <i>t3</i>.
     */
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3> MetaJpaConstructor<E,Tuple3<T1, T2, T3>,Tuple3<T1, T2, T3>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3) {
        return makeTuple(t1, t2, t3);
    }

    /**
     * Project the query result to the values <i>t1</i> and <i>t2</i> and <i>t3</i> and <i>t4</i>.
     */
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4> MetaJpaConstructor<E,Tuple4<T1, T2, T3, T4>,Tuple4<T1, T2, T3, T4>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4) {
        return makeTuple(t1, t2, t3, t4);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5> MetaJpaConstructor<E,Tuple5<T1, T2, T3, T4, T5>,Tuple5<T1, T2, T3, T4, T5>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5) {
        return makeTuple(t1, t2, t3, t4, t5);
    }

    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6> MetaJpaConstructor<E,Tuple6<T1, T2, T3, T4, T5, T6>,Tuple6<T1, T2, T3, T4, T5, T6>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6) {
        return makeTuple(t1, t2, t3, t4, t5, t6);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7> MetaJpaConstructor<E,Tuple7<T1, T2, T3, T4, T5, T6, T7>,Tuple7<T1, T2, T3, T4, T5, T6, T7>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8> MetaJpaConstructor<E,Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>,Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9> MetaJpaConstructor<E,Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>,Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MetaJpaConstructor<E,Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>,Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MetaJpaConstructor<E,Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>,Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MetaJpaConstructor<E,Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>,Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MetaJpaConstructor<E,Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>,Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MetaJpaConstructor<E,Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>,Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MetaJpaConstructor<E,Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>,Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MetaJpaConstructor<E,Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>,Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MetaJpaConstructor<E,Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>,Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MetaJpaConstructor<E,Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>,Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MetaJpaConstructor<E,Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>,Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MetaJpaConstructor<E,Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>,Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MetaJpaConstructor<E,Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>,Tuple21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MetaJpaConstructor<E,Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>,Tuple22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23> MetaJpaConstructor<E,Tuple23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23>,Tuple23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24> MetaJpaConstructor<E,Tuple24<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24>,Tuple24<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25> MetaJpaConstructor<E,Tuple25<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25>,Tuple25<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26> MetaJpaConstructor<E,Tuple26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26>,Tuple26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27> MetaJpaConstructor<E,Tuple27<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27>,Tuple27<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28> MetaJpaConstructor<E,Tuple28<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28>,Tuple28<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29> MetaJpaConstructor<E,Tuple29<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29>,Tuple29<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30> MetaJpaConstructor<E,Tuple30<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30>,Tuple30<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31> MetaJpaConstructor<E,Tuple31<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31>,Tuple31<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32> MetaJpaConstructor<E,Tuple32<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32>,Tuple32<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33> MetaJpaConstructor<E,Tuple33<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33>,Tuple33<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34> MetaJpaConstructor<E,Tuple34<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34>,Tuple34<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35> MetaJpaConstructor<E,Tuple35<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35>,Tuple35<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36> MetaJpaConstructor<E,Tuple36<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36>,Tuple36<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37> MetaJpaConstructor<E,Tuple37<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37>,Tuple37<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38> MetaJpaConstructor<E,Tuple38<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38>,Tuple38<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39> MetaJpaConstructor<E,Tuple39<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39>,Tuple39<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38, Attribute<? super E, ? extends T39> t39) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40> MetaJpaConstructor<E,Tuple40<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40>,Tuple40<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38, Attribute<? super E, ? extends T39> t39, Attribute<? super E, ? extends T40> t40) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39, t40);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41> MetaJpaConstructor<E,Tuple41<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41>,Tuple41<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38, Attribute<? super E, ? extends T39> t39, Attribute<? super E, ? extends T40> t40, Attribute<? super E, ? extends T41> t41) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39, t40, t41);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42> MetaJpaConstructor<E,Tuple42<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42>,Tuple42<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38, Attribute<? super E, ? extends T39> t39, Attribute<? super E, ? extends T40> t40, Attribute<? super E, ? extends T41> t41, Attribute<? super E, ? extends T42> t42) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39, t40, t41, t42);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42, T43> MetaJpaConstructor<E,Tuple43<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42, T43>,Tuple43<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42, T43>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38, Attribute<? super E, ? extends T39> t39, Attribute<? super E, ? extends T40> t40, Attribute<? super E, ? extends T41> t41, Attribute<? super E, ? extends T42> t42, Attribute<? super E, ? extends T43> t43) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39, t40, t41, t42, t43);
    }
    
    @SuppressWarnings("unchecked")
    public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42, T43, T44> MetaJpaConstructor<E,Tuple44<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42, T43, T44>,Tuple44<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, T33, T34, T35, T36, T37, T38, T39, T40, T41, T42, T43, T44>> tuple(Attribute<? super E, ? extends T1> t1, Attribute<? super E, ? extends T2> t2, Attribute<? super E, ? extends T3> t3, Attribute<? super E, ? extends T4> t4, Attribute<? super E, ? extends T5> t5, Attribute<? super E, ? extends T6> t6, Attribute<? super E, ? extends T7> t7, Attribute<? super E, ? extends T8> t8, Attribute<? super E, ? extends T9> t9, Attribute<? super E, ? extends T10> t10, Attribute<? super E, ? extends T11> t11, Attribute<? super E, ? extends T12> t12, Attribute<? super E, ? extends T13> t13, Attribute<? super E, ? extends T14> t14, Attribute<? super E, ? extends T15> t15, Attribute<? super E, ? extends T16> t16, Attribute<? super E, ? extends T17> t17, Attribute<? super E, ? extends T18> t18, Attribute<? super E, ? extends T19> t19, Attribute<? super E, ? extends T20> t20, Attribute<? super E, ? extends T21> t21, Attribute<? super E, ? extends T22> t22, Attribute<? super E, ? extends T23> t23, Attribute<? super E, ? extends T24> t24, Attribute<? super E, ? extends T25> t25, Attribute<? super E, ? extends T26> t26, Attribute<? super E, ? extends T27> t27, Attribute<? super E, ? extends T28> t28, Attribute<? super E, ? extends T29> t29, Attribute<? super E, ? extends T30> t30, Attribute<? super E, ? extends T31> t31, Attribute<? super E, ? extends T32> t32, Attribute<? super E, ? extends T33> t33, Attribute<? super E, ? extends T34> t34, Attribute<? super E, ? extends T35> t35, Attribute<? super E, ? extends T36> t36, Attribute<? super E, ? extends T37> t37, Attribute<? super E, ? extends T38> t38, Attribute<? super E, ? extends T39> t39, Attribute<? super E, ? extends T40> t40, Attribute<? super E, ? extends T41> t41, Attribute<? super E, ? extends T42> t42, Attribute<? super E, ? extends T43> t43, Attribute<? super E, ? extends T44> t44) {
        return makeTuple(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35, t36, t37, t38, t39, t40, t41, t42, t43, t44);
    }
    
    private static <E, T extends Tuple> MetaJpaConstructor<E,T,T> makeTuple(@SuppressWarnings("unchecked") Attribute<? super E,?>... attributes) {
        for (Attribute<? super E, ?> a: attributes) {
            checkOptionalAttributes(a);
        }
        return Constructors.tuple(attributes);
    }

}
