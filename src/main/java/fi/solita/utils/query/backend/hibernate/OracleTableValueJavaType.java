package fi.solita.utils.query.backend.hibernate;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

import fi.solita.utils.query.entities.Table;

public class OracleTableValueJavaType extends AbstractClassJavaType<Table.Value> {
    
    public static final OracleTableValueJavaType INSTANCE = new OracleTableValueJavaType();

    public OracleTableValueJavaType() {
        super(Table.Value.class);
    }
    
    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return OracleTableValueJDBCType.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <X> X unwrap(Table.Value value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (getJavaType().isAssignableFrom(type)) {
            return (X) value;
        }
        return (X) value.values;
    }

    @Override
    public final <X> Table.Value wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof Table.Value) {
            return (Table.Value) value;
        }
        throw new UnsupportedOperationException("Shouldn't be here");
    }
    
    @Override
    public Table.Value fromString(CharSequence string) {
        throw new UnsupportedOperationException();
    }
}