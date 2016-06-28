package fi.solita.utils.query.db;

import java.sql.Connection;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;

public interface TableInClauseOptimization {
    public Option<Tuple3<String, Option<String>, Apply<Connection, Iterable<Object>>>> getSqlTypeAndValues(Iterable<?> vals);
}
