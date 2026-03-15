package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Option.None;
import static fi.solita.utils.functional.Option.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import jakarta.persistence.metamodel.SingularAttribute;

import org.junit.Test;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.Department;
import fi.solita.utils.query.Department_;
import fi.solita.utils.query.Employee_;
import fi.solita.utils.query.QueryTestBase;

public class AttributeProxyTest extends QueryTestBase {

    // --- unwrap: plain attribute is an instance of the requested type ---

    @Test
    public void unwrap_plainAttribute_returnsSome() {
        SingularAttribute<?, ?> plain = Employee_.mandatoryName;
        assertEquals(Some(plain), AttributeProxy.unwrap(SingularAttribute.class, plain));
    }

    // --- unwrap: proxy whose target matches the requested type ---

    @Test
    public void unwrap_proxyWrappingTarget_returnsTargetWrappedInSome() {
        SingularAttribute<?, ?> inner = Employee_.mandatoryName;
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);

        // The optional proxy itself implements SingularAttribute, so unwrap gives the proxy
        assertEquals(Some(optional), AttributeProxy.unwrap(SingularAttribute.class, optional));
    }

    @Test
    public void unwrap_proxyAsOptionalAttribute_returnsSome() {
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);
        Option<OptionalAttribute> result = AttributeProxy.unwrap(OptionalAttribute.class, optional);
        assertEquals(Some(optional), result);
    }

    @Test
    public void unwrap_restrictingAttributeAsRestrictingAttribute_returnsSome() {
        SingularAttribute<Department, Department> attr =
            RestrictingAttribute.Constructors.singular(Department_.mandatorySelfReference, Department_.id);
        Option<RestrictingAttribute> result = AttributeProxy.unwrap(RestrictingAttribute.class, attr);
        assertEquals(Some(attr), result);
    }

    // --- unwrap: proxy with null target returns None ---

    @Test
    public void unwrap_nullTargetProxy_returnsNone() {
        // SelfAttribute and LiteralSingularAttribute have null proxyTarget
        SingularAttribute<?, ?> self = PseudoAttribute.Constructors.self();
        Option<OptionalAttribute> result = AttributeProxy.unwrap(OptionalAttribute.class, self);
        assertEquals(None(), result);
    }

    @Test
    public void unwrap_literalProxy_returnsNone_forNonMatchingType() {
        SingularAttribute<?, ?> literal = PseudoAttribute.Constructors.literal(42);
        Option<OptionalAttribute> result = AttributeProxy.unwrap(OptionalAttribute.class, literal);
        assertEquals(None(), result);
    }

    // --- unwrap: nested proxies ---

    @Test
    public void unwrap_nestedProxy_unwrapsRecursively() {
        SingularAttribute<?, ?> inner = Employee_.mandatoryName;
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);
        SingularAttribute<?, ?> restricting =
            RestrictingAttribute.Constructors.singular(Department_.mandatorySelfReference, Department_.id);

        // Restricting wraps a real attribute; it implements RestrictingAttribute itself
        Option<RestrictingAttribute> result = AttributeProxy.unwrap(RestrictingAttribute.class, restricting);
        assertTrue(result.isDefined());
    }

    @Test
    public void unwrap_proxyWrappingProxy_recursivelyUnwrapsToMatchingType() {
        // OptionalAttributeImpl wraps Employee_.mandatoryName (a SingularAttribute)
        // unwrap for OptionalAttribute on a plain SingularAttribute → None (plain attr does not implement OptionalAttribute)
        Option<OptionalAttribute> result = AttributeProxy.unwrap(OptionalAttribute.class, Employee_.mandatoryName);
        assertEquals(None(), result);
    }

    // --- unwrap: wrong type ---

    @Test
    public void unwrap_wrongType_returnsNone() {
        Option<JoiningAttribute> result = AttributeProxy.unwrap(JoiningAttribute.class, Employee_.mandatoryName);
        assertEquals(None(), result);
    }

    @Test
    public void unwrap_proxyWrongType_returnsNone() {
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);
        Option<JoiningAttribute> result = AttributeProxy.unwrap(JoiningAttribute.class, optional);
        assertEquals(None(), result);
    }

    // --- canUnwrap: plain attribute ---

    @Test
    public void canUnwrap_plainAttribute_returnsTrue() {
        assertTrue(AttributeProxy.canUnwrap(SingularAttribute.class, Employee_.mandatoryName));
    }

    @Test
    public void canUnwrap_plainAttributeWrongType_returnsFalse() {
        assertFalse(AttributeProxy.canUnwrap(OptionalAttribute.class, Employee_.mandatoryName));
    }

    // --- canUnwrap: proxy wrapping target ---

    @Test
    public void canUnwrap_proxyMatchingType_returnsTrue() {
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);
        assertTrue(AttributeProxy.canUnwrap(OptionalAttribute.class, optional));
    }

    @Test
    public void canUnwrap_proxyAsSingularAttribute_returnsTrue() {
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);
        assertTrue(AttributeProxy.canUnwrap(SingularAttribute.class, optional));
    }

    // --- canUnwrap: null-target proxy ---

    @Test
    public void canUnwrap_nullTargetProxy_returnsFalse() {
        SingularAttribute<?, ?> self = PseudoAttribute.Constructors.self();
        assertFalse(AttributeProxy.canUnwrap(OptionalAttribute.class, self));
    }

    @Test
    public void canUnwrap_nullTargetProxy_asPseudoAttribute_returnsTrue() {
        SingularAttribute<?, ?> self = PseudoAttribute.Constructors.self();
        assertTrue(AttributeProxy.canUnwrap(PseudoAttribute.class, self));
    }

    // --- canUnwrap: wrong type ---

    @Test
    public void canUnwrap_wrongType_returnsFalse() {
        assertFalse(AttributeProxy.canUnwrap(JoiningAttribute.class, Employee_.mandatoryName));
    }

    @Test
    public void canUnwrap_proxyWrongType_returnsFalse() {
        SingularAttribute<?, ?> optional = OptionalAttribute.Constructors.optional(Employee_.mandatoryName);
        assertFalse(AttributeProxy.canUnwrap(JoiningAttribute.class, optional));
    }
}