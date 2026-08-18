package fi.solita.utils.query.db;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Function;

import fi.solita.utils.functional.Tuple3;

public interface TableInClauseOptimization {
    public Optional<Tuple3<String, Class<?>, Function<Connection, Iterable<Object>>>> getSqlTypeAndValues(Iterable<?> vals);
}
