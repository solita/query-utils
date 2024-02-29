package fi.solita.utils.query;

import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class MoneyType extends AbstractClassJavaType<Money> {
    
    public MoneyType() {
        super(Money.class);
    }
    
    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return indicators.getTypeConfiguration()
                .getJdbcTypeRegistry()
                .getDescriptor(Types.BIGINT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Money value, Class<T> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Money.class.isAssignableFrom(type)) {
            return (T) value;
        }
        if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return (T)(Long)value.euros;
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <T> Money wrap(T value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof Money) {
            return (Money) value;
        }
        if (value instanceof Long) {
            return new Money((long) value);
        }
        throw unknownWrap(value.getClass());
    }
    
    @Override
    public Money fromString(CharSequence string) {
        throw new UnsupportedOperationException();
    }
}
