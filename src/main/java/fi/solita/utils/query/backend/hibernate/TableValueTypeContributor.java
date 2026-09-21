package fi.solita.utils.query.backend.hibernate;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;

public class TableValueTypeContributor implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        typeContributions.contributeJavaType(OracleTableValueJavaType.INSTANCE);
        typeContributions.contributeJdbcType(OracleTableValueJDBCType.INSTANCE);
    }

}
