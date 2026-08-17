package fi.solita.utils.query.db;

import java.sql.Connection;
import java.util.Optional;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Tuple3;

public interface TableInClauseOptimization {
    public Optional<Tuple3<String, Class<?>, Apply<Connection, Iterable<Object>>>> getSqlTypeAndValues(Iterable<?> vals);
}
