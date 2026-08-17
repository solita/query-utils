package fi.solita.utils.query.generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.PartTimeEmployee_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.hibernate.HibernateTypeProvider;
import fi.solita.utils.query.projection.Project;
import fi.solita.utils.query.projection.Related;
import fi.solita.utils.query.projection.Select;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

public class CastTest extends QueryTestBase {
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projectvalue_needs_optional() {
        Project.value(Employee_.optionalSalary);
    }

    @Test
    public void Projectvalue_succeeds_with_optional_for_mandatory_attribute() {
        Project.value(Cast.optional(Employee_.mandatoryName));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projectvalue_embedded_needs_optional() {
        Project.value(Employee_.optionalReport);
    }
    
    @Test
    public void Projectvalue_embedded_succeeds_with_optional_for_mandatory_attribute() {
        Project.value(Cast.optional(Municipality_.mandatoryReport));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projecttuple_needs_optional() {
        Project.tuple(Employee_.optionalSalary, Employee_.optionalSalary);
    }

    @Test
    public void Projecttuple_succeeds_with_optional_for_mandatory_attribute() {
        Project.tuple(Cast.optional(Employee_.mandatoryName), Cast.optional(Employee_.mandatoryName));
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
   
    @Test
    public void canWrapProjectedOptionalIfInsideAnotherObject() {
         Cast.optional(Related.projection(Department_.optionalManager, Project.pair(Employee_.optionAge, Employee_.mandatoryName)));
    }
    
    @Test
    public void optional_Type_creates_OptionalType() {
        Type<String> stringType = HibernateTypeProvider.HibernateType.javaType(String.class);
        Type.OptionalType<String> optionalType = Cast.optional(stringType);
        assertNotNull(optionalType);
        assertEquals(stringType, optionalType.type);
    }
    
    @Test
    public void castSuper_SingularAttribute_works() {
        SingularAttribute<Employee, String> castedAttribute = Cast.castSuper(PartTimeEmployee_.mandatoryName);
        assertNotNull(castedAttribute);
        assertEquals("mandatoryName", castedAttribute.getName());
    }
    
    @Test
    public void castSuper_ListAttribute_works() {
        ListAttribute<Department, Integer> castedAttribute = Cast.castSuper(Department_.numbers);
        assertNotNull(castedAttribute);
        assertEquals("numbers", castedAttribute.getName());
    }
    
    @Test
    public void cast_SingularAttribute_works() {
        // Cast from Employee to PartTimeEmployee (child entity)
        // This simulates casting a parent's attribute to be used with child
        SingularAttribute<Employee, String> parentAttribute = Employee_.mandatoryName;
        SingularAttribute castedAttribute = Cast.cast(parentAttribute);
        assertNotNull(castedAttribute);
    }
}
