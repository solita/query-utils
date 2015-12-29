package fi.solita.utils.query;

import java.io.Serializable;

public interface Id<T> extends EntityRepresentation<T>, Serializable, Comparable<Id<T>> {
    Class<T> getOwningClass();
}
