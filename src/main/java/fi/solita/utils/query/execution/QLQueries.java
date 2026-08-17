package fi.solita.utils.query.execution;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOptional;
import static fi.solita.utils.functional.Functional.map;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.QLQueryExecutor;
import fi.solita.utils.query.generation.QLQuery;

public class QLQueries {

    private final QLQueryExecutor queryExecutor;

    public QLQueries(QLQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public long count(QLQuery<?> query) {
        // TODO: optimize
        return queryExecutor.getMany(query, Page.NoPaging).size();
    }

    public boolean exists(QLQuery<?> query) {
        // TODO: optimize?
        return count(query) > 0;
    }

    public <T> T get(QLQuery<T> query) {
        return find(query).get();
    }

    public <T, P> P get(QLQuery<T> query, Apply<T, P> constructor) {
        return find(query).map(constructor).get();
    }

    public <T> Optional<T> find(QLQuery<T> query) {
        return queryExecutor.find(query);
    }

    public <T, P> Optional<P> find(QLQuery<T> query, Apply<T, P> constructor) {
        return queryExecutor.find(query).map(constructor);
    }

    public <T> Optional<T> findFirst(QLQuery<T> query) {
        return headOptional(getMany(query, Page.FIRST.withSize(1)));
    }
    
    public <T, P> Optional<P> findFirst(QLQuery<T> query, Apply<T, P> constructor) {
        List<T> t = getMany(query, Page.FIRST.withSize(1));
        if (t.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(constructor.apply(head(t)));
    }

    public <T> Collection<T> getMany(QLQuery<T> query) {
        return getMany(query, Page.NoPaging);
    }

    public <T, P> Collection<P> getMany(QLQuery<T> query, Apply<T, P> constructor) {
        return getMany(query, Page.NoPaging, constructor);
    }

    public <T> List<T> getMany(QLQuery<T> query, Page page) {
        return queryExecutor.getMany(query, page);
    }

    public <T, P> List<P> getMany(QLQuery<T> query, Page page, Apply<T, P> constructor) {
        return newList(map(constructor, getMany(query, page)));
    }
}
