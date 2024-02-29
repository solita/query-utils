package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.query.QueryUtils.isRequiredByMetamodel;
import static fi.solita.utils.query.QueryUtils.isRequiredByQueryAttribute;
import static fi.solita.utils.query.QueryUtils_.isRequiredByMetamodel;
import static fi.solita.utils.query.QueryUtils_.isRequiredByQueryAttribute;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import jakarta.persistence.metamodel.Attribute;

import org.junit.Test;

import fi.solita.utils.functional.Apply;
import fi.solita.utils.functional.Predicate;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.Related;
import fi.solita.utils.query.projection.Select;

public class QueryUtilsTest extends QueryTestBase {
    
    static final List<? extends Predicate<Attribute<?,?>>> bothFunctions = newList(isRequiredByMetamodel, isRequiredByQueryAttribute);

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
                Municipality_.emps,        // set
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
        
        assertFalse("option",             isRequiredByMetamodel(Employee_.optionAge));
    }
    
    @Test
    public void metamodel_joiningSingularAttributeIsRequiredExactlyWhenAllAttributesRequired() {
        assertTrue("id",          isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.id)));
        assertTrue("value",       isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryDepName)));
        assertTrue("entity",      isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference)));
        assertTrue("embeddable",  isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryReport)));
        
        assertFalse("id",         isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.id)));
        assertFalse("value",      isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.mandatoryName)));
        assertFalse("entity",     isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment)));
        assertFalse("embeddable", isRequiredByMetamodel(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport)));
        
        assertFalse("value",      isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.optionalBudget)));
        assertFalse("entity",     isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepMunicipality)));
        assertFalse("embeddable", isRequiredByMetamodel(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepReport)));
        
        assertFalse("option",     isRequiredByMetamodel(Related.value(Department_.optionalManager, Employee_.optionAge)));
    }
    
    @Test
    public void metamodel_joiningToSetListAttributeIsRequiredWhenLastIsRequired() {
        assertTrue("set",  isRequiredByMetamodel(Related.set(Municipality_.mandatorySelfReference, Municipality_.emps)));
        assertTrue("set",  isRequiredByMetamodel(Related.set(Employee_.optionalMunicipality, Municipality_.emps)));
        
        assertTrue("list", isRequiredByMetamodel(Related.list(Employee_.mandatoryDepartment, Department_.employees)));
        assertTrue("list", isRequiredByMetamodel(Related.list(Employee_.optionalDepartment, Department_.employees)));
    }
    
    @Test
    public void metamodel_joiningFromSetListAttributeIsAlwaysRequired() {
        assertTrue("set",  isRequiredByMetamodel(Related.set(Municipality_.emps, Employee_.mandatoryName)));
        assertTrue("set",  isRequiredByMetamodel(Related.set(Municipality_.emps, Employee_.optionalSalary)));
        assertTrue("set",  isRequiredByMetamodel(Related.set(Municipality_.emps, Employee_.optionAge)));
        
        assertTrue("list", isRequiredByMetamodel(Related.list(Department_.employees, Employee_.mandatoryName)));
        assertTrue("list", isRequiredByMetamodel(Related.list(Department_.employees, Employee_.optionalSalary)));
        assertTrue("list", isRequiredByMetamodel(Related.list(Department_.employees, Employee_.optionAge)));
    }
    
    @Test
    public void metamodel_relationAttributeIsRequiredExactlyWhenAllAttributesRequired() {
        assertTrue("id",          isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.id))));
        assertTrue("value",       isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryDepName))));
        assertTrue("entity",      isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatorySelfReference))));
        assertTrue("embeddable",  isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryReport))));
        
        assertFalse("id",         isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.id))));
        assertFalse("value",      isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryName))));
        assertFalse("entity",     isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryDepartment))));
        assertFalse("embeddable", isRequiredByMetamodel(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.mandatoryReport))));
        
        assertFalse("value",      isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalBudget)))));
        assertFalse("entity",     isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalDepMunicipality)))));
        assertFalse("embeddable", isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalDepReport)))));
        
        assertFalse("option",     isRequiredByMetamodel(Related.projection(Department_.optionalManager, Project.value(Employee_.optionAge))));
        
        assertTrue("set",         isRequiredByMetamodel(Related.projection(Municipality_.mandatorySelfReference, Project.value(Municipality_.emps))));
        assertTrue("list",        isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.employees))));
        
        assertFalse("set",        isRequiredByMetamodel(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.emps))));
        assertFalse("list",       isRequiredByMetamodel(Related.projection(Employee_.optionalDepartment, Project.value(Department_.employees))));
        
        // Project.value is special, in a sense that it "disappears" and the optionality from underneath propagates upwards.
        // In every other case (pair, tuple, class...) the value is always required by metamodel.
        assertTrue("non-value1",       isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.pair(Cast.optional(Department_.optionalBudget), Cast.optional(Department_.optionalBudget)))));
        assertTrue("non-value2",       isRequiredByMetamodel(Related.projection(Employee_.mandatoryDepartment, Project.pair(Department_.mandatoryDepName, Cast.optional(Department_.optionalBudget)))));
    }
    
    @Test
    public void queryAttribute_optionalAreRequired() {
        assertTrue("value",      isRequiredByQueryAttribute(Employee_.optionalSalary));
        assertTrue("entity",     isRequiredByQueryAttribute(Employee_.optionalMunicipality));
        assertTrue("embeddable", isRequiredByQueryAttribute(Employee_.optionalReport));
    }
    
    @Test
    public void queryAttribute_optionIsNotRequired() {
        assertFalse("option", isRequiredByQueryAttribute(Employee_.optionAge));
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
    public void queryAttribute_joiningToValueIsRequired() {
        assertTrue("id",          isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.id)));
        assertTrue("value",       isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryDepName)));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.mandatorySelfReference)));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.mandatoryReport)));
        
        assertTrue("id",          isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.id)));
        assertTrue("value",       isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.mandatoryName)));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment)));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport)));
        
        assertTrue("value",       isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionalBudget)));
        assertTrue("entity",      isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepMunicipality)));
        assertTrue("embeddable",  isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepReport)));
    }
    
    @Test
    public void queryAttribute_joiningToCollectionIsRequired() {
        assertTrue("set",         isRequiredByQueryAttribute(Related.set(Municipality_.mandatorySelfReference, Municipality_.emps)));
        assertTrue("list",        isRequiredByQueryAttribute(Related.list(Employee_.mandatoryDepartment, Department_.employees)));
        assertTrue("set",         isRequiredByQueryAttribute(Related.set(Employee_.optionalMunicipality, Municipality_.emps)));
        assertTrue("list",        isRequiredByQueryAttribute(Related.list(Employee_.optionalDepartment, Department_.employees))); 
    }
    
    @Test
    public void queryAttribute_joiningWrappedIsNotRequired() {
        assertFalse("id",         isRequiredByQueryAttribute(Cast.optional(Related.value(Department_.optionalManager, Employee_.id))));
        assertFalse("value",      isRequiredByQueryAttribute(Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryName))));
        assertFalse("entity",     isRequiredByQueryAttribute(Cast.optional(Related.value(Department_.optionalManager, Employee_.mandatoryDepartment))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.optionalMunicipality, Municipality_.mandatoryReport))));
        
        assertFalse("value",      isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalBudget))));
        assertFalse("entity",     isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepMunicipality))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Cast.optional(Related.value(Employee_.mandatoryDepartment, Department_.optionalDepReport))));
    }
    
    @Test
    public void queryAttribute_joiningOptionIsNotRequired() {
        assertFalse("option", isRequiredByQueryAttribute(Related.value(Department_.optionalManager, Employee_.optionAge)));
        assertFalse("option", isRequiredByQueryAttribute(Related.value(Employee_.mandatoryDepartment, Department_.optionSize)));
    }
    
    @Test
    public void queryAttribute_relationToValueIsRequired() {
        assertTrue("id",         isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.id))));
        assertTrue("value",      isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryDepName))));
        assertTrue("entity",     isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatorySelfReference))));
        assertTrue("embeddable", isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryReport))));
        
        assertTrue("id",         isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.id))));
        assertTrue("value",      isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryName))));
        assertTrue("entity",     isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryDepartment))));
        assertTrue("embeddable", isRequiredByQueryAttribute(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.mandatoryReport))));
    }
    
    @Test
    public void queryAttribute_relationToCollectionIsRequired() {
        assertTrue("set",        isRequiredByQueryAttribute(Related.projection(Municipality_.mandatorySelfReference, Project.value(Municipality_.emps))));
        assertTrue("list",       isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.employees))));
        
        assertTrue("set",        isRequiredByQueryAttribute(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.emps))));
        assertTrue("list",       isRequiredByQueryAttribute(Related.projection(Employee_.optionalDepartment, Project.value(Department_.employees)))); 
    }
    
    @Test
    public void queryAttribute_relationWrappedIsNotRequired() {
        assertFalse("id",         isRequiredByQueryAttribute(Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.id)))));
        assertFalse("value",      isRequiredByQueryAttribute(Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryName)))));
        assertFalse("entity",     isRequiredByQueryAttribute(Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.mandatoryDepartment)))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Cast.optional(Related.projection(Employee_.optionalMunicipality, Project.value(Municipality_.mandatoryReport)))));
        
        assertFalse("value",      isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalBudget)))));
        assertFalse("entity",     isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalDepMunicipality)))));
        assertFalse("embeddable", isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Cast.optional(Department_.optionalDepReport)))));
    }
    
    @Test
    public void queryAttribute_relationOptionIsNotRequired() {
        assertFalse("option", isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.value(Employee_.optionAge))));
        assertFalse("option", isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.optionSize))));
    }
    
    @Test
    public void queryAttribute_relationOptionWithinProjectionIsRequired() {
        assertTrue("option", isRequiredByQueryAttribute(Related.projection(Department_.optionalManager, Project.pair(Employee_.mandatoryName, Employee_.optionAge))));
        assertTrue("option", isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Project.pair(Department_.mandatoryDepName, Department_.optionSize))));
        assertTrue("option", isRequiredByQueryAttribute(Related.projection(Employee_.mandatoryDepartment, Dto_.$$$(Select.literal(Dto.VALUE.a), Department_.optionSize))));
    }
}
