package fi.solita.utils.query;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Tuplizer;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query.backend.hibernate.OptionAwarePojoEntityTuplizer;
@Tuplizer(impl = OptionAwarePojoEntityTuplizer.class)
import jakarta.persistence.Access;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;


@jakarta.persistence.Entity
@Access(jakarta.persistence.AccessType.FIELD)
public class Employee implements IEntity<Employee>, Identifiable<Employee.ID> {

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
    
    @org.hibernate.annotations.Type(type = "org.hibernate.type.IntegerType")
    private Option<Integer> optionAge;

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
    @jakarta.persistence.EmbeddedId
    @GeneratedValue(generator = "IdGenerator")
    @GenericGenerator(name = "IdGenerator", strategy = "fi.solita.utils.query.IdGenerator")
    @Access(jakarta.persistence.AccessType.PROPERTY)
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
    
    public Option<Integer> getOptionAge() {
        return optionAge;
    }
    
    public Employee setOptionalSalary(Money optionalSalary) {
        this.optionalSalary = optionalSalary;
        return this;
    }
    
    public Employee setOptionalDepartment(Department optionalDepartment) {
        this.optionalDepartment = optionalDepartment;
        return this;
    }
    
    public void setOptionAge(Option<Integer> optionAge) {
        this.optionAge = optionAge;
    }
}
