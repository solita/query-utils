package fi.solita.utils.query;

import static fi.solita.utils.functional.Option.Some;

import org.junit.Test;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query._.A;
import fi.solita.utils.query._.B;
import fi.solita.utils.query._.C;
import fi.solita.utils.query._.D;
import fi.solita.utils.query._.E;
import fi.solita.utils.query._.F;
import fi.solita.utils.query._.G;
import fi.solita.utils.query._.H;
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

class _ {
    enum A {_}
    enum B {_}
    enum C {_}
    enum D {_}
    enum E {_}
    enum F {_}
    enum G {_}
    enum H {_}
}

class FooDto {
    public FooDto(_.A _, Id<Foo> a) {
    }
    public FooDto(_.B _, FooId a) {
    }
    public FooDto(_.C _, Option<Id<Foo>> a) {
    }
    public FooDto(_.D _, Option<FooId> a) {
    }
    public FooDto(_.E _, Option<? extends Id<Foo>> a) {
    }
    public FooDto(_.F _, Foo a) {
    }
    public FooDto(_.G _, Option<Foo> a) {
    }
    public FooDto(_.H _, Option<? extends Foo> a) {
    }
}

public class GeneratorTest {
    
    @Test
    public void generatedConstructorAcceptsIdRegardlessOfWhetherUsingInterfaceOrConcreteClass() {
         FooDto_.c1(Select.literal(A._), Select.literal(new Foo()));
         FooDto_.c2(Select.literal(B._), Select.literal(new Foo()));
         FooDto_.c3(Select.literal(C._), Select.literal(Some(new Foo())));
         FooDto_.c4(Select.literal(D._), Select.literal(Some(new Foo())));
         FooDto_.c5(Select.literal(E._), Select.literal(Some(new Foo())));
         FooDto_.c6(Select.literal(F._), Select.literal(new Foo()));
         FooDto_.c7(Select.literal(G._), Select.literal(Some(new Foo())));
         FooDto_.c8(Select.literal(H._), Select.literal(Some(new Foo())));
    }
}
