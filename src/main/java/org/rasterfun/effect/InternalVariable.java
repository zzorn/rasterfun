package org.rasterfun.effect;

/**
 *
 */
public class InternalVariable extends VariableBase {
    private String expression;

    protected InternalVariable(Class<?> type) {
        super(type);
    }

    public InternalVariable(Class<?> type, String name) {
        super(type, name, null);
    }

    public InternalVariable(Class<?> type, String name, String expression) {
        super(type, name, null);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
