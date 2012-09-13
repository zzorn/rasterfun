package org.rasterfun.effect.variable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents code fields created by effects, or from the picture generator parameters.
 * Can generate an unique variable name to the source code.
 */
public class OutputVariable extends ExpressionVariableBase {

    private List<InputVariable> users = new ArrayList<InputVariable>();

    private String channelToWriteTo;

    public OutputVariable(Class<?> type, String name, String description, VariableExpression expression) {
        super(type, name, description, expression);
    }

    public OutputVariable(Class<?> type, String name, String description, VariableExpression expression, String channelToWriteTo) {
        super(type, name, description, expression);
        this.channelToWriteTo = channelToWriteTo;
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

    public boolean hasUsers() {
        return !users.isEmpty();
    }

    public boolean hasChannel() {
        return channelToWriteTo != null;
    }
}
