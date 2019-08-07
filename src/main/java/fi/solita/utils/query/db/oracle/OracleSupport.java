package fi.solita.utils.query.db.oracle;

import static fi.solita.utils.functional.Functional.headOption;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Map.Entry;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Function;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.db.TableInClauseOptimization;

public class OracleSupport implements TableInClauseOptimization {
    
    private final Configuration config;
    
    public OracleSupport(Configuration config) {
        this.config = config;
    }
    
    public Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> getSqlTypeAndValues(final Iterable<?> values) {
        Option<?> h = headOption(values);
        if (!h.isDefined()) {
            return None();
        }
        
        Map<Class<?>, Tuple3<String, Option<String>, ? extends Function2<Connection, ?, ?>>> regTypes = config.getRegisteredTableTypes();
        
        String t;
        Option<String> o;
        Apply<Connection,Iterable<Object>> v;
        if (regTypes.containsKey(h.get().getClass())) {
            final Tuple3<String,Option<String>,? extends Function2<Connection, ?, ?>> tabletypeAndConverter = regTypes.get(h.get().getClass());
            Tuple3<String,Option<String>,Apply<Connection, Iterable<Object>>> res = foo(tabletypeAndConverter, values);
            t = res._1;
            o = res._2;
            v = res._3;
        } else if (h.get().getClass().getSuperclass() != null && regTypes.containsKey(h.get().getClass().getSuperclass())) {
            final Tuple3<String,Option<String>,? extends Function2<Connection, ?, ?>> tabletypeAndConverter = regTypes.get(h.get().getClass().getSuperclass());
            Tuple3<String,Option<String>,Apply<Connection, Iterable<Object>>> res = foo(tabletypeAndConverter, values);
            t = res._1;
            o = res._2;
            v = res._3;
        } else if (h.get().getClass().getSuperclass().getSuperclass() != null && regTypes.containsKey(h.get().getClass().getSuperclass().getSuperclass())) {
            final Tuple3<String,Option<String>,? extends Function2<Connection, ?, ?>> tabletypeAndConverter = regTypes.get(h.get().getClass().getSuperclass().getSuperclass());
            Tuple3<String,Option<String>,Apply<Connection, Iterable<Object>>> res = foo(tabletypeAndConverter, values);
            t = res._1;
            o = res._2;
            v = res._3;
        } else {
            // if no exact type found, try some registered for another superclass/interface
            for (Entry<Class<?>, Tuple3<String,Option<String>, ? extends Function2<Connection, ?, ?>>> entry: regTypes.entrySet()) {
                if (entry.getKey().isAssignableFrom(h.get().getClass())) {
                    return Some(foo(entry.getValue(), values));
                }
            }
            
            if (h.get() instanceof CharSequence) {
                t = "SYS.ODCIVARCHAR2LIST";
                o = None();
                @SuppressWarnings("unchecked")
                Iterable<Object> m = (Iterable<Object>)(Object)map(OracleSupport_.toStr, (Iterable<CharSequence>)values);
                v = Function.constant(m);
            } else if (h.get() instanceof Number) {
                t = "SYS.ODCINUMBERLIST";
                o = None();
                @SuppressWarnings("unchecked")
                Iterable<Object> m = (Iterable<Object>)values;
                if (h.get() instanceof Short) {
                    m = map(OracleSupport_.castShortToLong, m);
                }
                v = Function.constant(m);
            } else {
                return None();
            }
        }
        return Some(Tuple.of(t, o, v));
    }

    public static Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>> foo(final Tuple3<String,Option<String>,? extends Function2<Connection, ?, ?>> tabletypeAndConverter, final Iterable<?> values) {
        Apply<Connection,Iterable<Object>> v = new Function1<Connection, Iterable<Object>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Iterable<Object> apply(Connection t) {
                return map((Apply<Object,Object>)tabletypeAndConverter._3.ap(t), (Iterable<Object>)values);
            }
        };
        return Tuple.of(tabletypeAndConverter._1, tabletypeAndConverter._2, v); 
    }
    
    public static Class<? extends Connection> oracleConnectionClass;
    public static Class<? extends PreparedStatement> oraclePreparedStatementClass;
    public static Method oraclePreparedStatementMethod;
    private static Class<?> ARRAYClass;
    public static Constructor<?> ARRAYConstructor;
    public static Method arrayDescriptorMethod;
    
    static {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Connection> oc = (Class<? extends Connection>) Class.forName("oracle.jdbc.OracleConnection");
            OracleSupport.oracleConnectionClass = oc;
            @SuppressWarnings("unchecked")
            Class<? extends PreparedStatement> ps = (Class<? extends PreparedStatement>) Class.forName("oracle.jdbc.OraclePreparedStatement");
            oraclePreparedStatementClass = ps;
            ARRAYClass = (Class<?>) Class.forName("oracle.sql.ARRAY");
            Class<?> arrayDescriptorClass = Class.forName("oracle.sql.ArrayDescriptor");
            ARRAYConstructor = ARRAYClass.getConstructor(arrayDescriptorClass, Connection.class, Object.class);
            oraclePreparedStatementMethod = oraclePreparedStatementClass.getMethod("setARRAY", int.class, ARRAYClass);
            arrayDescriptorMethod = arrayDescriptorClass.getMethod("createDescriptor", String.class, Connection.class);
        } catch (Exception e) {
        }
    }
    
    static final String toStr(CharSequence cs) {
        return cs.toString();
    }
    
    static Object castShortToLong(Object o) {
        return Long.valueOf(((Short)o).longValue());
    }
}
