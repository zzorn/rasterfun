package org.rasterfun.effect;

import org.rasterfun.utils.ParameterChecker;

/**
 * Represents some input that an effect has.
 * Can be bound to some output variable with the correct type, or a constant.
 */
// TODO: Should we return an editor directly, or pass parameters to editor from here?
// We need e.g. range information.  Maybe input variable subtype for numbers with range info?
public class InputVariable extends VariableBase {

    private OutputVariable sourceVariable;
    private Object         constantValue;

    public InputVariable(Class<?> type, Object constantValue) {
        super(type);
        this.constantValue = constantValue;
    }

    public InputVariable(Class<?> type, String name, String description, Object constantValue) {
        super(type, name, description);
        ParameterChecker.checkNotNull(constantValue, "constantValue");
        this.constantValue = constantValue;
    }

    public void bindToConstant(Object constantValue) {
        ParameterChecker.checkNotNull(constantValue, "constantValue");

        this.constantValue = constantValue;
        sourceVariable = null;
    }

    public void bindToVariable(OutputVariable sourceVariable) {
        ParameterChecker.checkNotNull(sourceVariable, "sourceVariable");
        if (!canBindTo(sourceVariable)) throw new IllegalArgumentException("Can not bind variable "+this+" to the source variable " + sourceVariable + " (incompatible types).");

        this.sourceVariable = sourceVariable;
    }

    public OutputVariable getSourceVariable() {
        return sourceVariable;
    }

    public Object getConstantValue() {
        return constantValue;
    }

    public boolean canBindTo(OutputVariable outputVariable) {
        return getType().isAssignableFrom(outputVariable.getType());
    }
}
