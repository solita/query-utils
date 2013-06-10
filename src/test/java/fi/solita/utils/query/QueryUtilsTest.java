package fi.solita.utils.query;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.metamodel.Attribute;

import org.junit.Test;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Function1;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.Related;
import fi.solita.utils.query.projection.Select;
import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.query.QueryUtils.*;
import static fi.solita.utils.query.QueryUtils_.*;

public class QueryUtilsTest extends QueryTestBase {
    
    static final List<Function1<Attribute<?,?>,Boolean>> bothFunctions = newList(isRequiredByMetamodel, isRequiredByQueryAttribute);

    @Test
    public void selfAttributeAndLiteralAttributesAreAlwaysRequired() throws Exception {
        List<? extends Attribute<?,?>> testset = newList(
                Select.self(),
                Select.literal(42),
                Select.literal(newSet(42)),
                Select.literal(newList(42)));
        
        for (Apply<Attribute<?,?>,Boolean> f: bothFunctions) {
            for (Attribute<?,?> a: testset) {
                assertTrue(f.apply(a));
            }
        }
    }
    
    @Test
    public void mandatoryAttributesAreAlwaysRequired() throws Exception {
        List<? extends Attribute<?,?>> testset = newList(
                Employee_.id,                   // id
                Employee_.mandatoryName,        // value
                Employee_.mandatoryDepartment,  // entity
                Department_.mandatoryReport,    // embeddable
                Municipality_.employees,        // set
                Department_.employees);         // list
        
        for (Apply<Attribute<?,?>,Boolean> f: bothFunctions) {
            for (Attribute<?,?> a: testset) {
                assertTrue(f.apply(a));
            }
        }
    }

    @Test
    public void metamodel_optionalIsNotRequired() {
        assertFalse("value",              isRequiredByMetamodel(Employee_.optionalSalary));
        assertFalse("entity",             isRequiredByMetamodel(Employee_.optionalMunicipality));
        assertFalse("embeddable",         isRequiredByMetamodel(Employee_.optionalReport));
        
        assertFalse("value",              isRequiredByMetamodel(Cast.optional(Employee_.optionalSalary)));
        assertFalse("entity",             isRequiredByMetamodel(Cast.optional(Employee_.optionalMunicipality)));
        assertFalse("embeddable",         isRequiredByMetamodel(Cast.optional(Employee_.optionalReport)));
        
        assertFalse("value_subtype",      isRequiredByMetamodel(Cast.optionalSubtype(Employee_.optionalSalary)));
        assertFalse("entity_subtype",     isRequiredByMetamodel(Cast.optionalSubtype(Employee_.optionalMunicipality)));
        assertFalse("embeddable_subtype", isRequiredByMetamodel(Cast.optionalSubtype(Employee_.optionalReport)));
    }
    
    @Test
    public void metamodel_joiningSingularAttributeIsRequiredExactlyWhenAllAttributesRequired() {
        assertTrue("id",          isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.id)));
        assertTrue("value",       isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryName)));
        assertTrue("entity",      isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference)));
        assertTrue("embeddable",  isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryReport)));
        
        assertFalse("id",         isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.id)));
        assertFalse("value",      isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.mandatoryName)));
        assertFalse("entity",     isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment)));
        assertFalse("embeddable", isRequiredByMetamodel(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport)));
        
        assertFalse("value",      isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.optionalBudget)));
        assertFalse("entity",     isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.optionalMunicipality)));
        assertFalse("embeddable", isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.optionalReport)));
    }
    
    @Test
    public void metamodel_joiningToSetListAttributeIsAlwaysRequired() {
        assertTrue("set",  isRequiredByMetamodel(Related.value(Municipality_.mandatorySelfReference, Municipality_.employees)));
        assertTrue("set",  isRequiredByMetamodel(Related.value(Employee_.optionalMunicipality, Municipality_.employees)));
        
        assertTrue("list", isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.employees)));
        assertTrue("list", isRequiredByMetamodel(Related.value(Employee_.optionalDepartment, Department_.employees)));
    }
    
    @Test
    public void metamodel_joiningFromSetListAttributeIsAlwaysRequired() {
        assertTrue("set",  isRequiredByMetamodel(Related.value(Municipality_.employees, Employee_.mandatoryName)));
        assertTrue("set",  isRequiredByMetamodel(Related.value(Municipality_.employees, Employee_.optionalSalary)));
        
        assertTrue("list", isRequiredByMetamodel(Related.value(Department_.employees, Employee_.mandatoryName)));
        assertTrue("list", isRequiredByMetamodel(Related.value(Department_.employees, Employee_.optionalSalary)));
    }
    
    @Test
    public void metamodel_relationAttributeIsRequiredExactlyWhenAllAttributesRequired() {
        assertTrue("id",          isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.id))));
        assertTrue("value",       isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryName))));
        assertTrue("entity",      isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatorySelfReference))));
        assertTrue("embeddable",  isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryReport))));
        
        assertFalse("id",         isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.id))));
        assertFalse("value",      isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryName))));
        assertFalse("entity",     isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryDepartment))));
        assertFalse("embeddable", isRequiredByMetamodel(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.mandatoryReport))));
        
        assertFalse("value",      isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalBudget)))));
        assertFalse("entity",     isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalMunicipality)))));
        assertFalse("embeddable", isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalReport)))));
        
        assertTrue("set",         isRequiredByMetamodel(Related.projection(Municipality_.mandatorySelfReference, Project.value(Municipality_.employees))));
        assertTrue("list",        isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.employees))));
        
        assertFalse("set",        isRequiredByMetamodel(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.employees))));
        assertFalse("list",       isRequiredByMetamodel(Related.projection(Employee_.optionalDepartment, Project.value(Department_.employees)))); 
    }
    
    @Test
    public void queryAttribute_optionalAttributesAreRequired() {
        assertTrue("value",      isRequiredByQueryAttribute(Employee_.optionalSalary));
        assertTrue("entity",     isRequiredByQueryAttribute(Employee_.optionalMunicipality));
        assertTrue("embeddable", isRequiredByQueryAttribute(Employee_.optionalReport)); 
    }
    
    @Test
    public void queryAttribute_wrappedIsNotRequired() {
        assertFalse("value",              isRequiredByQueryAttribute(Cast.optional(Employee_.optionalSalary)));
        assertFalse("entity",             isRequiredByQueryAttribute(Cast.optional(Employee_.optionalMunicipality)));
        assertFalse("embeddable",         isRequiredByQueryAttribute(Cast.optional(Employee_.optionalReport)));
        
        assertFalse("value_subtype",      isRequiredByQueryAttribute(Cast.optionalSubtype(Employee_.optionalSalary)));
        assertFalse("entity_subtype",     isRequiredByQueryAttribute(Cast.optionalSubtype(Employee_.optionalMunicipality)));
        assertFalse("embeddable_subtype", isRequiredByQueryAttribute(Cast.optionalSubtype(Employee_.optionalReport)));
    }
    
    @Test
    public void queryAttribute_joiningAttributeWithoutWrappedAttributesIsAlwaysRequired() {
        assertTrue("id",          isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.id)));
        assertTrue("value",       isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryName)));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference)));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryReport)));
        
        assertTrue("id",          isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.id)));
        assertTrue("value",       isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.mandatoryName)));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment)));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport)));
        
        assertTrue("value",       isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionalBudget)));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionalMunicipality)));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionalReport)));
        
        assertTrue("set",         isRequiredByQueryAttribute(Related.value(Municipality_.mandatorySelfReference, Municipality_.employees)));
        assertTrue("list",        isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.employees)));
        assertTrue("set",         isRequiredByQueryAttribute(Related.value(Employee_.optionalMunicipality, Municipality_.employees)));
        assertTrue("list",        isRequiredByQueryAttribute(Related.value(Employee_.optionalDepartment, Department_.employees))); 
    }
    
    @Test
    public void queryAttribute_joiningAttributeWrappedIsNotRequired() {
        assertFalse("id",         isRequiredByQueryAttribute(Cast.optional(Related.value(Department_.optionalManager, Employee_.id))));
        assertFalse("value",      isRequiredByQueryAttribute(Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryName))));
        assertFalse("entity",     isRequiredByQueryAttribute(Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport))));
        
        assertFalse("value",      isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalBudget))));
        assertFalse("entity",     isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalMunicipality))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalReport))));
    }
    
    @Test
    public void queryAttribute_relationAttributeWithoutWrappedAttributesIsAlwaysRequired() {
        assertTrue("id",          isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.id))));
        assertTrue("value",       isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryName))));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatorySelfReference))));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryReport))));
        
        assertTrue("id",         isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.id))));
        assertTrue("value",      isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryName))));
        assertTrue("entity",     isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryDepartment))));
        assertTrue("embeddable", isRequiredByQueryAttribute(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.mandatoryReport))));
        
        assertTrue("set",         isRequiredByQueryAttribute(Related.projection(Municipality_.mandatorySelfReference, Project.value(Municipality_.employees))));
        assertTrue("list",        isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.employees))));
        
        assertTrue("set",        isRequiredByQueryAttribute(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.employees))));
        assertTrue("list",       isRequiredByQueryAttribute(Related.projection(Employee_.optionalDepartment, Project.value(Department_.employees)))); 
    }
    
    @Test
    public void queryAttribute_relationAttributeWrappedIsNotRequired() {
        assertFalse("id",         isRequiredByQueryAttribute(Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.id)))));
        assertFalse("value",      isRequiredByQueryAttribute(Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryName)))));
        assertFalse("entity",     isRequiredByQueryAttribute(Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryDepartment)))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Cast.optional(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.mandatoryReport)))));
        
        assertFalse("value",      isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalBudget)))));
        assertFalse("entity",     isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalMunicipality)))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalReport)))));
    }
}
