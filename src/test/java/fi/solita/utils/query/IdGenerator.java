package fi.solita.utils.query;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class IdGenerator extends SequenceStyleGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
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
    protected DatabaseStructure buildDatabaseStructure(Type type, Properties params, JdbcEnvironment jdbcEnvironment, boolean forceTableUse, QualifiedName sequenceName, int initialValue, int incrementSize) {
        return super.buildDatabaseStructure(LongId.class.isAssignableFrom(type.getReturnedClass()) ? StandardBasicTypes.LONG : type, params, jdbcEnvironment, forceTableUse, sequenceName, initialValue, incrementSize);
    }
}