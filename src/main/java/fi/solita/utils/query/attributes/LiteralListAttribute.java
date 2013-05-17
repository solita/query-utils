package fi.solita.utils.query.attributes;

import java.util.List;

import javax.persistence.metamodel.ListAttribute;


public final class LiteralListAttribute<X, Y> extends PluralAttributeProxy<X, List<Y>, Y> implements ListAttribute<X, Y>, LiteralAttribute<X,List<Y>> {

    private final List<Y> value;

    @Override
    public List<Y> getValue() {
        return value;
    }

    public LiteralListAttribute(List<Y> value) {
        super(null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<Y>> getJavaType() {
        return (Class<List<Y>>) value.getClass();
    }
}