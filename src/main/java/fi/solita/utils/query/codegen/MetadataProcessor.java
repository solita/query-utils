package fi.solita.utils.query.codegen;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;

import java.util.List;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import fi.solita.utils.codegen.CommonMetadataProcessor;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.query.codegen.generators.ConstructorsAsJpaProjections;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"MetadataProcessor." + CommonMetadataProcessor.Options.generatedClassNamePattern,
                   "MetadataProcessor." + CommonMetadataProcessor.Options.includesRegex,
                   "MetadataProcessor." + CommonMetadataProcessor.Options.excludesRegex,
                   "MetadataProcessor." + CommonMetadataProcessor.Options.onlyPublicMembers,
                   "MetadataProcessor." + CommonMetadataProcessor.Options.includesAnnotation,
                   "MetadataProcessor." + CommonMetadataProcessor.Options.excludesAnnotation})
public class MetadataProcessor extends CommonMetadataProcessor {

    @Override
    public CommonMetadataProcessor.GeneratorOptions generatorOptions() {
        return new CommonMetadataProcessor.GeneratorOptions() {
            @Override
            public boolean makeFieldsPublic() {
                return true;
            }
        };
    }

    public List<Function1<TypeElement,Iterable<String>>> generators() {
        return newList(cons(ConstructorsAsJpaProjections.instance.apply(processingEnv).apply(generatorOptions()), super.generators()));
    }

}
