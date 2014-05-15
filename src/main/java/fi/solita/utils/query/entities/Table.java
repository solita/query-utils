package fi.solita.utils.query.entities;

import java.util.Collection;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.query.backend.hibernate.TableValueType;

@Entity
@javax.persistence.Table(name = "table(?)")
public class Table {
    @Id
    long id_which_is_only_here_since_hibernate_requires_it;

    Table.Value column_value;

    Table.Value helper_column_to_be_removed_from_query;

    public static final Pattern processSqlPattern = Pattern.compile("select\\s+([^.]+\\.)?([^ ]+\\s+from\\s+table[^ ]+\\s+)([^ ]+)\\s+where\\s+([^.]+\\.)?helper_column_to_be_removed_from_query\\s+in[^)]+\\)", Pattern.CASE_INSENSITIVE);

    public static boolean enabled = false;
    
    public static final boolean isSupported(Iterable<?> values) {
        return enabled && TableValueType.isAvailable() && TableValueType.getSqlTypeAndValues(values).isDefined();
    }
    
    public static class Value {
        public final Collection<?> values;
        public Value(Collection<?> values) {
            this.values = values;
        }
    }
    
    public static class TableAccessor {
        public static SingularAttribute<Table, Value> column_value() {
            return Table_.column_value;
        }
        
        public static SingularAttribute<Table,Value> helper_column_to_be_removed_from_query() {
            return Table_.helper_column_to_be_removed_from_query;
        }
    }
}
