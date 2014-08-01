package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.init;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.query.QueryUtils.join;
import static fi.solita.utils.query.attributes.AttributeProxy.unwrap;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.NotDistinctable;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.attributes.RestrictingAttribute;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

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
            logger.info("Adding restrictions from: {}", from);
            From<?, ?> join = from;
            for (Attribute<?,?> rest: r.getRestrictionChain()) {
                logger.info("Restricting (inner joining) to: {}", rest);
                join = QueryUtils.join(join, rest, JoinType.INNER);
                doRestrictions(join, rest);
            }
        }
        return from;
    }

    static Pair<? extends From<?,?>, ? extends Attribute<?,?>> doJoins(From<?,?> root, Attribute<?,?> target, JoinType type) {
        logger.debug("doJoins({},{},{})", new Object[] {root, target, type});
        From<?,?> exp = root;
        for (JoiningAttribute joining: unwrap(JoiningAttribute.class, target)) {
            logger.info("JoiningAttribute detected. Performing joins from: {}", root);
            List<? extends Attribute<?, ?>> attributes = joining.getAttributes();
            for (Attribute<?,?> join: init(attributes)) {
                if (!(join instanceof PseudoAttribute)) {
                    logger.debug("Joining from {} to {} with {}", new Object[]{exp, join, type});
                    exp = join((From<?,?>)exp, join, type);
                } else {
                    logger.debug("Skipping PseudoAttribute: {}", join);
                }
            }
            target = last(attributes);
        }
        Pair<? extends From<?,?>, ? extends Attribute<?,?>> ret = Pair.of(exp, target);
        logger.debug("doJoins -> {}", ret);
        return ret;
    }
}
