package fi.solita.utils.query.projection;

import static fi.solita.utils.functional.Collections.newList;
import static fi.solita.utils.functional.Functional.repeat;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import fi.solita.utils.functional.Collections;
import fi.solita.utils.functional.Option;
import fi.solita.utils.functional.Pair;
import fi.solita.utils.functional.Tuple;
import fi.solita.utils.query.Id;
import fi.solita.utils.query.Identifiable;
import fi.solita.utils.query.generation.Cast;
import fi.solita.utils.query.meta.MetaJpaConstructor;

public class Constructors {
    
    public static interface TransparentProjection {
        Attribute<?,?> getWrapped();
    }
    
    public static interface ExpressionProjection<R> extends TransparentProjection {
        Expression<R> getExpression(CriteriaBuilder cb, Expression<R> e);
    }
    
    static <E extends Identifiable<?>> MetaJpaConstructor<E,Id<E>,Id<E>> id() {
        return new IdProjection<E>();
    }
    
    static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> sum(SingularAttribute<? super E, T> attribute) {
        return new SumAttributeProjection<E,T>(Cast.optional(attribute));
    }
    
    static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> max(SingularAttribute<? super E, T> attribute) {
        return new MaxAttributeProjection<E,T>(Cast.optional(attribute));
    }
    
    static <E, T extends Number> MetaJpaConstructor<E,Option<T>,Option<T>> min(SingularAttribute<? super E, T> attribute) {
        return new MinAttributeProjection<E,T>(Cast.optional(attribute));
    }
    
    static <E, T extends Comparable<? super T>> MetaJpaConstructor<E,Option<T>,Option<T>> greatest(SingularAttribute<? super E, T> attribute) {
        return new GreatestAttributeProjection<E,T>(Cast.optional(attribute));
    }
    
    static <E, T extends Comparable<? super T>> MetaJpaConstructor<E,Option<T>,Option<T>> least(SingularAttribute<? super E, T> attribute) {
        return new LeastAttributeProjection<E,T>(Cast.optional(attribute));
    }

    static <E, T> MetaJpaConstructor<E,T,T> value(SingularAttribute<? super E, ? extends T> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    static <E, T> MetaJpaConstructor<E,T,T> value(PluralAttribute<? super E, ? extends T, ?> attribute) {
        return new ValueAttributeProjection<E,T>(attribute);
    }

    static <E, LEFT, RIGHT> MetaJpaConstructor<E,Pair<LEFT,RIGHT>,Map.Entry<? extends LEFT,? extends RIGHT>> pair(Attribute<? super E, ? extends LEFT> left, Attribute<? super E, ? extends RIGHT> right) {
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
        private final Attribute<? super E, ? extends LEFT> left;
        private final Attribute<? super E, ? extends RIGHT> right;

        public PairProjection(Attribute<? super E, ? extends LEFT> left, Attribute<? super E, ? extends RIGHT> right) {
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
    
    private static class ValueAttributeProjection<E,R> extends MetaJpaConstructor.C1<E,R,R> implements TransparentProjection {
        private final Attribute<? super E,? extends R> attribute;

        public ValueAttributeProjection(Attribute<? super E,? extends R> attribute) {
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

        @Override
        public Attribute<?, ?> getWrapped() {
            return attribute;
        }
    }
    
    private static final class SumAttributeProjection<E,R extends Number> extends ValueAttributeProjection<E, Option<R>> implements ExpressionProjection<R> {
        public SumAttributeProjection(Attribute<? super E, Option<R>> attribute) {
            super(attribute);
        }

        @Override
        public Expression<R> getExpression(CriteriaBuilder cb, Expression<R> e) {
            return cb.sum(e);
        }
    }
    
    private static final class MaxAttributeProjection<E,R extends Number> extends ValueAttributeProjection<E, Option<R>> implements ExpressionProjection<R> {
        public MaxAttributeProjection(Attribute<? super E, Option<R>> attribute) {
            super(attribute);
        }

        @Override
        public Expression<R> getExpression(CriteriaBuilder cb, Expression<R> e) {
            return cb.max(e);
        }
    }
    
    private static final class MinAttributeProjection<E,R extends Number> extends ValueAttributeProjection<E, Option<R>> implements ExpressionProjection<R> {
        public MinAttributeProjection(Attribute<? super E, Option<R>> attribute) {
            super(attribute);
        }

        @Override
        public Expression<R> getExpression(CriteriaBuilder cb, Expression<R> e) {
            return cb.min(e);
        }
    }
    
    private static final class GreatestAttributeProjection<E,R extends Comparable<? super R>> extends ValueAttributeProjection<E, Option<R>> implements ExpressionProjection<R> {
        public GreatestAttributeProjection(Attribute<? super E, Option<R>> attribute) {
            super(attribute);
        }

        @Override
        public Expression<R> getExpression(CriteriaBuilder cb, Expression<R> e) {
            return cb.greatest(e);
        }
    }
    
    private static final class LeastAttributeProjection<E,R extends Comparable<? super R>> extends ValueAttributeProjection<E, Option<R>> implements ExpressionProjection<R> {
        public LeastAttributeProjection(Attribute<? super E, Option<R>> attribute) {
            super(attribute);
        }

        @Override
        public Expression<R> getExpression(CriteriaBuilder cb, Expression<R> e) {
            return cb.least(e);
        }
    }
}
