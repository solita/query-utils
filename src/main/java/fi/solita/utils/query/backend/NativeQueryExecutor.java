package fi.solita.utils.query.backend;

import java.util.List;

import fi.solita.utils.query.Page;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.generation.NativeQuery;

public interface NativeQueryExecutor {
    int execute(NativeQuery<Void> query);
    <T> Option<T> find(NativeQuery<? extends T> query);
    <T> List<T> getMany(NativeQuery<? extends T> query, Page page);
}