package fi.solita.utils.query;

import java.io.Serializable;

public interface Id<T> extends EntityRepresentation<T>, Serializable {
    Class<T> getOwningClass();
}
