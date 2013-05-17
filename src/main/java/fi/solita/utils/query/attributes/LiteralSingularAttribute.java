package fi.solita.utils.query.attributes;

/**
 * Pseudo attribute to allow user to supply projections with literal values
 * that don't go through the database.
 *
 * @author Jyri-Matti Lähteenmäki / Solita Oy
 *
 */
public final class LiteralSingularAttribute<X, T> extends SingularAttributeProxy<X, T> implements LiteralAttribute<X,T> {
    private final T value;

    @Override
    public T getValue() {
        return value;
    }

    public LiteralSingularAttribute(T value) {
        super(null);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getJavaType() {
        return (Class<T>) value.getClass();
    }
}