package fi.solita.utils.query.backend.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.Test;

import fi.solita.utils.query.attributes.PseudoAttribute;

public class LiteralPlaceholderTypeTest {

    // -------------------------------------------------------------------------
    // constructor / INSTANCE
    // -------------------------------------------------------------------------

    @Test
    public void constructor_setsJavaTypeToPseudoAttributePlaceholder() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        assertEquals(PseudoAttribute.Placeholder.class, type.getJavaType());
    }

    @Test
    public void INSTANCE_isNotNull() {
        assertNotNull(LiteralPlaceholderType.INSTANCE);
    }

    @Test
    public void INSTANCE_isOfCorrectType() {
        assertNotNull(LiteralPlaceholderType.INSTANCE instanceof LiteralPlaceholderType);
    }

    @Test
    public void INSTANCE_hasCorrectJavaType() {
        assertEquals(PseudoAttribute.Placeholder.class, LiteralPlaceholderType.INSTANCE.getJavaType());
    }

    // -------------------------------------------------------------------------
    // getRecommendedJdbcType
    // -------------------------------------------------------------------------

    @Test
    public void getRecommendedJdbcType_returnsVarcharJdbcType() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        final TypeConfiguration typeConfiguration = new TypeConfiguration();
        JdbcTypeIndicators indicators = new JdbcTypeIndicators() {
            @Override
            public TypeConfiguration getTypeConfiguration() {
                return typeConfiguration;
            }
            @Override
            public org.hibernate.dialect.Dialect getDialect() {
                return new H2Dialect();
            }
        };
        // StringJavaType.INSTANCE.getRecommendedJdbcType returns VarcharJdbcType
        assertEquals(VarcharJdbcType.class, type.getRecommendedJdbcType(indicators).getClass());
    }

    // -------------------------------------------------------------------------
    // unwrap
    // -------------------------------------------------------------------------

    @Test
    public void unwrap_returnsNull_whenValueIsNull() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        assertNull(type.unwrap(null, String.class, null));
    }

    @Test
    public void unwrap_returnsValue_whenTypeIsAssignableFromPlaceholder() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        PseudoAttribute.Placeholder placeholder = PseudoAttribute.QUERY_PLACEHOLDER;
        Object result = type.unwrap(placeholder, PseudoAttribute.Placeholder.class, null);
        assertSame(placeholder, result);
    }

    @Test
    public void unwrap_returnsPlaceholderValue_whenTypeIsNotAssignable() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        PseudoAttribute.Placeholder placeholder = PseudoAttribute.QUERY_PLACEHOLDER;
        Object result = type.unwrap(placeholder, String.class, null);
        assertEquals(PseudoAttribute.Placeholder.PLACEHOLDER_VALUE, result);
    }

    // -------------------------------------------------------------------------
    // wrap
    // -------------------------------------------------------------------------

    @Test
    public void wrap_returnsNull_whenValueIsNull() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        assertNull(type.wrap(null, null));
    }

    @Test
    public void wrap_returnsValue_whenValueIsPlaceholder() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        PseudoAttribute.Placeholder placeholder = PseudoAttribute.QUERY_PLACEHOLDER;
        PseudoAttribute.Placeholder result = type.wrap(placeholder, null);
        assertSame(placeholder, result);
    }

    @Test
    public void wrap_returnsQueryPlaceholder_whenValueIsPlaceholderValue() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        PseudoAttribute.Placeholder result = type.wrap(PseudoAttribute.Placeholder.PLACEHOLDER_VALUE, null);
        assertSame(PseudoAttribute.QUERY_PLACEHOLDER, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrap_throwsIllegalArgumentException_forUnexpectedValue() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        type.wrap("unexpected", null);
    }

    // -------------------------------------------------------------------------
    // fromString
    // -------------------------------------------------------------------------

    @Test(expected = UnsupportedOperationException.class)
    public void fromString_throwsUnsupportedOperationException() {
        LiteralPlaceholderType type = new LiteralPlaceholderType();
        type.fromString("anything");
    }
}