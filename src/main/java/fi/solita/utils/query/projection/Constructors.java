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
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.codegen.ConstructorMeta_;

public class Constructors {
    
    static <E extends IEntity & Identifiable<?>> ConstructorMeta_<E,Id<E>,Id<E>> id() {
        return new IdProjection<E>();
    }

    static <E extends IEntity, T> ConstructorMeta_<E,T,T> value(SingularAttribute<? super E, T> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    static <E extends IEntity, T> ConstructorMeta_<E,T,T> value(PluralAttribute<? super E, T, ?> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    static <E extends IEntity, LEFT, RIGHT> ConstructorMeta_<E,Pair<LEFT,RIGHT>,Tuple2<LEFT,RIGHT>> pair(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
        return new PairProjection<E,LEFT,RIGHT>(left, right);
    }
    
    static <E extends IEntity, T extends Tuple> ConstructorMeta_<E,T,T> tuple(Attribute<? super E,?>... attributes) {
        return new TupleProjection<E,T>(attributes);
    }

    static final class IdProjection<E extends IEntity> extends ConstructorMeta_.F1<E,Id<E>, Id<E>> {
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
    }
    
    private static final class PairProjection<E extends IEntity,LEFT,RIGHT> extends ConstructorMeta_.F2<E,LEFT,RIGHT,Pair<LEFT,RIGHT>> {
        private final Attribute<? super E, LEFT> left;
        private final Attribute<? super E, RIGHT> right;

        public PairProjection(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
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
    }
    
    private static final class TupleProjection<E extends IEntity, T extends Tuple> implements ConstructorMeta_<E,T,T> {
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
    }
    
    private static final class ValueAttributeProjection<E extends IEntity,R> extends ConstructorMeta_.F1<E,R,R> {
        private final Attribute<? super E,R> attribute;

        public ValueAttributeProjection(Attribute<? super E,R> attribute) {
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
    }
}
