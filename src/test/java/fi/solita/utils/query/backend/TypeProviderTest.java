package fi.solita.utils.query.backend;

import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.backend.hibernate.HibernateTypeProvider;
import fi.solita.utils.query.backend.hibernate.HibernateTypeProvider.HibernateType;

public class TypeProviderTest extends QueryTestBase {

    @Autowired
    private TypeProvider typeProvider;

    // --- getEnumType (static) ---

    enum SimpleEnum { A, B }

    enum EnumWithBody {
        A {
            @Override
            public String toString() { return "a"; }
        }
    }

    @Test
    public void getEnumType_returnsNone_forNonEnumClass() {
        Option<Class<String>> result = HibernateTypeProvider.getEnumType(String.class);
        assertEquals(None(), result);
    }

    @Test
    public void getEnumType_returnsSome_forDirectEnum() {
        Option<Class<SimpleEnum>> result = HibernateTypeProvider.getEnumType(SimpleEnum.class);
        assertEquals(Some(SimpleEnum.class), result);
    }

    @Test
    public void getEnumType_returnsSome_forEnumWithBody() {
        // Enum constant with a body creates an anonymous subclass; its enclosing class is the enum
        Class<?> anonymousSubclass = EnumWithBody.A.getClass();
        assertTrue(anonymousSubclass.getEnclosingClass().isEnum());
        Option<?> result = HibernateTypeProvider.getEnumType(anonymousSubclass);
        assertTrue(result.isDefined());
        assertEquals(EnumWithBody.class, result.get());
    }

    @Test
    public void getEnumType_returnsNone_forInteger() {
        Option<Class<Integer>> result = HibernateTypeProvider.getEnumType(Integer.class);
        assertEquals(None(), result);
    }

    // --- type() ---

    @Test
    public void type_resolves_entityClass() {
        Type<Department> type = typeProvider.type(Department.class);
        assertNotNull(type);
        HibernateType<Department> ht = (HibernateType<Department>) type;
        assertTrue("Expected entity type", ht.entity.isDefined());
        assertEquals(Department.class, ht.entity.get());
    }

    @Test
    public void type_resolves_basicType_String() {
        Type<String> type = typeProvider.type(String.class);
        assertNotNull(type);
    }

    @Test
    public void type_resolves_basicType_Integer() {
        Type<Integer> type = typeProvider.type(Integer.class);
        assertNotNull(type);
    }

    @Test
    public void type_resolves_basicType_Long() {
        Type<Long> type = typeProvider.type(Long.class);
        assertNotNull(type);
    }

    @Test(expected = IllegalArgumentException.class)
    public void type_throwsForUnknownClass() {
        typeProvider.type(TypeProviderTest.class);
    }

    // --- idType() ---

    @Test
    public void idType_resolves_forEmployee() {
        Type<Employee.ID> type = typeProvider.idType(Employee.class);
        assertNotNull(type);
    }

    @Test
    public void idType_resolves_forDepartment() {
        Type<Department.ID> type = typeProvider.idType(Department.class);
        assertNotNull(type);
    }

    // --- getEntityClass() ---

    @Test
    public void getEntityClass_returnsConcreteClass_forPersistedEntity() {
        Department dept = new Department("test");
        persist(dept);
        em.flush();
        em.clear();

        IEntity<?> loaded = em.getReference(Department.class, dept.getId());
        Class<?> entityClass = typeProvider.getEntityClass(loaded);
        assertEquals(Department.class, entityClass);
    }

    @Test
    public void getEntityClass_returnsConcreteClass_forManagedEntity() {
        Department dept = new Department("managed");
        persist(dept);
        em.flush();

        Class<?> entityClass = typeProvider.getEntityClass(dept);
        assertEquals(Department.class, entityClass);
    }
}