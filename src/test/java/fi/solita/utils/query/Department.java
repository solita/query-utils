package fi.solita.utils.query;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.GenericGenerator;

import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.Removable;

@Entity
@Access(AccessType.FIELD)
public class Department implements IEntity, Identifiable<Department.ID>, Removable {

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

    @OneToMany(mappedBy = "department")
    @OrderColumn(name = "index")
    private List<Employee> employees;

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
