package fi.solita.utils.query.codegen;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;

import java.util.List;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import fi.solita.utils.codegen.CommonMetadataProcessor;
import fi.solita.utils.codegen.generators.Generator;
import fi.solita.utils.query.codegen.generators.ConstructorsAsJpaProjections;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"JpaConstructorProcessor." + CommonMetadataProcessor.Options.enabled,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.generatedClassNamePattern,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.includesRegex,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.excludesRegex,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.onlyPublicMembers,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.includesAnnotation,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.excludesAnnotation,
                   "JpaConstructorProcessor." + JpaConstructorProcessor.Options.makeFieldsPublic})
public class JpaConstructorProcessor extends CommonMetadataProcessor<JpaConstructorProcessor.ExtendedGeneratorOptions> {

    public static class Options {
        public static final String makeFieldsPublic = "makeFieldsPublic";
    }
    
    public boolean makeFieldsPublic() { return Boolean.parseBoolean(findOption(Options.makeFieldsPublic, "false")); }
    
    @Override
    public ExtendedGeneratorOptions generatorOptions() {
        return new ExtendedGeneratorOptions() {
            @Override
            public boolean makeFieldsPublic() {
                return JpaConstructorProcessor.this.makeFieldsPublic();
            }
            public boolean onlyPublicMembers() {
                return JpaConstructorProcessor.this.onlyPublicMembers();
            }
        };
    }
    
    public static class ExtendedGeneratorOptions extends CommonMetadataProcessor.CombinedGeneratorOptions implements ConstructorsAsJpaProjections.Options {
    }

    public List<Generator<? super ExtendedGeneratorOptions>> generators() {
        return newList(cons(ConstructorsAsJpaProjections.instance, super.generators()));
    }

}
