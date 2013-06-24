package fi.solita.utils.query;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.*;

@javax.persistence.Entity
@Access(javax.persistence.AccessType.FIELD)
public class Department implements IEntity, Identifiable<Department.ID>, Removable {

    @Embeddable
    public static class ID extends LongId<Department> {
        ID() {
            // for Hibernate
        }
    }
    
    private ID id;

    @Column(nullable = false)
    private String mandatoryName;

    @Column(nullable = false)
    private int mandatoryNumber;
    
    @org.hibernate.annotations.Type(type = "fi.solita.utils.query.Money$MoneyType")
    private Money optionalBudget;
    
    @ManyToOne 
    private Employee optionalManager;
    
    @ManyToOne
    private Municipality optionalMunicipality;
    
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
    private Report optionalReport;
    
    public Department() {
        this("");
    }

    public Department(String mandatoryName) {
        this(mandatoryName, 0);
    }
    
    public Department(Municipality municipality) {
        this();
        this.optionalMunicipality = municipality;
    }
    
    public Department(Money budget) {
        this("");
        this.optionalBudget = budget;
    }
    
    public Department(Report report) {
        this("", 0, report);
        this.optionalReport = report;
    }
    
    public Department(String mandatoryName, Employee manager) {
        this(mandatoryName);
        this.optionalManager = manager;
    }
    
    public Department(String mandatoryName, int number) {
        this(mandatoryName, number, new Report(0));
    }

    public Department(String mandatoryName, int number, Report report) {
        this.mandatoryName = mandatoryName;
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
        return mandatoryName;
    }

    public int getNumber() {
        return mandatoryNumber;
    }
    
    public Money getBudget() {
        return optionalBudget;
    }
}
