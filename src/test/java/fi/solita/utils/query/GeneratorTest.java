package fi.solita.utils.query;

import static fi.solita.utils.functional.Option.Some;

import org.junit.Test;

import fi.solita.utils.functional.Option;
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
    public FooDto(X.C x, Option<Id<Foo>> a) {
    }
    public FooDto(X.D x, Option<FooId> a) {
    }
    public FooDto(X.E x, Option<? extends Id<Foo>> a) {
    }
    public FooDto(X.F x, Foo a) {
    }
    public FooDto(X.G x, Option<Foo> a) {
    }
    public FooDto(X.H x, Option<? extends Foo> a) {
    }
}

public class GeneratorTest {
    
    @Test
    public void generatedConstructorAcceptsIdRegardlessOfWhetherUsingInterfaceOrConcreteClass() {
         FooDto_.$$(Select.literal(A.a), Select.literal(new Foo()));
         FooDto_.$$$(Select.literal(B.b), Select.literal(new Foo()));
         FooDto_.$$$$(Select.literal(C.c), Select.literal(Some(new Foo())));
         FooDto_.$$$$$(Select.literal(D.d), Select.literal(Some(new Foo())));
         FooDto_.$$$$$$(Select.literal(E.e), Select.literal(Some(new Foo())));
         FooDto_.$$$$$$$(Select.literal(F.f), Select.literal(new Foo()));
         FooDto_.$$$$$$$$(Select.literal(G.g), Select.literal(Some(new Foo())));
         FooDto_.$$$$$$$$$(Select.literal(H.h), Select.literal(Some(new Foo())));
    }
}
