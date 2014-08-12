package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.head;
import static fi.solita.utils.functional.Functional.last;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.FunctionalA.head;
import static fi.solita.utils.functional.FunctionalImpl.map;
import static fi.solita.utils.functional.FunctionalImpl.sort;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.query.QueryUtils_;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.AttributeProxy;
import fi.solita.utils.query.attributes.JoiningAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;

public class EmbeddableUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EmbeddableUtil.class);

    static Iterable<? extends Selection<?>> breakEmbeddableToParts(Metamodel metamodel, Bindable<?> target, final From<?,?> source) {
        return map(getEmbeddableAttributes(target, metamodel), QueryUtils_.get.ap(source));
    }
    
    static Object instantiate(Class<?> clazz) {
        logger.debug("instantiate({})", clazz);
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object ret = constructor.newInstance();
            logger.debug("instantiate -> {}", ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static Object collectEmbeddableFromParts(Metamodel metamodel, Bindable<?> attr, Iterable<Object> columns) {
        logger.debug("collectEmbeddableFromParts({},{},{})", new Object[] {metamodel, attr, columns});
        List<Object> cols = newList(columns);
        List<? extends Attribute<?, ?>> embeddableAttributes = getEmbeddableAttributes(attr, metamodel);
        if (embeddableAttributes.size() != cols.size()) {
            throw new IllegalStateException("Expected same size for: " + embeddableAttributes + ", " + cols);
        }
        Class<?> embeddableClass = getEmbeddableType(attr, metamodel).getJavaType();
        try {
            Object ret = instantiate(embeddableClass);
            for (Tuple2<? extends Attribute<?, ?>, Object> a: zip(embeddableAttributes, cols)) {
                Member member = a._1.getJavaMember();
                if (member instanceof Field) {
                    Field f = (Field)member;
                    f.setAccessible(true);
                    f.set(ret, a._2);
                } else {
                    Method m = (Method)member;
                    if (m.getParameterTypes().length == 1 && head(m.getParameterTypes()).isAssignableFrom(a._1.getJavaType())) {
                        m.setAccessible(true);
                        m.invoke(ret, a._2);
                    } else {
                        throw new UnsupportedOperationException("not implemented. Run, Forrest, run!");
                    }
                }
            }
            logger.debug("collectEmbeddableFromParts -> {}", ret);
            return ret;
        } catch (Exception e)  {
            throw new RuntimeException(e);
        }
    }
    
    static Option<? extends Attribute<?,?>> unwrapEmbeddableAttribute(Attribute<?,?> attribute) {
        logger.debug("unwrapEmbeddableAttribute({})", attribute);
        
        Option<? extends Attribute<?,?>> ret = None();
        if (attribute == null || attribute instanceof PseudoAttribute) {
            ret = None();
        } else if (AttributeProxy.unwrap(PseudoAttribute.class, attribute).isDefined()) {
            ret = None();
        } else if (attribute instanceof JoiningAttribute && last(((JoiningAttribute) attribute).getAttributes()) instanceof PseudoAttribute) {
            ret = None();
        } else if (attribute.getPersistentAttributeType() == PersistentAttributeType.EMBEDDED) {
            ret = Some(attribute);
        } else if (attribute instanceof AdditionalQueryPerformingAttribute) {
            List<Attribute<?, ?>> params = ((AdditionalQueryPerformingAttribute)attribute).getConstructor().getParameters();
            if (params.size() == 1) {
                ret = unwrapEmbeddableAttribute(head(params));
            }
        }
        
        logger.debug("unwrapEmbeddableAttribute -> {}", ret);
        return ret;
    }

    static boolean isCollectionOfEmbeddables(Attribute<?, ?> attribute) {
        logger.debug("isCollectionOfEmbeddables({})", attribute);
        boolean ret = attribute.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION &&
               attribute instanceof PluralAttribute &&
               ((PluralAttribute<?,?,?>)attribute).getElementType().getPersistenceType() == PersistenceType.EMBEDDABLE;
        logger.debug("isCollectionOfEmbeddables -> {}", ret);
        return ret;
    }

    static <T> EmbeddableType<T> getEmbeddableType(Bindable<T> attribute, Metamodel metamodel) {
        logger.debug("getEmbeddableType({},{})", attribute, metamodel);
        EmbeddableType<T> ret = metamodel.embeddable(attribute.getBindableJavaType());
        logger.debug("getEmbeddableType -> {}", ret);
        return ret;
    }
    
    static List<? extends Attribute<?,?>> getEmbeddableAttributes(Bindable<?> attribute, Metamodel metamodel) {
        logger.debug("getEmbeddableAttributes({},{})", attribute, metamodel);
        List<? extends Attribute<?,?>> ret = newList(sort(EmbeddableUtil.getEmbeddableType(attribute, metamodel).getAttributes(), attributeByName));
        logger.debug("getEmbeddableAttributes -> {}", ret);
        return ret;
    }
    
    static final Comparator<Attribute<?,?>> attributeByName = new Comparator<Attribute<?,?>>() {
        @Override
        public int compare(Attribute<?, ?> o1, Attribute<?, ?> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
