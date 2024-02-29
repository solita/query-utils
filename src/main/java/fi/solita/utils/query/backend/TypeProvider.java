package fi.solita.utils.query.backend;

import java.io.Serializable;

import org.hibernate.type.descriptor.java.JavaType;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;

public interface TypeProvider {
    <ID extends Serializable, T extends Identifiable<ID>> Type<ID> idType(Class<T> entityType);
    <T> Type<T> type(Class<T> clazz);
    
    <T> Type<T> javaType(Class<? extends JavaType<T>> clazz, @SuppressWarnings("unchecked") Class<T>... phantom);
    
    Class<?> getEntityClass(IEntity<?> entity);
}
