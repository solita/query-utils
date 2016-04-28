package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.emptyMap;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.sql.Connection;
import java.util.Map;

import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;

public class DefaultConfiguration extends Configuration {
    private final boolean oraclePresent;
    
    public DefaultConfiguration() {
        boolean oraclePresent;
        try {
            Class.forName("oracle.jdbc.OracleConnection");
            oraclePresent = true;
        } catch (ClassNotFoundException e) {
            oraclePresent = false;
        }
        this.oraclePresent = oraclePresent;
    }
    
    public String getAliasPrefix() {
        return "queryutils_";
    }

    public boolean isOracleTableInClauseEnabled() {
        return oraclePresent;
    }

    public int getMaxValuesForMemberOfRestriction() {
        return 49;
    }
    
    public int getMinValuesForMemberOfRestriction() {
        return 6;
    }
    
    public Option<Integer> getMaxInClauseValues() {
        // limits for other db vendors?
        return oraclePresent ? Some(1000) : Option.<Integer>None();
    }

    public Option<String> wrapComparedNumbersWithFunction() {
        return None();
    }
    
    public boolean makeProjectionQueriesDistinct() {
        return false;
    }

    public Map<Class<?>, Tuple3<String, Option<String>, ? extends Function2<Connection, ?, ?>>> getRegisteredTableTypes() {
        return emptyMap();
    }
}