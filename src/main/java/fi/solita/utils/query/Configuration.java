package fi.solita.utils.query;

import java.sql.Connection;
import java.util.Map;

import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.db.TableInClauseOptimization;

public interface Configuration {
    public String getAliasPrefix();
    
    public int getMaxValuesForMemberOfRestriction();

    public int getMinValuesForMemberOfRestriction();
    
    public Option<Integer> getMaxInClauseValues();
    
    public Option<String> wrapComparedNumbersWithFunction();

    public boolean makeProjectionQueriesDistinct();
    
    public Map<Class<?>, Tuple3<String, Option<String>, ? extends Function2<Connection,?,?>>> getRegisteredTableTypes();
    
    public Option<TableInClauseOptimization> getTableInClauseProvider();
}