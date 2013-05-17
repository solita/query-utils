package fi.solita.utils.query;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import fi.solita.utils.functional.*;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;


@Entity
@Access(AccessType.FIELD)
public class Employee implements IEntity, Identifiable<Employee.ID> {

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
