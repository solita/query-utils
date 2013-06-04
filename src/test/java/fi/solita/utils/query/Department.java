package fi.solita.utils.query;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Access(AccessType.FIELD)
public class Department implements IEntity, Identifiable<Department.ID>, Removable {

    @Embeddable
    public static class ID extends LongId<Department> {
        ID() {
            // for Hibernate
        }
    }
    
    private ID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int number;
    
    @ElementCollection
    @OrderColumn(name = "index")
    private List<Integer> numbers;

    @OneToMany(mappedBy = "department")
    @OrderColumn(name = "index")
    private List<Employee> employees;

    @ElementCollection
    @OrderColumn(name = "index")
    private List<Report> reports;
    
    public Department() {
        this("");
    }

    public Department(String name) {
        this(name, 0);
    }

    public Department(String name, int number) {
        this.name = name;
        this.number = number;
    }
    
    public Department(List<Integer> numbers) {
        this("", 0);
        this.numbers = numbers;
    }
    
    public Department(String name, List<Report> reports) {
        this(name, 0);
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

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
