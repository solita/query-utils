package fi.solita.utils.query.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import fi.solita.utils.query.backend.hibernate.TableValueType;

@Entity
@javax.persistence.Table(name = "table(/*")
public class Table {
    public static final String tableAlias = "query_utils";
    
    public static final Table.Value of(Collection<?> values) {
        return new Table.Value(values);
    }
    
    @Id
    @Column(name="column_value")
    long singleColumn;
    
    @Column(name="*")
    long star;
    
    @Column(name="*/")
    long commentEnd;
    
    @Column(name="*/)")
    long commentEndWithParen;

    @Column(name="*/?) " + tableAlias + " UNION ALL /*")
    Table.Value commentEndWithBindParameterAndUnionAll;
    
    @Column(name="*/?) " + tableAlias)
    Table.Value commentEndWithBindParameter;
    
    public static boolean enabled = false;
    
    public static final boolean isSupported(Iterable<?> values) {
        return enabled && TableValueType.isAvailable() && TableValueType.getSqlTypeAndValues(values).isDefined();
    }
    
    public static class Value {
        public final Collection<?> values;
        Value(Collection<?> values) {
            this.values = values;
        }
    }
}
