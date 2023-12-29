package fi.solita.utils.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fi.solita.utils.functional.Option;

public class Dto {
    // some placeholder to separate metaconstructors a bit more clearly
    public enum ID {a}
    public enum VALUE {a}
    public enum ENTITY {a}
    public enum EMBEDDABLE {a}
    public enum OPTIONAL_ID {a}
    public enum OPTIONAL_VALUE {a}
    public enum OPTIONAL_ENTITY {a}
    public enum OPTIONAL_EMBEDDABLE {a}
    public enum COLLECTION_OF_IDS {a}
    public enum COLLECTION_OF_VALUES {a}
    public enum COLLECTION_OF_ENTITIES {a}
    public enum COLLECTION_OF_EMBEDDABLES {a}
    public enum SET_OF_IDS {a}
    public enum SET_OF_VALUES {a}
    public enum SET_OF_ENTITIES {a}
    public enum SET_OF_EMBEDDABLES {a}
    public enum LIST_OF_IDS {a}
    public enum LIST_OF_VALUES {a}
    public enum LIST_OF_ENTITIES {a}
    public enum LIST_OF_EMBEDDABLES {a}
    
    public final Object value;
    
    public Dto(ID x, Id<?> id) {
        this.value = id;
    }
    public Dto(VALUE x, Object val) {
        this.value = val;
    }
    public Dto(ENTITY x, IEntity<?> val) {
        this.value = val;
    }
    public Dto(EMBEDDABLE x, Report val) {
        this.value = val;
    }
    public Dto(OPTIONAL_ID x, Option<Id<?>> val) {
        this.value = val;
    }
    public Dto(OPTIONAL_ENTITY x, Option<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(OPTIONAL_VALUE x, Option<?> val) {
        this.value = val;
    }
    public Dto(OPTIONAL_EMBEDDABLE x, Option<Report> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_IDS x, Collection<Id<?>> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_VALUES x, Collection<?> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_ENTITIES x, Collection<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_EMBEDDABLES x, Collection<Report> val) {
        this.value = val;
    }
    public Dto(SET_OF_IDS x, Set<Id<?>> val) {
        this.value = val;
    }
    public Dto(SET_OF_VALUES x, Set<?> val) {
        this.value = val;
    }
    public Dto(SET_OF_ENTITIES x, Set<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(SET_OF_EMBEDDABLES x, Set<Report> val) {
        this.value = val;
    }
    public Dto(LIST_OF_IDS x, List<Id<?>> val) {
        this.value = val;
    }
    public Dto(LIST_OF_VALUES x, List<?> val) {
        this.value = val;
    }
    public Dto(LIST_OF_ENTITIES x, List<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(LIST_OF_EMBEDDABLES x, List<Report> val) {
        this.value = val;
    }
    
    public static Dto projectionMethod(VALUE x, Object val) {
        return new Dto(x, val);
    }
}