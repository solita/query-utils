package fi.solita.utils.query;

import static fi.solita.utils.functional.Option.Some;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.entities.Table;
import fi.solita.utils.query.generation.Restrict;

public class Hacks {
    
    /**
     * Optimization for <i>in</i> clauses using Oracle
     * 
     * To use this, you need to register a function (works at least in Hibernate).
     * In your Dialect:
     * <code><pre>
     * registerFunction("dynamic_sampling", new NoArgSQLFunction("/*+ dynamic_sampling(query_utils,2) *
     * / query_utils.column_value", StandardBasicTypes.STRING, false));
     * </pre></code>
     *
     * And enable somewhere:
     * <code><pre>
     * static {
     *     Hacks.enableOracleTableInClause();
     * }
     * </pre></code>
     */
    public static void enableOracleTableInClause() {
        Table.enabled = true;
    }
    
    public static final Map<Class<?>, Pair<String, ? extends Function2<Connection,?,?>>> registeredTableTypesInternal = new ConcurrentHashMap<Class<?>, Pair<String,? extends Function2<Connection,?,?>>>();
    public static final <T> void registerTableType(Class<T> javaType, String typename, final Apply<T,?> converter) {
        registerTableType(javaType, typename, new Function2<Connection, T, Object>() {
            @Override
            public Object apply(Connection doesNotNeedThis, T t2) {
                return converter.apply(t2);
            }
        });
    }
    public static final <T> void registerTableType(Class<T> javaType, String typename, Function2<Connection,T,?> converter) {
        registeredTableTypesInternal.put(javaType, Pair.of(typename, converter));
    }
    
    public static void enableWrappingComparedNumbersToString(String unwrappingFunctionName) {
        Restrict.wrapComparedNumbersWithFunction = Some(unwrappingFunctionName);
    }
}
