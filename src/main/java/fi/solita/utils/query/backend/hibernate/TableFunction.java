package fi.solita.utils.query.backend.hibernate;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public final class TableFunction implements SQLFunction {
    
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
        // "Returning an integer" since this is used by comparing equality to 1
        return StandardBasicTypes.INTEGER;
    }

    @Override
    public String render(Type columnType, @SuppressWarnings("rawtypes") List args, SessionFactoryImplementor factory) throws QueryException {
        if ( args.size()!=2 ) {
            throw new QueryException("table requires two arguments");
        }
        
        return args.get(0) + " IN (SELECT /*+ dynamic_sampling(tt 2) */ * FROM table(" + args.get(1) + ") tt) AND 1";
    }
}