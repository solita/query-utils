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
import fi.solita.utils.query.Identifiable;
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

    public static <E, T> SingularAttribute<E, Option<T>> optionalSubtype(SingularAttribute<? extends E, T> attribute) throws IllegalArgumentException {
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
    public static <E extends IEntity<?>, T> SingularAttribute<E, T> castSuper(SingularAttribute<? extends E, T> attribute) {
        return (SingularAttribute<E, T>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> CollectionAttribute<E, T> castSuper(CollectionAttribute<? extends E, T> attribute) {
        return (CollectionAttribute<E, T>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> SetAttribute<E, T> castSuper(SetAttribute<? extends E, T> attribute) {
        return (SetAttribute<E, T>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> ListAttribute<E, T> castSuper(ListAttribute<? extends E, T> attribute) {
        return (ListAttribute<E, T>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> SingularAttribute<E, T> cast(SingularAttribute<? super E, ? super T> attribute) {
        return (SingularAttribute<E, T>) attribute;
    }
    
    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> CollectionAttribute<E, T> cast(CollectionAttribute<? super E, ? super T> attribute) {
        return (CollectionAttribute<E, T>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> SetAttribute<E, T> cast(SetAttribute<? super E, ? super T> attribute) {
        return (SetAttribute<E, T>) attribute;
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity<?>, T> ListAttribute<E, T> cast(ListAttribute<? super E, ? super T> attribute) {
        return (ListAttribute<E, T>) attribute;
    }
}
