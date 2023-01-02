package fi.solita.utils.query.backend.hibernate;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

/**
 * Same as {@link TableFunction} but with an undocumented Oracle hint.
 * 
 * */
public final class TableFunctionWithPrecompute implements SQLFunction {
    
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
        return StandardBasicTypes.INTEGER;
    }

    @Override
    public String render(Type columnType, @SuppressWarnings("rawtypes") List args, SessionFactoryImplementor factory) throws QueryException {
        if ( args.size()!=1 ) {
            throw new QueryException("table requires one argument");
        }
        
        return "SELECT /*+ precompute_subquery */ * FROM table(" + args.get(0) + ") tt";
    }
}