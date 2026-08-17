package fi.solita.utils.query.backend;

import java.util.Optional;

public interface Type<T> {

    public static final class OptionalType<T> implements Type<Optional<T>> {
        public final Type<T> type;

        public OptionalType(Type<T> type) {
            this.type = type;
        }
    }
}
