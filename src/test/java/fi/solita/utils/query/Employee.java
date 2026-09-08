package fi.solita.utils.query;

import java.util.Optional;

import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.JavaTypeRegistrations;
import org.hibernate.type.descriptor.java.IntegerJavaType;

import fi.solita.utils.query.backend.hibernate.OptionAwareDirectPropertyAccessor;
import jakarta.persistence.Access;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;


@jakarta.persistence.Entity
@Access(jakarta.persistence.AccessType.FIELD)
@JavaTypeRegistrations({
    @JavaTypeRegistration(javaType = Money.class, descriptorClass = MoneyType.class),
    @JavaTypeRegistration(javaType = Employee.ID.class, descriptorClass = EmployeeIdType.class)
})
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

    private Money optionalSalary;
    
    @AttributeAccessor(strategy = OptionAwareDirectPropertyAccessor.class)
    @JavaType(IntegerJavaType.class)
    private Optional<Integer> optionAge;

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
    @CustomIdGenerator
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

    public Optional<Money> getOptionalSalary() {
        return Optional.ofNullable(optionalSalary);
    }
    
    public Optional<Integer> getOptionAge() {
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
    
    public void setOptionAge(Optional<Integer> optionAge) {
        this.optionAge = optionAge;
    }
}
