package fi.solita.utils.query.codegen.generators;

import static fi.solita.utils.codegen.Helpers.boxed;
import static fi.solita.utils.codegen.Helpers.containedType;
import static fi.solita.utils.codegen.Helpers.element2Constructors;
import static fi.solita.utils.codegen.Helpers.elementGenericQualifiedName;
import static fi.solita.utils.codegen.Helpers.isSubtype;
import static fi.solita.utils.codegen.Helpers.qualifiedName;
import static fi.solita.utils.codegen.Helpers.relevantTypeParams;
import static fi.solita.utils.codegen.Helpers.resolveVisibility;
import static fi.solita.utils.codegen.Helpers.typeParameter2String;
import static fi.solita.utils.codegen.generators.Content.EmptyLine;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.concat;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.flatMap;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Functional.mkString;
import static fi.solita.utils.functional.Functional.range;
import static fi.solita.utils.functional.Functional.zip;
import static fi.solita.utils.functional.Functional.zipWithIndex;
import static fi.solita.utils.functional.Option.Some;
import static fi.solita.utils.functional.Transformers.mkString;
import static fi.solita.utils.functional.Transformers.prepend;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.codegen.CommonMetadataProcessor;
import fi.solita.utils.codegen.generators.ConstructorsAsFunctions;
import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Function0;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.functional.Function3;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Transformers;
import fi.solita.utils.query.codegen.ConstructorMeta_;

public class ConstructorsAsJpaProjections extends Function3<ProcessingEnvironment, CommonMetadataProcessor.GeneratorOptions, TypeElement, Iterable<String>> {

    private static final String ENTITY_BASE_CLASS = "fi.solita.utils.query.IEntity";
    private static final String ENTITY_REP_CLASS = "fi.solita.utils.query.EntityRepresentation";
    private static final String ID_CLASS = "fi.solita.utils.query.Id";

    public static Function1<ProcessingEnvironment, Function1<CommonMetadataProcessor.GeneratorOptions, Function1<TypeElement, Iterable<String>>>> instance = new ConstructorsAsJpaProjections().curried();

    @Override
    public Iterable<String> apply(ProcessingEnvironment processingEnv, CommonMetadataProcessor.GeneratorOptions options, TypeElement source) {
        if (source.getModifiers().contains(Modifier.ABSTRACT)) {
            return newList();
        }

        Function1<Entry<Integer, ExecutableElement>, Iterable<String>> singleElementTransformer = constructorGen.curried().apply(processingEnv).apply(options);
        return flatMap(zipWithIndex(element2Constructors.apply(source)), singleElementTransformer);
    }

    public static Function3<ProcessingEnvironment, CommonMetadataProcessor.GeneratorOptions, Map.Entry<Integer, ExecutableElement>, Iterable<String>> constructorGen = new Function3<ProcessingEnvironment, CommonMetadataProcessor.GeneratorOptions, Map.Entry<Integer, ExecutableElement>, Iterable<String>>() {
        @Override
        public Iterable<String> apply(ProcessingEnvironment processingEnv, CommonMetadataProcessor.GeneratorOptions generatorOption, Map.Entry<Integer, ExecutableElement> entry) {
            ExecutableElement constructor = entry.getValue();
            TypeElement enclosingElement = (TypeElement) constructor.getEnclosingElement();
            int index = entry.getKey();

            List<String> attributeTypes = newList();
            List<Integer> idIndexes = newList();
            for (Map.Entry<Integer, ? extends VariableElement> e: zipWithIndex(constructor.getParameters())) {
                int paramIndex = e.getKey();
                VariableElement argument = e.getValue();

                Class<?> attributeClass;
                String secondTypeParam;
                if (isSubtype(argument, ID_CLASS, processingEnv)) {
                    attributeClass = SingularAttribute.class;
                    secondTypeParam = "? extends " + ENTITY_REP_CLASS;
                } else {
                    boolean isEntity = isSubtype(argument, ENTITY_BASE_CLASS, processingEnv);
                    boolean isOption = isSubtype(argument, Option.class, processingEnv);
                    boolean isList = isSubtype(argument, List.class, processingEnv);
                    boolean isSet = isSubtype(argument, Set.class, processingEnv);

                    if (isSet || isList || isOption) {
                        attributeClass = isSet ? SetAttribute.class : isList ? ListAttribute.class : SingularAttribute.class;
                        String elementType = containedType(argument, processingEnv.getElementUtils());
                        boolean isId = elementType.startsWith(ID_CLASS);
                        if (isId) {
                            idIndexes.add(paramIndex);
                        }
                        if (isEntity || isId) {
                            secondTypeParam = "? extends " + ENTITY_REP_CLASS;
                        } else {
                            secondTypeParam = elementType.startsWith("?") ? elementType : "? extends " + elementType;
                        }
                        if (isOption) {
                            secondTypeParam = "? extends " + Option.class.getName() + "<" + secondTypeParam + ">";
                        }
                    } else {
                        attributeClass = SingularAttribute.class;
                        String type = qualifiedName.andThen(boxed).apply(argument);
                        secondTypeParam = "?".equals(type) ? type : "? extends " + type;
                    }
                }

                attributeTypes.add(attributeClass.getName() + "<? super OWNER, " + secondTypeParam + ">");
            }

            int argCount = constructor.getParameters().size();
            String returnType = elementGenericQualifiedName(enclosingElement);

            List<String> argumentTypes = newList(map(constructor.getParameters(), qualifiedName.andThen(boxed)));
            List<String> argumentNames = argCount == 0 ? Collections.<String>newList() : newList(map(range(1, argCount), Transformers.toString.andThen(prepend("$p"))));
            List<String> attributeNames = argCount == 0 ? Collections.<String>newList() : newList(map(range(1, argCount), Transformers.toString.andThen(prepend("$a"))));
            Iterable<String> relevantTypeParams = map(relevantTypeParams(constructor), typeParameter2String);
            
            String delegateMetaConstructor = "$" + (index == 0 ? "" : index) + (ConstructorsAsFunctions.needsToBeFunction(constructor) ? "()" : "");
            String constructorClass = getClassForConstructors(argCount).getName().replace('$', '.');

            String fundef = constructorClass + "<" + mkString(",", cons("OWNER", argumentTypes)) + "," + returnType + ">";
            String declaration = resolveVisibility(constructor) + " static final <" + mkString(",", cons("OWNER", relevantTypeParams)) + "> " + fundef + " c" + (index+1);

            Iterable<String> body = newList(
                "@Override",
                "public " + returnType + " apply(" + mkString(", ", map(zip(argumentTypes, argumentNames), mkString(" "))) + ") {",
                "    " + Function0.class.getPackage().getName() + ".Function" + argCount + "<" + mkString(",", concat(argumentTypes, newList(returnType))) + "> c = " + delegateMetaConstructor + ";",
                "    return c.apply(" + mkString(", ", argumentNames) + ");",
                "}",
                "",
                "public " + Constructor.class.getName() + "<" + returnType + "> getMember() {",
                "    " + fi.solita.utils.codegen.ConstructorMeta_.class.getName() + "<?," + returnType + "> c = " + delegateMetaConstructor + ";",
                "    return c.getMember();",
                "}",
                "",
                "@SuppressWarnings(\"unchecked\")",
                "public java.util.List<" + Attribute.class.getName() + "<?, ?>> getParameters() {",
                "    return java.util.Arrays.<" + Attribute.class.getName() + "<?, ?>>asList(" + mkString(", ", attributeNames) + ");",
                "}",
                "",
                "public java.util.List<Integer> getIndexesOfIdWrappingParameters() {",
                "    return java.util.Arrays.<Integer>asList(" + mkString(", ", map(idIndexes, Transformers.toString)) + ");",
                "}"
            );

            @SuppressWarnings("unchecked")
            Iterable<String> res = concat(
                Some(declaration + "(" + mkString(", ", map(zip(map(attributeTypes, prepend("final ")), attributeNames), mkString(" "))) + ") {"),
                Some("    return new " + fundef + "() {"),
                map(body, prepend("        ")),
                Some("    };"),
                Some("}"),
                EmptyLine
            );
            return res;
        }
    };

    private static Class<?> getClassForConstructors(int argCount) {
        try {
            return Class.forName(ConstructorMeta_.class.getName() + "$F" + argCount);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}