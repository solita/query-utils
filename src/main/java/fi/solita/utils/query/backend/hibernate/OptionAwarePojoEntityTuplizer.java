package fi.solita.utils.query.backend.hibernate;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.PojoEntityTuplizer;

public class OptionAwarePojoEntityTuplizer extends PojoEntityTuplizer {

    public OptionAwarePojoEntityTuplizer(EntityMetamodel emm, PersistentClass pc) {
      super(emm, pc);
    }

    @Override
    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
        if (!"property".equals(mappedProperty.getPropertyAccessorName())) {
            mappedProperty.setPropertyAccessorName(OptionAwareDirectPropertyAccessor.class.getName());
        }
        return super.buildPropertyGetter(mappedProperty, mappedEntity);
    }

    @Override
    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
        if (!"property".equals(mappedProperty.getPropertyAccessorName())) {
            mappedProperty.setPropertyAccessorName(OptionAwareDirectPropertyAccessor.class.getName());
        }
        return super.buildPropertySetter(mappedProperty, mappedEntity);
    }
  }