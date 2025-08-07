package fi.solita.utils.query.attributes;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

import fi.solita.utils.query.IEntity;


class SelfAttribute<X, T> extends SingularAttributeProxy<X, T> implements PseudoAttribute {

    public SelfAttribute() {
        super(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getJavaType() {
        // a bit of a hackish way...
        return (Class<T>) IEntity.class;
    }

    @Override
    public Expression<?> getSelectionForQuery(CriteriaBuilder cb, Path<?> currentSelection) {
        return currentSelection;
    }

    @Override
    public Object getValueToReplaceResult(Object resultFromDb) {
        return resultFromDb;
    }
}