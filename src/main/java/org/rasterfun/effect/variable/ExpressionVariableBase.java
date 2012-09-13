package org.rasterfun.effect.variable;

/**
 *
 */
public abstract class ExpressionVariableBase extends VariableBase {

    private VariableExpression expression;

    protected ExpressionVariableBase(Class<?> type, String name, String description, VariableExpression expression) {
        super(type, name, description);
        this.expression = expression;
    }

    public final VariableExpression getExpression() {
        return expression;
    }

    public final void setExpression(VariableExpression expression) {
        this.expression = expression;
    }

    @Override
    public final String getExpressionString() {
        return expression.getExpressionString();
    }
}
