package fi.solita.utils.query.backend.hibernate;

import java.util.List;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.ArgumentTypesValidator;
import org.hibernate.query.sqm.produce.function.FunctionParameterType;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionArgumentTypeResolvers;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Same as {@link TableFunction} but with an undocumented Oracle hint.
 * 
 * */
public final class TableFunctionWithPrecompute extends AbstractSqmSelfRenderingFunctionDescriptor {
    
    public TableFunctionWithPrecompute(String functionName, TypeConfiguration typeConfiguration) {
        super(
                functionName,
                new ArgumentTypesValidator(
                        StandardArgumentsValidators.exactly( 1 ),
                        FunctionParameterType.ANY
                ),
                StandardFunctionReturnTypeResolvers.useFirstNonNull(),
                StandardFunctionArgumentTypeResolvers.invariant(typeConfiguration, FunctionParameterType.INTEGER )
        );
    }

    @Override
    public void render(
            SqlAppender sqlAppender,
            List<? extends SqlAstNode> sqlAstArguments,
            ReturnableType<?> returnType,
            SqlAstTranslator<?> walker) {
        final Expression expression = (Expression) sqlAstArguments.get( 0 );
        sqlAppender.appendSql("SELECT /*+ precompute_subquery */ * FROM table(");
        expression.accept(walker);
        sqlAppender.appendSql(" ) tt");
    }

    @Override
    public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, SqlAstTranslator<?> walker) {
        render( sqlAppender, sqlAstArguments, (ReturnableType<?>) null, walker );
    }
}
