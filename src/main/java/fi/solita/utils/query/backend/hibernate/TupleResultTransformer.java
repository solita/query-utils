package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.zip;

import java.util.List;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.SemiGroups;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.functional.Tuple;

public final class TupleResultTransformer implements ResultTransformer {
    private final String[] a;

    public TupleResultTransformer(String[] aliases) {
        this.a = aliases;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Object> m = newMap(SemiGroups.fail(), zip(aliases, tuple));
        if (aliases.length == 2) {
            return Pair.of(m.get(a[0]), m.get(a[1]));
        } else {
            return Tuple.of(newArray(Object.class, map(getter(m), a)));
        }
    }
    
    static final Transformer<String,Object> getter(final Map<String,Object> map) {
        return new Transformer<String,Object>() {
            @Override
            public Object transform(String source) {
                return map.get(source);
            }
        };
    }
}