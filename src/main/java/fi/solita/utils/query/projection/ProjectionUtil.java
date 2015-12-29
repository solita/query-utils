package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.init;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple3;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.NotDistinctable;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.attributes.RestrictingAttribute;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public class ProjectionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectionUtil.class);

    public static Iterable<Object> objectToObjectList(Object obj) {
        logger.debug("objectToObjectList({})", obj);
        Iterable<Object> ret = obj instanceof Object[] ? newList((Object[]) obj) : newList(obj);
        logger.debug("objectToObjectList -> {}", ret);
        return ret;
    }
    
    static boolean shouldPerformAdditionalQuery(Attribute<?, ?> param) {
        logger.debug("shouldPerformAdditionalQuery({})", param);
        boolean ret = unwrap(AdditionalQueryPerformingAttribute.class, param).isDefined() ||
               (unwrap(PluralAttribute.class, param).isDefined() && !unwrap(PseudoAttribute.class, param).isDefined());
        logger.debug("shouldPerformAdditionalQuery -> {}", ret);
        return ret;
    }

    static boolean isId(Class<?> clazz) {
        logger.debug("isId({})", clazz);
        boolean ret = Id.class.isAssignableFrom(clazz);
        logger.debug("isId -> {}", ret);
        return ret;
    }

    /** whether constructor expects a collections of IDs (instead of a collection of entities) */
    static boolean isWrapperOfIds(MetaJpaConstructor<?, ?, ?> projection, int columnIndex) {
        logger.debug("isWrapperOfIds({},{})", projection, columnIndex);
        boolean ret = projection.getIndexesOfIdWrappingParameters().contains(columnIndex);
        logger.debug("isWrapperOfIds -> {}", ret);
        return ret;
    }

    static boolean isDistinctable(MetaJpaConstructor<?, ?, ?> projection, int columnIndex) {
        logger.debug("isDistinctable({},{})", projection, columnIndex);
        boolean ret = Set.class.isAssignableFrom(projection.getConstructorParameterTypes().get(columnIndex)) &&
               !NotDistinctable.class.isAssignableFrom(((Bindable<?>)projection.getParameters().get(columnIndex)).getBindableJavaType());
        logger.debug("isDistinctable -> {}", ret);
        return ret;
    }
    
    static <A extends From<?,?>> A doRestrictions(A from, Attribute<?,?> a) {
        for (RestrictingAttribute r: unwrap(RestrictingAttribute.class, a)) {
            logger.debug("Adding restrictions from: {}", from);
            From<?, ?> join = from;
            for (Attribute<?,?> rest: r.getRestrictionChain()) {
                logger.debug("Restricting (inner joining) to: {}", rest);
                join = QueryUtils.join(join, rest, JoinType.INNER);
                doRestrictions(join, rest);
            }
        }
        return from;
    }

    // TODO: needs cleanup...
    @SuppressWarnings("unchecked")
    static Tuple3<Map<Attribute<?,?>,From<?, ?>>, From<?, ?>, Attribute<?,?>> doJoins(From<?,?> root, Attribute<?,?> target, JoinType type) {
        logger.debug("doJoins({},{},{})", new Object[] {root, target, type});
        Map<Attribute<?,?>,From<?,?>> actualJoins = new HashMap<Attribute<?, ?>, From<?, ?>>();
        actualJoins.put(target, root);
        From<?,?> previous = root;
        for (JoiningAttribute joining: unwrap(JoiningAttribute.class, target)) {
            logger.debug("JoiningAttribute detected. Performing joins from: {}", root);
            List<? extends Attribute<?, ?>> attributes = joining.getAttributes();
            for (Attribute<?,?> join: init(attributes)) {
                if (unwrap(JoiningAttribute.class, join).isDefined()) {
                    Tuple3<Map<Attribute<?,?>,From<?,?>>,From<?,?>,Attribute<?,?>> res = doJoins(previous, join, type);
                    previous = QueryUtils.join(res._2, join, type);
                    actualJoins.putAll(res._1);
                } else {
                    logger.debug("Joining from {} to {} with {}", new Object[]{previous, join, type});
                    previous = QueryUtils.join(previous, join, type);
                    actualJoins.put(join, previous);
                }
            }
            for (Attribute<?,?> join: newList(last(attributes))) {
                if (unwrap(JoiningAttribute.class, join).isDefined()) {
                    Tuple3<Map<Attribute<?,?>,From<?,?>>,From<?,?>,Attribute<?,?>> res = doJoins(previous, join, type);
                    previous = res._2;
                    actualJoins.putAll(res._1);
                }
            }
            target = last(attributes);
        }
        return (Tuple3<Map<Attribute<?,?>,From<?, ?>>, From<?, ?>, Attribute<?,?>>)(Object)Tuple.of(actualJoins, previous, target);
    }
}
