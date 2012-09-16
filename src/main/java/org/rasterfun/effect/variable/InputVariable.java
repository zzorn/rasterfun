package org.rasterfun.effect.variable;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.variable.value.Value;
import org.rasterfun.effect.variable.value.ValueListener;
import org.rasterfun.utils.ClassUtils;
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
    private String         constantFieldName;

    private final ValueListener valueListener = new ValueListener() {
        @Override
        public void onValueChanged(Value value) {
            notifyVariableChanged();
        }
    };

    public InputVariable(Class<?> type, Object constantValue) {
        super(type);
        ParameterChecker.checkNotNull(constantValue, "constantValue");
        this.constantValue = constantValue;
    }

    public InputVariable(Class<?> type, String name, String description, Object constantValue) {
        super(type, name, description);
        ParameterChecker.checkNotNull(constantValue, "constantValue");
        this.constantValue = constantValue;
    }


    public void setValue(Object constantValue) {
        ParameterChecker.checkNotNull(constantValue, "constantValue");

        if (!constantValue.equals(this.constantValue)) {

            // Remove any variable binding we have
            if (sourceVariable != null) sourceVariable.removeUser(this);
            sourceVariable = null;

            // Stop listening to old value
            if (Value.class.isInstance(this.constantValue)) {
                ((Value)constantValue).removeListener(valueListener);
            }

            // Change value
            this.constantValue = constantValue;

            // Start listening to new value
            if (Value.class.isInstance(this.constantValue)) {
                ((Value)constantValue).addListener(valueListener);
            }

            notifyVariableChanged();
        }
    }

    public void setToVariable(OutputVariable newSourceVariable) {
        if (sourceVariable != newSourceVariable) {
            if (newSourceVariable != null && !canBindTo(newSourceVariable)) throw new IllegalArgumentException("Can not bind variable "+this+" to the source variable " +
                                                                                  newSourceVariable + " (incompatible types).");
            if (sourceVariable != null) sourceVariable.removeUser(this);
            sourceVariable = newSourceVariable;
            if (sourceVariable != null) sourceVariable.addUser(this);

            notifyVariableChanged();
        }
    }

    public OutputVariable getSourceVariable() {
        return sourceVariable;
    }

    public Object getValue() {
        return constantValue;
    }

    public boolean canBindTo(OutputVariable outputVariable) {
        return getType().isAssignableFrom(outputVariable.getType());
    }

    public void buildSource(RendererBuilder builder) {
        // Pass in the constant value as a parameter to the builder, if we use the constant value ant it is not a primitive type.
        if (sourceVariable == null && !ClassUtils.isWrappedPrimitiveType(constantValue.getClass())) {
            constantFieldName = builder.addParameter(getName(), constantValue, constantValue.getClass());
        }
    }

    public String getExpr() {
        if (sourceVariable != null) {
            // If we have a source value specified, get the value of that
            return sourceVariable.getCodeIdentifier();
        }
        else {
            if (ClassUtils.isWrappedPrimitiveType(constantValue.getClass())) {
                // If the constant is a primitive insert it directly in the source.
                return ClassUtils.wrappedPrimitiveTypeAsConstantString(constantValue);
            }
            else {
                // Otherwise we return a reference to the field holding the parameter, which we passed in in generateCode.
                return constantFieldName;
            }
        }
    }

}
