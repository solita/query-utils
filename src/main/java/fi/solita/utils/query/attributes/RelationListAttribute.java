package fi.solita.utils.query.attributes;

import java.util.List;

import org.hibernate.metamodel.model.domain.ListPersistentAttribute;
import org.hibernate.query.sqm.SqmPathSource;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.Type;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.meta.MetaJpaConstructor;

class RelationListAttribute<E, R, A extends Attribute<E, List<R>> & Bindable<R>> extends PluralAttributeProxy<E, List<R>, R, A> implements ListAttribute<E,R>, AdditionalQueryPerformingAttribute, ListPersistentAttribute<E,R> {
    private final MetaJpaConstructor<? extends IEntity<?>, R, ?> constructor;

    @SuppressWarnings("unchecked")
    public <E2 extends IEntity<?>> RelationListAttribute(ListAttribute<? super E, ? super E2> attribute, MetaJpaConstructor<? super E2, R, ?> constructor) {
        super((A)(Object)attribute, CollectionType.LIST, (Type<R>)(Object)attribute.getElementType());
        this.constructor = (MetaJpaConstructor<? extends IEntity<?>, R, ?>) constructor;
    }

    @Override
    public MetaJpaConstructor<? extends IEntity<?>, R, ?> getConstructor() {
        return constructor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SqmPathSource<Integer> getIndexPathSource() {
        return proxyTarget == null ? null :  ((ListPersistentAttribute<E,E>)(Object)proxyTarget).getIndexPathSource();
    }
}