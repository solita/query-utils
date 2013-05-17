package fi.solita.utils.query.backend;

import java.util.List;

import fi.solita.utils.query.Page;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.generation.NativeQuery;

public interface NativeQueryExecutor {
    void execute(NativeQuery<Void> query);
    <T> Option<T> find(NativeQuery<T> query);
    <T> List<T> getMany(NativeQuery<T> query, Page page);
}