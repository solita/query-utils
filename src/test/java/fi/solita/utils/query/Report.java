package fi.solita.utils.query;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class Report {
    private Integer year1;
    
    Report() {
        // for hibernate
    }
    
    public Report(Integer year) {
        this.year1 = year;
    }
    
    public Integer getYear() {
        return year1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((year1 == null) ? 0 : year1.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Report other = (Report) obj;
        if (year1 == null) {
            if (other.year1 != null)
                return false;
        } else if (!year1.equals(other.year1))
            return false;
        return true;
    }
    
    
}