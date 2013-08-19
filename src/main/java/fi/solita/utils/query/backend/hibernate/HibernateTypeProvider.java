package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMap;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.find;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.groupBy;
import static fi.solita.utils.functional.Functional.map;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.TypeHelper;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

import fi.solita.utils.functional.Functional_;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Predicate;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;

public class HibernateTypeProvider implements TypeProvider {

    public static class HibernateType<T> implements Type<T> {
        public final org.hibernate.type.Type type;

        public HibernateType(org.hibernate.type.Type type) {
            this.type = type;
        }
    }

    @PersistenceContext
    private EntityManager em;
    
    // cache these, since it's static data.
    private Map<Class<?>,String> typesByUniqueReturnedClassCache;
    private Map<Class<?>,String> typesByUniqueReturnedClass() {
        if (typesByUniqueReturnedClassCache == null) {
            Iterable<ClassMetadata>                    allClassMetadata           = em.unwrap(Session.class).getSessionFactory().getAllClassMetadata().values();
            Map<String, List<org.hibernate.type.Type>> allPropertyTypesByName     = groupBy(flatMap(allClassMetadata, HibernateTypeProvider_.classMetadata2propertyTypes), HibernateTypeProvider_.type2Name);
            Iterable<org.hibernate.type.Type>          allDifferentPropertyTypes  = map(allPropertyTypesByName.values(), Functional_.<org.hibernate.type.Type>head1());
            Iterable<List<org.hibernate.type.Type>>    typesByReturnedClass       = groupBy(allDifferentPropertyTypes, HibernateTypeProvider_.type2ReturnedClass).values();
            Iterable<List<org.hibernate.type.Type>>    typesUniqueByReturnedClass = filter(typesByReturnedClass, Functional_.size.andThen(new Predicate<Integer>() {
                @Override
                public boolean accept(Integer candidate) {
                    return candidate == 1;
                }
            }));
            typesByUniqueReturnedClassCache = newMap(map(typesUniqueByReturnedClass, Functional_.<org.hibernate.type.Type>head1().andThen(HibernateTypeProvider_.type2ReturnedClassAndNamePair)));
        }
        return typesByUniqueReturnedClassCache;
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
        return new HibernateType<ID>(em.unwrap(Session.class).getSessionFactory().getClassMetadata(entityType).getIdentifierType());
    }

    @Override
    public <T> Type<T> type(final Class<T> clazz) {
        TypeHelper typeHelper = em.unwrap(Session.class).getTypeHelper();
        
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
            em.getMetamodel().entity(clazz);
            org.hibernate.type.Type entityType = typeHelper.entity(clazz);
            if (entityType != null) {
                return new HibernateType<T>(entityType);
            }
        } catch (IllegalArgumentException e) {
            // was not an entity...
        }
        
        // no luck with standard stuff, try searching a unique match from all entity metadata
        for (String typeName: find(typesByUniqueReturnedClass(), clazz)) {
            org.hibernate.type.Type heuristicType = typeHelper.heuristicType(typeName);
            if (heuristicType != null) {
                return new HibernateType<T>(heuristicType);
            }
        }
        
        // still no luck. Fallback to Hibernate heuristics
        org.hibernate.type.Type heuristicType = typeHelper.heuristicType(clazz.getName());
        if (heuristicType != null) {
            return new HibernateType<T>(heuristicType);
        }
        
        throw new IllegalArgumentException("Could not resolve Hibernate Type for: " + clazz);
    }

}
