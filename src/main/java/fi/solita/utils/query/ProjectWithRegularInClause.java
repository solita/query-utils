package fi.solita.utils.query;

/**
 * Use regular in-clause ( IN (:1, :2, :3, ...) ) for projections with this view
 * instead of the array-optimized one ( IN (SELECT * FROM table(:1)) ).
 * Needed since Oracle cannot utilize predicate-push-down properly with nested tables :( 
 */
public interface ProjectWithRegularInClause {

}
