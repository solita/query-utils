package fi.solita.utils.query.meta;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.cons;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

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
        return new ExtendedGeneratorOptions() {
            public boolean onlyPublicMembers() {
                return onlyPublicMembers;
            }
        };
    }
    
    public static class ExtendedGeneratorOptions extends CommonMetadataProcessor.CombinedGeneratorOptions implements JpaMetamodel.Options {
    }

    public List<Generator<? super ExtendedGeneratorOptions>> generators() {
        return newList(cons(JpaMetamodel.instance, super.generators()));
    }
}
