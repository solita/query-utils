package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.headOption;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeRegistration;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.db.oracle.OracleSupport;
import fi.solita.utils.query.entities.Table;

@TypeRegistration(basicClass = Table.Value.class, userType = OracleTableValueType.class)
public class OracleTableValueType implements UserType<Table.Value>, Serializable {
    
    public static Configuration config;
    
    @Override
    public int getSqlType() {
        return StandardBasicTypes.BLOB.getSqlTypeCode(); // not actually used
    }
    
    @Override
    public Table.Value nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        throw new UnsupportedOperationException("Shouldn't be here");
    }
    
    @Override
    public void nullSafeSet(PreparedStatement st, Table.Value value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            throw new UnsupportedOperationException("Shouldn't be here");
        } else {
            Connection c = st.getConnection().unwrap(OracleSupport.oracleConnectionClass);
            Collection<?> values = value.values;
            
            Option<Tuple3<String,Class<?>,Apply<Connection,Iterable<Object>>>> sqlTypeAndValues = new OracleSupport(config).getSqlTypeAndValues(values);
            if (sqlTypeAndValues.isDefined()) {
                try {
                    Object ad = OracleSupport.arrayDescriptorMethod.invoke(null, sqlTypeAndValues.get()._1, c);
                    OracleSupport.oraclePreparedStatementMethod.invoke(st.unwrap(OracleSupport.oraclePreparedStatementClass), index, OracleSupport.ARRAYConstructor.newInstance(ad, c, newArray(Object.class, sqlTypeAndValues.get()._3.apply(c))));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new UnsupportedOperationException("type not implemented: " + headOption(values).map(OracleTableValueType_.className));
            }
        }
    }
    
    public static String className(Object o) {
        return o.getClass().getName();
    }
    
    @Override
    public Class<Table.Value> returnedClass() {
        return Table.Value.class;
    }

    @Override
    public final Table.Value replace(Table.Value original, Table.Value target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public final Table.Value deepCopy(Table.Value value) throws HibernateException {
        return value;
    }

    @Override
    public final boolean equals(Table.Value x, Table.Value y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public final int hashCode(Table.Value x) throws HibernateException {
        return (x != null) ? x.hashCode() : 0;
    }

    @Override
    public final boolean isMutable() {
        return false;
    }

    @Override
    public final Table.Value assemble(Serializable cached, Object owner) throws HibernateException {
        return (Table.Value)cached;
    }

    @Override
    public final Serializable disassemble(Table.Value value) throws HibernateException {
        return (Serializable) value;
    }
}