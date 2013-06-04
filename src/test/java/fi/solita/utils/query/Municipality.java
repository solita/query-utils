package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newSet;

import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Access(AccessType.FIELD)
public class Municipality implements IEntity, Identifiable<Municipality.ID> {

    @Embeddable
    public static class ID extends LongId<Municipality> {
        ID() {
            // for Hibernate
        }
    }

    private ID id;
    
    @ElementCollection
    private Set<Integer> postalCodes = newSet();

    @OneToMany(mappedBy = "municipality")
    private Set<Employee> employees;
    
    @ElementCollection
    private Set<Report> reports;
    
    @Embedded
    @Basic(optional = false)
    private Report report;

    public Municipality() {
        this(new Report(123));
    }
    
    public Municipality(Report report) {
        this.report = report;
    }
    
    public Municipality(Set<Integer> postalCodes) {
        this(new Report(123));
        this.postalCodes = postalCodes;
    }
    
    public Municipality(Set<Report> reports, boolean _) {
        this(new Report(123));
        this.reports = reports;
    }
    
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
