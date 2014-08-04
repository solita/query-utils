package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.repeat;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.functional.Tuple2;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.codegen.MetaJpaConstructor;

public class Constructors {
    
    public static interface TransparentProjection {
    }
    
    static <E extends Identifiable<?>> MetaJpaConstructor<E,Id<E>,Id<E>> id() {
        return new IdProjection<E>();
    }

    static <E, T> MetaJpaConstructor<E,T,T> value(SingularAttribute<? super E, T> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    static <E, T> MetaJpaConstructor<E,T,T> value(PluralAttribute<? super E, T, ?> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    static <E, LEFT, RIGHT> MetaJpaConstructor<E,Pair<LEFT,RIGHT>,Tuple2<LEFT,RIGHT>> pair(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
        return new PairProjection<E,LEFT,RIGHT>(left, right);
    }
    
    static <E, T extends Tuple> MetaJpaConstructor<E,T,T> tuple(Attribute<? super E,?>... attributes) {
        return new TupleProjection<E,T>(attributes);
    }

    static final class IdProjection<E extends Identifiable<?>> extends MetaJpaConstructor.C1<E,Id<E>, Id<E>> {
        public IdProjection() {
            super(null, (Class<?>[])null);
        }

        @Override
        public List<Attribute<?, ?>> getParameters() {
            // There is actually one attribute, we just don't know the instance here...
            return newList(new Attribute<?,?>[]{null});
        }

        @Override
        public List<Integer> getIndexesOfIdWrappingParameters() {
            return Collections.emptyList();
        }

        @Override
        public Constructor<Id<E>> getMember() {
            throw new UnsupportedOperationException("Shouldn't be here");
        }

        @Override
        public Id<E> apply(Id<E> t) {
            return t;
        }
        
        @Override
        public String toString() {
            return Helper.className.apply(getClass()) + "()";
        }
    }
    
    private static final class PairProjection<E,LEFT,RIGHT> extends MetaJpaConstructor.C2<E,LEFT,RIGHT,Pair<LEFT,RIGHT>> {
        private final Attribute<? super E, LEFT> left;
        private final Attribute<? super E, RIGHT> right;

        public PairProjection(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
            super(null, (Class<?>[])null);
            this.left = left;
            this.right = right;
        }

        @Override
        public List<Attribute<?, ?>> getParameters() {
            return Arrays.<Attribute<?, ?>>asList(left, right);
        }

        @Override
        public List<Integer> getIndexesOfIdWrappingParameters() {
            return Collections.emptyList();
        }
        
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.<Class<?>>asList(Object.class, Object.class);
        }

        @Override
        public Constructor<Pair<LEFT, RIGHT>> getMember() {
            throw new UnsupportedOperationException("Shouldn't be here");
        }

        @Override
        public Pair<LEFT, RIGHT> apply(LEFT left, RIGHT right) {
            return Pair.of(left, right);
        }
        
        @Override
        public String toString() {
            return MetaJpaConstructor.Helper.toString(this);
        }
    }
    
    private static final class TupleProjection<E, T extends Tuple> implements MetaJpaConstructor<E,T,T> {
        private final List<Attribute<?, ?>> attributes;

        public TupleProjection(Attribute<?, ?>... attributes) {
            this.attributes = Arrays.asList(attributes);
        }

        @Override
        public List<Attribute<?, ?>> getParameters() {
            return attributes;
        }

        @Override
        public List<Integer> getIndexesOfIdWrappingParameters() {
            return Collections.emptyList();
        }

        @Override
        public T apply(T t) {
            return t;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return (List<Class<?>>)(Object)Collections.newList(repeat(Object.class, attributes.size()));
        }
        
        @Override
        public Constructor<T> getMember() {
            throw new UnsupportedOperationException("Shouldn't be here");
        }
        

        @Override
        public String toString() {
            return MetaJpaConstructor.Helper.toString(this);
        }
    }
    
    private static final class ValueAttributeProjection<E,R> extends MetaJpaConstructor.C1<E,R,R> implements TransparentProjection {
        private final Attribute<? super E,R> attribute;

        public ValueAttributeProjection(Attribute<? super E,R> attribute) {
            super(null, (Class<?>[])null);
            this.attribute = attribute;
        }

        @Override
        public List<Attribute<?, ?>> getParameters() {
            return Arrays.<Attribute<?, ?>>asList(attribute);
        }

        @Override
        public List<Integer> getIndexesOfIdWrappingParameters() {
            return Collections.emptyList();
        }
        
        @Override
        public List<Class<?>> getConstructorParameterTypes() {
            return Arrays.<Class<?>>asList(attribute.getJavaType());
        }

        @Override
        public Constructor<R> getMember() {
            throw new UnsupportedOperationException("Shouldn't be here");
        }

        @Override
        public R apply(R t) {
            return t;
        }
        
        @Override
        public String toString() {
            return MetaJpaConstructor.Helper.toString(this);
        }
    }
}
