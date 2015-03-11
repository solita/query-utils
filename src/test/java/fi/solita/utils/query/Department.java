package fi.solita.utils.query;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Tuplizer;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.backend.hibernate.OptionAwarePojoEntityTuplizer;

@javax.persistence.Entity
@Access(javax.persistence.AccessType.FIELD)
@Tuplizer(impl = OptionAwarePojoEntityTuplizer.class)
public class Department implements IEntity<Department>, Identifiable<Department.ID>, Removable {

    @Embeddable
    public static class ID extends LongId<Department> {
        ID() {
            // for Hibernate
        }
    }
    
    @Transient
    private ID id;

    @Column(nullable = false)
    private String mandatoryDepName;

    @Column(nullable = false)
    private int mandatoryNumber;
    
    @org.hibernate.annotations.Type(type = "org.hibernate.type.IntegerType")
    private Option<Integer> optionSize;
    
    @org.hibernate.annotations.Type(type = "fi.solita.utils.query.Money$MoneyType")
    private Money optionalBudget;
    
    @ManyToOne 
    private Employee optionalManager;
    
    @ManyToOne
    private Municipality optionalDepMunicipality;
    
    @ManyToOne(optional = false) 
    private Department mandatorySelfReference;
    
    @ElementCollection
    @OrderColumn(name = "index")
    private List<Integer> numbers;

    @OneToMany(mappedBy = "mandatoryDepartment")
    @OrderColumn(name = "index")
    private List<Employee> employees;

    @ElementCollection
    @OrderColumn(name = "index")
    private List<Report> reports;
    
    @Embedded
    @Basic(optional = false)
    private Report mandatoryReport;
    
    @Embedded
    @AttributeOverride(name="year", column = @Column(name = "year2"))
    private Report optionalDepReport;
    
    public Department() {
        this("");
    }

    public Department(String mandatoryName) {
        this(mandatoryName, 0);
    }
    
    public Department(String name, Municipality municipality) {
        this(name);
        this.optionalDepMunicipality = municipality;
    }
    
    public Department(Municipality municipality) {
        this();
        this.optionalDepMunicipality = municipality;
    }
    
    public Department(Money budget) {
        this("");
        this.optionalBudget = budget;
    }
    
    public Department(Report report) {
        this("", 0, report);
        this.optionalDepReport = report;
    }
    
    public Department(String mandatoryName, Employee manager) {
        this(mandatoryName);
        this.optionalManager = manager;
    }
    
    public Department(String mandatoryName, int number) {
        this(mandatoryName, number, new Report(0));
    }

    public Department(String mandatoryName, int number, Report report) {
        this.mandatoryDepName = mandatoryName;
        this.mandatoryNumber = number;
        this.mandatoryReport = report;
        this.mandatorySelfReference = this;
    }
    
    public Department(List<Integer> numbers) {
        this("", 0);
        this.numbers = numbers;
    }
    
    public Department(String mandatoryName, List<Report> reports) {
        this(mandatoryName, 0);
        this.reports = reports;
    }

    @Override
    @javax.persistence.Id
    @GeneratedValue(generator = "IdGenerator")
    @GenericGenerator(name = "IdGenerator", strategy = "fi.solita.utils.query.IdGenerator")
    @Access(javax.persistence.AccessType.PROPERTY)
    public ID getId() {
        return id;
    }

    void setId(ID id) {
        this.id = id;
    }

    public String getMandatoryName() {
        return mandatoryDepName;
    }

    public int getNumber() {
        return mandatoryNumber;
    }
    
    public Money getBudget() {
        return optionalBudget;
    }
    
    public Option<Integer> getOptionSize() {
        return optionSize;
    }
    
    public void setOptionSize(Option<Integer> optionSize) {
        this.optionSize = optionSize;
    }
}
