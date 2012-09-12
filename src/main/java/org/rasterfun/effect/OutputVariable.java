package org.rasterfun.effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents code fields created by effects, or from the picture generator parameters.
 * Can generate an unique variable name to the source code.
 */
public class OutputVariable extends VariableBase {

    private List<InputVariable> users = new ArrayList<InputVariable>();

    private String expression;
    private String channelToWriteTo;

    public OutputVariable(Class<?> type, String name, String description, String expression) {
        super(type, name, description);
        this.expression = expression;
    }

    public OutputVariable(Class<?> type, String name, String description, String expression, String channelToWriteTo) {
        super(type, name, description);
        this.expression = expression;
        this.channelToWriteTo = channelToWriteTo;
    }

    public String getExpression() {
        return expression;
    }

    public String getChannelToWriteTo() {
        return channelToWriteTo;
    }

    public void addUser(InputVariable inputVariable) {
        users.add(inputVariable);
    }

    public void removeUser(InputVariable inputVariable) {
        users.remove(inputVariable);
    }

    boolean hasUsers() {
        return !users.isEmpty();
    }

    boolean hasChannel() {
        return channelToWriteTo != null;
    }
}
