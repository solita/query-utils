package fi.solita.utils.query.projection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.generation.JpaCriteriaQuery;

public class CastTest extends QueryTestBase {
    
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;

    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projectvalue_needs_optional() {
        Project.value(Employee_.optionalSalary);
    }

    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void Projectvalue_fails_with_optional_for_mandatory_attribute() {
        Project.value(Cast.optional(Employee_.mandatoryName));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projectvalue_embedded_needs_optional() {
        Project.value(Employee_.optionalReport);
    }
    
    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void Projectvalue_embedded_fails_with_optional_for_mandatory_attribute() {
        Project.value(Cast.optional(Municipality_.mandatoryReport));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void Projecttuple_needs_optional() {
        Project.tuple(Employee_.optionalSalary);
    }

    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void Projecttuple_fails_with_optional_for_mandatory_attribute() {
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
}
