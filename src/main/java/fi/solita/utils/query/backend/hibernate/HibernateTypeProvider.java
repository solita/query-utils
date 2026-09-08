package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.it;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hibernate.Hibernate;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.metamodel.model.domain.JpaMetamodel;
import org.hibernate.type.BindableType;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.spi.TypeConfiguration;

import fi.solita.utils.functional.Functional;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import jakarta.persistence.EntityManagerFactory;

public class HibernateTypeProvider implements TypeProvider {

    public static class HibernateType<T> implements Type<T> {
        public final Optional<BindableType<T>> bindableType;
        public final Optional<Class<?>> entity;
        public final Optional<Class<?>> javaType;
        

        private HibernateType(Optional<BindableType<T>> bindableType, Optional<Class<?>> entity, Optional<Class<?>> javaType) {
            this.bindableType = bindableType;
            this.entity = entity;
            this.javaType = javaType;
        }
        
        public static <T> Type<T> bindable(BindableType<T> x) {
            return new HibernateType<T>(Optional.of(x), Optional.empty(), Optional.empty());
        }
        
        public static <T> Type<T> entity(Class<T> x) {
            return new HibernateType<T>(Optional.empty(), Optional.of(x), Optional.empty());
        }
        
        public static <T> Type<T> javaType(Class<T> x) {
            return new HibernateType<T>(Optional.empty(), Optional.empty(), Optional.of(x));
        }
    }

    private final Supplier<EntityManagerFactory> emf;
    
    static Function<List<org.hibernate.type.Type>, org.hibernate.type.Type> head = new Function<List<org.hibernate.type.Type>,org.hibernate.type.Type>() {
        @Override
        public org.hibernate.type.Type apply(List<org.hibernate.type.Type> source) {
            return Functional.head(source);
        }
    };
    
    static Function<Iterable<?>, Long> size = new Function<Iterable<?>,Long>() {
        @Override
        public Long apply(Iterable<?> source) {
            return Functional.size(source);
        }
    };
    
    public HibernateTypeProvider(Supplier<EntityManagerFactory> emf) {
        this.emf = emf;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <ID extends Serializable, T extends Identifiable<ID>> Type<ID> idType(Class<T> entityType) {
        SessionFactoryImplementor sf = (SessionFactoryImplementor) emf.get().unwrap(SessionFactoryImplementor.class);
        JavaType<?> t = ((MappingMetamodel) sf.getMappingMetamodel()).getEntityDescriptor(entityType).getIdentifierMapping().getJavaType();
        return HibernateType.javaType((Class<ID>)t.getJavaTypeClass());
    }
    
    @Override
    public <T> Type<T> type(final Class<T> clazz) {
        TypeConfiguration typeHelper = emf.get().unwrap(SessionFactoryImplementor.class).getTypeConfiguration();
        
        // entity?
        Object e = ((JpaMetamodel)emf.get().getMetamodel()).findEntityType(clazz);
        if (e != null && typeHelper.getJavaTypeRegistry().resolveEntityTypeDescriptor(clazz) != null) {
            return HibernateType.entity(clazz);
        }
        
        // javatype?
        JavaType<T> javaType = typeHelper.getJavaTypeRegistry().findDescriptor(clazz);
        if (javaType != null) {
            return HibernateType.javaType(clazz);
        }
        
        // basic type?
        org.hibernate.type.BasicType<T> basicType = typeHelper.getBasicTypeForJavaType(clazz);
        if (basicType != null) {
            return HibernateType.bindable(new BasicTypeReference<T>(basicType.getName(), clazz, basicType.getJdbcType().getDefaultSqlTypeCode()));
        }
        
        for (Class<T> en: it(getEnumType(clazz))) {
            // hibernate doesn't recognize enum values with class bodies as enums themselves
            JavaType<T> enumType = typeHelper.getJavaTypeRegistry().resolveDescriptor(en);
            if (enumType != null) {
                return HibernateType.javaType(en);
            }
        }
        
        if (clazz.isArray()) {
            // array types seem to be created dynamically, so lets resolve one here
            JavaType<T> arrayType = typeHelper.getJavaTypeRegistry().resolveDescriptor(clazz);
            if (arrayType != null) {
                return HibernateType.javaType(clazz);
            }
        }
        
        throw new IllegalArgumentException("Could not resolve Hibernate Type for: " + clazz + ". Please provide the type explicitly");
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Optional<Class<T>> getEnumType(Class<T> type) {
        if (type.isEnum()) {
            return Optional.of(type);
        }
        if (type.getEnclosingClass() != null && type.getEnclosingClass().isEnum()) {
            return Optional.of((Class<T>) type.getEnclosingClass());
        }
        return Optional.empty();
    }

    @Override
    public Class<?> getEntityClass(IEntity<?> entity) {
        return Hibernate.getClassLazy(entity);
    }
}
