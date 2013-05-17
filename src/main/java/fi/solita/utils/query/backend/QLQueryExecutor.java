package fi.solita.utils.query.backend;

import java.util.List;

import fi.solita.utils.query.Page;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.generation.QLQuery;

public interface QLQueryExecutor {
    <T> Option<T> find(QLQuery<T> query);
    <T> List<T> getMany(QLQuery<T> query, Page page);
}