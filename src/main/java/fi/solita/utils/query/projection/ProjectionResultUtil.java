package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newArray;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.range;
import static fi.solita.utils.functional.Functional.size;
import static fi.solita.utils.functional.Functional.zip;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple2;
import static fi.solita.utils.query.QueryUtils.*;
import static fi.solita.utils.query.attributes.AttributeProxy.*;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;
import static fi.solita.utils.query.projection.ProjectionResultUtil_.*;

class ProjectionResultUtil {
    
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
        if (attr == null) {
            // null is used as a placeholder in SelfAttribute an Constructors.IdProjection... Yeah, should use something else...
            return resultFromDb;
        }
        return transformPseudoResultToActualValue.ap(attr).andThen(
               wrapNullsToOptionsWhereAppropriate.ap(attr)).andThen(
               convertNullsToEmbeddableWhereRequired.ap(attr))
               .apply(resultFromDb);
    }
    
    static Iterable<Object> postProcessRow(ConstructorMeta_<?, ?, ?> constructor_, Iterable<Object> row) {
        return map(zip(constructor_.getParameters(), row), postProcessValue);
    }

    static <R> Iterable<R> transformAllRows(ConstructorMeta_<?, R, ?> constructor_, Iterable<Iterable<Object>> rows) {
        return map(rows, ProjectionResultUtil_.<R>transformRow().ap(constructor_));
    }
    
    static <T> T transformRow(ConstructorMeta_<?,T,?> constructor_, Iterable<Object> row) {
        row = postProcessRow(constructor_, row);
        // at this point there should be no nulls...
        for (Tuple2<Object, Integer> result: zip(row, range(0))) {
            if (result._1 == null) {
                throw new ProjectionResultUtil.NullValueButNonOptionConstructorArgumentException(constructor_.getClass(), constructor_.getConstructorParameterTypes().get(result._2), result._2);
            }
        }

        @SuppressWarnings("unchecked")
        T instance = ((ConstructorMeta_<?,T,Object>)constructor_).apply(size(row) == 1 ? head(row) : Tuple.of(newArray(Object.class, row)));
        return instance;
    }
    
    static Object postProcessResult(Class<?> constructorParameterType, Attribute<?, ?> attr, List<Object> val) {
        Object v;
        if (attr instanceof SingularAttribute) {
            if (!isRequiredByQueryAttribute(attr) && val.isEmpty()) {
                v = null;
            } else {
                if (val.size() != 1) {
                    throw new IllegalArgumentException("Collection expected to be of size " + 1 + " but was: " + val);
                }
                v = head(val);
            }
        } else {
            if (List.class.isAssignableFrom(constructorParameterType)) {
                v = newList(val);
            } else if (SortedSet.class.isAssignableFrom(constructorParameterType)) {
                v = new TreeSet<Object>(val);
            } else {
                v = newSet(val);
            }
        }
        return v;
    }

    static Object transformPseudoResultToActualValue(Attribute<?,?> attribute, Object resultFromDb) {
        for (PseudoAttribute pseudo: unwrap(PseudoAttribute.class, attribute)) {
            return pseudo.getValueToReplaceResult(resultFromDb);
        }
        return resultFromDb;
    }

    /** Wraps values to Some and nulls to None for optional parameters, leave others as is */
    static Object wrapNullsToOptionsWhereAppropriate(Attribute<?,?> attribute, Object resultFromDb) {
        return isRequiredByQueryAttribute(attribute) || resultFromDb instanceof Option ? resultFromDb : Option.of(resultFromDb);
    }
    
    /** Hibernate cannot handle embeddables with all-null values correctly, since it doesn't separate a missing embeddable and an existing all-null embeddable.
     *  So we instantiate the empty embeddable if the result has been left null but the attribute is required */
    static Object convertNullsToEmbeddableWhereRequired(Attribute<?,?> attribute, Object resultFromDb) {
        Option<? extends Attribute<?, ?>> embeddable = EmbeddableUtil.unwrapEmbeddableAttribute(attribute);
        if (embeddable.isDefined() && resultFromDb == null && isRequiredByMetamodel(attribute)) {
            return EmbeddableUtil.instantiate(embeddable.get().getJavaType());
        }
        return resultFromDb;
    }
    
}
