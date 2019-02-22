package fi.solita.utils.query;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

public class Money implements Comparable<Money> {
    public final long euros;

    public Money(long euros) {
        this.euros = euros;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (euros ^ (euros >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (euros != other.euros)
            return false;
        return true;
    }
    
    public static class MoneyType implements UserType {
        @Override
        public int[] sqlTypes() {
            return new int[] { StandardBasicTypes.LONG.sqlType() };
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Class returnedClass() {
            return Money.class;
        }

        @Override
        public boolean equals(Object x, Object y) throws HibernateException {
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


        @Override
        public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
            Object value = StandardBasicTypes.LONG.nullSafeGet(rs, names, session, owner);
            if (value == null) {
                return null;
            }
            return new Money((Long) value);
        }

        @Override
        public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
            StandardBasicTypes.LONG.nullSafeSet(st, value == null ? null : ((Money)value).euros, index, session);
        }

        @Override
        public Object deepCopy(Object value) throws HibernateException {
            return value;
        }

        @Override
        public Object replace(Object original, Object target, Object owner) throws HibernateException {
            return original;
        }
    }

    @Override
    public int compareTo(Money o) {
        return Long.valueOf(euros).compareTo(o.euros);
    }
}