package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.FunctionalA.map;

import java.util.Arrays;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import fi.solita.utils.functional.Pair;
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
        List<String> aliasList = Arrays.asList(aliases);
        if (aliases.length == 2) {
            return Pair.of(tuple[aliasList.indexOf(a[0])], tuple[aliasList.indexOf(a[1])]);
        } else {
            return Tuple.of(newArray(Object.class, map(getter(aliasList, tuple), a)));
        }
    }
    
    static final Transformer<String,Object> getter(final List<String> aliasList, final Object[] tuple) {
        return new Transformer<String,Object>() {
            @Override
            public Object transform(String source) {
                return tuple[aliasList.indexOf(source)];
            }
        };
    }
}