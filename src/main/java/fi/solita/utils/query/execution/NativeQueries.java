package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.util.Collection;
import java.util.List;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.NativeQueryExecutor;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.generation.NativeQuery;

public class NativeQueries {

    private final NativeQueryExecutor queryExecutor;

    private final TypeProvider typeProvider;

    public NativeQueries(NativeQueryExecutor queryExecutor, TypeProvider typeProvider) {
        this.queryExecutor = queryExecutor;
        this.typeProvider = typeProvider;
    }
    
    public int execute(NativeQuery<Void> query) {
        return queryExecutor.execute(query);
    }

    public long count(NativeQuery<?> query) {
        @SuppressWarnings("unchecked")
        Option<Type<?>> type = (Option<Type<?>>)(Object)Some(typeProvider.type(long.class));
        return find(new NativeQuery.NativeQueryT1<Long>("select count(*) c from (" + query.query + ")", newList(Pair.of("c", type)), query.params)).get();
    }

    public boolean exists(NativeQuery<?> query) {
        // TODO: optimize?
        return count(query) > 0;
    }

    public <T> T get(NativeQuery<? extends T> query) {
        return find(query).get();
    }

    public <T, P> P get(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return head(map(constructor, find(query)));
    }

    public <T> Option<T> find(NativeQuery<? extends T> query) {
        return queryExecutor.find(query);
    }

    public <T, P> Option<P> find(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return headOption(map(constructor, queryExecutor.find(query)));
    }

    public <T> Option<T> findFirst(NativeQuery<? extends T> query) {
        return headOption(getMany(query, Page.FIRST.withSize(1)));
    }
    
    public <T, P> Option<P> findFirst(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        List<T> t = getMany(query, Page.FIRST.withSize(1));
        if (t.isEmpty()) {
            return None();
        }
        return Some(constructor.apply(head(t)));
    }

    public <T> Collection<T> getMany(NativeQuery<? extends T> query) {
        return getMany(query, Page.NoPaging);
    }

    public <T, P> List<P> getMany(NativeQuery<? extends T> query, Apply<T, P> constructor) {
        return getMany(query, Page.NoPaging, constructor);
    }

    public <T> List<T> getMany(NativeQuery<? extends T> query, Page page) {
        return queryExecutor.getMany(query, page);
    }

    public <T, P> List<P> getMany(NativeQuery<? extends T> query, Page page, Apply<T, P> constructor) {
        return newList(map(constructor, getMany(query, page)));
    }
}
