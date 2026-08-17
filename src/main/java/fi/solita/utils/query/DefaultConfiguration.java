package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.emptyMap;
import static fi.solita.utils.functional.Collections.newSortedSet;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

import fi.solita.utils.functional.Function2;
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
        return newSortedSet(Optional.of(1000));
    }
    
    public Optional<String> wrapComparedNumbersWithFunction() {
        return Optional.empty();
    }
    
    public boolean makeProjectionQueriesDistinct() {
        return false;
    }

    public Map<Class<?>, Tuple3<String, Class<?>, ? extends Function2<Connection, ?, ?>>> getRegisteredTableTypes() {
        return emptyMap();
    }

    public Optional<TableInClauseOptimization> getTableInClauseProvider() {
        return Optional.empty();
    }
    
    @Override
    public Optional<Object> getInListPadValue(Class<?> valueType) {
        return Optional.empty();
    }
}