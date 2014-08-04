package fi.solita.utils.query.attributes;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.mkString;
import static fi.solita.utils.functional.FunctionalImpl.map;

import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Transformer;

/**
 * 
 * Do not use this class directly.
 *
 * An attribute containing other attributes to be inner joined to the
 * attribute itself, thus changing the resulting type of the original attribute.
 */
public interface JoiningAttribute {
    public abstract List<? extends Attribute<?, ?>> getAttributes();
    
    public static class Constructors {
        public static <E,R> SingularAttribute<E,R> singular(SingularAttribute<?,?>... attrs) {
            return new JoiningSingularAttribute<E, R>(attrs);
        }
        
    
        
        public static <E,Y,Y2,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
        CollectionAttribute<E,R> collection(A1 a1, A2 a2) {
            return new JoiningCollectionAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2));
        }
        
        public static <E,Y,Y2,Y3,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
        CollectionAttribute<E,R> collection(A1 a1, A2 a2, A3 a3) {
            return new JoiningCollectionAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3));
        }
        
        public static <E,Y,Y2,Y3,Y4,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
        CollectionAttribute<E,R> collection(A1 a1, A2 a2, A3 a3, A4 a4) {
            return new JoiningCollectionAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
        CollectionAttribute<E,R> collection(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return new JoiningCollectionAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5));
        }
        
        
    
        public static <E,Y,Y2,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
        SetAttribute<E,R> set(A1 a1, A2 a2) {
            return new JoiningSetAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2));
        }
        
        public static <E,Y,Y2,Y3,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
        SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3) {
            return new JoiningSetAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3));
        }
        
        public static <E,Y,Y2,Y3,Y4,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
        SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, A4 a4) {
            return new JoiningSetAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
        SetAttribute<E,R> set(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return new JoiningSetAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5));
        }
        
        
        
        public static <E,Y,Y2,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>>
        ListAttribute<E,R> list(A1 a1, A2 a2) {
            return new JoiningListAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2));
        }
        
        public static <E,Y,Y2,Y3,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>>
        ListAttribute<E,R> list(A1 a1, A2 a2, A3 a3) {
            return new JoiningListAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3));
        }
        
        public static <E,Y,Y2,Y3,Y4,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>>
        ListAttribute<E,R> list(A1 a1, A2 a2, A3 a3, A4 a4) {
            return new JoiningListAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4));
        }
        
        public static <E,Y,Y2,Y3,Y4,Y5,R,A1 extends Attribute<E,?> & Bindable<Y>, A2 extends Attribute<? super Y,?> & Bindable<Y2>, A3 extends Attribute<? super Y2,?> & Bindable<Y3>, A4 extends Attribute<? super Y3,?> & Bindable<Y4>, A5 extends Attribute<? super Y4,?> & Bindable<Y5>>
        ListAttribute<E,R> list(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return new JoiningListAttribute<E,R>(newList((Attribute<?,?>)a1, (Attribute<?,?>)a2, (Attribute<?,?>)a3, (Attribute<?,?>)a4, (Attribute<?,?>)a5));
        }
        
        
        
        static <A extends Attribute<?,?> & JoiningAttribute> String joiningAttributeToString(A a) {
            return a.getClass().getSimpleName() + "(" + mkString("->", map(a.getAttributes(), new Transformer<Attribute<?,?>,String>() {
                @Override
                public String transform(Attribute<?,?> source) {
                    return source.getDeclaringType() == null ? "?" : source.getDeclaringType().getJavaType().getSimpleName() + "." + source.getName();
                }
            })) + ")";
        }
    }
}
