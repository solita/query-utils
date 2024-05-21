package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.query.BindableType;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.spi.TypeConfiguration;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Functional;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import jakarta.persistence.EntityManager;

public class HibernateTypeProvider implements TypeProvider {

    public static class HibernateType<T> implements Type<T> {
        public final Option<BindableType<T>> bindableType;
        public final Option<Class<?>> entity;
        public final Option<Class<?>> javaType;
        

        private HibernateType(Option<BindableType<T>> bindableType, Option<Class<?>> entity, Option<Class<?>> javaType) {
            this.bindableType = bindableType;
            this.entity = entity;
            this.javaType = javaType;
        }
        
        public static <T> Type<T> bindable(BindableType<T> x) {
            return new HibernateType<T>(Some(x), Option.<Class<?>>None(), Option.<Class<?>>None());
        }
        
        public static <T> Type<T> entity(Class<T> x) {
            return new HibernateType<T>(Option.<BindableType<T>>None(), Option.<Class<?>>Some(x), Option.<Class<?>>None());
        }
        
        public static <T> Type<T> javaType(Class<T> x) {
            return new HibernateType<T>(Option.<BindableType<T>>None(), Option.<Class<?>>None(), Option.<Class<?>>Some(x));
        }
    }

    private final ApplyZero<EntityManager> em;
    
    static Transformer<List<org.hibernate.type.Type>, org.hibernate.type.Type> head = new Transformer<List<org.hibernate.type.Type>,org.hibernate.type.Type>() {
        @Override
        public org.hibernate.type.Type transform(List<org.hibernate.type.Type> source) {
            return Functional.head(source);
        }
    };
    
    static Transformer<Iterable<?>, Long> size = new Transformer<Iterable<?>,Long>() {
        @Override
        public Long transform(Iterable<?> source) {
            return Functional.size(source);
        }
    };
    
    public HibernateTypeProvider(ApplyZero<EntityManager> em) {
        this.em = em;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <ID extends Serializable, T extends Identifiable<ID>> Type<ID> idType(Class<T> entityType) {
        SessionFactoryImplementor sf = (SessionFactoryImplementor) em.get().unwrap(Session.class).getSessionFactory();
        JavaType<?> t = ((MappingMetamodel) sf.getMappingMetamodel()).getEntityDescriptor(entityType).getIdentifierMapping().getJavaType();
        return HibernateType.javaType((Class<ID>)t.getJavaTypeClass());
    }
    
    @Override
    public <T> Type<T> type(final Class<T> clazz) {
        TypeConfiguration typeHelper = em.get().getEntityManagerFactory().unwrap( SessionFactoryImplementor.class ).getTypeConfiguration();
        
        // entity?
        try {
            em.get().getMetamodel().entity(clazz);
            JavaType<T> entityType = typeHelper.getJavaTypeRegistry().resolveEntityTypeDescriptor(clazz);
            if (entityType != null) {
                return HibernateType.entity(clazz);
            }
        } catch (IllegalArgumentException e) {
            // was not an entity...
        }
        
        // javatype?
        JavaType<Object> javaType = typeHelper.getJavaTypeRegistry().findDescriptor(clazz);
        if (javaType != null) {
            return HibernateType.javaType(clazz);
        }
        
        // basic type?
        org.hibernate.type.BasicType<T> basicType = typeHelper.getBasicTypeForJavaType(clazz);
        if (basicType != null) {
            return HibernateType.bindable(new BasicTypeReference<T>(basicType.getName(), clazz, basicType.getJdbcType().getDefaultSqlTypeCode()));
        }
        
        for (Class<T> en: getEnumType(clazz)) {
            // hibernate doesn't recognize enum values with class bodies as enums themselves
            JavaType<Object> enumType = typeHelper.getJavaTypeRegistry().resolveDescriptor(en);
            if (enumType != null) {
                return HibernateType.javaType(en);
            }
        }
        
        if (clazz.isArray()) {
            // array types seem to be created dynamically, so lets resolve one here
            JavaType<Object> arrayType = typeHelper.getJavaTypeRegistry().resolveDescriptor(clazz);
            if (arrayType != null) {
                return HibernateType.javaType(clazz);
            }
        }
        
        throw new IllegalArgumentException("Could not resolve Hibernate Type for: " + clazz + ". Please provide the type explicitly");
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Option<Class<T>> getEnumType(Class<T> type) {
        if (type.isEnum()) {
            return Some(type);
        }
        if (type.getEnclosingClass() != null && type.getEnclosingClass().isEnum()) {
            return Some((Class<T>) type.getEnclosingClass());
        }
        return None();
    }

    @Override
    public Class<?> getEntityClass(IEntity<?> entity) {
        return Hibernate.getClassLazy(entity);
    }
}
