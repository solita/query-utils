package fi.solita.utils.query.backend.hibernate;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

import fi.solita.utils.query.attributes.PseudoAttribute;


public class LiteralPlaceholderType extends AbstractClassJavaType<PseudoAttribute.Placeholder> {
    public static final LiteralPlaceholderType INSTANCE = new LiteralPlaceholderType();

    public LiteralPlaceholderType() {
        super(PseudoAttribute.Placeholder.class);
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return StringJavaType.INSTANCE.getRecommendedJdbcType(indicators);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(PseudoAttribute.Placeholder value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (getJavaType().isAssignableFrom(type)) {
            return (X) value;
        }
        return (X)PseudoAttribute.Placeholder.PLACEHOLDER_VALUE;
    }

    @Override
    public <X> PseudoAttribute.Placeholder wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (getJavaType().isInstance(value)) {
            return (PseudoAttribute.Placeholder) value;
        }

        if (value == null || value.equals(PseudoAttribute.Placeholder.PLACEHOLDER_VALUE)) {
            return PseudoAttribute.QUERY_PLACEHOLDER;
        }
        throw new IllegalArgumentException("Expected placeholder value, but was: " + value);
    }
}
