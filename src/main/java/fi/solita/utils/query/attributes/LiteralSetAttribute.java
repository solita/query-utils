package fi.solita.utils.query.attributes;

import java.util.Set;

import javax.persistence.metamodel.SetAttribute;


public final class LiteralSetAttribute<X, Y> extends PluralAttributeProxy<X, Set<Y>, Y> implements SetAttribute<X, Y>, LiteralAttribute<X,Set<Y>> {

    private final Set<Y> value;

    @Override
    public Set<Y> getValue() {
        return value;
    }

    public LiteralSetAttribute(Set<Y> value) {
        super(null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Set<Y>> getJavaType() {
        return (Class<Set<Y>>) value.getClass();
    }
}