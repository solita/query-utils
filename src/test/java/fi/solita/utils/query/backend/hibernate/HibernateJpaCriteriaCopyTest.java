package fi.solita.utils.query.backend.hibernate;

import static fi.solita.utils.functional.Option.None;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import org.junit.Test;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.QueryTestBase;

public class HibernateJpaCriteriaCopyTest extends QueryTestBase {

    // --- copyCriteria ---

    @Test
    public void copyCriteria_returnsSome_forStandardCriteriaQuery() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        q.from(Department.class);

        Option<CriteriaQuery<Department>> result = HibernateJpaCriteriaCopy.copyCriteria(q);

        assertTrue("Should return Some for SqmSelectStatement", result.isDefined());
    }

    @Test
    public void copyCriteria_returnedCopyIsNotSameInstance() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        q.from(Department.class);

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        assertNotSame("Copy should be a different instance", q, copy);
    }

    @Test
    public void copyCriteria_copiedQueryIsExecutable() {
        Department dep = new Department("testDep");
        persist(dep);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        q.select(root);

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();
        assertNotNull("Copied query should be executable", em.createQuery(copy).getResultList());
    }

    @Test
    public void copyCriteria_copiedQueryYieldsSameResults() {
        Department dep1 = new Department("d1");
        Department dep2 = new Department("d2");
        persist(dep1, dep2);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        q.select(root);

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        assertEquals(
            em.createQuery(q).getResultList().size(),
            em.createQuery(copy).getResultList().size()
        );
    }

    @Test
    public void copyCriteria_copiedQueryCanBeModifiedIndependently() {
        Department dep = new Department("copyCriteriaTest");
        persist(dep);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> original = cb.createQuery(Department.class);
        Root<Department> root = original.from(Department.class);
        original.select(root);

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(original).get();

        // Apply an always-false restriction to the copy so its result differs
        copy.where(cb.equal(cb.literal(1), cb.literal(0)));

        int originalCount = em.createQuery(original).getResultList().size();
        int copyCount = em.createQuery(copy).getResultList().size();

        assertEquals("Original should return results", 1, originalCount);
        assertEquals("Modified copy should return no results", 0, copyCount);
    }

    @Test
    public void copyCriteria_withRestriction_copyPreservesRestriction() {
        Department dep1 = new Department("alpha");
        Department dep2 = new Department("beta");
        persist(dep1, dep2);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        q.select(root).where(cb.equal(root.get(Department_.mandatoryDepName), "alpha"));

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        assertEquals("Copied query should preserve WHERE clause", 1, em.createQuery(copy).getResultList().size());
        assertEquals("alpha", em.createQuery(copy).getSingleResult().getMandatoryName());
    }

    @Test
    public void copyCriteria_withOrderBy_copyPreservesOrderBy() {
        Department dep1 = new Department("zzz");
        Department dep2 = new Department("aaa");
        persist(dep1, dep2);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        q.select(root).orderBy(cb.asc(root.get(Department_.mandatoryDepName)));

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        java.util.List<Department> results = em.createQuery(copy).getResultList();
        assertEquals(2, results.size());
        assertEquals("aaa", results.get(0).getMandatoryName());
        assertEquals("zzz", results.get(1).getMandatoryName());
    }

    @Test
    public void copyCriteria_withDistinct_copyPreservesDistinct() {
        Department dep = new Department("dup");
        persist(dep);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> q = cb.createQuery(String.class);
        Root<Department> root = q.from(Department.class);
        q.select(root.get(Department_.mandatoryDepName)).distinct(true);

        CriteriaQuery<String> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        assertTrue("Copied query should be distinct", copy.isDistinct());
        assertEquals(1, em.createQuery(copy).getResultList().size());
    }

    @Test
    public void copyCriteria_withJoin_copyPreservesJoin() {
        Department dep = new Department("joinDep");
        Employee emp = new Employee("joinEmp", dep);
        persist(dep, emp);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> q = cb.createQuery(String.class);
        Root<Department> root = q.from(Department.class);
        root.join(Department_.employees);
        q.select(root.get(Department_.mandatoryDepName));

        CriteriaQuery<String> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        java.util.List<String> results = em.createQuery(copy).getResultList();
        assertEquals(1, results.size());
        assertEquals("joinDep", results.get(0));
    }

    @Test
    public void copyCriteria_emptyTableReturnsEmptyList() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);
        q.select(root);

        CriteriaQuery<Department> copy = HibernateJpaCriteriaCopy.copyCriteria(q).get();

        assertEquals(0, em.createQuery(copy).getResultList().size());
    }

    // --- createListIndexSortOrder ---

    @Test
    public void createListIndexSortOrder_returnsNone_forNonListJoinPath() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> q = cb.createQuery(Employee.class);
        Root<Employee> root = q.from(Employee.class);

        // A singular path (not a list join) should return None
        Path<?> namePath = root.get(Employee_.mandatoryName);
        Option<jakarta.persistence.criteria.Order> result = HibernateJpaCriteriaCopy.createListIndexSortOrder(namePath);

        assertEquals(None(), result);
    }

    @Test
    public void createListIndexSortOrder_returnsNone_forManyToOneJoin() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> q = cb.createQuery(Employee.class);
        Root<Employee> root = q.from(Employee.class);

        // A regular join (ManyToOne) is not an SqmListJoin
        Path<?> deptPath = root.join(Employee_.mandatoryDepartment);
        Option<jakarta.persistence.criteria.Order> result = HibernateJpaCriteriaCopy.createListIndexSortOrder(deptPath);

        assertEquals(None(), result);
    }

    @Test
    public void createListIndexSortOrder_returnsSome_forListJoin() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> q = cb.createQuery(Employee.class);
        Root<Department> root = q.from(Department.class);

        // Department.employees is a @OrderColumn List — joining it yields SqmListJoin
        Path<?> listJoinPath = root.join(Department_.employees);
        Option<jakarta.persistence.criteria.Order> result = HibernateJpaCriteriaCopy.createListIndexSortOrder(listJoinPath);

        assertTrue("Should return Some for list join", result.isDefined());
        assertNotNull(result.get());
    }

    @Test
    public void createListIndexSortOrder_returnsNone_forRootPath() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> q = cb.createQuery(Department.class);
        Root<Department> root = q.from(Department.class);

        // A root is not a list join
        Option<jakarta.persistence.criteria.Order> result = HibernateJpaCriteriaCopy.createListIndexSortOrder(root);

        assertEquals(None(), result);
    }

    @Test
    public void createListIndexSortOrder_returnedOrder_isUsableInQuery() {
        Department dep = new Department("orderedDep");
        Employee emp1 = new Employee("emp1", dep);
        Employee emp2 = new Employee("emp2", dep);
        persist(dep, emp1, emp2);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> q = cb.createQuery(String.class);
        Root<Department> root = q.from(Department.class);
        jakarta.persistence.criteria.ListJoin<Department, Employee> listJoin = root.joinList("employees");
        q.select(listJoin.get(Employee_.mandatoryName));

        Option<jakarta.persistence.criteria.Order> orderOpt = HibernateJpaCriteriaCopy.createListIndexSortOrder(listJoin);
        assertTrue(orderOpt.isDefined());
        q.orderBy(orderOpt.get());

        // Query should execute without errors using the list index ordering
        java.util.List<String> results = em.createQuery(q).getResultList();
        assertEquals(2, results.size());
    }
}