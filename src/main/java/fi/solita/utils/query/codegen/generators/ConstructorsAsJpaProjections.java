package fi.solita.utils.query.codegen.generators;

import static fi.solita.utils.codegen.Helpers.boxed;
import static fi.solita.utils.codegen.Helpers.containedType;
import static fi.solita.utils.codegen.Helpers.element2Constructors;
import static fi.solita.utils.codegen.Helpers.elementGenericQualifiedName;
import static fi.solita.utils.codegen.Helpers.importType;
import static fi.solita.utils.codegen.Helpers.importTypes;
import static fi.solita.utils.codegen.Helpers.isPrivate;
import static fi.solita.utils.codegen.Helpers.joinWithSpace;
import static fi.solita.utils.codegen.Helpers.padding;
import static fi.solita.utils.codegen.Helpers.parameterTypesAsClasses;
import static fi.solita.utils.codegen.Helpers.publicElement;
import static fi.solita.utils.codegen.Helpers.qualifiedName;
import static fi.solita.utils.codegen.Helpers.relevantTypeParams;
import static fi.solita.utils.codegen.Helpers.resolveVisibility;
import static fi.solita.utils.codegen.Helpers.toString;
import static fi.solita.utils.codegen.Helpers.typeParameter2String;
import static fi.solita.utils.codegen.generators.Content.EmptyLine;
import static fi.solita.utils.codegen.generators.Content.catchBlock;
import static fi.solita.utils.codegen.generators.Content.reflectionInvokationArgs;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.mkString;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.Functional.zipWithIndex;
import static fi.solita.utils.functional.FunctionalA.concat;
import static fi.solita.utils.functional.FunctionalImpl.filter;
import static fi.solita.utils.functional.FunctionalImpl.flatMap;
import static fi.solita.utils.functional.FunctionalImpl.map;
import static fi.solita.utils.functional.FunctionalS.range;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.functional.Transformers.prepend;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
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
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.codegen.Helpers;
import fi.solita.utils.codegen.Helpers_;
import fi.solita.utils.codegen.generators.Generator;
import fi.solita.utils.codegen.generators.GeneratorOptions;
import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.functional.Function3;
import fi.solita.utils.functional.Option;
import fi.solita.utils.query.EntityRepresentation;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;

public class ConstructorsAsJpaProjections extends Generator<ConstructorsAsJpaProjections.Options> {

    public static interface Options extends GeneratorOptions {
        boolean onlyPublicMembers();
        @SuppressWarnings("rawtypes")
        Class<? extends Apply> getClassForJpaConstructors(int argCount);
    }
    
    public static final ConstructorsAsJpaProjections instance = new ConstructorsAsJpaProjections();

    @Override
    public Iterable<String> apply(ProcessingEnvironment processingEnv, ConstructorsAsJpaProjections.Options options, TypeElement source) {
        if (source.getModifiers().contains(Modifier.ABSTRACT)) {
            return newList();
        }
        
        Iterable<ExecutableElement> elements = element2Constructors.apply(source);
        if (options.onlyPublicMembers()) {
            elements = filter(elements, publicElement);
        }

        Function1<Entry<Integer, ExecutableElement>, Iterable<String>> singleElementTransformer = constructorGen.ap(new Helpers.EnvDependent(processingEnv), options);
        return flatMap(zipWithIndex(elements), singleElementTransformer);
    }

    public static Function3<Helpers.EnvDependent, ConstructorsAsJpaProjections.Options, Map.Entry<Integer, ExecutableElement>, Iterable<String>> constructorGen = new Function3<Helpers.EnvDependent, ConstructorsAsJpaProjections.Options, Map.Entry<Integer, ExecutableElement>, Iterable<String>>() {
        @Override
        public Iterable<String> apply(Helpers.EnvDependent helper, ConstructorsAsJpaProjections.Options options, Map.Entry<Integer, ExecutableElement> entry) {
            ExecutableElement constructor = entry.getValue();
            TypeElement enclosingElement = (TypeElement) constructor.getEnclosingElement();
            String enclosingElementQualifiedName = qualifiedName.apply(enclosingElement);
            int index = entry.getKey();

            List<String> attributeTypes = newList();
            List<Integer> idIndexes = newList();
            for (Map.Entry<Integer, ? extends VariableElement> e: zipWithIndex(constructor.getParameters())) {
                int paramIndex = e.getKey();
                VariableElement argument = e.getValue();
                Class<?> attributeClass;
                String otherTypeParams;
                TypeMirror argumentType = argument.asType();
                if (helper.isSubtype(argumentType, Id.class)) {
                    attributeClass = SingularAttribute.class;
                    otherTypeParams = "? extends " + importType(EntityRepresentation.class);
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
                        if (isEntity || isId) {
                            elementType = importType(EntityRepresentation.class);
                        }
                        elementType = elementType.startsWith("?") ? elementType : "? extends " + elementType;
                        
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

            int argCount = constructor.getParameters().size();
            String returnTypeImported = importTypes(elementGenericQualifiedName(enclosingElement));

            List<String> argumentTypes = newList(map(constructor.getParameters(), qualifiedName.andThen(boxed).andThen(Helpers_.importTypes)));
            List<String> argumentNames = argCount == 0 ? Collections.<String>newList() : newList(map(range(1, argCount), toString.andThen(prepend("$p"))));
            List<String> attributeNames = argCount == 0 ? Collections.<String>newList() : newList(map(range(1, argCount), toString.andThen(prepend("$a"))));
            List<? extends TypeParameterElement> relevantTypeParamsForConstructor = newList(relevantTypeParams(constructor));
            List<String> relevantTypeParams = newList(map(relevantTypeParamsForConstructor, typeParameter2String));
            
            boolean isPrivate = isPrivate(constructor);
            boolean throwsChecked = helper.throwsCheckedExceptions(constructor);
            String constructorClass = options.getClassForJpaConstructors(argCount).getName().replace('$', '.');

            String fundef = importTypes(constructorClass) + "<" + mkString(",", cons("OWNER", argumentTypes)) + "," + returnTypeImported + ">";
            String declaration = resolveVisibility(constructor) + " static final <" + mkString(",", cons("OWNER", relevantTypeParams)) + "> " + fundef + " c" + (index+1);

            Iterable<String> tryBlock = isPrivate 
                    ? Some("return (" + returnTypeImported + ")getMember().newInstance(" + mkString(", ", argumentNames) + ");")
                    : Some("return new " + returnTypeImported + "(" + mkString(", ", argumentNames) + ");");
            
            Iterable<String> tryCatchBlock = isPrivate || throwsChecked
                ? concat(
                    Some("try {"),
                    map(tryBlock, padding),
                    catchBlock,
                    Some("}"))
                : tryBlock;
                    
            Iterable<String> applyBlock = concat(
                Some("@Override"),
                Some("public " + returnTypeImported + " apply(" + mkString(", ", map(zip(argumentTypes, argumentNames), joinWithSpace)) + ") {"),
                map(tryCatchBlock, padding),
                Some("}")
            );
            
            Iterable<String> getParametersBlock = newList(
                "public " + importType(List.class) + "<" + importType(Attribute.class) + "<?, ?>> getParameters() {",
                padding.apply("return " + importType(Arrays.class) + ".<" + importType(Attribute.class) + "<?, ?>>asList(" + mkString(", ", attributeNames) + ");"),
                "}"
            );
            Iterable<String> getIndexesOfIdWrappingParametersBlock = newList(
                "public " + importType(List.class) + "<Integer> getIndexesOfIdWrappingParameters() {",
                padding.apply("return " + importType(Arrays.class) + ".<Integer>asList(" + mkString(", ", map(idIndexes, toString)) + ");"),
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
                Some(declaration + "(" + mkString(", ", map(zip(map(attributeTypes, prepend("final ")), attributeNames), joinWithSpace)) + ") {"),
                map(concat(
                    Some("return new " + fundef + "(" + mkString(", ", cons(importTypes(enclosingElementQualifiedName) + ".class", reflectionInvokationArgs(parameterTypesAsClasses(constructor, relevantTypeParamsForConstructor)))) + ") {"),
                    map(body, padding),
                    Some("};")
                ), padding),
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