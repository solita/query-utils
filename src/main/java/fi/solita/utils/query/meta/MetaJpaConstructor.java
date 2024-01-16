package fi.solita.utils.query.meta;

import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.mkString;

import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.Attribute;

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
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.functional.Tuple5;
import fi.solita.utils.functional.Tuple6;
import fi.solita.utils.functional.Tuple7;
import fi.solita.utils.functional.Tuple8;
import fi.solita.utils.functional.Tuple9;
import fi.solita.utils.meta.MetaConstructor;
import fi.solita.utils.meta.MetaConstructors;

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
    public static abstract class C2<OWNER,T1,T2,R> extends MetaConstructors.C2<T1,T2,R> implements MetaJpaConstructor<OWNER,R,Map.Entry<? extends T1,? extends T2>> {
        public C2(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C3<OWNER,T1,T2,T3,R> extends MetaConstructors.C3<T1,T2,T3,R> implements MetaJpaConstructor<OWNER,R,Tuple3<? extends T1,? extends T2,? extends T3>> {
        public C3(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C4<OWNER,T1,T2,T3,T4,R> extends MetaConstructors.C4<T1,T2,T3,T4,R> implements MetaJpaConstructor<OWNER,R,Tuple4<? extends T1,? extends T2,? extends T3,? extends T4>> {
        public C4(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C5<OWNER,T1,T2,T3,T4,T5,R> extends MetaConstructors.C5<T1,T2,T3,T4,T5,R> implements MetaJpaConstructor<OWNER,R,Tuple5<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5>> {
        public C5(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C6<OWNER,T1,T2,T3,T4,T5,T6,R> extends MetaConstructors.C6<T1,T2,T3,T4,T5,T6,R> implements MetaJpaConstructor<OWNER,R,Tuple6<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6>> {
        public C6(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C7<OWNER,T1,T2,T3,T4,T5,T6,T7,R> extends MetaConstructors.C7<T1,T2,T3,T4,T5,T6,T7,R> implements MetaJpaConstructor<OWNER,R,Tuple7<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7>> {
        public C7(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C8<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,R> extends MetaConstructors.C8<T1,T2,T3,T4,T5,T6,T7,T8,R> implements MetaJpaConstructor<OWNER,R,Tuple8<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8>> {
        public C8(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C9<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,R> extends MetaConstructors.C9<T1,T2,T3,T4,T5,T6,T7,T8,T9,R> implements MetaJpaConstructor<OWNER,R,Tuple9<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9>> {
        public C9(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C10<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> extends MetaConstructors.C10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> implements MetaJpaConstructor<OWNER,R,Tuple10<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10>> {
        public C10(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C11<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> extends MetaConstructors.C11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> implements MetaJpaConstructor<OWNER,R,Tuple11<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11>> {
        public C11(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C12<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> extends MetaConstructors.C12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> implements MetaJpaConstructor<OWNER,R,Tuple12<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12>> {
        public C12(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C13<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> extends MetaConstructors.C13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> implements MetaJpaConstructor<OWNER,R,Tuple13<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13>> {
        public C13(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C14<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> extends MetaConstructors.C14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> implements MetaJpaConstructor<OWNER,R,Tuple14<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14>> {
        public C14(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C15<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> extends MetaConstructors.C15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> implements MetaJpaConstructor<OWNER,R,Tuple15<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15>> {
        public C15(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C16<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R> extends MetaConstructors.C16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R> implements MetaJpaConstructor<OWNER,R,Tuple16<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16>> {
        public C16(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C17<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R> extends MetaConstructors.C17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R> implements MetaJpaConstructor<OWNER,R,Tuple17<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17>> {
        public C17(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C18<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R> extends MetaConstructors.C18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R> implements MetaJpaConstructor<OWNER,R,Tuple18<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18>> {
        public C18(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C19<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R> extends MetaConstructors.C19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R> implements MetaJpaConstructor<OWNER,R,Tuple19<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19>> {
        public C19(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C20<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,R> extends MetaConstructors.C20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,R> implements MetaJpaConstructor<OWNER,R,Tuple20<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20>> {
        public C20(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C21<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,R> extends MetaConstructors.C21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,R> implements MetaJpaConstructor<OWNER,R,Tuple21<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21>> {
        public C21(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C22<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,R> extends MetaConstructors.C22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,R> implements MetaJpaConstructor<OWNER,R,Tuple22<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22>> {
        public C22(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C23<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,R> extends MetaConstructors.C23<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,R> implements MetaJpaConstructor<OWNER,R,Tuple23<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23>> {
        public C23(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C24<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,R> extends MetaConstructors.C24<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,R> implements MetaJpaConstructor<OWNER,R,Tuple24<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24>> {
        public C24(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C25<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,R> extends MetaConstructors.C25<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,R> implements MetaJpaConstructor<OWNER,R,Tuple25<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25>> {
        public C25(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C26<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,R> extends MetaConstructors.C26<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,R> implements MetaJpaConstructor<OWNER,R,Tuple26<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26>> {
        public C26(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C27<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,R> extends MetaConstructors.C27<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,R> implements MetaJpaConstructor<OWNER,R,Tuple27<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27>> {
        public C27(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C28<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,R> extends MetaConstructors.C28<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,R> implements MetaJpaConstructor<OWNER,R,Tuple28<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28>> {
        public C28(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C29<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,R> extends MetaConstructors.C29<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,R> implements MetaJpaConstructor<OWNER,R,Tuple29<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29>> {
        public C29(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C30<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,R> extends MetaConstructors.C30<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,R> implements MetaJpaConstructor<OWNER,R,Tuple30<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30>> {
        public C30(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    public static abstract class C31<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,R> extends MetaConstructors.C31<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,R> implements MetaJpaConstructor<OWNER,R,Tuple31<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31>> {
        public C31(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    
    public static abstract class C32<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,R> extends MetaConstructors.C32<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,R> implements MetaJpaConstructor<OWNER,R,Tuple32<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32>> {
        public C32(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    
    public static abstract class C33<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,R> extends MetaConstructors.C33<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,R> implements MetaJpaConstructor<OWNER,R,Tuple33<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32,? extends T33>> {
        public C33(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    
    public static abstract class C34<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,R> extends MetaConstructors.C34<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,R> implements MetaJpaConstructor<OWNER,R,Tuple34<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32,? extends T33,? extends T34>> {
        public C34(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    
    public static abstract class C35<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,R> extends MetaConstructors.C35<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,R> implements MetaJpaConstructor<OWNER,R,Tuple35<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32,? extends T33,? extends T34,? extends T35>> {
        public C35(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    
    public static abstract class C36<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,R> extends MetaConstructors.C36<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,R> implements MetaJpaConstructor<OWNER,R,Tuple36<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32,? extends T33,? extends T34,? extends T35,? extends T36>> {
        public C36(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }

    public static abstract class C37<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,R> extends MetaConstructors.C37<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,R> implements MetaJpaConstructor<OWNER,R,Tuple37<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32,? extends T33,? extends T34,? extends T35,? extends T36,? extends T37>> {
        public C37(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }
    
    public static abstract class C38<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,T38,R> extends MetaConstructors.C38<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,T23,T24,T25,T26,T27,T28,T29,T30,T31,T32,T33,T34,T35,T36,T37,T38,R> implements MetaJpaConstructor<OWNER,R,Tuple38<? extends T1,? extends T2,? extends T3,? extends T4,? extends T5,? extends T6,? extends T7,? extends T8,? extends T9,? extends T10,? extends T11,? extends T12,? extends T13,? extends T14,? extends T15,? extends T16,? extends T17,? extends T18,? extends T19,? extends T20,? extends T21,? extends T22,? extends T23,? extends T24,? extends T25,? extends T26,? extends T27,? extends T28,? extends T29,? extends T30,? extends T31,? extends T32,? extends T33,? extends T34,? extends T35,? extends T36,? extends T37,? extends T38>> {
        public C38(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
    }

    List<Attribute<?, ?>> getParameters();
    List<Integer> getIndexesOfIdWrappingParameters();
    
    public static class Helper {
        public static final String toString(MetaJpaConstructor<?,?,?> c) {
            return className.apply(c.getClass()) + "(" + mkString(",", map(className, c.getConstructorParameterTypes())) + ")";
        }
        
        public static final Transformer<Class<?>,String> className = new Transformer<Class<?>,String>() {
            @Override
            public String transform(Class<?> source) {
                return source.getName().replaceAll("([^.])[^.]+\\.", "$1.");
            }
        };
    }
}
