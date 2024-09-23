package fi.solita.utils.query.backend.hibernate;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class OracleTableFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry().register("table", new TableFunction("table", functionContributions.getTypeConfiguration()));
    }

}
