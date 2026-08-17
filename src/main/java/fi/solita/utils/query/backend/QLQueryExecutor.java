package fi.solita.utils.query.backend;

import java.util.List;
import java.util.Optional;

import fi.solita.utils.query.Page;
import fi.solita.utils.query.generation.QLQuery;

public interface QLQueryExecutor {
    <T> Optional<T> find(QLQuery<T> query);
    <T> List<T> getMany(QLQuery<T> query, Page page);
}