package fi.solita.utils.query.backend.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.solita.utils.query.Department;
import fi.solita.utils.query.Employee;
import fi.solita.utils.query.Municipality;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.backend.Type;
import fi.solita.utils.query.backend.TypeProvider;
import fi.solita.utils.query.backend.hibernate.HibernateTypeProvider.HibernateType;

public class HibernateTypeProviderTest extends QueryTestBase {

    @Autowired
    private TypeProvider typeProvider;

    // -------------------------------------------------------------------------
    // idType
    // -------------------------------------------------------------------------

    @Test
    public void idType_returnsJavaTypeHibernateType_forEntity() {
        Type<Department.ID> t = typeProvider.idType(Department.class);

        assertTrue("Should be HibernateType", t instanceof HibernateType);
        HibernateType<Department.ID> ht = (HibernateType<Department.ID>) t;
        assertTrue("bindableType should be None", ht.bindableType.equals(Optional.empty()));
        assertTrue("entity should be None", ht.entity.equals(Optional.empty()));
        assertTrue("javaType should be Some", ht.javaType.isPresent());
    }

    @Test
    public void idType_returnsCorrectIdClass_forDepartment() {
        Type<Department.ID> t = typeProvider.idType(Department.class);

        HibernateType<Department.ID> ht = (HibernateType<Department.ID>) t;
        assertEquals(Department.ID.class, ht.javaType.get());
    }

    @Test
    public void idType_returnsCorrectIdClass_forEmployee() {
        Type<Employee.ID> t = typeProvider.idType(Employee.class);

        HibernateType<Employee.ID> ht = (HibernateType<Employee.ID>) t;
        assertEquals(Employee.ID.class, ht.javaType.get());
    }

    // -------------------------------------------------------------------------
    // type - entity
    // -------------------------------------------------------------------------

    @Test
    public void type_returnsEntityHibernateType_forEntityClass() {
        Type<Department> t = typeProvider.type(Department.class);

        HibernateType<Department> ht = (HibernateType<Department>) t;
        assertTrue("entity should be Some for an entity class", ht.entity.isPresent());
        assertEquals(Department.class, ht.entity.get());
        assertFalse("bindableType should be None", ht.bindableType.isPresent());
        assertFalse("javaType should be None", ht.javaType.isPresent());
    }

    @Test
    public void type_returnsEntityHibernateType_forEmployeeClass() {
        Type<Employee> t = typeProvider.type(Employee.class);

        HibernateType<Employee> ht = (HibernateType<Employee>) t;
        assertTrue(ht.entity.isPresent());
        assertEquals(Employee.class, ht.entity.get());
    }

    @Test
    public void type_returnsEntityHibernateType_forMunicipalityClass() {
        Type<Municipality> t = typeProvider.type(Municipality.class);

        HibernateType<Municipality> ht = (HibernateType<Municipality>) t;
        assertTrue(ht.entity.isPresent());
        assertEquals(Municipality.class, ht.entity.get());
    }

    // -------------------------------------------------------------------------
    // type - java type / basic type
    // -------------------------------------------------------------------------

    @Test
    public void type_returnsJavaTypeOrBindable_forStringClass() {
        Type<String> t = typeProvider.type(String.class);

        HibernateType<String> ht = (HibernateType<String>) t;
        assertTrue("String should be resolved as javaType or bindable",
            ht.javaType.isPresent() || ht.bindableType.isPresent());
        assertFalse("entity should be None for String", ht.entity.isPresent());
    }

    @Test
    public void type_returnsJavaTypeOrBindable_forIntegerClass() {
        Type<Integer> t = typeProvider.type(Integer.class);

        HibernateType<Integer> ht = (HibernateType<Integer>) t;
        assertTrue("Integer should be resolved as javaType or bindable",
            ht.javaType.isPresent() || ht.bindableType.isPresent());
        assertFalse("entity should be None for Integer", ht.entity.isPresent());
    }

    @Test
    public void type_returnsJavaTypeOrBindable_forLongClass() {
        Type<Long> t = typeProvider.type(Long.class);

        HibernateType<Long> ht = (HibernateType<Long>) t;
        assertTrue("Long should be resolved as javaType or bindable",
            ht.javaType.isPresent() || ht.bindableType.isPresent());
    }

    // -------------------------------------------------------------------------
    // type - enum
    // -------------------------------------------------------------------------

    enum SimpleEnum { A, B, C }

    @Test
    public void type_resolvesEnumType_forSimpleEnum() {
        Type<SimpleEnum> t = typeProvider.type(SimpleEnum.class);

        HibernateType<SimpleEnum> ht = (HibernateType<SimpleEnum>) t;
        assertNotNull("Should resolve enum type without throwing", ht);
        assertTrue("Enum should be resolved as javaType or bindable",
            ht.javaType.isPresent() || ht.bindableType.isPresent());
    }

    enum EnumWithBody {
        X { @Override public String toString() { return "x"; } },
        Y { @Override public String toString() { return "y"; } }
    }

    @Test
    public void type_resolvesEnumType_forEnumWithClassBody() {
        // An anonymous subclass of an enum has the enclosing enum as its declaring class
        Class<?> anonymousSubclass = EnumWithBody.X.getClass();
        assertTrue("Should be an anonymous subclass of enum", anonymousSubclass.getEnclosingClass() != null && anonymousSubclass.getEnclosingClass().isEnum());

        @SuppressWarnings("unchecked")
        Type<Object> t = (Type<Object>) typeProvider.type(anonymousSubclass);

        HibernateType<Object> ht = (HibernateType<Object>) t;
        assertNotNull(ht);
        assertTrue("Enum-body subclass should be resolved via its enclosing enum",
            ht.javaType.isPresent() || ht.bindableType.isPresent());
    }

    // -------------------------------------------------------------------------
    // type - array
    // -------------------------------------------------------------------------

    @Test
    public void type_resolvesArrayType_forByteArray() {
        Type<byte[]> t = typeProvider.type(byte[].class);

        HibernateType<byte[]> ht = (HibernateType<byte[]>) t;
        assertNotNull("byte[] should be resolved", ht);
        assertTrue("byte[] should be resolved as javaType or bindable",
            ht.javaType.isPresent() || ht.bindableType.isPresent());
    }

    // -------------------------------------------------------------------------
    // type - unknown / throws
    // -------------------------------------------------------------------------

    @Test
    public void type_throwsIllegalArgumentException_forUnknownType() {
        class Unknown {}
        try {
            typeProvider.type(Unknown.class);
            fail("Expected IllegalArgumentException for unknown type");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Could not resolve Hibernate Type for"));
        }
    }

    // -------------------------------------------------------------------------
    // getEnumType (static)
    // -------------------------------------------------------------------------

    @Test
    public void getEnumType_returnsSome_forSimpleEnum() {
        Optional<Class<SimpleEnum>> result = HibernateTypeProvider.getEnumType(SimpleEnum.class);
        assertEquals(Optional.of(SimpleEnum.class), result);
    }

    @Test
    public void getEnumType_returnsSomeOfEnclosingEnum_forEnumWithClassBody() {
        Class<?> anonymousSubclass = EnumWithBody.X.getClass();
        @SuppressWarnings("unchecked")
        Optional<Class<?>> result = (Optional<Class<?>>) (Object) HibernateTypeProvider.getEnumType(anonymousSubclass);
        assertTrue("Should return Some", result.isPresent());
        assertEquals(EnumWithBody.class, result.get());
    }

    @Test
    public void getEnumType_returnsNone_forNonEnumClass() {
        Optional<Class<String>> result = HibernateTypeProvider.getEnumType(String.class);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void getEnumType_returnsNone_forEntityClass() {
        Optional<Class<Department>> result = HibernateTypeProvider.getEnumType(Department.class);
        assertEquals(Optional.empty(), result);
    }

    // -------------------------------------------------------------------------
    // getEntityClass
    // -------------------------------------------------------------------------

    @Test
    public void getEntityClass_returnsCorrectClass_forPersistedDepartment() {
        Department dep = new Department("entityClassTest");
        persist(dep);
        em.flush();

        Class<?> entityClass = typeProvider.getEntityClass(dep);

        assertEquals(Department.class, entityClass);
    }

    @Test
    public void getEntityClass_returnsCorrectClass_forPersistedEmployee() {
        Department dep = new Department("dept");
        persist(dep);
        Employee emp = new Employee("emp", dep);
        persist(emp);
        em.flush();

        Class<?> entityClass = typeProvider.getEntityClass(emp);

        assertEquals(Employee.class, entityClass);
    }
}