package fi.solita.utils.query;

import java.lang.reflect.InvocationTargetException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

class DepartmentIdType extends LongIdType<Department.ID> {
    public DepartmentIdType() {
        super(Department.ID.class);
    }
}
class EmployeeIdType extends LongIdType<Employee.ID> {
    public EmployeeIdType() {
        super(Employee.ID.class);
    }
}
class MunicipalityIdType extends LongIdType<Municipality.ID> {
    public MunicipalityIdType() {
        super(Municipality.ID.class);
    }
}

public abstract class LongIdType<ID extends LongId<?>> extends AbstractClassJavaType<ID> {
    
    protected LongIdType(Class<ID> clazz) {
        super(clazz);
    }
    
    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return indicators.getTypeConfiguration()
                .getJdbcTypeRegistry()
                .getDescriptor(Types.BIGINT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(ID value, Class<T> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return (T)(Long)value.id;
        }
        throw unknownUnwrap(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ID wrap(T value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (getJavaType().isInstance(value)) {
            return (ID) value;
        }
        if (value instanceof Long) {
            ID ret;
            try {
                ret = (ID)getJavaType().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
            ret.setId((long)value);
            return ret;
        }
        throw unknownWrap(value.getClass());
    }
    
    @Override
    public ID fromString(CharSequence string) {
        throw new UnsupportedOperationException();
    }
}
