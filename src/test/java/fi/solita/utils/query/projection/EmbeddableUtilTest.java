package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.List;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.Metamodel;

import org.junit.Test;

import java.util.ArrayList;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.Municipality_;
import fi.solita.utils.query.QueryTestBase;
import fi.solita.utils.query.Report;
import fi.solita.utils.query.Report_;
import fi.solita.utils.query.generation.Cast;

public class EmbeddableUtilTest extends QueryTestBase {

    // -------------------------------------------------------------------------
    // attributeByName
    // -------------------------------------------------------------------------

    @Test
    public void attributeByName_sortsAlphabetically() {
        Comparator<Attribute<?, ?>> cmp = EmbeddableUtil.attributeByName;
        // mandatoryDepName < mandatoryNumber (lexicographic)
        assertTrue(cmp.compare(Department_.mandatoryDepName, Department_.mandatoryNumber) < 0);
        assertTrue(cmp.compare(Department_.mandatoryNumber, Department_.mandatoryDepName) > 0);
    }

    @Test
    public void attributeByName_equalNamesReturnZero() {
        Comparator<Attribute<?, ?>> cmp = EmbeddableUtil.attributeByName;
        assertEquals(0, cmp.compare(Department_.mandatoryDepName, Department_.mandatoryDepName));
    }

    // -------------------------------------------------------------------------
    // instantiate
    // -------------------------------------------------------------------------

    @Test
    public void instantiate_embeddableClass_returnsNewInstance() {
        Object result = EmbeddableUtil.instantiate(Report.class);
        assertNotNull(result);
        assertTrue(result instanceof Report);
    }

    @Test
    public void instantiate_twoCallsReturnDistinctInstances() {
        Object first = EmbeddableUtil.instantiate(Report.class);
        Object second = EmbeddableUtil.instantiate(Report.class);
        assertFalse(first == second);
    }

    @Test(expected = RuntimeException.class)
    public void instantiate_classWithoutNoArgConstructor_throws() {
        // Integer has no accessible no-arg constructor
        EmbeddableUtil.instantiate(Integer.class);
    }

    // -------------------------------------------------------------------------
    // getEmbeddableType
    // -------------------------------------------------------------------------

    @Test
    public void getEmbeddableType_reportAttribute_returnsEmbeddableType() {
        Metamodel metamodel = em.getMetamodel();
        EmbeddableType<?> type = EmbeddableUtil.getEmbeddableType(Department_.mandatoryReport, metamodel);
        assertNotNull(type);
        assertEquals(Report.class, type.getJavaType());
    }

    @Test
    public void getEmbeddableType_optionalReportAttribute_returnsEmbeddableType() {
        Metamodel metamodel = em.getMetamodel();
        EmbeddableType<?> type = EmbeddableUtil.getEmbeddableType(Department_.optionalDepReport, metamodel);
        assertNotNull(type);
        assertEquals(Report.class, type.getJavaType());
    }

    // -------------------------------------------------------------------------
    // getEmbeddableAttributes
    // -------------------------------------------------------------------------

    @Test
    public void getEmbeddableAttributes_reportEmbeddable_returnsSortedAttributes() {
        Metamodel metamodel = em.getMetamodel();
        List<? extends Attribute<?, ?>> attrs = EmbeddableUtil.getEmbeddableAttributes(Department_.mandatoryReport, metamodel);
        assertNotNull(attrs);
        // Report has exactly one attribute: year1
        assertEquals(1, attrs.size());
        assertEquals("year1", attrs.get(0).getName());
    }

    @Test
    public void getEmbeddableAttributes_sortedByName() {
        Metamodel metamodel = em.getMetamodel();
        List<? extends Attribute<?, ?>> attrs = EmbeddableUtil.getEmbeddableAttributes(Department_.mandatoryReport, metamodel);
        List<String> names = new ArrayList<String>();
        for (Attribute<?, ?> a : attrs) {
            names.add(a.getName());
        }
        List<String> sorted = new ArrayList<String>(names);
        sorted.sort(null);
        assertEquals(sorted, names);
    }

    // -------------------------------------------------------------------------
    // collectEmbeddableFromParts
    // -------------------------------------------------------------------------

    @Test
    public void collectEmbeddableFromParts_singleField_setsFieldValue() {
        Metamodel metamodel = em.getMetamodel();
        // Report has one attribute: year1, sorted by name → pass [2024]
        Object result = EmbeddableUtil.collectEmbeddableFromParts(metamodel, Department_.mandatoryReport, newList((Object) 2024));
        assertTrue(result instanceof Report);
        assertEquals(Integer.valueOf(2024), ((Report) result).getYear());
    }

    @Test
    public void collectEmbeddableFromParts_nullValue_setsNullField() {
        Metamodel metamodel = em.getMetamodel();
        Object result = EmbeddableUtil.collectEmbeddableFromParts(metamodel, Department_.mandatoryReport, newList((Object) null));
        assertTrue(result instanceof Report);
        assertEquals(null, ((Report) result).getYear());
    }

    @Test(expected = IllegalStateException.class)
    public void collectEmbeddableFromParts_wrongNumberOfColumns_throws() {
        Metamodel metamodel = em.getMetamodel();
        // Report has 1 attribute, passing 2 columns must throw
        EmbeddableUtil.collectEmbeddableFromParts(metamodel, Department_.mandatoryReport, newList((Object) 1, (Object) 2));
    }

    // -------------------------------------------------------------------------
    // isCollectionOfEmbeddables
    // -------------------------------------------------------------------------

    @Test
    public void isCollectionOfEmbeddables_listOfReports_true() {
        // Department.reports is @ElementCollection of Report (@Embeddable)
        assertTrue(EmbeddableUtil.isCollectionOfEmbeddables(Department_.reports));
    }

    @Test
    public void isCollectionOfEmbeddables_setOfReports_true() {
        // Municipality.reports is @ElementCollection of Report (@Embeddable)
        assertTrue(EmbeddableUtil.isCollectionOfEmbeddables(Municipality_.reports));
    }

    @Test
    public void isCollectionOfEmbeddables_listOfIntegers_false() {
        // Department.numbers is @ElementCollection of Integer (BASIC, not EMBEDDABLE)
        assertFalse(EmbeddableUtil.isCollectionOfEmbeddables(Department_.numbers));
    }

    @Test
    public void isCollectionOfEmbeddables_singularAttribute_false() {
        // A plain singular attribute is never a collection of embeddables
        assertFalse(EmbeddableUtil.isCollectionOfEmbeddables(Department_.mandatoryReport));
    }

    @Test
    public void isCollectionOfEmbeddables_manyToOneAttribute_false() {
        // A many-to-one relationship is MANY_TO_ONE, not ELEMENT_COLLECTION
        assertFalse(EmbeddableUtil.isCollectionOfEmbeddables(Employee_.mandatoryDepartment));
    }

    // -------------------------------------------------------------------------
    // unwrapEmbeddableAttribute
    // -------------------------------------------------------------------------

    @Test
    public void unwrapEmbeddableAttribute_null_returnsNone() {
        assertEquals(None(), EmbeddableUtil.unwrapEmbeddableAttribute(null));
    }

    @Test
    public void unwrapEmbeddableAttribute_embeddedAttribute_returnsSome() {
        // mandatoryReport is @Embedded → EMBEDDED persistent type
        Option<? extends Attribute<?, ?>> result = EmbeddableUtil.unwrapEmbeddableAttribute(Department_.mandatoryReport);
        assertEquals(Some(Department_.mandatoryReport), result);
    }

    @Test
    public void unwrapEmbeddableAttribute_optionalEmbeddedAttribute_returnsSome() {
        // optionalDepReport is @Embedded → EMBEDDED
        Option<? extends Attribute<?, ?>> result = EmbeddableUtil.unwrapEmbeddableAttribute(Department_.optionalDepReport);
        assertEquals(Some(Department_.optionalDepReport), result);
    }

    @Test
    public void unwrapEmbeddableAttribute_singularBasicAttribute_returnsNone() {
        // mandatoryDepName is a basic attribute (BASIC), not embedded
        assertEquals(None(), EmbeddableUtil.unwrapEmbeddableAttribute(Department_.mandatoryDepName));
    }

    @Test
    public void unwrapEmbeddableAttribute_manyToOneAttribute_returnsNone() {
        // mandatoryDepartment is MANY_TO_ONE, not EMBEDDED
        assertEquals(None(), EmbeddableUtil.unwrapEmbeddableAttribute(Employee_.mandatoryDepartment));
    }

    @Test
    public void unwrapEmbeddableAttribute_pseudoAttribute_returnsNone() {
        // Select.literal creates a PseudoAttribute
        assertEquals(None(), EmbeddableUtil.unwrapEmbeddableAttribute(Select.literal(42)));
    }

    @Test
    public void unwrapEmbeddableAttribute_additionalQueryPerformingAttribute_singleEmbeddedParam_returnsSome() {
        // Related.projection wraps the attribute in AdditionalQueryPerformingAttribute (RelationSingularAttribute).
        // The outer proxy itself has PersistentAttributeType EMBEDDED (delegating to the wrapped attribute),
        // so unwrapEmbeddableAttribute returns Some wrapping that proxy.
        // Report.year1 is nullable so must be wrapped with Cast.optional().
        Option<? extends Attribute<?, ?>> result = EmbeddableUtil.unwrapEmbeddableAttribute(
            Related.projection(Department_.mandatoryReport, Project.value(Cast.optional(Report_.year1))));
        assertTrue(result.isDefined());
    }

    @Test
    public void unwrapEmbeddableAttribute_additionalQueryPerformingAttribute_nonEmbeddedParam_returnsNone() {
        // When the single parameter of Related.projection is a non-embedded attribute, result is None.
        // Employee_.mandatoryDepartment is SingularAttribute<Employee, Department>, so the
        // projection constructor must be MetaJpaConstructor<? super Department, ?, ?>.
        Option<? extends Attribute<?, ?>> result = EmbeddableUtil.unwrapEmbeddableAttribute(
            Related.projection(Employee_.mandatoryDepartment, Project.value(Department_.mandatoryDepName)));
        assertEquals(None(), result);
    }

    @Test
    public void unwrapEmbeddableAttribute_castOptionalEmbeddedAttribute_returnsSome() {
        // Cast.optional wraps in OptionalAttributeImpl (not PseudoAttribute), which proxies
        // the underlying embedded attribute → getPersistentAttributeType() is still EMBEDDED
        // → unwrapEmbeddableAttribute returns Some with the optional wrapper attribute.
        Option<? extends Attribute<?, ?>> result = EmbeddableUtil.unwrapEmbeddableAttribute(Cast.optional(Department_.optionalDepReport));
        assertTrue(result.isDefined());
    }
}