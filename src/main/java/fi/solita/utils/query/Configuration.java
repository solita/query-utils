package fi.solita.utils.query;

import java.sql.Connection;
import java.util.Map;
import java.util.SortedSet;

import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.db.TableInClauseOptimization;

public interface Configuration {
    public String getAliasPrefix();
    
    public int getMaxValuesForMemberOfRestriction();

    public int getMinValuesForMemberOfRestriction();
    
    public SortedSet<Integer> getInClauseValuesAmounts();
    
    public boolean makeProjectionQueriesDistinct();
    
    public Map<Class<?>, Tuple3<String, Class<?>, ? extends Function2<Connection,?,?>>> getRegisteredTableTypes();
    
    public Option<TableInClauseOptimization> getTableInClauseProvider();

    /**
     * @return Value used to pad in-lists to reduce hard-parsing. Return None to pad with the last real value in the list.
     */
    public Option<Object> getInListPadValue(Class<?> valueType);
}