package fi.solita.utils.query.meta;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.mkString;
import static fi.solita.utils.functional.Functional.remove;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;

import fi.solita.utils.meta.CommonMetadataProcessor;
import fi.solita.utils.meta.generators.Generator;
import fi.solita.utils.meta.generators.InstanceFieldsAsTuple;
import fi.solita.utils.query.meta.JpaMetamodelProcessor.ExtendedGeneratorOptions;
import fi.solita.utils.query.meta.generators.JpaMetamodel;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
@SupportedOptions({"JpaMetamodelProcessor." + CommonMetadataProcessor.Options.enabled,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.generatedClassNamePattern,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.generatedPackagePattern,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.includesRegex,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.excludesRegex,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.onlyPublicMembers,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.includePrivateMembers,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.makeFieldsPublic,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.includesAnnotation,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.excludesAnnotation,
                   "JpaMetamodelProcessor." + JpaMetamodelProcessor.Options.extendClassNamePattern})
public class JpaMetamodelProcessor extends CommonMetadataProcessor<ExtendedGeneratorOptions> {
    
    public static class Options {
        public static final String extendClassNamePattern = "extendClassNamePattern";
    }
    
    public Pattern extendClassNamePattern() { return Pattern.compile(findOption(Options.extendClassNamePattern, "<>")); }
    
    @Override
    public ExtendedGeneratorOptions generatorOptions() {
        final boolean onlyPublicMembers = JpaMetamodelProcessor.this.onlyPublicMembers();
        final boolean includePrivateMembers = JpaMetamodelProcessor.this.includePrivateMembers();
        final boolean makeFieldsPublic = JpaMetamodelProcessor.this.makeFieldsPublic();
        final String generatedPackagePattern = JpaMetamodelProcessor.this.generatedPackagePattern();
        final String generatedClassNamePattern = JpaMetamodelProcessor.this.generatedClassNamePattern();
        final boolean methodsAsFunctionsEnabled = methodsAsFunctionsEnabled();
        final boolean constructorsAsFunctionsEnabled = constructorsAsFunctionsEnabled();
        final boolean instanceFieldsAsEnumEnabled = instanceFieldsAsEnumEnabled();
        final boolean instanceFieldsAsFunctionsEnabled = instanceFieldsAsFunctionsEnabled();
        final boolean instanceFieldsAsTupleEnabled = instanceFieldsAsTupleEnabled();
        return new ExtendedGeneratorOptions() {
            public boolean onlyPublicMembers() {
                return onlyPublicMembers;
            }
            
            @Override
            public boolean includePrivateMembers() {
                return includePrivateMembers;
            }
            
            @Override
            public boolean makeFieldsPublic() {
                return makeFieldsPublic;
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
        };
    }
    
    @Override
    public boolean onlyPublicMembers() {
        return Boolean.parseBoolean(findOption(CommonMetadataProcessor.Options.onlyPublicMembers, "true"));
    }
    
    @Override
    public String includesAnnotation() {
        return findOption(CommonMetadataProcessor.Options.includesAnnotation, mkString(",", newList(Entity.class.getName(), MappedSuperclass.class.getName(), Embeddable.class.getName())));
    }
    
    @Override
    public String excludesAnnotation() {
        return findOption(CommonMetadataProcessor.Options.excludesAnnotation, "");
    }
    
    public static abstract class ExtendedGeneratorOptions extends CommonMetadataProcessor.CombinedGeneratorOptions implements JpaMetamodel.Options {
    }

    public List<Generator<? super ExtendedGeneratorOptions>> generators() {
        return newList(remove(InstanceFieldsAsTuple.instance, // doesn't work with inheritance in java8
                       cons(JpaMetamodel.instance, super.generators())));
    }
}
