package org.rasterfun.effect;

/**
 *
 */
public class InternalVariable extends VariableBase {
    private String expression;

    public InternalVariable(Class<?> type, String name, String expression) {
        super(type, name, null);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
