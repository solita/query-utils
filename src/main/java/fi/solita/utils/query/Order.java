package fi.solita.utils.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jakarta.persistence.metamodel.SingularAttribute;

public class Order<X,T> implements Iterable<Order<X,T>> {

    public enum Direction {
        ASC,DESC
    }

    public final Order<X,T> desc;

    private final Direction direction;
    private final SingularAttribute<X, T> attribute;

    private Order(SingularAttribute<X, T> attribute, Direction direction) {
        this.attribute = attribute;
        this.direction = direction;
        this.desc = direction == Direction.ASC ? new Order<X,T>(attribute, Direction.DESC) : this;
    }

    public static <X,T> Order<X,T> by(SingularAttribute<X, T> attribute) {
        return new Order<X,T>(attribute, Direction.ASC);
    }

    public static <X> List<Order<? super X,?>> of(Order<? super X,?> order) {
        return Collections.<Order<? super X,?>>singletonList(order);
    }

    public static <X> List<Order<? super X,?>> of(Order<? super X,?> order1, Order<? super X,?> order2) {
        return Arrays.<Order<? super X,?>>asList(order1, order2);
    }

    public static <X> List<Order<? super X,?>> of(Order<? super X,?> order1, Order<? super X,?> order2, Order<? super X,?> order3) {
        return Arrays.<Order<? super X,?>>asList(order1, order2, order3);
    }

    public static <X> List<Order<? super X,?>> of(Order<? super X,?> order1, Order<? super X,?> order2, Order<? super X,?> order3, Order<? super X,?> order4) {
        return Arrays.<Order<? super X,?>>asList(order1, order2, order3, order4);
    }

    public SingularAttribute<X, T> getAttribute() {
        return attribute;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public Iterator<Order<X, T>> iterator() {
        return Arrays.asList(this).iterator();
    }
}
