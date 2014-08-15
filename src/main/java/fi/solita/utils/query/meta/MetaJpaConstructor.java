package fi.solita.utils.query.meta;

import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.mkString;

import java.util.List;

import javax.persistence.metamodel.Attribute;

import fi.solita.utils.meta.MetaConstructor;
import fi.solita.utils.meta.MetaConstructors;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple0;
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
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.functional.Tuple5;
import fi.solita.utils.functional.Tuple6;
import fi.solita.utils.functional.Tuple7;
import fi.solita.utils.functional.Tuple8;
import fi.solita.utils.functional.Tuple9;

public interface MetaJpaConstructor<OWNER,R,PARAMS> extends MetaConstructor<PARAMS,R> {
    public static abstract class C0<OWNER,R> extends MetaConstructors.C0<R> implements MetaJpaConstructor<OWNER,R,Tuple0> {
        public C0(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C1<OWNER,T1,R> extends MetaConstructors.C1<T1,R> implements MetaJpaConstructor<OWNER,R,T1> {
        public C1(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C2<OWNER,T1,T2,R> extends MetaConstructors.C2<T1,T2,R> implements MetaJpaConstructor<OWNER,R,Tuple2<T1,T2>> {
        public C2(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C3<OWNER,T1,T2,T3,R> extends MetaConstructors.C3<T1,T2,T3,R> implements MetaJpaConstructor<OWNER,R,Tuple3<T1,T2,T3>> {
        public C3(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C4<OWNER,T1,T2,T3,T4,R> extends MetaConstructors.C4<T1,T2,T3,T4,R> implements MetaJpaConstructor<OWNER,R,Tuple4<T1,T2,T3,T4>> {
        public C4(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C5<OWNER,T1,T2,T3,T4,T5,R> extends MetaConstructors.C5<T1,T2,T3,T4,T5,R> implements MetaJpaConstructor<OWNER,R,Tuple5<T1,T2,T3,T4,T5>> {
        public C5(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C6<OWNER,T1,T2,T3,T4,T5,T6,R> extends MetaConstructors.C6<T1,T2,T3,T4,T5,T6,R> implements MetaJpaConstructor<OWNER,R,Tuple6<T1,T2,T3,T4,T5,T6>> {
        public C6(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C7<OWNER,T1,T2,T3,T4,T5,T6,T7,R> extends MetaConstructors.C7<T1,T2,T3,T4,T5,T6,T7,R> implements MetaJpaConstructor<OWNER,R,Tuple7<T1,T2,T3,T4,T5,T6,T7>> {
        public C7(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C8<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,R> extends MetaConstructors.C8<T1,T2,T3,T4,T5,T6,T7,T8,R> implements MetaJpaConstructor<OWNER,R,Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> {
        public C8(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C9<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,R> extends MetaConstructors.C9<T1,T2,T3,T4,T5,T6,T7,T8,T9,R> implements MetaJpaConstructor<OWNER,R,Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> {
        public C9(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C10<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> extends MetaConstructors.C10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> implements MetaJpaConstructor<OWNER,R,Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> {
        public C10(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C11<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> extends MetaConstructors.C11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> implements MetaJpaConstructor<OWNER,R,Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> {
        public C11(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C12<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> extends MetaConstructors.C12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> implements MetaJpaConstructor<OWNER,R,Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> {
        public C12(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C13<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> extends MetaConstructors.C13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> implements MetaJpaConstructor<OWNER,R,Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> {
        public C13(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C14<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> extends MetaConstructors.C14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> implements MetaJpaConstructor<OWNER,R,Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> {
        public C14(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C15<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> extends MetaConstructors.C15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> implements MetaJpaConstructor<OWNER,R,Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> {
        public C15(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C16<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R> extends MetaConstructors.C16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R> implements MetaJpaConstructor<OWNER,R,Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> {
        public C16(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C17<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R> extends MetaConstructors.C17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R> implements MetaJpaConstructor<OWNER,R,Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> {
        public C17(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C18<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R> extends MetaConstructors.C18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R> implements MetaJpaConstructor<OWNER,R,Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> {
        public C18(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C19<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R> extends MetaConstructors.C19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R> implements MetaJpaConstructor<OWNER,R,Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> {
        public C19(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C20<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,R> extends MetaConstructors.C20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,R> implements MetaJpaConstructor<OWNER,R,Tuple20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>> {
        public C20(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C21<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,R> extends MetaConstructors.C21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,R> implements MetaJpaConstructor<OWNER,R,Tuple21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21>> {
        public C21(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C22<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,R> extends MetaConstructors.C22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,R> implements MetaJpaConstructor<OWNER,R,Tuple22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22>> {
        public C22(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }

    List<Attribute<?, ?>> getParameters();
    List<Integer> getIndexesOfIdWrappingParameters();
    
    public static class Helper {
        public static final String toString(MetaJpaConstructor<?,?,?> c) {
            return className.apply(c.getClass()) + "(" + mkString(",", map(c.getConstructorParameterTypes(), className)) + ")";
        }
        
        public static final Transformer<Class<?>,String> className = new Transformer<Class<?>,String>() {
            @Override
            public String transform(Class<?> source) {
                return source.getName().replaceAll("([^.])[^.]+\\.", "$1.");
            }
        };
    }
}
