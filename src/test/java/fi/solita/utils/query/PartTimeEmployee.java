package fi.solita.utils.query;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class PartTimeEmployee extends Employee {

    public PartTimeEmployee(String name, Department department) {
        super(name, department);
    }

}
