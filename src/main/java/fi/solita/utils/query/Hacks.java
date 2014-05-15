package fi.solita.utils.query;

import static fi.solita.utils.functional.Option.Some;
import fi.solita.utils.query.backend.hibernate.TableValueType;
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
    
    public static final void registerTableType(Class<?> javaType, String typename) {
        TableValueType.registeredTableTypes.put(javaType, typename);
    }
    
    public static void enableWrappingComparedNumbersToString(String unwrappingFunctionName) {
        Restrict.wrapComparedNumbersWithFunction = Some(unwrappingFunctionName);
    }
}
