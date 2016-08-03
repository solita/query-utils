package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.size;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.FunctionalS.range;
import static fi.solita.utils.functional.Predicates.not;
import static fi.solita.utils.query.QueryUtils.isRequiredByMetamodel;
import static fi.solita.utils.query.QueryUtils.isRequiredByQueryAttribute;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.convertNullsToEmbeddableWhereRequired;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.isNoneOrNull;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.optionGet;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.postProcessValue;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.removeNonesAndSomesFromCollections;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.transformPseudoResultToActualValue;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.wrapNullsToOptionsWhereAppropriate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.meta.MetaJpaConstructor;

class ProjectionResultUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectionResultUtil.class);
    
    public static final class NullValueButNonOptionConstructorArgumentException extends RuntimeException {
        private final Class<?> argumentType;
        private final int argumentIndex;
        private final Class<?> constructor;
    
        public NullValueButNonOptionConstructorArgumentException(Class<?> constructor, Class<?> argumentType, int argumentIndex) {
            this.constructor = constructor;
            this.argumentType = argumentType;
            this.argumentIndex = argumentIndex;
        }
    
        @Override
        public String getMessage() {
            return "Constructor " + constructor.getName() + " had a non-Option argument of type " + argumentType.getName() + " at position " + argumentIndex + " which was tried to supply with a null";
        }
    }
    
    static Object postProcessValue(Attribute<?, ?> attr, Object resultFromDb) {
        logger.debug("postProcessValue({},{})", attr, resultFromDb);
        if (attr == null) {
            logger.debug("Skipping processing since attr was null");
            // null is used as a placeholder in SelfAttribute and Constructors.IdProjection... Yeah, should use something else...
            return resultFromDb;
        }
        Object ret = transformPseudoResultToActualValue.ap(attr).andThen(
               wrapNullsToOptionsWhereAppropriate.ap(attr)).andThen(
               convertNullsToEmbeddableWhereRequired.ap(attr)).andThen(
               removeNonesAndSomesFromCollections.ap(attr))
               .apply(resultFromDb);
        logger.debug("postProcessValue -> {}", ret);
        return ret;
    }
    
    static Iterable<Object> postProcessRow(List<Attribute<?,?>> projectionParameters, Iterable<Object> row) {
        logger.debug("postProcessRow({},{})", projectionParameters, row);
        Iterable<Object> ret = map(postProcessValue, zip(projectionParameters, row));
        logger.debug("postProcessRow -> {}", ret);
        return ret;
    }

    static <R> Iterable<R> transformAllRows(MetaJpaConstructor<?, R, ?> projection, Iterable<Iterable<Object>> rows) {
        logger.debug("transformAllRows({},{})", projection, rows);
        Iterable<R> ret = map(ProjectionResultUtil_.<R>transformRow().ap(projection), rows);
        logger.debug("transformAllRows -> {}", ret);
        return ret;
    }
    
    static <T> T transformRow(MetaJpaConstructor<?,T,?> projection, Iterable<Object> row) {
        logger.debug("transformRow({},{})", projection, row);
        List<Object> r = newList(postProcessRow(projection.getParameters(), row));
        // at this point there should be no nulls, except explicit null-literals
        for (Tuple2<Object, Integer> result: zip(r, range(0))) {
            if (result._1 == null && !(projection.getParameters().get(result._2) instanceof PseudoAttribute)) {
                throw new ProjectionResultUtil.NullValueButNonOptionConstructorArgumentException(projection.getClass(), projection.getConstructorParameterTypes().get(result._2), result._2);
            }
        }
        
        @SuppressWarnings("unchecked")
        T ret = ((MetaJpaConstructor<?,T,Object>)projection).apply(size(r) == 1 ? head(r) : Tuple.of(newArray(Object.class, r)));
        logger.debug("transformRow -> {}", ret);
        return ret;
    }
    
    static Object postProcessResult(Class<?> constructorParameterType, Attribute<?, ?> attr, List<Object> val) {
        logger.debug("postProcessResult({},{},{})", new Object[] {constructorParameterType, attr, val});
        Object ret;
        if (attr instanceof SingularAttribute) {
            if (!isRequiredByQueryAttribute(attr) && val.isEmpty()) {
                logger.debug("Optional SingularAttribute and empty resultList, returning null to be later replaced by None()");
                ret = null;
            } else {
                if (val.size() != 1) {
                    throw new IllegalArgumentException("Collection expected to be of size " + 1 + " but was: " + val);
                }
                ret = head(val);
            }
        } else {
            if (constructorParameterType.equals(ArrayList.class)) {
                logger.debug("Constructor expecting an ArrayList: {}", constructorParameterType.getName());
                ret = val instanceof ArrayList ? val : new ArrayList<Object>(val);
            } else if (constructorParameterType.equals(List.class) || constructorParameterType.equals(Object.class) && attr instanceof ListAttribute) {
                logger.debug("Constructor expecting a List: {}", constructorParameterType.getName());
                ret = val;
            } else if (constructorParameterType.equals(Collection.class) || constructorParameterType.equals(Object.class) && attr instanceof CollectionAttribute) {
                logger.debug("Constructor expecting a Collection: {}", constructorParameterType.getName());
                ret = val;
            } else if (constructorParameterType.equals(SortedSet.class) || constructorParameterType.equals(TreeSet.class)) {
                logger.debug("Constructor expecting a SortedSet: {}", constructorParameterType.getName());
                ret = new TreeSet<Object>(val);
            } else if (constructorParameterType.equals(Set.class) || constructorParameterType.equals(Object.class) && attr instanceof SetAttribute) {
                logger.debug("Constructor expecting a Set: {}", constructorParameterType.getName());
                ret = newSet(val);
            } else {
                ret = val;
            }
            if (((Collection<?>)ret).size() != val.size()) {
                logger.warn("size of a Set/SortedSet was different from the size of the originating data! Have you maybe suboptimally implemented equals/compareTo? Enable debug logging for stack trace. Attribute: {}, List: {}, Set: {}", attr, val, ret);
                logger.debug("size of a Set/SortedSet... stack: ", new Exception());
            }
        }
        logger.debug("postProcessResult -> {}", ret);
        return ret;
    }

    static Object transformPseudoResultToActualValue(Attribute<?,?> attribute, Object resultFromDb) {
        logger.debug("transformPseudoResultToActualValue({},{})", attribute, resultFromDb);
        Object ret = resultFromDb;
        for (PseudoAttribute pseudo: unwrap(PseudoAttribute.class, attribute)) {
            logger.debug("Replacing pseudo placeholder with actual value");
            ret = pseudo.getValueToReplaceResult(resultFromDb);
        }
        logger.debug("transformPseudoResultToActualValue -> {}", ret);
        return ret;
    }

    /** Wraps values to Some and nulls to None for optional parameters, leave others as is */
    static Object wrapNullsToOptionsWhereAppropriate(Attribute<?,?> attribute, Object resultFromDb) {
        logger.debug("wrapNullsToOptionsWhereAppropriate({},{})", attribute, resultFromDb);
        Object ret;
        if (attribute.isCollection()) {
            if (!(resultFromDb instanceof Collection) && isOption(attribute)) {
                ret = Option.of(resultFromDb);
            } else {
                ret = resultFromDb;
            }
        } else if (isRequiredByQueryAttribute(attribute)) {
            ret = resultFromDb;
        } else {
            ret = Option.of(resultFromDb);
        }
        logger.debug("wrapNullsToOptionsWhereAppropriate -> {}", ret);
        return ret;
    }
    
    /** Hibern cannot handle embeddables with all-null values correctly, since it doesn't separate a missing embeddable and an existing all-null embeddable.
     *  So we instantiate the empty embeddable if the result has been left null but the attribute is required */
    static Object convertNullsToEmbeddableWhereRequired(Attribute<?,?> attribute, Object resultFromDb) {
        logger.debug("convertNullsToEmbeddableWhereRequired({},{})", attribute, resultFromDb);
        Object ret = resultFromDb;
        Option<? extends Attribute<?, ?>> embeddable = EmbeddableUtil.unwrapEmbeddableAttribute(attribute);
        if (embeddable.isDefined() && resultFromDb == null && isRequiredByMetamodel(attribute)) {
            Class<?> clazz = embeddable.get().getJavaType();
            logger.debug("Instantiating an empty Embeddable {} in place of a null result", clazz);
            ret = EmbeddableUtil.instantiate(clazz);
        }
        logger.debug("convertNullsToEmbeddableWhereRequired -> {}", ret);
        return ret;
    }
    
    static boolean isOption(Attribute<?,?> attr) {
        if (attr instanceof Bindable) {
            Class<?> b = ((Bindable<?>) attr).getBindableJavaType();
            return b != null && Option.class.isAssignableFrom(b);
        }
        return false;
    }
    
    /**
     * Removes all Option.None, and unwrap Option.Some from collections
     */
    static Object removeNonesAndSomesFromCollections(Attribute<?,?> attribute, Object resultFromDb) {
        logger.debug("removeNonesAndSomesFromCollections({},{})", attribute, resultFromDb);
        Object ret;
        if (isOption(attribute)) {
            return resultFromDb;
        }
        if (resultFromDb instanceof List) {
            ret = newList(map(optionGet, filter(not(isNoneOrNull), (Collection<?>)resultFromDb)));
        } else if (resultFromDb instanceof SortedSet) {
            ret = new TreeSet<Object>(newList(map(optionGet, filter(not(isNoneOrNull), (Collection<?>)resultFromDb))));
        } else if (resultFromDb instanceof Set) {
            ret = newSet(map(optionGet, filter(not(isNoneOrNull), (Collection<?>)resultFromDb)));
        } else {
            ret = resultFromDb;
        }
        logger.debug("removeNonesAndSomesFromCollections -> {}", ret);
        return ret;
    }
    
    static final boolean isNoneOrNull(Object possiblyOption) {
        if (possiblyOption == null) {
            return true;
        } else if (possiblyOption instanceof Option) {
            return !((Option<?>) possiblyOption).isDefined();
        } else {
            return false;
        }
    }
    
    static final Object optionGet(Object possiblyOption) {
        if (possiblyOption instanceof Option) {
            return ((Option<?>) possiblyOption).get();
        } else {
            return possiblyOption;
        }
    }
}
