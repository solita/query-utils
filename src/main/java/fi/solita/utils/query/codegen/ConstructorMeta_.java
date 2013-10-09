package fi.solita.utils.query.codegen;

import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.mkString;

import java.util.Arrays;
import java.util.List;

import javax.persistence.metamodel.Attribute;

import fi.solita.utils.codegen.MetaConstructor;
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

public interface ConstructorMeta_<OWNER,R,PARAMS> extends fi.solita.utils.codegen.ConstructorMeta_<PARAMS,R> {
    public static abstract class F0<OWNER,R> extends MetaConstructor.F0<R> implements ConstructorMeta_<OWNER,R,Tuple0> {
        public F0(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F1<OWNER,T1,R> extends MetaConstructor.F1<T1,R> implements ConstructorMeta_<OWNER,R,T1> {
        public F1(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F2<OWNER,T1,T2,R> extends MetaConstructor.F2<T1,T2,R> implements ConstructorMeta_<OWNER,R,Tuple2<T1,T2>> {
        public F2(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F3<OWNER,T1,T2,T3,R> extends MetaConstructor.F3<T1,T2,T3,R> implements ConstructorMeta_<OWNER,R,Tuple3<T1,T2,T3>> {
        public F3(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F4<OWNER,T1,T2,T3,T4,R> extends MetaConstructor.F4<T1,T2,T3,T4,R> implements ConstructorMeta_<OWNER,R,Tuple4<T1,T2,T3,T4>> {
        public F4(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F5<OWNER,T1,T2,T3,T4,T5,R> extends MetaConstructor.F5<T1,T2,T3,T4,T5,R> implements ConstructorMeta_<OWNER,R,Tuple5<T1,T2,T3,T4,T5>> {
        public F5(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F6<OWNER,T1,T2,T3,T4,T5,T6,R> extends MetaConstructor.F6<T1,T2,T3,T4,T5,T6,R> implements ConstructorMeta_<OWNER,R,Tuple6<T1,T2,T3,T4,T5,T6>> {
        public F6(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F7<OWNER,T1,T2,T3,T4,T5,T6,T7,R> extends MetaConstructor.F7<T1,T2,T3,T4,T5,T6,T7,R> implements ConstructorMeta_<OWNER,R,Tuple7<T1,T2,T3,T4,T5,T6,T7>> {
        public F7(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F8<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,R> extends MetaConstructor.F8<T1,T2,T3,T4,T5,T6,T7,T8,R> implements ConstructorMeta_<OWNER,R,Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> {
        public F8(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F9<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,R> extends MetaConstructor.F9<T1,T2,T3,T4,T5,T6,T7,T8,T9,R> implements ConstructorMeta_<OWNER,R,Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> {
        public F9(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F10<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> extends MetaConstructor.F10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> implements ConstructorMeta_<OWNER,R,Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> {
        public F10(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F11<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> extends MetaConstructor.F11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> implements ConstructorMeta_<OWNER,R,Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> {
        public F11(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F12<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> extends MetaConstructor.F12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> implements ConstructorMeta_<OWNER,R,Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> {
        public F12(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F13<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> extends MetaConstructor.F13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> implements ConstructorMeta_<OWNER,R,Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> {
        public F13(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F14<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> extends MetaConstructor.F14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R> implements ConstructorMeta_<OWNER,R,Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> {
        public F14(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F15<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> extends MetaConstructor.F15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R> implements ConstructorMeta_<OWNER,R,Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> {
        public F15(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F16<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R> extends MetaConstructor.F16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R> implements ConstructorMeta_<OWNER,R,Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> {
        public F16(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F17<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R> extends MetaConstructor.F17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R> implements ConstructorMeta_<OWNER,R,Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> {
        public F17(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F18<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R> extends MetaConstructor.F18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R> implements ConstructorMeta_<OWNER,R,Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> {
        public F18(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F19<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R> extends MetaConstructor.F19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R> implements ConstructorMeta_<OWNER,R,Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> {
        public F19(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F20<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,R> extends MetaConstructor.F20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,R> implements ConstructorMeta_<OWNER,R,Tuple20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>> {
        public F20(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F21<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,R> extends MetaConstructor.F21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,R> implements ConstructorMeta_<OWNER,R,Tuple21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21>> {
        public F21(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }
    public static abstract class F22<OWNER,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,R> extends MetaConstructor.F22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22,R> implements ConstructorMeta_<OWNER,R,Tuple22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22>> {
        public F22(Class<?> clazz, Class<?>... argClasses) {
            super(clazz, argClasses);
        }
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.asList(getMember().getParameterTypes());
        }
    }

    List<Attribute<?, ?>> getParameters();
    List<Integer> getIndexesOfIdWrappingParameters();
    List<Class<?>> getConstructorParameterTypes();
    
    public static class Helper {
        public static final String toString(ConstructorMeta_<?,?,?> c) {
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
