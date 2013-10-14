package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Functional.zip;

import java.util.List;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;

public abstract class TupleResultTransformers implements ResultTransformer {
    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }

    public static final ResultTransformer Tuple2(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Pair.of(m.get(a[0]), m.get(a[1]));
            }
        };
    }

    public static final ResultTransformer Tuple3(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]));
            }
        };
    }

    public static final ResultTransformer Tuple4(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]));
            }
        };
    }

    public static final ResultTransformer Tuple5(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]), m.get(a[4]));
            }
        };
    }

    public static final ResultTransformer Tuple6(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]), m.get(a[4]), m.get(a[5]));
            }
        };
    }
    
    public static final ResultTransformer Tuple7(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]), m.get(a[4]), m.get(a[5]), m.get(a[6]));
            }
        };
    }
    
    public static final ResultTransformer Tuple8(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]), m.get(a[4]), m.get(a[5]), m.get(a[6]), m.get(a[7]));
            }
        };
    }
    
    public static final ResultTransformer Tuple9(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]), m.get(a[4]), m.get(a[5]), m.get(a[6]), m.get(a[7]), m.get(a[8]));
            }
        };
    }
    
    public static final ResultTransformer Tuple10(final String[] a) {
        return new TupleResultTransformers() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                Map<String, Object> m = newMap(zip(aliases, tuple));
                return Tuple.of(m.get(a[0]), m.get(a[1]), m.get(a[2]), m.get(a[3]), m.get(a[4]), m.get(a[5]), m.get(a[6]), m.get(a[7]), m.get(a[8]), m.get(a[9]));
            }
        };
    }
    
    // TODO: more 
}