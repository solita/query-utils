package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.head;

import java.io.Serializable;
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

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.db.oracle.OracleSupport;
import fi.solita.utils.query.entities.Table;

@MappedSuperclass
@TypeDef(defaultForType = Table.Value.class, typeClass = OracleTableValueType.class)
public class OracleTableValueType implements UserType, Serializable {
    
    public static Configuration config;

    @Override
    public int[] sqlTypes() {
        return new int[] { StandardBasicTypes.BLOB.sqlType() }; // not actually used
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException("Shouldn't be here");
    }
    
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            throw new UnsupportedOperationException("Shouldn't be here");
        } else {
            Connection c = st.getConnection().unwrap(OracleSupport.oracleConnectionClass);
            Collection<?> values = ((Table.Value)value).values;
            
            Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> sqlTypeAndValues = new OracleSupport(config).getSqlTypeAndValues(values);
            if (sqlTypeAndValues.isDefined()) {
                try {
                    Object ad = OracleSupport.arrayDescriptorMethod.invoke(null, sqlTypeAndValues.get()._1, c);
                    OracleSupport.oraclePreparedStatementMethod.invoke(st.unwrap(OracleSupport.oraclePreparedStatementClass), index, OracleSupport.ARRAYConstructor.newInstance(ad, c, newArray(Object.class, sqlTypeAndValues.get()._3.apply(c))));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new UnsupportedOperationException("type not implemented: " + head(values).getClass());
            }
        }
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