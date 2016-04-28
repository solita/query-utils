package fi.solita.utils.query.backend.hibernate;

import java.util.List;
import java.util.regex.Pattern;

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
        
        return args.get(0) + " IN (/*qu*/SELECT * FROM table(" + args.get(1) + ")) AND 1";
    }
    
    private static final Pattern union = Pattern.compile("\\) AND 1=1\\s+[oO][rR][^/]+/[*]qu[*]/");
    
    static String makeUnion(String sql) {
        return union.matcher(sql).replaceAll(" UNION ALL ");
    }
}