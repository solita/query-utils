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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map.Entry;

import javax.persistence.MappedSuperclass;

import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Function;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Hacks;
import fi.solita.utils.query.entities.Table;

@MappedSuperclass
@TypeDef(defaultForType = Table.Value.class, typeClass = TableValueType.class)
public class TableValueType implements UserType, Serializable {
    
    public TableValueType() {
        isAvailable();
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { StandardBasicTypes.BLOB.sqlType() }; // not actually used
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException("Shouldn't be here");
    }
    
    public static final String toStr(CharSequence cs) {
        return cs.toString();
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
                @SuppressWarnings("unchecked")
                Class<? extends Connection> oc = (Class<? extends Connection>) Class.forName("oracle.jdbc.OracleConnection");
                oracleConnectionClass = oc;
                @SuppressWarnings("unchecked")
                Class<? extends PreparedStatement> ps = (Class<? extends PreparedStatement>) Class.forName("oracle.jdbc.OraclePreparedStatement");
                oraclePreparedStatementClass = ps;
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
            
            Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> sqlTypeAndValues = getSqlTypeAndValues(values);
            if (sqlTypeAndValues.isDefined()) {
                try {
                    Object ad = arrayDescriptorMethod.invoke(null, sqlTypeAndValues.get()._1, c);
                    oraclePreparedStatementMethod.invoke(st.unwrap(oraclePreparedStatementClass), index, ARRAYConstructor.newInstance(ad, c, newArray(Object.class, sqlTypeAndValues.get()._3.apply(c))));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new UnsupportedOperationException("type not implemented: " + head(values).getClass());
            }
        }
    }
    
    public static Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> getSqlTypeAndValues(final Iterable<?> values) {
        Option<?> h = headOption(values);
        if (!h.isDefined()) {
            return None();
        }
        
        String t;
        Option<String> o;
        Apply<Connection,Iterable<Object>> v;
        if (h.get() instanceof CharSequence) {
            t = "SYS.ODCIVARCHAR2LIST";
            o = None();
            @SuppressWarnings("unchecked")
            Iterable<Object> m = (Iterable<Object>)(Object)map(TableValueType_.toStr, (Iterable<CharSequence>)values);
            v = Function.constant(m);
        } else if (h.get() instanceof Number) {
            t = "SYS.ODCINUMBERLIST";
            o = None();
            @SuppressWarnings("unchecked")
            Iterable<Object> m = (Iterable<Object>)values;
            if (h.get() instanceof Short) {
                m = map(TableValueType_.castShortToLong, m);
            }
            v = Function.constant(m);
        } else if (Hacks.registeredTableTypesInternal.containsKey(h.get().getClass())) {
            final Tuple3<String,Option<String>,? extends Function2<Connection, ?, ?>> tabletypeAndConverter = Hacks.registeredTableTypesInternal.get(h.get().getClass());
            Tuple3<String,Option<String>,Apply<Connection, Iterable<Object>>> res = foo(tabletypeAndConverter, values);
            t = res._1;
            o = res._2;
            v = res._3;
        } else {
            // if no exact type found, try some registered for a superclass
            for (Entry<Class<?>, Tuple3<String,Option<String>, ? extends Function2<Connection, ?, ?>>> entry: Hacks.registeredTableTypesInternal.entrySet()) {
                if (entry.getKey().isAssignableFrom(h.get().getClass())) {
                    return Some(foo(entry.getValue(), values));
                }
            }
            return None();
        }
        return Some(Tuple.of(t, o, v));
    }
    
    public static Object castShortToLong(Object o) {
        return Long.valueOf(((Short)o).longValue());
    }
    
    static Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>> foo(final Tuple3<String,Option<String>,? extends Function2<Connection, ?, ?>> tabletypeAndConverter, final Iterable<?> values) {
        Apply<Connection,Iterable<Object>> v = new Function1<Connection, Iterable<Object>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Iterable<Object> apply(Connection t) {
                return map((Apply<Object,Object>)tabletypeAndConverter._3.ap(t), (Iterable<Object>)values);
            }
        };
        return Tuple.of(tabletypeAndConverter._1, tabletypeAndConverter._2, v); 
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