package fi.solita.utils.query.backend.hibernate;

import fi.solita.utils.query.DefaultConfiguration;

public class DefaultHibernateConfiguration extends DefaultConfiguration {
    public DefaultHibernateConfiguration() {
        TableValueType.config = this;
    }
}