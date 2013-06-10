package fi.solita.utils.query;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class PartTimeEmployee extends Employee {

    public PartTimeEmployee(String name, Department department) {
        super(name, department);
    }

}
