package org.rasterfun.effect;

/**
 * Represents code fields created by effects, or from the picture generator parameters.
 * Can generate an unique variable name to the source code.
 */
public class OutputVariable extends VariableBase {

    private String expression;
    private String writeToChannel;

    public OutputVariable(Class<?> type, String name, String description, String expression) {
        super(type, name, description);
        this.expression = expression;
    }

    public OutputVariable(Class<?> type, String name, String description, String expression, String writeToChannel) {
        super(type, name, description);
        this.expression = expression;
        this.writeToChannel = writeToChannel;
    }

    public String getExpression() {
        return expression;
    }

    public String getWriteToChannel() {
        return writeToChannel;
    }
}
