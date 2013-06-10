package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;

import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;

import fi.solita.utils.query.Id;
import fi.solita.utils.query.NotDistinctable;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.attributes.RelationAttribute;
import fi.solita.utils.query.codegen.ConstructorMeta_;

public class ProjectionUtil {

    public static Iterable<Object> objectToObjectList(Object obj) {
        return obj instanceof Object[] ? newList((Object[]) obj) : newList(obj);
    }
    
    static boolean shouldPerformAdditionalQuery(Attribute<?, ?> param) {
        return unwrap(RelationAttribute.class, param).isDefined() ||
               (unwrap(PluralAttribute.class, param).isDefined() && !unwrap(PseudoAttribute.class, param).isDefined());
    }

    static boolean isId(Class<?> clazz) {
        return Id.class.isAssignableFrom(clazz);
    }

    /** whether constructor expects a collections of IDs (instead of a collection of entities) */
    static boolean isWrapperOfIds(ConstructorMeta_<?, ?, ?> constructor_, int columnIndex) {
        return constructor_.getIndexesOfIdWrappingParameters().contains(columnIndex);
    }

    static boolean isDistinctable(ConstructorMeta_<?, ?, ?> constructor_, int columnIndex) {
        return Set.class.isAssignableFrom(constructor_.getConstructorParameterTypes().get(columnIndex)) &&
               !NotDistinctable.class.isAssignableFrom(((Bindable<?>)constructor_.getParameters().get(columnIndex)).getBindableJavaType());
    }

}
