package fi.solita.utils.query.projection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality;
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
    public void get_fails_without_manual_option_projection() {
        dao.get(query.all(Employee.class), Project.value(Employee_.salary));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void getMany_fails_without_manual_option_projection() {
        dao.getMany(query.all(Employee.class), Project.value(Employee_.salary));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void find_fails_without_manual_option_projection() {
        dao.find(query.all(Employee.class), Project.value(Employee_.salary));
    }

    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void get_fails_with_manual_option_projection_for_mandatory_attribute() {
        dao.get(query.all(Employee.class), Project.value(Cast.optional(Employee_.name)));
    }
    
    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void getMany_fails_with_manual_option_projection_for_mandatory_attribute() {
        dao.getMany(query.all(Employee.class), Project.value(Cast.optional(Employee_.name)));
    }
    
    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void find_fails_with_manual_option_projection_for_mandatory_attribute() {
        dao.find(query.all(Employee.class), Project.value(Cast.optional(Employee_.name)));
    }
    
    @Test(expected = QueryUtils.OptionalAttributeNeedOptionTypeException.class)
    public void embeddable_fails_without_manual_option_projection() {
        dao.get(query.all(Employee.class), Project.value(Employee_.report));
    }
    
    @Test(expected = QueryUtils.RequiredAttributeMustNotHaveOptionTypeException.class)
    public void embeddable_fails_with_manual_option_projection_for_mandatory_attribute() {
        dao.get(query.all(Municipality.class), Project.value(Cast.optional(Municipality_.report)));
    }
}
