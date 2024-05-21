/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Functional.headOption;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.spi.TypeConfiguration;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Configuration;
import fi.solita.utils.query.db.oracle.OracleSupport;

public class OracleTableValueJDBCType implements JdbcType {
    
    public static final OracleTableValueJDBCType INSTANCE = new OracleTableValueJDBCType();
    
    public static Configuration config;

    @Override
    public int getJdbcTypeCode() {
        return Types.ARRAY;
    }

    @Override
    public int getDefaultSqlTypeCode() {
        return Types.ARRAY;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> JavaType<T> getJdbcRecommendedJavaTypeMapping(Integer precision, Integer scale, TypeConfiguration typeConfiguration) {
        return (JavaType<T>) OracleTableValueJavaType.INSTANCE;
    }

    @Override
    public <X> ValueBinder<X> getBinder(final JavaType<X> javaTypeDescriptor) {
        return new TableValueBinder<X>(javaTypeDescriptor);
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaType<X> javaType) {
        return new TableValueExtractor<X>(javaType, this);
    }

    private static final class TableValueBinder<J> implements ValueBinder<J> {
        private final JavaType<J> javaType;

        public TableValueBinder(JavaType<J> javaType) {
            this.javaType = javaType;
        }

        @Override
        public void bind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException {
            if (value == null) {
                throw new UnsupportedOperationException("Shouldn't be here");
            } else {
                Connection c = st.getConnection().unwrap(OracleSupport.oracleConnectionClass);
                Collection<?> values = javaType.unwrap(value, Collection.class, options);
                
                Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> sqlTypeAndValues = new OracleSupport(config).getSqlTypeAndValues(values);
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

        @Override
        public void bind(CallableStatement st, J value, String name, WrapperOptions options) throws SQLException {
            if (value == null) {
                throw new UnsupportedOperationException("Shouldn't be here");
            } else {
                Connection c = st.getConnection().unwrap(OracleSupport.oracleConnectionClass);
                Collection<?> values = javaType.unwrap(value, Collection.class, options);
                
                Option<Tuple3<String,Option<String>,Apply<Connection,Iterable<Object>>>> sqlTypeAndValues = new OracleSupport(config).getSqlTypeAndValues(values);
                if (sqlTypeAndValues.isDefined()) {
                    try {
                        Object ad = OracleSupport.arrayDescriptorMethod.invoke(null, sqlTypeAndValues.get()._1, c);
                        OracleSupport.oracleCallableStatementMethod.invoke(st.unwrap(OracleSupport.oracleCallableStatementClass), name, OracleSupport.ARRAYConstructor.newInstance(ad, c, newArray(Object.class, sqlTypeAndValues.get()._3.apply(c))));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new UnsupportedOperationException("type not implemented: " + headOption(values).map(OracleTableValueType_.className));
                }
            }
        }
    }

    private static final class TableValueExtractor<X> extends BasicExtractor<X> {
        public TableValueExtractor(JavaType<X> javaType, JdbcType jdbcType) {
            super(javaType, jdbcType);
        }

        @Override
        protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
            throw new UnsupportedOperationException("Shouldn't be here");
        }

        @Override
        protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
            throw new UnsupportedOperationException("Shouldn't be here");
        }

        @Override
        protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
            throw new UnsupportedOperationException("Shouldn't be here");
        }
    }
}
