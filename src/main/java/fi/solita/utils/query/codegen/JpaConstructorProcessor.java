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
import fi.solita.utils.functional.Apply;
import fi.solita.utils.query.codegen.generators.ConstructorsAsJpaProjections;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"JpaConstructorProcessor." + CommonMetadataProcessor.Options.enabled,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.generatedClassNamePattern,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.generatedPackagePattern,
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
        final boolean makeFieldsPublic = JpaConstructorProcessor.this.makeFieldsPublic();
        final boolean onlyPublicMembers = JpaConstructorProcessor.this.onlyPublicMembers();
        return new ExtendedGeneratorOptions() {
            @Override
            public boolean makeFieldsPublic() {
                return makeFieldsPublic;
            }
            public boolean onlyPublicMembers() {
                return onlyPublicMembers;
            }
        };
    }
    
    public static class ExtendedGeneratorOptions extends CommonMetadataProcessor.CombinedGeneratorOptions implements ConstructorsAsJpaProjections.Options {
        @SuppressWarnings("rawtypes")
        @Override
        public Class<? extends Apply> getClassForJpaConstructors(int argCount) {
            switch (argCount) {
            case 0: return ConstructorMeta_.F0.class;
            case 1: return ConstructorMeta_.F1.class;
            case 2: return ConstructorMeta_.F2.class;
            case 3: return ConstructorMeta_.F3.class;
            case 4: return ConstructorMeta_.F4.class;
            case 5: return ConstructorMeta_.F5.class;
            case 6: return ConstructorMeta_.F6.class;
            case 7: return ConstructorMeta_.F7.class;
            case 8: return ConstructorMeta_.F8.class;
            case 9: return ConstructorMeta_.F9.class;
            case 10: return ConstructorMeta_.F10.class;
            case 11: return ConstructorMeta_.F11.class;
            case 12: return ConstructorMeta_.F12.class;
            case 13: return ConstructorMeta_.F13.class;
            case 14: return ConstructorMeta_.F14.class;
            case 15: return ConstructorMeta_.F15.class;
            case 16: return ConstructorMeta_.F16.class;
            case 17: return ConstructorMeta_.F17.class;
            case 18: return ConstructorMeta_.F18.class;
            case 19: return ConstructorMeta_.F19.class;
            case 20: return ConstructorMeta_.F20.class;
            case 21: return ConstructorMeta_.F21.class;
            case 22: return ConstructorMeta_.F22.class;
        }
        throw new RuntimeException("Not implemented: F" + argCount);
        }
    }

    public List<Generator<? super ExtendedGeneratorOptions>> generators() {
        return newList(cons(ConstructorsAsJpaProjections.instance, super.generators()));
    }

}
