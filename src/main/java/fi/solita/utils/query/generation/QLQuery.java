package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Option.Some;

import java.util.Map;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.backend.Type;

public class QLQuery<T> {
    public final String query;
    public final Map<String, Pair<?, Option<Type<?>>>> params;

    public static final <T> QLQuery<T> of(String query) {
        return new QLQuery<T>(query, Collections.<String, Pair<?, Option<Type<?>>>>emptyMap());
    }

    private QLQuery(String query, Map<String, Pair<?, Option<Type<?>>>> params) {
        this.query = query;
        this.params = params;
    }

    public QLQuery<T> setParameter(String name, Object val) {
        return new QLQuery<T>(query, newMap(cons(Pair.of(name, Pair.of(val, Option.<Type<?>>None())), params.entrySet())));
    }

    public QLQuery<T> setParameter(String name, Object val, Type<?> type) {
        @SuppressWarnings("unchecked")
        Option<Type<?>> t = (Option<Type<?>>)(Object)Some(type);
        return new QLQuery<T>(query, newMap(cons(Pair.of(name, Pair.of(val, t)), params.entrySet())));
    }
}