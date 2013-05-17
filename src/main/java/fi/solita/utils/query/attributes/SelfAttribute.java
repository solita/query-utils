package fi.solita.utils.query.attributes;

import fi.solita.utils.query.IEntity;


/**
 * Pseudo attribute to select the entity self to a projection
 *
 * @author Jyri-Matti Lähteenmäki / Solita Oy
 *
 */
public final class SelfAttribute<X, T> extends SingularAttributeProxy<X, T> {

    public SelfAttribute() {
        super(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getJavaType() {
        // a bit of a hackish way...
        return (Class<T>) IEntity.class;
    }
}