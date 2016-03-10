package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.mkString;
import static fi.solita.utils.functional.Functional.map;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Predicate;
import fi.solita.utils.functional.Transformer;

/**
 * 
 * Do not use this class directly.
 *
 * An attribute containing other attributes to be inner joined to the
 * attribute itself, thus changing the resulting type of the original attribute.
 */
public interface JoiningAttribute {
    public static Predicate<Attribute<?,?>> illegalContainedAttribute = new Predicate<Attribute<?,?>>() {
        @Override
        public boolean accept(Attribute<?, ?> candidate) {
            return candidate instanceof PseudoAttribute ||
                   candidate instanceof AdditionalQueryPerformingAttribute ||
                   candidate instanceof RestrictingAttribute;
        }
    };

    public abstract List<? extends Attribute<?, ?>> getAttributes();
    
    public static class Constructors {
        public static <E,R> SingularAttribute<E,R> singular(SingularAttribute<?,?>... attrs) {
            return new JoiningSingularAttribute<E, R>(attrs);
        }
        
    
        
        public static <E,Y,Y2,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
        CollectionAttribute<E,Y2> collection(A1 a1, A2 a2) {
            return new JoiningCollectionAttribute<E,Y2,CollectionAttribute<E,Y2>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2));
        }
        
        public static <E,Y,Y2,Y3,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
        CollectionAttribute<E,Y3> collection(A1 a1, A2 a2, A3 a3) {
            return new JoiningCollectionAttribute<E,Y3,CollectionAttribute<E,Y3>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3));
        }
        
        public static <E,Y,Y2,Y3,Y4,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
        CollectionAttribute<E,Y4> collection(A1 a1, A2 a2, A3 a3, A4 a4) {
            return new JoiningCollectionAttribute<E,Y4,CollectionAttribute<E,Y4>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
        CollectionAttribute<E,Y5> collection(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return new JoiningCollectionAttribute<E,Y5,CollectionAttribute<E,Y5>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,Y6,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
        CollectionAttribute<E,Y6> collection(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
            return new JoiningCollectionAttribute<E,Y6,CollectionAttribute<E,Y6>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5, (Attribute<?,?>)a6));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>, A7 extends Attribute<? super Y6,?> & Bindable<Y7>>
        CollectionAttribute<E,Y7> collection(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
            return new JoiningCollectionAttribute<E,Y7,CollectionAttribute<E,Y7>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5, (Attribute<?,?>)a6, (Attribute<?,?>)a7));
        }
        
        
    
        public static <E,Y,Y2,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
        SetAttribute<E,Y2> set(A1 a1, A2 a2) {
            return new JoiningSetAttribute<E,Y2,SetAttribute<E,Y2>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2));
        }
        
        public static <E,Y,Y2,Y3,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
        SetAttribute<E,Y3> set(A1 a1, A2 a2, A3 a3) {
            return new JoiningSetAttribute<E,Y3,SetAttribute<E,Y3>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3));
        }
        
        public static <E,Y,Y2,Y3,Y4,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
        SetAttribute<E,Y4> set(A1 a1, A2 a2, A3 a3, A4 a4) {
            return new JoiningSetAttribute<E,Y4,SetAttribute<E,Y4>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
        SetAttribute<E,Y5> set(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return new JoiningSetAttribute<E,Y5,SetAttribute<E,Y5>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,Y6,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
        SetAttribute<E,Y6> set(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
            return new JoiningSetAttribute<E,Y6,SetAttribute<E,Y6>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5, (Attribute<?,?>)a6));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>, A7 extends Attribute<? super Y6,?> & Bindable<Y7>>
        SetAttribute<E,Y7> set(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
            return new JoiningSetAttribute<E,Y7,SetAttribute<E,Y7>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5, (Attribute<?,?>)a6, (Attribute<?,?>)a7));
        }
        
        
        
        public static <E,Y,Y2,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
        ListAttribute<E,Y2> list(A1 a1, A2 a2) {
            return new JoiningListAttribute<E,Y2,ListAttribute<E,Y2>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2));
        }
        
        public static <E,Y,Y2,Y3,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
        ListAttribute<E,Y3> list(A1 a1, A2 a2, A3 a3) {
            return new JoiningListAttribute<E,Y3,ListAttribute<E,Y3>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3));
        }
        
        public static <E,Y,Y2,Y3,Y4,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
        ListAttribute<E,Y4> list(A1 a1, A2 a2, A3 a3, A4 a4) {
            return new JoiningListAttribute<E,Y4,ListAttribute<E,Y4>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
        ListAttribute<E,Y5> list(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return new JoiningListAttribute<E,Y5,ListAttribute<E,Y5>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,Y6,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>>
        ListAttribute<E,Y6> list(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
            return new JoiningListAttribute<E,Y6,ListAttribute<E,Y6>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5, (Attribute<?,?>)a6));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,Y6,Y7,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>, A6 extends Attribute<? super Y5,?> & Bindable<Y6>, A7 extends Attribute<? super Y6,?> & Bindable<Y7>>
        ListAttribute<E,Y7> list(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
            return new JoiningListAttribute<E,Y7,ListAttribute<E,Y7>>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5, (Attribute<?,?>)a6, (Attribute<?,?>)a7));
        }
        
        
        
        static <A extends Attribute<?,?> & JoiningAttribute> String joiningAttributeToString(A a) {
            return a.getClass().getSimpleName() + "(" + mkString("->", map(new Transformer<Attribute<?,?>,String>() {
                @Override
                public String transform(Attribute<?,?> source) {
                    return source.getDeclaringType() == null ? "?" : source.getDeclaringType().getJavaType().getSimpleName() + "." + source.getName();
                }
            }, a.getAttributes())) + ")";
        }
    }
}
