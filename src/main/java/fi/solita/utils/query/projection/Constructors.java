package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.forAll;
import static fi.solita.utils.functional.Functional.repeat;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.metamodel.Attribute;

import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Predicate;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.query.IEntity;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.codegen.ConstructorMeta_;

class Constructors {

    static final class IdProjection<E extends IEntity> extends ConstructorMeta_.F1<E,Id<E>, Id<E>> {
        @Override
        public List<Attribute<? super E, ?>> getParameters() {
            return Collections.emptyList();
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
        private static final Constructor<Pair<?,?>> constructor;
        private static final Constructor<Pair<?,?>> serializableConstructor;
        static {
            try {
                @SuppressWarnings("unchecked")
                Constructor<Pair<?,?>> c = (Constructor<Pair<?,?>>)(Object)Pair.class.getDeclaredConstructor(Object.class, Object.class);
                c.setAccessible(true);
                constructor = c;

                @SuppressWarnings("unchecked")
                Constructor<Pair<?,?>> c2 = (Constructor<Pair<?,?>>)(Object)Pair.SerializablePair.class.getDeclaredConstructor(Serializable.class, Serializable.class);
                c2.setAccessible(true);
                serializableConstructor = c2;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private final Attribute<? super E, LEFT> left;
        private final Attribute<? super E, RIGHT> right;

        public PairProjection(Attribute<? super E, LEFT> left, Attribute<? super E, RIGHT> right) {
            this.left = left;
            this.right = right;
        }

        public Attribute<? super E, LEFT> getLeft() {
            return left;
        }

        public Attribute<? super E, RIGHT> getRight() {
            return right;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Attribute<? super E, ?>> getParameters() {
            return Arrays.<Attribute<? super E, ?>>asList(left, right);
        }

        @Override
        public List<Integer> getIndexesOfIdWrappingParameters() {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Constructor<Pair<LEFT, RIGHT>> getMember() {
            boolean areSerializable = (Serializable.class.isAssignableFrom(left.getJavaType()) || left.getJavaType().isPrimitive()) && (Serializable.class.isAssignableFrom(right.getJavaType()) || right.getJavaType().isPrimitive());
            Constructor<Pair<?,?>> c = areSerializable ? serializableConstructor : constructor;
            return (Constructor<Pair<LEFT,RIGHT>>)(Object)c;
        }

        @Override
        public Pair<LEFT, RIGHT> apply(LEFT left, RIGHT right) {
            return Pair.of(left, right);
        }
    }
    
    static final class TupleProjection<E extends IEntity, T extends Tuple> implements ConstructorMeta_<E,T,T> {
        private final List<Attribute<? super E, ?>> attributes;
        private final Constructor<?> c;

        public TupleProjection(Attribute<? super E, ?>... attributes) {
            this.attributes = Arrays.asList(attributes);
            try {
                boolean areSerializable = forAll(attributes, new Predicate<Attribute<?,?>>() {
                    @Override
                    public boolean accept(Attribute<?, ?> candidate) {
                        return Serializable.class.isAssignableFrom(candidate.getJavaType()) || candidate.getJavaType().isPrimitive();
                    }
                });
                Class<?>[] tupleConstructorParams = newList(repeat(areSerializable ? Serializable.class : Object.class, this.attributes.size())).toArray(new Class[0]);
                c = Class.forName(Tuple.class.getName() + this.attributes.size() + (areSerializable ? "$SerializableTuple" : "")).getDeclaredConstructor(tupleConstructorParams);
                c.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public List<Attribute<? super E, ?>> getAttributes() {
            return attributes;
        }

        @Override
        public List<Attribute<? super E, ?>> getParameters() {
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
        public Constructor<T> getMember() {
            return (Constructor<T>) c;
        }
    }
    
    static final class ValueAttributeProjection<E extends IEntity,R> extends ConstructorMeta_.F1<E,R,R> {
        static class Wrapper {
            private final Object wrapped;

            public Wrapper(Object wrapped) {
                this.wrapped = wrapped;
            }

            public Object getWrapped() {
                return wrapped;
            }
        }
        private static final Constructor<Wrapper> constructor;
        static {
            try {
                constructor = Wrapper.class.getDeclaredConstructor(Object.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private final Attribute<? super E,R> attribute;

        public ValueAttributeProjection(Attribute<? super E,R> attribute) {
            this.attribute = attribute;
        }

        public Attribute<?,R> getAttribute() {
            return attribute;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Attribute<? super E, ?>> getParameters() {
            return Arrays.<Attribute<? super E, ?>>asList(attribute);
        }

        @Override
        public List<Integer> getIndexesOfIdWrappingParameters() {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Constructor<R> getMember() {
            return (Constructor<R>) constructor;
        }

        @Override
        public R apply(R t) {
            return t;
        }
    }
}
