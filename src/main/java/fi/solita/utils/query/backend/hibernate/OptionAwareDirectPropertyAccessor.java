package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Functional.find;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.FunctionalA.concat;
import static fi.solita.utils.functional.Predicates.equalTo;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.PropertyAccessException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

import fi.solita.utils.functional.Option;

@SuppressWarnings("rawtypes")
public class OptionAwareDirectPropertyAccessor implements PropertyAccessStrategy {

    public static final String NAME = "fi.solita.utils.query.backend.hibernate.OptionAwareDirectPropertyAccessor";

    static {
        if (!OptionAwareDirectPropertyAccessor.class.getName().equals(NAME)) {
            throw new RuntimeException("whoops!");
        }
    }
    
    @Override
    public PropertyAccess buildPropertyAccess(final Class containerJavaType, final String propertyName) {
        return new PropertyAccess() {
            @Override
            public PropertyAccessStrategy getPropertyAccessStrategy() {
                return OptionAwareDirectPropertyAccessor.this;
            }
            @Override
            public Getter getGetter() {
                return OptionAwareDirectPropertyAccessor.getGetter(containerJavaType, propertyName);
            }

            @Override
            public Setter getSetter() {
                return OptionAwareDirectPropertyAccessor.getSetter(containerJavaType, propertyName);
            }
            
        };
    }
    
    private static Getter getGetter(final Class theClass, final String propertyName) throws PropertyNotFoundException {
        final Field field = find(OptionAwareDirectPropertyAccessor_.fieldName.andThen(equalTo(propertyName)), fields(theClass)).get();
        final boolean isOption = Option.class.isAssignableFrom(field.getType());
        return new Getter() {
            @Override
            public Class getReturnType() {
                return isOption ? (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0] : field.getType();
            }

            @Override
            public String getMethodName() {
                return null;
            }

            @Override
            public Method getMethod() {
                return null;
            }

            @Override
            public Member getMember() {
                return field;
            }

            @Override
            public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) throws HibernateException {
                return get(owner);
            }

            @Override
            public Object get(Object owner) throws HibernateException {
                try {
                    field.setAccessible(true);
                    Object value = field.get(owner);
                    return isOption && value != null ? ((Option<?>)value).getOrElse(null) : value;
                }
                catch (Exception e) {
                    throw new PropertyAccessException(e, "could not get a field value by reflection", false, theClass, propertyName);
                }
            }
        };
    }

    private static Setter getSetter(final Class theClass, final String propertyName) throws PropertyNotFoundException {
        final Field field = find(OptionAwareDirectPropertyAccessor_.fieldName.andThen(equalTo(propertyName)), fields(theClass)).get();
        final boolean isOption = Option.class.isAssignableFrom(field.getType());
        return new Setter() {
            @Override
            public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException {
                try {
                    field.setAccessible(true);
                    field.set(target, isOption ? Option.of(value) : value);
                }
                catch (Exception e) {
                    throw new PropertyAccessException(e, "could not set a field value by reflection", true, theClass, propertyName);
                }
            }

            @Override
            public String getMethodName() {
                return null;
            }

            @Override
            public Method getMethod() {
                return null;
            }
        };
    }
    
    static String fieldName(Field field) {
        return field.getName();
    }

    static Iterable<Field> fields(Class<?> root) {
        return concat(root.getDeclaredFields(), flatMap(OptionAwareDirectPropertyAccessor_.fields, Option.of(root.getSuperclass())));
    }
}
