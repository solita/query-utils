package fi.solita.utils.query;

import javax.persistence.*;

import org.hibernate.annotations.*;

import fi.solita.utils.functional.*;


@javax.persistence.Entity
@Access(javax.persistence.AccessType.FIELD)
public class Employee implements IEntity, Identifiable<Employee.ID> {

    @Embeddable
    public static class ID extends LongId<Employee> {
        ID() {
            // for Hibernate
        }
    }

    @Transient
    private ID id;

    @Column(nullable = false)
    private String mandatoryName;

    @org.hibernate.annotations.Type(type = "fi.solita.utils.query.Money$MoneyType")
    private Money optionalSalary;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Department mandatoryDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Municipality optionalMunicipality;
    
    @Embedded
    @Basic(optional = true)
    private Report optionalReport;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Department optionalDepartment;
    
    Employee() {
        // for hibernate
    }

    public Employee(String name, Department department) {
        this(name, null, department);
    }

    public Employee(String name, Money salary, Department department) {
        this.mandatoryName = name;
        this.optionalSalary = salary;
        this.mandatoryDepartment = department;
    }

    public Employee(String name, Department department, Municipality municipality) {
        this(name, department);
        this.optionalMunicipality = municipality;
    }
    
    public Employee(String name, Department department, Report report) {
        this(name, department);
        this.optionalReport = report;
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

    public String getName() {
        return mandatoryName;
    }

    public Option<Money> getOptionalSalary() {
        return Option.of(optionalSalary);
    }
    
    public Employee setOptionalSalary(Money optionalSalary) {
        this.optionalSalary = optionalSalary;
        return this;
    }
    
    public Employee setOptionalDepartment(Department optionalDepartment) {
        this.optionalDepartment = optionalDepartment;
        return this;
    }
}
