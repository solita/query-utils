package fi.solita.utils.query;

public class Money implements Comparable<Money> {
    public final long euros;

    public Money(long euros) {
        this.euros = euros;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (euros ^ (euros >>> 32));
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
        Money other = (Money) obj;
        if (euros != other.euros)
            return false;
        return true;
    }
    
    @Override
    public int compareTo(Money o) {
        return Long.valueOf(euros).compareTo(o.euros);
    }
}