package fi.solita.utils.query;

import java.io.Serializable;

public interface Id<T> extends EntityRepresentation, Serializable {
    Class<T> getOwningClass();
}
