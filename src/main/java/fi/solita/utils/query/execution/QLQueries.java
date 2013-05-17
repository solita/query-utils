package fi.solita.utils.query.execution;

import static fi.solita.utils.query.QueryUtils.NoPaging;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.util.List;

import fi.solita.utils.query.Page;
import fi.solita.utils.query.backend.QLQueryExecutor;
import fi.solita.utils.query.generation.QLQuery;
import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;

public class QLQueries {

    private final QLQueryExecutor queryExecutor;

    public QLQueries(QLQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public long count(QLQuery<?> query) {
        // TODO: optimize
        return queryExecutor.getMany(query, NoPaging).size();
    }

    public boolean exists(QLQuery<?> query) {
        // TODO: optimize?
        return count(query) > 0;
    }

    public <T> T get(QLQuery<T> query) {
        return find(query).get();
    }

    public <T, P> P get(QLQuery<T> query, Apply<T, P> constructor) {
        return head(map(find(query), constructor));
    }

    public <T> Option<T> find(QLQuery<T> query) {
        return queryExecutor.find(query);
    }

    public <T, P> Option<P> find(QLQuery<T> query, Apply<T, P> constructor) {
        return headOption(map(queryExecutor.find(query), constructor));
    }

    public <T> Option<T> findFirst(QLQuery<T> query) {
        return headOption(getList(query, Page.FIRST.withSize(1)));
    }

    public <T, P> Option<P> findFirst(QLQuery<T> query, Apply<T, P> constructor) {
        List<T> t = getList(query, Page.FIRST.withSize(1));
        if (t.isEmpty()) {
            return None();
        }
        return Some(constructor.apply(head(t)));
    }

    public <T> List<T> getList(QLQuery<T> query) {
        return getList(query, NoPaging);
    }

    public <T, P> List<P> getList(QLQuery<T> query, Apply<T, P> constructor) {
        return getList(query, NoPaging, constructor);
    }

    public <T> List<T> getList(QLQuery<T> query, Page page) {
        return queryExecutor.getMany(query, page);
    }

    public <T, P> List<P> getList(QLQuery<T> query, Page page, Apply<T, P> constructor) {
        return newList(map(getList(query, page), constructor));
    }
}
