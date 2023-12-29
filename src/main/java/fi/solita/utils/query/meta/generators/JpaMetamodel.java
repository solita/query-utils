package fi.solita.utils.query.meta.generators;

import static fi.solita.utils.meta.Helpers.boxed;
import static fi.solita.utils.meta.Helpers.containedType;
import static fi.solita.utils.meta.Helpers.element2Fields;
import static fi.solita.utils.meta.Helpers.element2Methods;
import static fi.solita.utils.meta.Helpers.simpleName;
import static fi.solita.utils.meta.Helpers.typeMirror2GenericQualifiedName;
import static fi.solita.utils.meta.Helpers.typeParameter2String;
import static fi.solita.utils.meta.Helpers.withAnnotations;
import static fi.solita.utils.meta.generators.Content.EmptyLine;
import static fi.solita.utils.functional.Collections.newMutableList;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.init;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.zipWithIndex;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.functional.Predicates.not;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import fi.solita.utils.meta.Helpers;
import fi.solita.utils.meta.generators.Generator;
import fi.solita.utils.meta.generators.GeneratorOptions;
import fi.solita.utils.functional.Function2;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Predicate;
import fi.solita.utils.functional.Transformer;

/**
 * Note! This implementation is not based on any spec, so it may not work correctly for all situations.
 */
public class JpaMetamodel extends Generator<JpaMetamodel.Options> {

    public static interface Options extends GeneratorOptions {
    }
    
    public static JpaMetamodel instance = new JpaMetamodel();

    public static final Predicate<Element> withModifier(final Modifier modifier) {
        return new Predicate<Element>() {
            @Override
            public boolean accept(Element candidate) {
                return candidate.getModifiers().contains(modifier);
            }
        };
    }
    
    public static final Predicate<Element> isTransient = withModifier(Modifier.TRANSIENT).or(withAnnotations(Transient.class.getName(), false));
    
    public static final Predicate<Element> isPersistentClass = 
        withAnnotations(Entity.class.getName(), false).or(
        withAnnotations(MappedSuperclass.class.getName(), false).or(
        withAnnotations(Embeddable.class.getName(), false)));
    
    @Override
    public Iterable<String> apply(ProcessingEnvironment processingEnv, Options options, TypeElement source) {
        if (!isPersistentClass.apply(source)) {
            return newMutableList();
        }
        
        Access classAccess = source.getAnnotation(Access.class);
        final AccessType defaultAccess = classAccess == null ? resolveAccessTypeFromId(source) : classAccess.value();
        
        Iterable<? extends Element> fields = filter(not(isTransient).and(new Predicate<Element>() {
            @Override
            public boolean accept(Element candidate) {
                Access acc = candidate.getAnnotation(Access.class);
                return defaultAccess == AccessType.FIELD && acc == null || acc != null && acc.value() == AccessType.FIELD;
            }
        }), element2Fields.apply(source));
        Iterable<? extends Element> methods = filter(not(isTransient).and(new Predicate<Element>() {
            @Override
            public boolean accept(Element candidate) {
                Access acc = candidate.getAnnotation(Access.class);
                return defaultAccess == AccessType.PROPERTY && acc == null || acc != null && acc.value() == AccessType.PROPERTY;
            }
        }), element2Methods.apply(source));
        
        return flatMap(singleElementHandler.ap(new Helpers.EnvDependent(processingEnv)), zipWithIndex(concat(fields, methods)));
    }

    private AccessType resolveAccessTypeFromId(TypeElement source) {
        // TODO: not implemented
        return AccessType.FIELD;
    }
    
    private static final String propertyNameToAttributeName(String propertyName) {
        if (propertyName.startsWith("get")) {
            return Character.toString(propertyName.charAt(3)).toLowerCase() + propertyName.substring(4);
        } else if (propertyName.startsWith("is")) {
            return Character.toString(propertyName.charAt(2)).toLowerCase() + propertyName.substring(3);
        }
        return propertyName;
    }

    public static Function2<Helpers.EnvDependent, Map.Entry<Integer, ? extends Element>, Iterable<String>> singleElementHandler = new Function2<Helpers.EnvDependent, Map.Entry<Integer, ? extends Element>, Iterable<String>>() {
        @Override
        public Iterable<String> apply(Helpers.EnvDependent helper, Map.Entry<Integer, ? extends Element> entry) {
            Element member = entry.getValue();
            TypeElement enclosingElement = (TypeElement) member.getEnclosingElement();
            TypeMirror returnType = member instanceof ExecutableElement ? ((ExecutableElement)member).getReturnType() : member.asType();
            
            boolean isSet = helper.isSubtype(returnType, Set.class);
            boolean isList = helper.isSubtype(returnType, List.class);
            boolean isMap = helper.isSubtype(returnType, Map.class);
            boolean isCollection = helper.isSubtype(returnType, Collection.class);

            String attributeName = member.getSimpleName().toString();
            if (member instanceof ExecutableElement) {
                attributeName = propertyNameToAttributeName(attributeName);
            }
            
            String attributeClass = isSet ? "Set" :
                                   isList ? "List" :
                                   isMap ? "Map" :
                                   isCollection ? "Collection" :
                                   "Singular";
            
            Iterable<Pair<String, String>> classTypeParameters = map(new Transformer<TypeParameterElement, Pair<String,String>>() {
                @Override
                public Pair<String, String> transform(TypeParameterElement source) {
                    return Pair.of(simpleName.apply(source), typeParameter2String.apply(source));
                }
            }, enclosingElement.getTypeParameters());
            
            String ownerType = typeMirror2GenericQualifiedName.apply(enclosingElement.asType());
            String attributeType = isCollection ? containedType(member) : typeMirror2GenericQualifiedName.andThen(boxed).apply(returnType);
            if (isMap) {
                attributeType = init(attributeType.replace("java.util.Map<", ""));
            }
            
            String typeSignature = "<" + ownerType + "," + attributeType + ">";
            for (Pair<String, String> param: classTypeParameters) {
                typeSignature = typeSignature.replaceAll("(?<=[,< ])" + Pattern.quote(param.left()) + "(?=[,> ])", Matcher.quoteReplacement(param.right()));
            }
            for (Pair<String, String> param: classTypeParameters) {
                typeSignature = typeSignature.replaceAll("(?<=[,< ])" + Pattern.quote(param.left()) + "(?=[,> ])", "?");
            }
            
            String type = "javax.persistence.metamodel." + attributeClass + "Attribute" + typeSignature;
            
            Iterable<String> res = concat(
                Some("public static volatile " + type + " " + attributeName + ";"),
                EmptyLine
            );
            return res;
        }

    };
}