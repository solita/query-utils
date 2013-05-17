package fi.solita.utils.query;

import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;

@Entity
@Access(AccessType.FIELD)
public class Municipality implements IEntity, Identifiable<Municipality.ID> {

    public static class ID extends LongId<Municipality> {
        ID() {
            // for Hibernate
        }
    }

    private ID id;

    @OneToMany(mappedBy = "municipality")
    private Set<Employee> employees;

    @Override
    @javax.persistence.Id
    @GeneratedValue(generator = "IdGenerator")
    @GenericGenerator(name = "IdGenerator", strategy = "fi.solita.utils.query.IdGenerator")
    @Access(AccessType.PROPERTY)
    public ID getId() {
        return id;
    }

    void setId(ID id) {
        this.id = id;
    }

}
