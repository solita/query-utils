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

    @Transient
    private ID id;
    
    @ElementCollection
    private Set<Integer> postalCodes = newSet();
    
    private String optionalArea;

    @OneToMany(mappedBy = "optionalMunicipality")
    private Set<Employee> employees;
    
    @OneToMany(mappedBy = "optionalDepMunicipality")
    private Set<Department> deps;
    
    @ElementCollection
    private Set<Report> reports;
    
    @Embedded
    @Basic(optional = false)
    private Report mandatoryReport;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Municipality mandatorySelfReference;

    public Municipality() {
        this(new Report(123));
    }
    
    public Municipality(String area) {
        this(new Report(123));
        this.optionalArea = area;
    }
    
    public Municipality(Report report) {
        this.mandatoryReport = report;
        this.mandatorySelfReference = this;
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

    public String getOptionalArea() {
        return optionalArea;
    }
}
