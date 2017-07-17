package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.emptyMap;
import static fi.solita.utils.functional.Collections.newSortedSet;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.sql.Connection;
import java.util.Map;
import java.util.SortedSet;

import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.db.TableInClauseOptimization;

public class DefaultConfiguration implements Configuration {
    public String getAliasPrefix() {
        return "queryutils_";
    }

    public int getMaxValuesForMemberOfRestriction() {
        return 49;
    }
    
    public int getMinValuesForMemberOfRestriction() {
        return 6;
    }
    
    @Override
    public SortedSet<Integer> getInClauseValuesAmounts() {
        // ora has a limit of 1000. Don't know of other db verndors.
        return newSortedSet(Some(1000));
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

    public Option<TableInClauseOptimization> getTableInClauseProvider() {
        return None();
    }
}