package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.query.Dao;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;


public class JpaCriteriaQueriesTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private Dao dao;
    
    @Autowired
    private Restrict restrict;

    @Test
    public void single() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(dep1.getId(), dao.get(query.single(dep1.getId())).getId());
        assertEquals(dep2.getId(), dao.get(query.single(dep2.getId())).getId());
    }



    @Test
    public void all() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, dao.getMany(query.all(Department.class)))));
    }

    @Test
    public void ofIds() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals("find by single id", dep1.getId(), dao.get(query.ofIds(newSet(dep1.getId()), Department.class)).getId());
        assertEquals("find by multiple ids", newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, dao.getMany(query.ofIds(newSet(dep1.getId(), dep2.getId()), Department.class)))));
    }

    @Test(expected = NoResultException.class)
    public void ofIds_empty() {
        dao.get(query.ofIds(Collections.<Id<Department>>emptySet(), Department.class));
    }

    @Test
    public void cast() {
        assertEquals(Department_.employees, Cast.cast(Department_.employees));
        assertEquals(Employee_.mandatoryDepartment, Cast.cast(Employee_.mandatoryDepartment));
    }
    
    @Test
    public void castSuper() {
        assertEquals(Department_.employees, Cast.castSuper(Department_.employees));
        assertEquals(Employee_.mandatoryDepartment, Cast.castSuper(Employee_.mandatoryDepartment));
    }

    @Test
    public void related() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);

        assertEquals("relation toOne", dep.getId(), dao.get(query.related(emp, Employee_.mandatoryDepartment)).getId());
        assertEquals("relation toMany", emp.getId(), dao.get(query.related(dep, Department_.employees)).getId());
        assertEquals("relation toMany toOne", mun.getId(), dao.get(query.related(dep, Department_.employees, Employee_.optionalMunicipality)).getId());
    }
    
    @Test
    public void related_variants() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);

        assertEquals(mun.getId(), dao.get(
            query.related(dep,
                Department_.employees,
                Employee_.optionalMunicipality)).getId());

        assertEquals(emp.getId(), dao.get(
            query.related(dep,
                Department_.employees)).getId());
    }

    @Test
    public void related_query() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("", dep, mun);
        persist(dep, mun, emp);

        assertEquals(mun.getId(), dao.get(
                query.related(
                        Department_.employees,
                    Employee_.optionalMunicipality,
                    query.single(dep.getId()))).getId());

        assertEquals(emp.getId(), dao.get(
            query.related(
                    Department_.employees,
                    query.single(dep.getId()))).getId());
    }
    
    @Test
    public void related_query_restrictions() throws Exception {
        Department dep = new Department("dep");
        Municipality mun = new Municipality("mun");
        Municipality mun2 = new Municipality("mun2");
        Employee emp = new Employee("emp", dep, mun);
        Employee emp2 = new Employee("emp2", dep, mun2);
        Employee emp3 = new Employee("emp", dep);
        
        Department dep2 = new Department("dep");
        Department dep3 = new Department("dep");
        Employee emp4 = new Employee("emp4", dep3);
        persist(dep, mun, mun2, emp, emp2, emp3, dep2, dep3, emp4);
        
        CriteriaQuery<Municipality> queryWithoutRestrictions = query.related(Employee_.optionalMunicipality,
            query.related(Department_.employees, 
                query.all(Department.class)));
        
        assertEquals(newSet(mun.getId(), mun2.getId()), newSet(map(Municipality_.getId, dao.getMany(queryWithoutRestrictions))));
        
        CriteriaQuery<Municipality> queryWithRestrictions =
            restrict.equals(Municipality_.optionalArea, Some("mun"),                // drop emp2 since its municipality has wrong area
                query.related(Employee_.optionalMunicipality,                       // drop emp3 since it has no municipality
                    restrict.equals(Employee_.mandatoryName, Some("emp"),           // drop dep3 since its employee has wrong name
                        query.related(Department_.employees,                        // drop (by inner join) dep2 since it has no employees
                            restrict.equals(Department_.mandatoryDepName, Some("dep"),
                                query.all(Department.class))))));                   // start with both departments...
        
        assertEquals(mun.getId(), dao.get(queryWithRestrictions).getId());
    }
}
