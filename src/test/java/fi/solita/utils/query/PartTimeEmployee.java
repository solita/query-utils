package fi.solita.utils.query;

import javax.persistence.Entity;

@Entity
public class PartTimeEmployee extends Employee {

    public PartTimeEmployee(String name, Department department) {
        super(name, department);
    }

}
