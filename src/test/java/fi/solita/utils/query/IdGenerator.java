package fi.solita.utils.query;

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.hibernate.id.enhanced.SequenceStructure;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.TableStructure;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

public class IdGenerator extends SequenceStyleGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        EntityPersister persister = session.getEntityPersister(null, object);
        Class<?> idClass = persister.getIdentifierType().getReturnedClass();
        if (LongId.class.isAssignableFrom(idClass)) {
            @SuppressWarnings("unchecked")
            Class<? extends LongId<?>> longIdClass = (Class<? extends LongId<?>>)idClass;
            Long id = (Long)super.generate(session, object);
            Constructor<? extends LongId<?>> c;
            try {
                c = longIdClass.getDeclaredConstructor();
                c.setAccessible(true);
                return c.newInstance().setId(id);
            } catch (Exception e) {
                throw new HibernateException(e);
            }
        }
        return super.generate(session, object);
    }
    
    @Override
    protected DatabaseStructure buildSequenceStructure(
            Type type,
            Properties params,
            JdbcEnvironment jdbcEnvironment,
            QualifiedName sequenceName,
            int initialValue,
            int incrementSize) {
        return new SequenceStructure(
                jdbcEnvironment,
                determineContributor( params ),
                sequenceName,
                initialValue,
                incrementSize,
                LongId.class.isAssignableFrom(type.getReturnedClass()) ? Long.class : type.getReturnedClass()
        );
    }

    @Override
    protected DatabaseStructure buildTableStructure(
            Type type,
            Properties params,
            JdbcEnvironment jdbcEnvironment,
            QualifiedName sequenceName,
            int initialValue,
            int incrementSize) {
        final Identifier valueColumnName = determineValueColumnName( params, jdbcEnvironment );
        final String contributor = determineContributor( params );

        return new TableStructure(
                jdbcEnvironment,
                contributor,
                sequenceName,
                valueColumnName,
                initialValue,
                incrementSize,
                LongId.class.isAssignableFrom(type.getReturnedClass()) ? Long.class : type.getReturnedClass()
        );
    }
    
    private String determineContributor(Properties params) {
        final String contributor = params.getProperty( IdentifierGenerator.CONTRIBUTOR_NAME );
        return contributor == null ? "orm" : contributor;
    }
}