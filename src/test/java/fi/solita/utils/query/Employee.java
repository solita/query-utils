package fi.solita.utils.query;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import fi.solita.utils.functional.*;


@Entity
@Access(AccessType.FIELD)
public class Employee implements IEntity, Identifiable<Employee.ID> {

    @Embeddable
    public static class ID extends LongId<Employee> {
        ID() {
            // for Hibernate
        }
    }

    private ID id;

    @Column(nullable = false)
    private String name;

    @org.hibernate.annotations.Type(type = "fi.solita.utils.query.Money$MoneyType")
    private Money salary;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    private Municipality municipality;
    
    @Embedded
    @Basic(optional = true)
    private Report report;
    
    Employee() {
        // for hibernate
    }

    public Employee(String name, Department department) {
        this(name, null, department);
    }

    public Employee(String name, Money salary, Department department) {
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

    public Employee(String name, Department department, Municipality municipality) {
        this(name, department);
        this.municipality = municipality;
    }
    
    public Employee(String name, Department department, Report report) {
        this(name, department);
        this.report = report;
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

    public String getName() {
        return name;
    }

    public Option<Money> getSalary() {
        return Option.of(salary);
    }
}
