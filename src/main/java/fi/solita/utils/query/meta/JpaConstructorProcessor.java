package fi.solita.utils.query.meta;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;

import java.util.List;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.meta.CommonMetadataProcessor;
import fi.solita.utils.meta.generators.Generator;
import fi.solita.utils.query.meta.generators.ConstructorsAsJpaProjections;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
@SupportedOptions({"JpaConstructorProcessor." + CommonMetadataProcessor.Options.enabled,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.generatedClassNamePattern,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.generatedPackagePattern,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.includesRegex,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.excludesRegex,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.onlyPublicMembers,
                   "JpaConstructorProcessor." + CommonMetadataProcessor.Options.includePrivateMembers,
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
        final boolean includePrivateMembers = JpaConstructorProcessor.this.includePrivateMembers();
        final String generatedPackagePattern = JpaConstructorProcessor.this.generatedPackagePattern();
        final String generatedClassNamePattern = JpaConstructorProcessor.this.generatedClassNamePattern();
        final boolean methodsAsFunctionsEnabled = methodsAsFunctionsEnabled();
        final boolean constructorsAsFunctionsEnabled = constructorsAsFunctionsEnabled();
        final boolean instanceFieldsAsEnumEnabled = instanceFieldsAsEnumEnabled();
        final boolean instanceFieldsAsFunctionsEnabled = instanceFieldsAsFunctionsEnabled();
        final boolean instanceFieldsAsTupleEnabled = instanceFieldsAsTupleEnabled();
        final String includesAnnotation = JpaConstructorProcessor.this.includesAnnotation();
        return new ExtendedGeneratorOptions() {
            @Override
            public boolean makeFieldsPublic() {
                return makeFieldsPublic;
            }
            public boolean onlyPublicMembers() {
                return onlyPublicMembers;
            }
            @Override
            public boolean includePrivateMembers() {
                return includePrivateMembers;
            }
            @Override
            public String generatedPackagePattern() {
                return generatedPackagePattern;
            }
            @Override
            public String generatedClassNamePattern() {
                return generatedClassNamePattern;
            }
            @Override
            public boolean methodsAsFunctionsEnabled() {
                return methodsAsFunctionsEnabled;
            }
            @Override
            public boolean constructorsAsFunctionsEnabled() {
                return constructorsAsFunctionsEnabled;
            }
            @Override
            public boolean instanceFieldsAsEnumEnabled() {
                return instanceFieldsAsEnumEnabled;
            }
            @Override
            public boolean instanceFieldsAsFunctionsEnabled() {
                return instanceFieldsAsFunctionsEnabled;
            }
            @Override
            public boolean instanceFieldsAsTupleEnabled() {
                return instanceFieldsAsTupleEnabled;
            }
            @Override
            public String includesAnnotation() {
                return includesAnnotation;
            }
        };
    }
    
    public static abstract class ExtendedGeneratorOptions extends CommonMetadataProcessor.CombinedGeneratorOptions implements ConstructorsAsJpaProjections.Options {
        @SuppressWarnings("rawtypes")
        @Override
        public Class<? extends Apply> getClassForJpaConstructors(int argCount) {
            switch (argCount) {
            case 0: return MetaJpaConstructor.C0.class;
            case 1: return MetaJpaConstructor.C1.class;
            case 2: return MetaJpaConstructor.C2.class;
            case 3: return MetaJpaConstructor.C3.class;
            case 4: return MetaJpaConstructor.C4.class;
            case 5: return MetaJpaConstructor.C5.class;
            case 6: return MetaJpaConstructor.C6.class;
            case 7: return MetaJpaConstructor.C7.class;
            case 8: return MetaJpaConstructor.C8.class;
            case 9: return MetaJpaConstructor.C9.class;
            case 10: return MetaJpaConstructor.C10.class;
            case 11: return MetaJpaConstructor.C11.class;
            case 12: return MetaJpaConstructor.C12.class;
            case 13: return MetaJpaConstructor.C13.class;
            case 14: return MetaJpaConstructor.C14.class;
            case 15: return MetaJpaConstructor.C15.class;
            case 16: return MetaJpaConstructor.C16.class;
            case 17: return MetaJpaConstructor.C17.class;
            case 18: return MetaJpaConstructor.C18.class;
            case 19: return MetaJpaConstructor.C19.class;
            case 20: return MetaJpaConstructor.C20.class;
            case 21: return MetaJpaConstructor.C21.class;
            case 22: return MetaJpaConstructor.C22.class;
            case 23: return MetaJpaConstructor.C23.class;
            case 24: return MetaJpaConstructor.C24.class;
            case 25: return MetaJpaConstructor.C25.class;
            case 26: return MetaJpaConstructor.C26.class;
            case 27: return MetaJpaConstructor.C27.class;
            case 28: return MetaJpaConstructor.C28.class;
            case 29: return MetaJpaConstructor.C29.class;
            case 30: return MetaJpaConstructor.C30.class;
            case 31: return MetaJpaConstructor.C31.class;
            case 32: return MetaJpaConstructor.C32.class;
            case 33: return MetaJpaConstructor.C33.class;
            case 34: return MetaJpaConstructor.C34.class;
            case 35: return MetaJpaConstructor.C35.class;
            case 36: return MetaJpaConstructor.C36.class;
            case 37: return MetaJpaConstructor.C37.class;
            case 38: return MetaJpaConstructor.C38.class;
            case 39: return MetaJpaConstructor.C39.class;
        }
        throw new RuntimeException("Not implemented: F" + argCount);
        }
    }

    public List<Generator<? super ExtendedGeneratorOptions>> generators() {
        return newList(cons(ConstructorsAsJpaProjections.instance, super.generators()));
    }

}
