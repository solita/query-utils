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
     * To use this, you need to be able to modify the generated SQL.
     * In Hibernate, this can be done with an Interceptor:
     *
     * <code><pre>
     * public class EntityInterceptor extends EmptyInterceptor {
     *     static {
     *         Hacks.enableOracleTableInClause();
     *     }
     *     @Override
     *     public String onPrepareStatement(String sql) {
     *         return Hacks.processOracleTableInClauseSql(super.onPrepareStatement(sql));
     *     }
     * }
     * </pre></code>
     */
    public static void enableOracleTableInClause() {
        Table.enabled = true;
    }
    
    public static final String processOracleTableInClauseSql(String sql) {
        return Table.processSqlPattern.matcher(sql).replaceAll("select /*+ dynamic_sampling($3,2) */ $1$2$3");
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
