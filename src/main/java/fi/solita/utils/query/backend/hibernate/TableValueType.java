package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.MappedSuperclass;

import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.Numeric;
import fi.solita.utils.query.Numeric_;
import fi.solita.utils.query.entities.Table;

@SuppressWarnings("unchecked")
@MappedSuperclass
@TypeDef(defaultForType = Table.Value.class, typeClass = TableValueType.class)
public class TableValueType implements UserType, Serializable {

    @Override
    public int[] sqlTypes() {
        return new int[] { StandardBasicTypes.BLOB.sqlType() }; // not actually used
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException("Shouldn't be here");
    }

    public static final String toString(CharSequence cs) {
        return cs.toString();
    }

    public static final long toLong(Number n) {
        return n.longValue();
    }

    private static Class<? extends Connection> oracleConnectionClass;
    private static Class<? extends PreparedStatement> oraclePreparedStatementClass;
    private static Method oraclePreparedStatementMethod;
    private static Class<?> ARRAYClass;
    private static Constructor<?> ARRAYConstructor;
    private static Method arrayDescriptorMethod;
    private static Boolean available;
    public static boolean isAvailable() {
        if (available == null) {
            try {
                oracleConnectionClass = (Class<? extends Connection>) Class.forName("oracle.jdbc.OracleConnection");
                oraclePreparedStatementClass = (Class<? extends PreparedStatement>) Class.forName("oracle.jdbc.OraclePreparedStatement");
                ARRAYClass = (Class<?>) Class.forName("oracle.sql.ARRAY");
                Class<?> arrayDescriptorClass = Class.forName("oracle.sql.ArrayDescriptor");
                ARRAYConstructor = ARRAYClass.getConstructor(arrayDescriptorClass, Connection.class, Object.class);
                oraclePreparedStatementMethod = oraclePreparedStatementClass.getMethod("setARRAY", int.class, ARRAYClass);
                arrayDescriptorMethod = arrayDescriptorClass.getMethod("createDescriptor", String.class, Connection.class);
                available = true;
            } catch (Exception e) {
                available = false;
            }
        }
        return available;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            throw new UnsupportedOperationException("Shouldn't be here");
        } else {
            Connection c = st.getConnection().unwrap(oracleConnectionClass);
            Collection<?> values = ((Table.Value)value).values;
            
            Option<? extends Pair<String, Iterable<Object>>> sqlTypeAndValues = getSqlTypeAndValues(values);
            if (sqlTypeAndValues.isDefined()) {
                try {
                    Object ad = arrayDescriptorMethod.invoke(null, sqlTypeAndValues.get().left, c);
                    oraclePreparedStatementMethod.invoke(st.unwrap(oraclePreparedStatementClass), index, ARRAYConstructor.newInstance(ad, c, newArray(Object.class, sqlTypeAndValues.get().right)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new UnsupportedOperationException("type not implemented: " + head(values).getClass());
            }
        }
    }
    
    public static Option<Pair<String,Iterable<Object>>> getSqlTypeAndValues(Iterable<?> values) {
        Option<?> h = headOption(values);
        if (!h.isDefined()) {
            return None();
        }
        
        String t;
        Iterable<Object> v;
        if (h.get() instanceof CharSequence) {
            t = "SYS.ODCIVARCHAR2LIST";
            v = (Iterable<Object>)(Object)map(TableValueType_.toString, (Iterable<CharSequence>)values);
        } else if (h.get() instanceof BigDecimal) {
            t = "SYS.ODCINUMBERLIST";
            v = (Iterable<Object>)values;
        } else if (h.get() instanceof Number) {
            t = "SYS.ODCINUMBERLIST";
            v = (Iterable<Object>)(Object)map(TableValueType_.toLong, (Iterable<Number>)values);
        } else if (h.get() instanceof Numeric) {
            t = "SYS.ODCINUMBERLIST";
            v = (Iterable<Object>)(Object)map(Numeric_.toNumber, (Iterable<Numeric>)values);
        } else {
            return None();
        }
        return Some(Pair.of(t, v));
    }

    @Override
    public Class<Table.Value> returnedClass() {
        return Table.Value.class;
    }

    @Override
    public final Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public final Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public final boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public final int hashCode(Object x) throws HibernateException {
        return (x != null) ? x.hashCode() : 0;
    }

    @Override
    public final boolean isMutable() {
        return false;
    }

    @Override
    public final Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public final Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }
}