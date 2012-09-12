package org.rasterfun.effect;

/**
 * Represents code fields created by effects, or from the picture generator parameters.
 * Can generate an unique variable name to the source code.
 */
public class OutputVariable extends VariableBase {

    private String expression;

    public OutputVariable(Class<?> type) {
        super(type);
    }

    public OutputVariable(Class<?> type, String name, String description) {
        super(type, name, description);
    }

    public OutputVariable(Class<?> type, String name, String description, String expression) {
        super(type, name, description);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
