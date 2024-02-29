package fi.solita.utils.query.meta.generators;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newMutableList;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.filter;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.mkString;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.Functional.zipWithIndex;
import static fi.solita.utils.functional.FunctionalC.repeat;
import static fi.solita.utils.functional.FunctionalS.range;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.functional.Predicates.not;
import static fi.solita.utils.functional.Transformers.prepend;
import static fi.solita.utils.meta.Helpers.boxed;
import static fi.solita.utils.meta.Helpers.containedType;
import static fi.solita.utils.meta.Helpers.element2Constructors;
import static fi.solita.utils.meta.Helpers.element2Methods;
import static fi.solita.utils.meta.Helpers.elementGenericQualifiedName;
import static fi.solita.utils.meta.Helpers.importType;
import static fi.solita.utils.meta.Helpers.importTypes;
import static fi.solita.utils.meta.Helpers.isPrivate;
import static fi.solita.utils.meta.Helpers.joinWithSpace;
import static fi.solita.utils.meta.Helpers.padding;
import static fi.solita.utils.meta.Helpers.parameterTypesAsClasses;
import static fi.solita.utils.meta.Helpers.privateElement;
import static fi.solita.utils.meta.Helpers.publicElement;
import static fi.solita.utils.meta.Helpers.qualifiedName;
import static fi.solita.utils.meta.Helpers.relevantTypeParams;
import static fi.solita.utils.meta.Helpers.resolveVisibility;
import static fi.solita.utils.meta.Helpers.simpleName;
import static fi.solita.utils.meta.Helpers.staticElement;
import static fi.solita.utils.meta.Helpers.toString;
import static fi.solita.utils.meta.Helpers.typeParameter2String;
import static fi.solita.utils.meta.Helpers.withAnnotations;
import static fi.solita.utils.meta.generators.Content.EmptyLine;
import static fi.solita.utils.meta.generators.Content.catchBlock;
import static fi.solita.utils.meta.generators.Content.reflectionInvokationArgs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.functional.Function3;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Predicate;
import fi.solita.utils.functional.Transformer;
import fi.solita.utils.meta.Helpers;
import fi.solita.utils.meta.generators.Generator;
import fi.solita.utils.meta.generators.GeneratorOptions;
import fi.solita.utils.query.EntityRepresentation;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;

public class ConstructorsAsJpaProjections extends Generator<ConstructorsAsJpaProjections.Options> {

    public static interface Options extends GeneratorOptions {
        boolean onlyPublicMembers();
        boolean includePrivateMembers();
        @SuppressWarnings("rawtypes")
        Class<? extends Apply> getClassForJpaConstructors(int argCount);
        public String includesAnnotation();
    }
    
    public static final ConstructorsAsJpaProjections instance = new ConstructorsAsJpaProjections();

    @Override
    public Iterable<String> apply(ProcessingEnvironment processingEnv, final ConstructorsAsJpaProjections.Options options, final TypeElement source) {
        if (source.getModifiers().contains(Modifier.ABSTRACT)) {
            return newMutableList();
        }
        
        Iterable<ExecutableElement> elements = concat(element2Constructors.apply(source), (source.getKind().equals(ElementKind.ENUM) ? Collections.<ExecutableElement>emptyList() : filter(staticElement, element2Methods.apply(source))));
        if (options.onlyPublicMembers()) {
            elements = filter(publicElement, elements);
        } else if (!options.includePrivateMembers()) {
            elements = filter(not(privateElement), elements);
        }
        // include only constructors/methods annotated with includesAnnotation, or all of them is present on class
        elements = filter(new Predicate<ExecutableElement>() {
            @Override
            public boolean accept(ExecutableElement candidate) {
                return options.includesAnnotation().isEmpty() || // no includesAnnotation -> include all
                       withAnnotations(options.includesAnnotation(), false).accept(source) || // annotation present on this class
                       withAnnotations(options.includesAnnotation(), false).accept(candidate); // annotation present on this contructor
            }
        }, elements);

        Function1<Entry<Integer, ExecutableElement>, Iterable<String>> singleElementTransformer = elementGen.ap(new Helpers.EnvDependent(processingEnv), options);
        return flatMap(singleElementTransformer, zipWithIndex(elements));
    }
    
    static Transformer<CharSequence,String> helpersImportTypes = new Transformer<CharSequence,String>() {
        @Override
        public String transform(CharSequence source) {
            return Helpers.importTypes(source);
        }
    };

    public static Function3<Helpers.EnvDependent, ConstructorsAsJpaProjections.Options, Map.Entry<Integer, ExecutableElement>, Iterable<String>> elementGen = new Function3<Helpers.EnvDependent, ConstructorsAsJpaProjections.Options, Map.Entry<Integer, ExecutableElement>, Iterable<String>>() {
        @Override
        public Iterable<String> apply(final Helpers.EnvDependent helper, ConstructorsAsJpaProjections.Options options, Map.Entry<Integer, ExecutableElement> entry) {
            ExecutableElement element = entry.getValue();
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            String enclosingElementQualifiedName = qualifiedName.apply(enclosingElement);
            int index = entry.getKey();

            List<String> attributeTypes = newMutableList();
            List<Integer> idIndexes = newMutableList();
            for (Map.Entry<Integer, ? extends VariableElement> e: zipWithIndex(element.getParameters())) {
                int paramIndex = e.getKey();
                VariableElement argument = e.getValue();
                Class<?> attributeClass;
                String otherTypeParams;
                TypeMirror argumentType = argument.asType();
                if (helper.isSameType(argumentType, Id.class)) {
                    attributeClass = SingularAttribute.class;
                    String elementType = containedType(argument);
                    otherTypeParams = "? extends " + importType(EntityRepresentation.class) + (elementType.equals("?") ? "<?>" : "<? super " + importTypes(elementType) +">");
                } else if (helper.isSubtype(argumentType, Id.class)) {
                    attributeClass = SingularAttribute.class;
                    // TODO: real paramerer type
                    otherTypeParams = "? extends " + importType(EntityRepresentation.class) + "<?>";
                } else {
                    Elements elements = helper.elementUtils;
                    Types types = helper.typeUtils;
                    
                    boolean isEntity = helper.isSubtype(argumentType, IEntity.class);
                    boolean isExactlyOption = helper.isSameType(argumentType, Option.class);
                    boolean isList = helper.isSubtype(argumentType, List.class);
                    boolean isSet = helper.isSubtype(argumentType, Set.class);
                    boolean isExactlyCollection = helper.isSameType(argumentType, Collection.class);

                    if (isSet || isList || isExactlyCollection || isExactlyOption) {
                        attributeClass = isSet ? SetAttribute.class :
                                         isList ? ListAttribute.class :
                                         isExactlyCollection ? PluralAttribute.class :
                                         SingularAttribute.class;
                        TypeElement wrapperType = isSet ? elements.getTypeElement(Set.class.getName()) :
                                                  isList ? elements.getTypeElement(List.class.getName()) :
                                                  isExactlyCollection ? elements.getTypeElement(Collection.class.getName()) :
                                                  elements.getTypeElement(Option.class.getName());
                        
                        boolean isId = isId(argument, elements, types, wrapperType);
                        if (isId) {
                            idIndexes.add(paramIndex);
                        }
                        
                        String elementType = containedType(argument);
                        if (elementType.startsWith(Id.class.getName())) {
                            elementType = containedType(elementType);
                        } /*else {
                            elementType = containedType(Helpers.typeMirror2QualifiedName.apply(find(new Predicate<TypeMirror>() {
                                    @Override
                                    public boolean accept(TypeMirror candidate) {
                                        return helper.isSameType(candidate, Id.class);
                                    }
                                }, types.directSupertypes(elements.getTypeElement(containedType(elementType)).asType())).get()));
                        }*/
                        if (isEntity) {
                            elementType = elementType.startsWith("?") ? elementType : "? super " + elementType;
                            elementType = "? extends " + importType(EntityRepresentation.class) + "<" + importTypes(elementType) + ">";
                        } else if (isId) {
                            // TODO: real parameter type
                            elementType = "? extends " + importType(EntityRepresentation.class) + "<?>";
                        } else {
                            elementType = elementType.startsWith("?") ? importTypes(elementType) : "? extends " + importTypes(elementType);
                        }
                        
                        if (isExactlyCollection) {
                            otherTypeParams = "? extends " + importType(Collection.class) + "<" + elementType +  ">, " + elementType;
                        } else {
                            otherTypeParams = elementType;
                        }
                        if (isExactlyOption) {
                            otherTypeParams = "? extends " + importType(Option.class) + "<" + otherTypeParams + ">";
                        }
                    } else {
                        attributeClass = SingularAttribute.class;
                        String type = importTypes(qualifiedName.andThen(boxed).apply(argument));
                        otherTypeParams = "?".equals(type) ? type : "? extends " + type;
                    }
                }

                attributeTypes.add(importType(attributeClass) + "<? super OWNER, " + otherTypeParams + ">");
            }

            int argCount = element.getParameters().size();
            String returnTypeImported = importTypes(elementGenericQualifiedName(enclosingElement));

            List<String> argumentTypes = newList(map(qualifiedName.andThen(boxed).andThen(helpersImportTypes), element.getParameters()));
            List<String> argumentNames = argCount == 0 ? Collections.<String>newMutableList() : newList(map(toString.andThen(prepend("$p")), range(1, argCount)));
            List<String> attributeNames = argCount == 0 ? Collections.<String>newMutableList() : newList(map(toString.andThen(prepend("$a")), range(1, argCount)));
            List<? extends TypeParameterElement> relevantTypeParamsForConstructor = newList(relevantTypeParams(element));
            List<String> relevantTypeParams = newList(map(typeParameter2String, relevantTypeParamsForConstructor));
            
            String methodName = element.getSimpleName().toString();
            boolean isPrivate = isPrivate(element);
            boolean throwsChecked = helper.throwsCheckedExceptions(element);
            String constructorClass = options.getClassForJpaConstructors(argCount).getName().replace('$', '.');

            String fundef = importTypes(constructorClass) + "<" + mkString(",", cons("OWNER", argumentTypes)) + "," + returnTypeImported + ">";
            String declaration = resolveVisibility(element) + " static final <" + mkString(",", cons("OWNER", relevantTypeParams)) + "> " + fundef + " " + ("<init>".equals(methodName) ? repeat('$', index+2) : methodName);

            String returnClause =  "return " + (isPrivate ? "(" + returnTypeImported + ")" : "");
            String instanceName = importTypes(enclosingElementQualifiedName);
            List<? extends TypeParameterElement> typeParameters = element.getTypeParameters();
            
            Iterable<String> tryBlock = element.getKind().equals(ElementKind.METHOD)
                    ? (isPrivate
                        ? Some(returnClause + "getMember().invoke(" + mkString(", ", cons("null", argumentNames)) + ");")
                        : Some(returnClause + instanceName + "." + (typeParameters.isEmpty() ? "" : "<" + mkString(", ", map(simpleName, typeParameters)) + ">") + methodName + "(" + mkString(", ", argumentNames) + ");"))
                    : isPrivate
                    ? Some("return (" + returnTypeImported + ")getMember().newInstance(" + mkString(", ", argumentNames) + ");")
                    : Some("return new " + returnTypeImported + "(" + mkString(", ", argumentNames) + ");");
            
            Iterable<String> tryCatchBlock = isPrivate || throwsChecked
                ? concat(
                    Some("try {"),
                    map(padding, tryBlock),
                    catchBlock,
                    Some("}"))
                : tryBlock;
                    
            Iterable<String> applyBlock = concat(
                Some("@Override"),
                Some("public " + returnTypeImported + " apply(" + mkString(", ", map(joinWithSpace, zip(argumentTypes, argumentNames))) + ") {"),
                map(padding, tryCatchBlock),
                Some("}")
            );
            
            Iterable<String> getParametersBlock = newList(
                "public " + importType(List.class) + "<" + importType(Attribute.class) + "<?, ?>> getParameters() {",
                padding.apply("return " + importType(Arrays.class) + ".<" + importType(Attribute.class) + "<?, ?>>asList(" + mkString(", ", attributeNames) + ");"),
                "}"
            );
            Iterable<String> getIndexesOfIdWrappingParametersBlock = newList(
                "public " + importType(List.class) + "<Integer> getIndexesOfIdWrappingParameters() {",
                padding.apply("return " + importType(Arrays.class) + ".<Integer>asList(" + mkString(", ", map(toString, idIndexes)) + ");"),
                "}"
            );
            
            Iterable<String> body = concat(
                applyBlock,
                EmptyLine,
                getParametersBlock,
                EmptyLine,
                getIndexesOfIdWrappingParametersBlock
            );

            return concat(
                Some(declaration + "(" + mkString(", ", map(joinWithSpace, zip(map(prepend("final "), attributeTypes), attributeNames))) + ") {"),
                map(padding, concat(
                    Some("return new " + fundef + "(" + mkString(", ", cons(importTypes(enclosingElementQualifiedName) + ".class", reflectionInvokationArgs(parameterTypesAsClasses(element, relevantTypeParamsForConstructor)))) + ") {"),
                    map(padding, body),
                    Some("};")
                )),
                Some("}"),
                EmptyLine
            );
        }
    };
    
    private static final boolean isId(VariableElement argument, Elements elements, Types types, TypeElement wrapperType) {
        TypeMirror innerInnerType;
        String innerTypeName = containedType(argument);
        if (innerTypeName.replaceFirst("<.*", "").equals(Id.class.getName())) {
            String innerInnerTypeName = containedType(innerTypeName).replaceAll("<.*", "");
            if (innerInnerTypeName.equals("?")) {
                innerInnerType = types.getWildcardType(null, null);
            } else {
                innerInnerType = elements.getTypeElement(innerInnerTypeName).asType();
            }
        } else {
            innerInnerType = types.getWildcardType(null, null);
        }
        TypeMirror idType = types.getDeclaredType(elements.getTypeElement(Id.class.getName()), innerInnerType);
        
        WildcardType wildInnerType = types.getWildcardType(idType, null);
        DeclaredType wholeType = types.getDeclaredType(wrapperType, wildInnerType);
        return types.isAssignable(argument.asType(), wholeType);
    }
}