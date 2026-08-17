package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Collections.newSet;
import static fi.solita.utils.functional.Functional.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Page;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.generation.NativeQuery;
import fi.solita.utils.query.generation.QLQuery;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class HibernateQueryExecutorTest extends QueryTestBase {

    @Autowired
    private HibernateQueryExecutor executor;

    @Autowired
    private TypeProvider typeProvider;

    // -------------------------------------------------------------------------
    // get(CriteriaQuery, LockModeType)
    // -------------------------------------------------------------------------

    @Test
    public void get_criteriaQuery_returnsSingleResult() {
        Department dep = new Department("getCriteria");
        persist(dep);

        CriteriaQuery<Department> q = allDepartments();
        Department result = executor.get(q, LockModeType.NONE);

        assertNotNull(result);
        assertEquals(dep.getId(), result.getId());
    }

    @Test(expected = NoResultException.class)
    public void get_criteriaQuery_throwsWhenEmpty() {
        executor.get(allDepartments(), LockModeType.NONE);
    }

    @Test
    public void get_criteriaQuery_throwsWhenMultipleResults() {
        persist(new Department("a"), new Department("b"));
        try {
            executor.get(allDepartments(), LockModeType.NONE);
            fail("Expected exception for non-unique result");
        } catch (RuntimeException e) {
            // ok
        }
    }

    // -------------------------------------------------------------------------
    // getMany(CriteriaQuery, Page, LockModeType)
    // -------------------------------------------------------------------------

    @Test
    public void getMany_criteriaQuery_returnsAllResults() {
        Department dep1 = new Department("x");
        Department dep2 = new Department("y");
        persist(dep1, dep2);

        List<Department> results = executor.getMany(allDepartmentsOrdered(), Page.NoPaging, LockModeType.NONE);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, results)));
    }

    @Test
    public void getMany_criteriaQuery_returnsEmptyListWhenNoData() {
        List<Department> results = executor.getMany(allDepartments(), Page.NoPaging, LockModeType.NONE);
        assertTrue(results.isEmpty());
    }

    @Test
    public void getMany_criteriaQuery_withPaging_firstPage() {
        persist(new Department("a"), new Department("b"), new Department("c"));

        List<Department> results = executor.getMany(allDepartmentsOrdered(), Page.FIRST.withSize(2), LockModeType.NONE);

        assertEquals(2, results.size());
    }

    @Test
    public void getMany_criteriaQuery_withPaging_secondPage() {
        persist(new Department("a"), new Department("b"), new Department("c"));

        List<Department> page2 = executor.getMany(allDepartmentsOrdered(), Page.FIRST.withSize(2).nextPage(), LockModeType.NONE);

        assertEquals(1, page2.size());
    }

    // -------------------------------------------------------------------------
    // execute(NativeQuery<Void>)
    // -------------------------------------------------------------------------

    @Test
    public void execute_nativeQuery_returnsAffectedRowCount() {
        persist(new Department("original"));
        em.flush();

        int affected = executor.execute(NativeQuery.of("UPDATE Department SET mandatoryDepName = 'updated'"));

        assertEquals(1, affected);
    }

    @Test
    public void execute_nativeQuery_returnsZeroWhenNoRowsAffected() {
        int affected = executor.execute(NativeQuery.of("UPDATE Department SET mandatoryDepName = 'x'"));
        assertEquals(0, affected);
    }

    @Test
    public void execute_nativeQuery_multipleRows() {
        persist(new Department("a"), new Department("b"));
        em.flush();

        int affected = executor.execute(NativeQuery.of("UPDATE Department SET mandatoryDepName = 'same'"));

        assertEquals(2, affected);
    }

    // -------------------------------------------------------------------------
    // find(NativeQuery)
    // -------------------------------------------------------------------------

    @Test
    public void find_nativeQuery_returnsSomeWhenSingleResult() {
        Department dep = new Department("findNative");
        persist(dep);

        Optional<Department> result = executor.find(
            NativeQuery.of("SELECT * FROM Department").returns(typeProvider.type(Department.class)));

        assertTrue(result.isPresent());
        assertEquals(dep.getId(), result.get().getId());
    }

    @Test
    public void find_nativeQuery_returnsNoneWhenEmpty() {
        Optional<Department> result = executor.find(
            NativeQuery.of("SELECT * FROM Department").returns(typeProvider.type(Department.class)));

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void find_nativeQuery_throwsWhenMultipleResults() {
        persist(new Department("a"), new Department("b"));
        try {
            executor.find(
                NativeQuery.of("SELECT * FROM Department").returns(typeProvider.type(Department.class)));
            fail("Expected exception for non-unique result");
        } catch (RuntimeException e) {
            // ok
        }
    }

    // -------------------------------------------------------------------------
    // getMany(NativeQuery, Page)
    // -------------------------------------------------------------------------

    @Test
    public void getMany_nativeQuery_returnsAllResults() {
        Department dep1 = new Department("n1");
        Department dep2 = new Department("n2");
        persist(dep1, dep2);

        List<Department> results = executor.getMany(
            NativeQuery.of("SELECT * FROM Department").returns(typeProvider.type(Department.class)),
            Page.NoPaging);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, results)));
    }

    @Test
    public void getMany_nativeQuery_returnsEmptyListWhenNoData() {
        List<Department> results = executor.getMany(
            NativeQuery.of("SELECT * FROM Department").returns(typeProvider.type(Department.class)),
            Page.NoPaging);

        assertTrue(results.isEmpty());
    }

    @Test
    public void getMany_nativeQuery_withPaging() {
        persist(new Department("a"), new Department("b"), new Department("c"));

        List<Department> firstPage = executor.getMany(
            NativeQuery.of("SELECT * FROM Department ORDER BY mandatoryDepName").returns(typeProvider.type(Department.class)),
            Page.FIRST.withSize(2));

        assertEquals(2, firstPage.size());
        assertEquals("a", firstPage.get(0).getMandatoryName());
    }

    @Test
    public void getMany_nativeQuery_withPaging_secondPage() {
        persist(new Department("a"), new Department("b"), new Department("c"));

        List<Department> secondPage = executor.getMany(
            NativeQuery.of("SELECT * FROM Department ORDER BY mandatoryDepName").returns(typeProvider.type(Department.class)),
            Page.FIRST.withSize(2).nextPage());

        assertEquals(1, secondPage.size());
        assertEquals("c", secondPage.get(0).getMandatoryName());
    }

    // -------------------------------------------------------------------------
    // find(QLQuery)
    // -------------------------------------------------------------------------

    @Test
    public void find_qlQuery_returnsSomeWhenSingleResult() {
        Department dep = new Department("findQL");
        persist(dep);

        Optional<Department> result = executor.find(QLQuery.<Department>of("from Department"));

        assertTrue(result.isPresent());
        assertEquals(dep.getId(), result.get().getId());
    }

    @Test
    public void find_qlQuery_returnsNoneWhenEmpty() {
        Optional<Department> result = executor.find(QLQuery.<Department>of("from Department"));
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void find_qlQuery_throwsWhenMultipleResults() {
        persist(new Department("a"), new Department("b"));
        try {
            executor.find(QLQuery.<Department>of("from Department"));
            fail("Expected exception for non-unique result");
        } catch (RuntimeException e) {
            // ok
        }
    }

    // -------------------------------------------------------------------------
    // getMany(QLQuery, Page)
    // -------------------------------------------------------------------------

    @Test
    public void getMany_qlQuery_returnsAllResults() {
        Department dep1 = new Department("q1");
        Department dep2 = new Department("q2");
        persist(dep1, dep2);

        List<Department> results = executor.getMany(
            QLQuery.<Department>of("from Department"), Page.NoPaging);

        assertEquals(newSet(dep1.getId(), dep2.getId()), newSet(map(Department_.getId, results)));
    }

    @Test
    public void getMany_qlQuery_returnsEmptyListWhenNoData() {
        List<Department> results = executor.getMany(
            QLQuery.<Department>of("from Department"), Page.NoPaging);

        assertTrue(results.isEmpty());
    }

    @Test
    public void getMany_qlQuery_withPaging_firstPage() {
        persist(new Department("a"), new Department("b"), new Department("c"));

        List<Department> firstPage = executor.getMany(
            QLQuery.<Department>of("from Department order by mandatoryDepName"),
            Page.FIRST.withSize(2));

        assertEquals(2, firstPage.size());
        assertEquals("a", firstPage.get(0).getMandatoryName());
    }

    @Test
    public void getMany_qlQuery_withPaging_secondPage() {
        persist(new Department("a"), new Department("b"), new Department("c"));

        List<Department> secondPage = executor.getMany(
            QLQuery.<Department>of("from Department order by mandatoryDepName"),
            Page.FIRST.withSize(2).nextPage());

        assertEquals(1, secondPage.size());
        assertEquals("c", secondPage.get(0).getMandatoryName());
    }

    // -------------------------------------------------------------------------
    // replaceProxy(T)
    // -------------------------------------------------------------------------

    @Test
    public void replaceProxy_returnsNonProxyAsIs() {
        Department dep = new Department("proxy");
        Department result = HibernateQueryExecutor.replaceProxy(dep);
        assertSame(dep, result);
    }

    @Test
    public void replaceProxy_returnsNullAsNull() {
        Object result = HibernateQueryExecutor.replaceProxy(null);
        assertEquals(null, result);
    }

    @Test
    public void replaceProxy_returnsStringAsIs() {
        String s = "hello";
        String result = HibernateQueryExecutor.replaceProxy(s);
        assertSame(s, result);
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private CriteriaQuery<Department> allDepartments() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        q.from(Department.class);
        return q;
    }

    private CriteriaQuery<Department> allDepartmentsOrdered() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        q.select(root).orderBy(cb.asc(root.get(Department_.mandatoryDepName)));
        return q;
    }
}
