package fi.solita.utils.query.entities;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@jakarta.persistence.Table(name = "table(/*")
public class Table {
    public static final Table.Value of(Collection<?> values) {
        return new Table.Value(values);
    }
    
    @Id
    @Column(name="column_value")
    long singleColumn;
    
    @Column(name="*")
    long star;
    
    public static class Value {
        public final Collection<?> values;
        Value(Collection<?> values) {
            this.values = values;
        }
    }
}
