package fi.solita.utils.query.projection;

import org.junit.Test;

import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.generation.Cast;

public class CastTest extends QueryTestBase {
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projectvalue_needs_optional() {
        Project.value(Employee_.optionalSalary);
    }

    public void Projectvalue_succeeds_with_optional_for_mandatory_attribute() {
        Project.value(Cast.optional(Employee_.mandatoryName));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projectvalue_embedded_needs_optional() {
        Project.value(Employee_.optionalReport);
    }
    
    public void Projectvalue_embedded_succeeds_with_optional_for_mandatory_attribute() {
        Project.value(Cast.optional(Municipality_.mandatoryReport));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projecttuple_needs_optional() {
        Project.tuple(Employee_.optionalSalary);
    }

    public void Projecttuple_succeeds_with_optional_for_mandatory_attribute() {
        Project.tuple(Cast.optional(Employee_.mandatoryName));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapLiteralAttribute() {
         Cast.optional(Select.literal(42));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapLiteralAttribute_subtype() {
         Cast.optionalSubtype(Select.literal(42));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapSelfAttribute() {
         Cast.optional(Select.self());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapSelfAttribute_subtype() {
         Cast.optionalSubtype(Select.self());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapOption() {
         Cast.optional(Employee_.optionAge);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapOption_subtype() {
         Cast.optionalSubtype(Employee_.optionAge);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapWrappedOption() {
         Cast.optional(Related.value(Department_.optionalManager, Employee_.optionAge));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void cannotWrapProjectedOption() {
         Cast.optional(Related.projection(Department_.optionalManager, Project.value(Employee_.optionAge)));
    }
    
    public void canWrapProjectedOptionIfInsideAnotherObject() {
         Cast.optional(Related.projection(Department_.optionalManager, Project.pair(Employee_.optionAge, Employee_.mandatoryName)));
    }
}
