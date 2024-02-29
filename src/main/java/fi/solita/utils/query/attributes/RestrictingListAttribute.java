package fi.solita.utils.query.attributes;

import java.util.List;

import org.hibernate.metamodel.model.domain.ListPersistentAttribute;
import org.hibernate.query.sqm.SqmPathSource;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ListAttribute;

class RestrictingListAttribute<E, R, A extends Attribute<E, List<R>> & Bindable<R>> extends PluralAttributeProxy<E, List<R>, R, A> implements ListAttribute<E,R>, RestrictingAttribute, ListPersistentAttribute<E,R> {
    
    private final List<? extends Attribute<?, ?>> attributes;

    @SuppressWarnings("unchecked")
    RestrictingListAttribute(ListAttribute<E,R> attribute, List<Attribute<?,?>> restrictionChain) {
        super((A)attribute, CollectionType.LIST, attribute.getElementType());
        attributes = restrictionChain;
    }

    @Override
    public List<? extends Attribute<?, ?>> getRestrictionChain() {
        return attributes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<Integer> getIndexPathSource() {
        return proxyTarget == null ? null :  ((ListPersistentAttribute<E,E>)(Object)proxyTarget).getIndexPathSource();
    }
}
