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
    
    public Dto(ID _, Id<?> id) {
        this.value = id;
    }
    public Dto(VALUE _, Object val) {
        this.value = val;
    }
    public Dto(ENTITY _, IEntity<?> val) {
        this.value = val;
    }
    public Dto(EMBEDDABLE _, Report val) {
        this.value = val;
    }
    public Dto(OPTIONAL_ID _, Option<Id<?>> val) {
        this.value = val;
    }
    public Dto(OPTIONAL_ENTITY _, Option<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(OPTIONAL_VALUE _, Option<?> val) {
        this.value = val;
    }
    public Dto(OPTIONAL_EMBEDDABLE _, Option<Report> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_IDS _, Collection<Id<?>> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_VALUES _, Collection<?> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_ENTITIES _, Collection<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(COLLECTION_OF_EMBEDDABLES _, Collection<Report> val) {
        this.value = val;
    }
    public Dto(SET_OF_IDS _, Set<Id<?>> val) {
        this.value = val;
    }
    public Dto(SET_OF_VALUES _, Set<?> val) {
        this.value = val;
    }
    public Dto(SET_OF_ENTITIES _, Set<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(SET_OF_EMBEDDABLES _, Set<Report> val) {
        this.value = val;
    }
    public Dto(LIST_OF_IDS _, List<Id<?>> val) {
        this.value = val;
    }
    public Dto(LIST_OF_VALUES _, List<?> val) {
        this.value = val;
    }
    public Dto(LIST_OF_ENTITIES _, List<? extends IEntity<?>> val) {
        this.value = val;
    }
    public Dto(LIST_OF_EMBEDDABLES _, List<Report> val) {
        this.value = val;
    }
}