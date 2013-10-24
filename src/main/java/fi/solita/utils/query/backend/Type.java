package fi.solita.utils.query.backend;

import fi.solita.utils.functional.Option;

public interface Type<T> {

    public static final class Optional<T> implements Type<Option<T>> {
        public final Type<T> type;

        public Optional(Type<T> type) {
            this.type = type;
        }
    }
}
