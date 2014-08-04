package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Functional.head;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.execution.JpaProjectionQueries;
import fi.solita.utils.query.generation.JpaCriteriaQuery;
import fi.solita.utils.query.generation.Restrict;

public class RestrictTest extends QueryTestBase {
    
    @Autowired
    private JpaCriteriaQuery query;

    @Autowired
    private JpaProjectionQueries dao;

    @Test
    public void self() {
        Department dep = new Department();
        Employee emp = new Employee("emp", dep);
        Department emptyDep = new Department();
        persist(dep, emp, emptyDep);
        long queryCount = getQueryCount();
        
        Department department = dao.get(query.all(Department.class), Project.value(Restrict.innerJoin(Select.<Department>self(), Department_.employees)));
        assertEquals(dep.getId(), department.getId());
        
        assertEquals(1, getQueryCount() - queryCount);
    }
    
    @Test
    public void basic() {
        Department dep = new Department();
        Municipality mun = new Municipality();
        Employee emp = new Employee("emp", dep, mun);
        Employee otherEmp = new Employee("emp2", dep);
        persist(dep, mun, emp, otherEmp);
        long queryCount = getQueryCount();
        
        List<Employee> employeesHavingMunicipality = dao.get(query.all(Department.class), Project.value(Restrict.innerJoin(Department_.employees, Employee_.optionalMunicipality)));
        assertEquals(1, employeesHavingMunicipality.size());
        assertEquals(emp.getId(), head(employeesHavingMunicipality).getId());
        
        assertEquals(2, getQueryCount() - queryCount);
    }
    
    @Test
    public void innerJoiningProducingMultipleRowsDoesNotAffectResult() {
        Municipality mun = new Municipality();
        Department dep = new Department(mun);
        Employee emp = new Employee("emp", dep);
        Employee emp2 = new Employee("emp2", dep);
        Department otherDep = new Department();
        persist(mun, dep, emp, emp2, otherDep);
        long queryCount = getQueryCount();
        
        Set<Department> deps = dao.get(query.all(Municipality.class), Project.value(Restrict.innerJoin(Municipality_.deps, Department_.employees)));
        assertEquals(1, deps.size());
        assertEquals(dep.getId(), head(deps).getId());
        
        assertEquals(2, getQueryCount() - queryCount);
    }
}
