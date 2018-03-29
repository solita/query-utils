package fi.solita.utils.query.backend.hibernate;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.tuple.component.PojoComponentTuplizer;

public class OptionAwarePojoComponentTuplizer extends PojoComponentTuplizer {
    public OptionAwarePojoComponentTuplizer(Component component) {
        super(component);
    }

    @Override
    protected Getter buildGetter(Component component, Property prop) {
        prop.setPropertyAccessorName(OptionAwareDirectPropertyAccessor.class.getName());
        return prop.getGetter(component.getComponentClass());
    }

    @Override
    protected Setter buildSetter(Component component, Property prop) {
        prop.setPropertyAccessorName(OptionAwareDirectPropertyAccessor.class.getName());
        return prop.getSetter(component.getComponentClass());
    }
  }