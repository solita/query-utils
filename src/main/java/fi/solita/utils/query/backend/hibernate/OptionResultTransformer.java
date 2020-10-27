package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Functional.head;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.SemiGroups;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.generation.NativeQuery;

public class OptionResultTransformer implements ResultTransformer {

    private Map<String, Option<Type<?>>> retvals;

    public OptionResultTransformer(List<Pair<String, Option<Type<?>>>> retvals) {
        this.retvals = newMap(SemiGroups.<Option<Type<?>>>fail(), retvals);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }
    
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object[] ret = Arrays.copyOf(tuple, tuple.length);
        for (int i = 0; i < tuple.length; ++i) {
            Option<Type<?>> typeForValue = aliases.length == 1 && retvals.size() == 1 && head(retvals.keySet()) == NativeQuery.ENTITY_RETURN_VALUE ? head(retvals.values()) : retvals.get(aliases[i]);
            if (typeForValue.isDefined() && typeForValue.get() instanceof Type.Optional) {
                ret[i] = Option.of(ret[i]);
            }
        }
        return ret.length == 1 ? ret[0] : ret;
    }
}
