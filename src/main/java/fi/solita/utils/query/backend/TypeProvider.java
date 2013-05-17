package fi.solita.utils.query.backend;

import java.io.Serializable;

import fi.solita.utils.query.Identifiable;

public interface TypeProvider {
    <ID extends Serializable, T extends Identifiable<ID>> Type<ID> idType(Class<T> entityType);
    <T> Type<T> type(Class<T> clazz);
}
