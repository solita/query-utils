package fi.solita.utils.query.backend.hibernate;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import fi.solita.utils.functional.Option;

public final class MemberOfCastFunction implements SQLFunction {
    private final String targetTableType;
    private final Option<String> targetObjectType;
    
    public MemberOfCastFunction(String targetTableType, Option<String> targetObjectType) {
        this.targetTableType = targetTableType;
        this.targetObjectType = targetObjectType;
    }
    
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
        if ( args.size()!=2 ) {
            throw new QueryException("member_of_cast requires one argument");
        }
        
        return "CAST(" + args.get(0) + " AS " + targetTableType + ")";
    }
}