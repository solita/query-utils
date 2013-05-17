package fi.solita.utils.query.attributes;

import javax.persistence.metamodel.Attribute;

public interface LiteralAttribute<X,T> extends Attribute<X, T> {

    static final String QUERY_PLACEHOLDER = "<>";

    T getValue();
}
