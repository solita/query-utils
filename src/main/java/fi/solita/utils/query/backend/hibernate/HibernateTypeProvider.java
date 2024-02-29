package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.find;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.groupBy;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


import org.hibernate.Session;
import org.hibernate.TypeHelper;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

import fi.solita.utils.functional.ApplyZero;
import fi.solita.utils.functional.Functional;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Predicates;
import fi.solita.utils.functional.SemiGroups;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import jakarta.persistence.EntityManager;

public class HibernateTypeProvider implements TypeProvider {

    public static class HibernateType<T> implements Type<T> {
        public final org.hibernate.type.Type type;

        public HibernateType(org.hibernate.type.Type type) {
            this.type = type;
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
    
    // cache these, since it's static data.
    private Map<Class<?>,String> typesByUniqueReturnedClassCache;
    private Map<Class<?>,String> typesByUniqueReturnedClass() {
        if (typesByUniqueReturnedClassCache == null) {
            MetamodelImplementor                       metamodelImpl              = (MetamodelImplementor) em.get().unwrap(Session.class).getSessionFactory().getMetamodel();
            Iterable<ClassMetadata>                    allClassMetadata           = map(HibernateTypeProvider_.entityPersister2ClassMetadata, metamodelImpl.entityPersisters().values());
            Map<String, List<org.hibernate.type.Type>> allPropertyTypesByName     = groupBy(HibernateTypeProvider_.type2Name, flatMap(HibernateTypeProvider_.classMetadata2propertyTypes, allClassMetadata));
            Iterable<org.hibernate.type.Type>          allDifferentPropertyTypes  = map(head, allPropertyTypesByName.values());
            Iterable<List<org.hibernate.type.Type>>    typesByReturnedClass       = groupBy(HibernateTypeProvider_.type2ReturnedClass, allDifferentPropertyTypes).values();
            Iterable<List<org.hibernate.type.Type>>    typesUniqueByReturnedClass = filter(size.andThen(Predicates.equalTo(1l)), typesByReturnedClass);
            typesByUniqueReturnedClassCache = newMap(SemiGroups.<String>fail(), map(head.andThen(HibernateTypeProvider_.type2ReturnedClassAndNamePair), typesUniqueByReturnedClass));
        }
        return typesByUniqueReturnedClassCache;
    }
    
    public HibernateTypeProvider(ApplyZero<EntityManager> em) {
        this.em = em;
    }

    static ClassMetadata entityPersister2ClassMetadata(EntityPersister ep) {
        return (ClassMetadata) ep;
    }

    static List<org.hibernate.type.Type> classMetadata2propertyTypes(ClassMetadata c) {
        return newList(c.getPropertyTypes());
    }
    
    static Class<?> type2ReturnedClass(org.hibernate.type.Type type) {
        return type.getReturnedClass();
    }
    
    static String type2Name(org.hibernate.type.Type type) {
        return type.getName();
    }
    
    @SuppressWarnings("unchecked")
    static Pair<Class<?>,String> type2ReturnedClassAndNamePair(org.hibernate.type.Type type) {
        return (Pair<Class<?>,String>)(Object)Pair.of(type.getReturnedClass(), type.getName());
    }

    @Override
    public <ID extends Serializable, T extends Identifiable<ID>> Type<ID> idType(Class<T> entityType) {
        return new HibernateType<ID>(((MetamodelImplementor) em.get().unwrap(Session.class).getSessionFactory().getMetamodel()).entityPersister(entityType).getIdentifierType());
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Option<Class<T>> getEnumType(Class<T> type) {
        if (type.isEnum()) {
            return Some((Class<T>) type);
        }
        if (type.getEnclosingClass() != null && type.getEnclosingClass().isEnum()) {
            return Some((Class<T>) type.getEnclosingClass());
        }
        return None();
    }

    @Override
    public <T> Type<T> type(final Class<T> clazz) {
        TypeHelper typeHelper = em.get().unwrap(Session.class).getTypeHelper();
        
        // basic type?
        org.hibernate.type.Type basicType = typeHelper.basic(clazz);
        if (basicType != null) {
            return new HibernateType<T>(basicType);
        }
        
        // user type?
        if (UserType.class.isAssignableFrom(clazz) || CompositeUserType.class.isAssignableFrom(clazz)) {
            org.hibernate.type.Type customType = typeHelper.custom(clazz);
            if (customType != null) {
                return new HibernateType<T>(customType);
            }
        }
        
        // entity?
        try {
            em.get().getMetamodel().entity(clazz);
            org.hibernate.type.Type entityType = typeHelper.entity(clazz);
            if (entityType != null) {
                return new HibernateType<T>(entityType);
            }
        } catch (IllegalArgumentException e) {
            // was not an entity...
        }
        
        // no luck with standard stuff, try searching a unique match from all entity metadata
        for (String typeName: find(clazz, typesByUniqueReturnedClass())) {
            org.hibernate.type.Type heuristicType = typeHelper.heuristicType(typeName);
            if (heuristicType != null) {
                return new HibernateType<T>(heuristicType);
            }
        }
        
        // enum type?
        for (Class<?> enumClass: getEnumType(clazz)) {
            org.hibernate.type.Type heuristicType = typeHelper.heuristicType(enumClass.getName());
            if (heuristicType != null) {
                return new HibernateType<T>(heuristicType);
            }
        }
        
        // still no luck. Fallback to Hibernate heuristics
        org.hibernate.type.Type heuristicType = typeHelper.heuristicType(clazz.getName());
        if (heuristicType != null) {
            return new HibernateType<T>(heuristicType);
        }
        
        throw new IllegalArgumentException("Could not resolve Hibernate Type for: " + clazz + ". Please provide the type explicitly");
    }

    @Override
    public Class<?> getEntityClass(IEntity<?> entity) {
        return HibernateProxyHelper.getClassWithoutInitializingProxy(entity);
    }
}
