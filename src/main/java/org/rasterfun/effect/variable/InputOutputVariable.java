package org.rasterfun.effect.variable;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.core.compiler.SourceLocation;
import org.rasterfun.effect.Effect;
import org.rasterfun.effect.container.EffectContainer;

/**
 * Variables used to pass in or out values to an effect container.
 */
public class InputOutputVariable<T> {

    private final InputVariable inputVariable;
    private final OutputVariable outputVariable;

    public String getName() {
        return inputVariable.getName();
    }

    public String getDescription() {
        return inputVariable.getDescription();
    }

    public Class<?> getType() {
        return inputVariable.getType();
    }

    public Object getValue() {
        return inputVariable.getValue();
    }

    public InputOutputVariable(String name, String description, Class<T> type, T defaultValue) {
        inputVariable = new InputVariable(type, name, description, defaultValue);
        outputVariable = new OutputVariable(type, name, description, new VariableExpression() {
            @Override
            public String getExpressionString(EffectContainer container, Effect effect, String internalVarPrefix) {
                return inputVariable.getExpr();
            }
        });
    }

    public InputVariable getInputVariable() {
        return inputVariable;
    }

    public OutputVariable getOutputVariable() {
        return outputVariable;
    }

    public void generateCode(RendererBuilder builder) {
        builder.addVariable(SourceLocation.AT_PIXEL,
                            outputVariable.getCodeIdentifier(),
                            inputVariable.getExpr(),
                            outputVariable.getType(),
                            true);
    }

    public void addListener(VariableListener listener) {
        inputVariable.addListener(listener);
    }

    public void removeListener(VariableListener listener) {
        inputVariable.removeListener(listener);
    }
}
