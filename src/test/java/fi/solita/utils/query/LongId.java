package fi.solita.utils.query;

import java.io.Serializable;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Access(AccessType.FIELD)
class LongId<T> implements Id<T>, Serializable {

    long id;

    LongId() {
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().isInstance(obj) && getOwningClass().equals(((LongId<?>) obj).getOwningClass()) && id == ((LongId<?>)obj).id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }
    
    LongId<T> setId(long id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return getClass().getDeclaringClass().getSimpleName() + "[" + Long.toString(id) + "]";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getOwningClass() {
        return (Class<T>) getClass().getDeclaringClass();
    }

    @Override
    public int compareTo(Id<T> o) {
        return o instanceof LongId ? Long.valueOf(id).compareTo(((LongId<?>)o).id) : -1;
    }
}