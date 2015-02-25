package fi.solita.utils.query.meta;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;
import static fi.solita.utils.functional.Functional.mkString;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import fi.solita.utils.meta.CommonMetadataProcessor;
import fi.solita.utils.meta.generators.Generator;
import fi.solita.utils.query.meta.JpaMetamodelProcessor.ExtendedGeneratorOptions;
import fi.solita.utils.query.meta.generators.JpaMetamodel;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"JpaMetamodelProcessor." + CommonMetadataProcessor.Options.enabled,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.generatedClassNamePattern,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.generatedPackagePattern,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.includesRegex,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.excludesRegex,
                   "JpaMetamodelProcessor." + CommonMetadataProcessor.Options.onlyPublicMembers,
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
        final String generatedPackagePattern = JpaMetamodelProcessor.this.generatedPackagePattern();
        final String generatedClassNamePattern = JpaMetamodelProcessor.this.generatedClassNamePattern();
        return new ExtendedGeneratorOptions() {
            public boolean onlyPublicMembers() {
                return onlyPublicMembers;
            }

            @Override
            public String generatedPackagePattern() {
                return generatedPackagePattern;
            }

            @Override
            public String generatedClassNamePattern() {
                return generatedClassNamePattern;
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
        return newList(cons(JpaMetamodel.instance, super.generators()));
    }
}
