package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Option.Some;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.StandardBasicTypes;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.entities.Table;
import fi.solita.utils.query.generation.Restrict;

public class Hacks {
    
    /**
     * Optimization for <i>array-valued in clauses</i> using Oracle
     * 
     * Enable somewhere:
     * <code><pre>
     * static {
     *     Hacks.enableOracleTableInClause();
     * }
     * </pre></code>
     * 
     * and call Hacks.makeUnion from EntityInterceptor.prepareStatement
     */
    public static void enableOracleTableInClause() {
        Table.enabled = true;
    }
    
    public static final Map<Class<?>, Tuple3<String, Option<String>, ? extends Function2<Connection,?,?>>> registeredTableTypesInternal = new ConcurrentHashMap<Class<?>, Tuple3<String,Option<String>,? extends Function2<Connection,?,?>>>();
    public static final <T> void registerTableType(Class<T> javaType, String tableTypeName, Option<String> objectTypeName, final Apply<T,?> converter) {
        registerTableType(javaType, tableTypeName, objectTypeName, new Function2<Connection, T, Object>() {
            @Override
            public Object apply(Connection doesNotNeedThis, T t2) {
                return converter.apply(t2);
            }
        });
    }
    public static final <T> void registerTableType(Class<T> javaType, String tableTypeName, Option<String> objectTypeName, Function2<Connection,T,?> converter) {
        registeredTableTypesInternal.put(javaType, Tuple.of(tableTypeName, objectTypeName, converter));
    }
    
    public static final void enableWrappingComparedNumbersToString(String unwrappingFunctionName) {
        Restrict.wrapComparedNumbersWithFunction = Some(unwrappingFunctionName);
    }
    
    public static final Map<String,SQLFunction> getFunctionsForHibernate() {
        Map<String,SQLFunction> ret = newMap();
        ret.put("column_value", (SQLFunction)new NoArgSQLFunction("column_value", StandardBasicTypes.STRING, false));
        ret.put("table", new TableFunction());
        for (Map.Entry<Class<?>, Tuple3<String, Option<String>, ? extends Function2<Connection, ?, ?>>> e: registeredTableTypesInternal.entrySet()) {
            ret.put("member_of_cast_" + e.getValue()._1, new MemberOfCastFunction(e.getValue()._1, e.getValue()._2));
        }
        return ret;
    }
    
    public static final String makeUnion(String sql) {
        return TableFunction.makeUnion(sql);
    }
}
