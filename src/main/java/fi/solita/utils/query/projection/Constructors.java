package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.repeat;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import javax.persistence.metamodel.Attribute;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.codegen.ConstructorMeta_;

class Constructors {

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
    
    static final class PairProjection<E extends IEntity,LEFT,RIGHT> extends ConstructorMeta_.F2<E,LEFT,RIGHT,Pair<LEFT,RIGHT>> {
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
    
    static final class TupleProjection<E extends IEntity, T extends Tuple> implements ConstructorMeta_<E,T,T> {
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
    
    static final class ValueAttributeProjection<E extends IEntity,R> extends ConstructorMeta_.F1<E,R,R> {
        private final Attribute<? super E,R> attribute;

        public ValueAttributeProjection(Attribute<? super E,R> attribute) {
            this.attribute = attribute;
        }

        public Attribute<?,R> getAttribute() {
            return attribute;
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
