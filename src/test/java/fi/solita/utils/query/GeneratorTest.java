package fi.solita.utils.query;

import java.util.Optional;

import org.junit.Test;

import fi.solita.utils.query.X.A;
import fi.solita.utils.query.X.B;
import fi.solita.utils.query.X.C;
import fi.solita.utils.query.X.D;
import fi.solita.utils.query.X.E;
import fi.solita.utils.query.X.F;
import fi.solita.utils.query.X.G;
import fi.solita.utils.query.X.H;
import fi.solita.utils.query.projection.Select;

class Foo implements IEntity<Foo> {
}

class FooId implements Id<Foo> {
    @Override
    public Class<Foo> getOwningClass() {
        return null;
    }

    @Override
    public int compareTo(Id<Foo> o) {
        return -1;
    }
}

class X {
    enum A {a}
    enum B {b}
    enum C {c}
    enum D {d}
    enum E {e}
    enum F {f}
    enum G {g}
    enum H {h}
}

class FooDto {
    public FooDto(X.A x, Id<Foo> a) {
    }
    public FooDto(X.B x, FooId a) {
    }
    public FooDto(X.C x, Optional<Id<Foo>> a) {
    }
    public FooDto(X.D x, Optional<FooId> a) {
    }
    public FooDto(X.E x, Optional<? extends Id<Foo>> a) {
    }
    public FooDto(X.F x, Foo a) {
    }
    public FooDto(X.G x, Optional<Foo> a) {
    }
    public FooDto(X.H x, Optional<? extends Foo> a) {
    }
}

public class GeneratorTest {
    
    @Test
    public void generatedConstructorAcceptsIdRegardlessOfWhetherUsingInterfaceOrConcreteClass() {
         FooDto_.$$(Select.literal(A.a), Select.literal(new Foo()));
         FooDto_.$$$(Select.literal(B.b), Select.literal(new Foo()));
         FooDto_.$$$$(Select.literal(C.c), Select.literal(Optional.of(new Foo())));
         FooDto_.$$$$$(Select.literal(D.d), Select.literal(Optional.of(new Foo())));
         FooDto_.$$$$$$(Select.literal(E.e), Select.literal(Optional.of(new Foo())));
         FooDto_.$$$$$$$(Select.literal(F.f), Select.literal(new Foo()));
         FooDto_.$$$$$$$$(Select.literal(G.g), Select.literal(Optional.of(new Foo())));
         FooDto_.$$$$$$$$$(Select.literal(H.h), Select.literal(Optional.of(new Foo())));
    }
}
