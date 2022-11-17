package fi.solita.utils.query.generation;

import static fi.solita.utils.functional.Functional.head;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.QueryUtils;
import fi.solita.utils.query.attributes.AdditionalQueryPerformingAttribute;
import fi.solita.utils.query.attributes.OptionalAttribute;
import fi.solita.utils.query.attributes.PseudoAttribute;
import fi.solita.utils.query.backend.Type;

public class Cast {

    public static <E, T> SingularAttribute<E, Option<T>> optional(SingularAttribute<E, T> attribute) throws IllegalArgumentException, QueryUtils.RequiredAttributeMustNotHaveOptionTypeException {
        if (attribute instanceof PseudoAttribute) {
            throw new IllegalArgumentException("No reason to wrap a PseudoAttribute. Right?");
        }
        if (attribute instanceof Bindable && Option.class.isAssignableFrom(((Bindable<?>) attribute).getBindableJavaType())) {
            throw new IllegalArgumentException("No reason to wrap an Option<?> type. Right?");
        }
        if (attribute instanceof AdditionalQueryPerformingAttribute) {
            List<Attribute<?,?>> parameters = ((AdditionalQueryPerformingAttribute) attribute).getConstructor().getParameters();
            if (parameters.size() == 1 && head(parameters) != null && Option.class.isAssignableFrom(head(parameters).getJavaType())) {
                throw new IllegalArgumentException("No reason to wrap an Option<?> type. Right?");
            }
        }
        /* not checking this here allows a use case with subtyping. Any drawbacks in addition to allowing user to wrap values to Option by accident? 
          if (QueryUtils.isRequiredByMetamodel(attribute)) {
            throw new QueryUtils.RequiredAttributeMustNotHaveOptionTypeException(attribute);
        }*/
        return OptionalAttribute.Constructors.optional(attribute);
    }
    
    public static <T> Type.Optional<T> optional(Type<T> type) {
        return new Type.Optional<T>(type);
    }

    public static <PARENT, VALUE> SingularAttribute<PARENT, Option<VALUE>> optionalSubtype(SingularAttribute<? extends PARENT, VALUE> attribute) throws IllegalArgumentException {
        if (attribute instanceof PseudoAttribute) {
            throw new IllegalArgumentException("No reason to wrap a PseudoAttribute. Right?");
        }
        if (attribute instanceof Bindable && Option.class.isAssignableFrom(((Bindable<?>) attribute).getBindableJavaType())) {
            throw new IllegalArgumentException("No reason to wrap an Option<?> type. Right?");
        }
        if (attribute instanceof AdditionalQueryPerformingAttribute) {
            List<Attribute<?,?>> parameters = ((AdditionalQueryPerformingAttribute) attribute).getConstructor().getParameters();
            if (parameters.size() == 1 && Option.class.isAssignableFrom(head(parameters).getJavaType())) {
                throw new IllegalArgumentException("No reason to wrap an Option<?> type. Right?");
            }
        }
        return OptionalAttribute.Constructors.optional(attribute);
    }

    @SuppressWarnings("unchecked")
    public static <PARENT extends IEntity<?>, VALUE> SingularAttribute<PARENT, VALUE> castSuper(SingularAttribute<? extends PARENT, VALUE> attribute) {
        return (SingularAttribute<PARENT, VALUE>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <PARENT extends IEntity<?>, VALUE> CollectionAttribute<PARENT, VALUE> castSuper(CollectionAttribute<? extends PARENT, VALUE> attribute) {
        return (CollectionAttribute<PARENT, VALUE>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <PARENT extends IEntity<?>, VALUE> SetAttribute<PARENT, VALUE> castSuper(SetAttribute<? extends PARENT, VALUE> attribute) {
        return (SetAttribute<PARENT, VALUE>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <PARENT extends IEntity<?>, VALUE> ListAttribute<PARENT, VALUE> castSuper(ListAttribute<? extends PARENT, VALUE> attribute) {
        return (ListAttribute<PARENT, VALUE>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <CHILD extends IEntity<?>, VALUE> SingularAttribute<CHILD, VALUE> cast(SingularAttribute<? super CHILD, ? super VALUE> attribute) {
        return (SingularAttribute<CHILD, VALUE>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <CHILD extends IEntity<?>, VALUE> CollectionAttribute<CHILD, VALUE> cast(CollectionAttribute<? super CHILD, ? super VALUE> attribute) {
        return (CollectionAttribute<CHILD, VALUE>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <CHILD extends IEntity<?>, VALUE> SetAttribute<CHILD, VALUE> cast(SetAttribute<? super CHILD, ? super VALUE> attribute) {
        return (SetAttribute<CHILD, VALUE>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <CHILD extends IEntity<?>, VALUE> ListAttribute<CHILD, VALUE> cast(ListAttribute<? super CHILD, ? super VALUE> attribute) {
        return (ListAttribute<CHILD, VALUE>) attribute;
    }
}
