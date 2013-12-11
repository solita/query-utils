package fi.solita.utils.query;

import java.util.Collection;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Id;

import fi.solita.utils.query.backend.hibernate.TableValueType;

/**
 * Optimization for <i>in</i> clauses using Oracle
 * 
 * To use this class, you need to be able to modify the generated SQL.
 * In Hibernate, this can be done with an Interceptor:
 *
 * <code><pre>
 * public class EntityInterceptor extends EmptyInterceptor {
 *     static {
 *         Table.enabled = true;
 *     }
 *     @Override
 *     public String onPrepareStatement(String sql) {
 *         return Table.processSql(super.onPrepareStatement(sql));
 *     }
 * }
 * </pre></code>
 */
@Entity
@javax.persistence.Table(name = "table(?)")
public class Table {
    @Id
    long id_which_is_only_here_since_hibernate_requires_it;

    Table.Value column_value;

    Table.Value helper_column_to_be_removed_from_query;

    private static final Pattern processSqlPattern = Pattern.compile("select\\s+([^.]+\\.)?([^ ]+\\s+from\\s+table[^ ]+\\s+)([^ ]+)\\s+where\\s+([^.]+\\.)?helper_column_to_be_removed_from_query\\s+in[^)]+\\)", Pattern.CASE_INSENSITIVE);

    public static boolean enabled = false;
    
    public static final boolean isSupported(Iterable<?> values) {
        return enabled && TableValueType.isAvailable() && TableValueType.getSqlTypeAndValues(values).isDefined();
    }
    
    public static final String processSql(String sql) {
        return processSqlPattern.matcher(sql).replaceAll("select /*+ cardinality($3,1) */ $1$2$3");
    }

    public static class Value {
        public final Collection<?> values;
        public Value(Collection<?> values) {
            this.values = values;
        }
    }
}
