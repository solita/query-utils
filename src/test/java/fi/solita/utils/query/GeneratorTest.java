package fi.solita.utils.query;

import static fi.solita.utils.functional.Option.Some;

import org.junit.Test;

import fi.solita.utils.functional.Option;
import fi.solita.utils.query._.A;
import fi.solita.utils.query._.B;
import fi.solita.utils.query._.C;
import fi.solita.utils.query._.D;
import fi.solita.utils.query._.E;
import fi.solita.utils.query.projection.Select;

class Foo implements IEntity {
}

class FooId implements Id<Foo> {
    @Override
    public Class<Foo> getOwningClass() {
        return null;
    }
}

class _ {
    enum A {_}
    enum B {_}
    enum C {_}
    enum D {_}
    enum E {_}
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
}

public class GeneratorTest {
    
    @Test
    public void generatedConstructorAcceptsIdRegardlessOfWhetherUsingInterfaceOrConcreteClass() {
         FooDto_.c1(Select.literal(A._), Select.literal(new Foo()));
         FooDto_.c2(Select.literal(B._), Select.literal(new Foo()));
         FooDto_.c3(Select.literal(C._), Select.literal(Some(new Foo())));
         FooDto_.c4(Select.literal(D._), Select.literal(Some(new Foo())));
         FooDto_.c5(Select.literal(E._), Select.literal(Some(new Foo())));
    }
}
