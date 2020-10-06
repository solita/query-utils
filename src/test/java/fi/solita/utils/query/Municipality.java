package fi.solita.utils.query;

import static fi.solita.utils.functional.Collections.newMutableSet;

import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Access(AccessType.FIELD)
public class Municipality implements IEntity<Municipality>, Identifiable<Municipality.ID> {

    @Embeddable
    public static class ID extends LongId<Municipality> {
        ID() {
            // for Hibernate
        }
    }

    @Transient
    private ID id;
    
    @ElementCollection
    private Set<Integer> postalCodes = newMutableSet();
    
    private String optionalArea;

    @OneToMany(mappedBy = "optionalMunicipality")
    private Set<Employee> emps;
    
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
