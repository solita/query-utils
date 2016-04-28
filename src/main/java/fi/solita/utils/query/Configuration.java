package fi.solita.utils.query;

import java.sql.Connection;
import java.util.Map;

import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;

public abstract class Configuration {
    public abstract String getAliasPrefix();
    
    /**
     * Optimization for <i>array-valued in clauses</i> using Oracle
     */
    public abstract boolean isOracleTableInClauseEnabled();

    public abstract int getMaxValuesForMemberOfRestriction();

    public abstract int getMinValuesForMemberOfRestriction();
    
    public abstract Option<Integer> getMaxInClauseValues();
    
    public abstract Option<String> wrapComparedNumbersWithFunction();

    public abstract boolean makeProjectionQueriesDistinct();
    
    public abstract Map<Class<?>, Tuple3<String, Option<String>, ? extends Function2<Connection,?,?>>> getRegisteredTableTypes();
}