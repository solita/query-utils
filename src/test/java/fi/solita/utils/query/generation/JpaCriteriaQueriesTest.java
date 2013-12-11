package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.*;

import javax.persistence.NoResultException;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.*;
import fi.solita.utils.functional.Collections;
import fi.solita.utils.query.execution.JpaCriteriaQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;


public class JpaCriteriaQueriesTest extends QueryTestBase {

    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaCriteriaQueries dao;

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

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(dao.getMany(query.all(Department.class)), Department_.getId)));
    }

    @Test
    public void ofIds() {
        Department dep1 = new Department();
        Department dep2 = new Department();
        persist(dep1, dep2);

        assertEquals("find by single id", dep1.getId(), dao.get(query.ofIds(newList(dep1.getId()), Department.class)).getId());
        assertEquals("find by multiple ids", newSet(dep1.getId(), dep2.getId()), newSet(map(dao.getMany(query.ofIds(newList(dep1.getId(), dep2.getId()), Department.class)), Department_.getId)));
    }

    @Test(expected = NoResultException.class)
    public void ofIds_empty() {
        dao.get(query.ofIds(Collections.<Id<Department>>emptyList(), Department.class));
    }

    @Test
    public void cast() {
        assertEquals(Department_.employees, Cast.cast(Department_.employees));
        assertEquals(Employee_.mandatoryDepartment, Cast.cast(Employee_.mandatoryDepartment));
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
    public void relatedEmbeddable() {
        Department dep = new Department();
        Employee emp = new Employee("", dep, new Report(42));
        persist(dep, emp);

        assertEquals((Integer)42, dao.get(query.related(emp, Employee_.optionalReport)).getYear());
    }
    
    @Test
    @Ignore("Hibernate (4.0.0) generated an invalid query")
    public void relatedEmbeddableSet() {
        Municipality mun = new Municipality(newSet(new Report(42)), false);
        persist(mun);

        assertEquals((Integer)42, dao.get(query.related(mun, Municipality_.reports)).getYear());
    }
    
    @Test
    @Ignore("Hibernate (4.0.0) generated an invalid query")
    public void relatedEmbeddableList() {
        Department dep = new Department("", newList(new Report(42)));
        persist(dep);

        assertEquals((Integer)42, dao.get(query.related(dep, Department_.reports)).getYear());
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
}
