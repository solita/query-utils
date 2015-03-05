package fi.solita.utils.query.generation;


import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Option.Some;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
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
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.functional.Tuple4;
import fi.solita.utils.functional.Tuple5;
import fi.solita.utils.functional.Tuple6;
import fi.solita.utils.functional.Tuple7;
import fi.solita.utils.functional.Tuple8;
import fi.solita.utils.functional.Tuple9;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.backend.Type;

public abstract class NativeQuery<T> {

    public static final String ENTITY_RETURN_VALUE = "-";

    public final String query;
    public final Map<String, Pair<?, Option<Type<?>>>> params;
    public final List<Pair<String, Option<Type<?>>>> retvals;

    public NativeQuery(String query, Map<String, Pair<?, Option<Type<?>>>> params, List<Pair<String, Option<Type<?>>>> retvals) {
        this.query = query;
        this.params = params;
        this.retvals = retvals;
    }

    public static NativeQueryVoid of(String query) {
        return new NativeQueryVoid(query, Collections.<String, Pair<?, Option<Type<?>>>>emptyMap());
    }

    public static class NativeQueryVoid extends ReturningNativeQuery<Void, NativeQuery<Void>> {
        public NativeQueryVoid(String query, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, Collections.<Pair<String, Option<Type<?>>>>emptyList(), params);
        }

        public <E extends IEntity<?>> NativeQuerySingleEntity<E> returns(Type<E> entityType) {
            return new NativeQuerySingleEntity<E>(query, withRetval(ENTITY_RETURN_VALUE, entityType), params);
        }
        
        public <E extends IEntity<?>> NativeQuerySingleEntity<Option<E>> returnsOptional(Type.Optional<E> entityType) {
            return new NativeQuerySingleEntity<Option<E>>(query, withRetval(ENTITY_RETURN_VALUE, entityType), params);
        }
        
        public NativeQueryT1<Object> returns(String alias) {
            return new NativeQueryT1<Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT1<T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT1<T>(query, withRetval(alias, type), params);
        }

        @Override
        protected NativeQuery<Void> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryVoid(query, params);
        }
    }

    public static class NativeQuerySingleEntity<T> extends ReturningNativeQuery<T, NativeQuerySingleEntity<T>> {
        public NativeQuerySingleEntity(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        @Override
        protected NativeQuerySingleEntity<T> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQuerySingleEntity<T>(query, retvals, params);
        }
    }

    public static class NativeQueryT1<T> extends ReturningNativeQuery<T, NativeQueryT1<T>> {
        public NativeQueryT1(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryPair<T,Object> returns(String alias) {
            return new NativeQueryPair<T,Object>(query, withRetval(alias), params);
        }

        public <R> NativeQueryPair<T,R> returns(String alias, Type<? extends R> type) {
            return new NativeQueryPair<T,R>(query, withRetval(alias, type), params);
        }

        @Override
        protected NativeQueryT1<T> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT1<T>(query, retvals, params);
        }
    }

    public static class NativeQueryPair<L,R> extends ReturningNativeQuery<Pair<L,R>, NativeQueryPair<L,R>> {
        public NativeQueryPair(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT3<L,R,Object> returns(String alias) {
            return new NativeQueryT3<L,R,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT3<L,R,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT3<L,R,T>(query, withRetval(alias, type), params);
        }

        @Override
        protected NativeQueryPair<L, R> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryPair<L, R>(query, retvals, params);
        }
    }

    public static class NativeQueryT3<T1,T2,T3> extends ReturningNativeQuery<Tuple3<T1,T2,T3>, NativeQueryT3<T1,T2,T3>> {
        public NativeQueryT3(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT4<T1,T2,T3,Object> returns(String alias) {
            return new NativeQueryT4<T1,T2,T3,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT4<T1,T2,T3,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT4<T1,T2,T3,T>(query, withRetval(alias, type), params);
        }

        @Override
        protected NativeQueryT3<T1, T2, T3> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT3<T1, T2, T3>(query, retvals, params);
        }
    }

    public static class NativeQueryT4<T1,T2,T3,T4> extends ReturningNativeQuery<Tuple4<T1,T2,T3,T4>, NativeQueryT4<T1,T2,T3,T4>> {
        public NativeQueryT4(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT5<T1,T2,T3,T4,Object> returns(String alias) {
            return new NativeQueryT5<T1,T2,T3,T4,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT5<T1,T2,T3,T4,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT5<T1,T2,T3,T4,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT4<T1, T2, T3, T4> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT4<T1, T2, T3, T4>(query, retvals, params);
        }
    }

    public static class NativeQueryT5<T1,T2,T3,T4,T5> extends ReturningNativeQuery<Tuple5<T1,T2,T3,T4,T5>, NativeQueryT5<T1,T2,T3,T4,T5>> {
        public NativeQueryT5(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT6<T1,T2,T3,T4,T5,Object> returns(String alias) {
            return new NativeQueryT6<T1,T2,T3,T4,T5,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT6<T1,T2,T3,T4,T5,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT6<T1,T2,T3,T4,T5,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT5<T1, T2, T3, T4, T5> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT5<T1, T2, T3, T4, T5>(query, retvals, params);
        }
    }

    public static class NativeQueryT6<T1,T2,T3,T4,T5,T6> extends ReturningNativeQuery<Tuple6<T1,T2,T3,T4,T5,T6>, NativeQueryT6<T1,T2,T3,T4,T5,T6>> {
        public NativeQueryT6(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT7<T1,T2,T3,T4,T5,T6,Object> returns(String alias) {
            return new NativeQueryT7<T1,T2,T3,T4,T5,T6,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT7<T1,T2,T3,T4,T5,T6,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT7<T1,T2,T3,T4,T5,T6,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT6<T1, T2, T3, T4, T5, T6> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT6<T1, T2, T3, T4, T5, T6>(query, retvals, params);
        }
    }

    public static class NativeQueryT7<T1,T2,T3,T4,T5,T6,T7> extends ReturningNativeQuery<Tuple7<T1,T2,T3,T4,T5,T6,T7>, NativeQueryT7<T1,T2,T3,T4,T5,T6,T7>> {
        public NativeQueryT7(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT8<T1,T2,T3,T4,T5,T6,T7,Object> returns(String alias) {
            return new NativeQueryT8<T1,T2,T3,T4,T5,T6,T7,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT8<T1,T2,T3,T4,T5,T6,T7,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT8<T1,T2,T3,T4,T5,T6,T7,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT7<T1, T2, T3, T4, T5, T6, T7> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT7<T1, T2, T3, T4, T5, T6, T7>(query, retvals, params);
        }
    }

    public static class NativeQueryT8<T1,T2,T3,T4,T5,T6,T7,T8> extends ReturningNativeQuery<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>, NativeQueryT8<T1,T2,T3,T4,T5,T6,T7,T8>> {
        public NativeQueryT8(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT9<T1,T2,T3,T4,T5,T6,T7,T8,Object> returns(String alias) {
            return new NativeQueryT9<T1,T2,T3,T4,T5,T6,T7,T8,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT9<T1,T2,T3,T4,T5,T6,T7,T8,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT9<T1,T2,T3,T4,T5,T6,T7,T8,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT8<T1, T2, T3, T4, T5, T6, T7, T8> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT8<T1, T2, T3, T4, T5, T6, T7, T8>(query, retvals, params);
        }
    }

    public static class NativeQueryT9<T1,T2,T3,T4,T5,T6,T7,T8,T9> extends ReturningNativeQuery<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>, NativeQueryT9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> {
        public NativeQueryT9(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT10<T1,T2,T3,T4,T5,T6,T7,T8,T9,Object> returns(String alias) {
            return new NativeQueryT10<T1,T2,T3,T4,T5,T6,T7,T8,T9,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT9<T1, T2, T3, T4, T5, T6, T7, T8, T9> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(query, retvals, params);
        }
    }


    public static class NativeQueryT10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> extends ReturningNativeQuery<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>, NativeQueryT10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> {
        public NativeQueryT10(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Object> returns(String alias) {
            return new NativeQueryT11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> extends ReturningNativeQuery<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>, NativeQueryT11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> {
        public NativeQueryT11(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Object> returns(String alias) {
            return new NativeQueryT12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> extends ReturningNativeQuery<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>, NativeQueryT12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> {
        public NativeQueryT12(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Object> returns(String alias) {
            return new NativeQueryT13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> extends ReturningNativeQuery<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>, NativeQueryT13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> {
        public NativeQueryT13(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Object> returns(String alias) {
            return new NativeQueryT14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> extends ReturningNativeQuery<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>, NativeQueryT14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> {
        public NativeQueryT14(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, Object> returns(String alias) {
            return new NativeQueryT15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> extends ReturningNativeQuery<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>, NativeQueryT15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> {
        public NativeQueryT15(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, Object> returns(String alias) {
            return new NativeQueryT16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> extends ReturningNativeQuery<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>, NativeQueryT16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> {
        public NativeQueryT16(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, Object> returns(String alias) {
            return new NativeQueryT17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17> extends ReturningNativeQuery<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>, NativeQueryT17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> {
        public NativeQueryT17(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, Object> returns(String alias) {
            return new NativeQueryT18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18> extends ReturningNativeQuery<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>, NativeQueryT18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> {
        public NativeQueryT18(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, Object> returns(String alias) {
            return new NativeQueryT19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19> extends ReturningNativeQuery<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>, NativeQueryT19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> {
        public NativeQueryT19(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, Object> returns(String alias) {
            return new NativeQueryT20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20> extends ReturningNativeQuery<Tuple20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>, NativeQueryT20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>> {
        public NativeQueryT20(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, Object> returns(String alias) {
            return new NativeQueryT21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21> extends ReturningNativeQuery<Tuple21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21>, NativeQueryT21<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21>> {
        public NativeQueryT21(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryT22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, Object> returns(String alias) {
            return new NativeQueryT22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, Object>(query, withRetval(alias), params);
        }

        public <T> NativeQueryT22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T> returns(String alias, Type<? extends T> type) {
            return new NativeQueryT22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T>(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>(query, retvals, params);
        }
    }
    
    public static class NativeQueryT22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22> extends ReturningNativeQuery<Tuple22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22>, NativeQueryT22<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,T22>> {
        public NativeQueryT22(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryBig returns(String alias) {
            return new NativeQueryBig(query, withRetval(alias), params);
        }

        public <T> NativeQueryBig returns(String alias, Type<? extends T> type) {
            return new NativeQueryBig(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryT22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryT22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>(query, retvals, params);
        }
    }

    public static class NativeQueryBig extends ReturningNativeQuery<Object[], NativeQueryBig> {
        public NativeQueryBig(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, retvals, params);
        }

        public NativeQueryBig returns(String alias) {
            return new NativeQueryBig(query, withRetval(alias), params);
        }

        public <T> NativeQueryBig returns(String alias, Type<? extends T> type) {
            return new NativeQueryBig(query, withRetval(alias, type), params);
        }
        
        @Override
        protected NativeQueryBig create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            return new NativeQueryBig(query, retvals, params);
        }
    }

    public static abstract class ReturningNativeQuery<T, THIS> extends NativeQuery<T> {
        public ReturningNativeQuery(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params) {
            super(query, params, retvals);
        }

        public THIS setParameter(String name, Object val) {
            return create(query, retvals, newMap(cons(Pair.of(name, Pair.of(val, Option.<Type<?>>None())), params.entrySet())));
        }

        @SuppressWarnings("unchecked")
        public <C> THIS setParameter(String name, C val, Type<? extends C> type) {
            return create(query, retvals, newMap(cons(Pair.of(name, Pair.of(val, (Option<Type<?>>)(Object)Some(type))), params.entrySet())));
        }

        public THIS setParameterList(String name, Collection<?> val) {
            return create(query, retvals, newMap(cons(Pair.of(name, Pair.of(val, Option.<Type<?>>None())), params.entrySet())));
        }

        @SuppressWarnings("unchecked")
        public <C> THIS setParameterList(String name, Collection<C> val, Type<? extends C> type) {
            return create(query, retvals, newMap(cons(Pair.of(name, Pair.of(val, (Option<Type<?>>)(Object)Some(type))), params.entrySet())));
        }

        protected List<Pair<String, Option<Type<?>>>> withRetval(String alias) {
            return newList(concat(retvals, newSet(Pair.of(alias, Option.<Type<?>>None()))));
        }

        @SuppressWarnings("unchecked")
        protected List<Pair<String, Option<Type<?>>>> withRetval(String alias, Type<?> type) {
            return newList(concat(retvals, newSet(Pair.of(alias, (Option<Type<?>>)(Object)Some(type)))));
        }
        
        protected abstract THIS create(String query, List<Pair<String, Option<Type<?>>>> retvals, Map<String, Pair<?, Option<Type<?>>>> params);
    }
}
