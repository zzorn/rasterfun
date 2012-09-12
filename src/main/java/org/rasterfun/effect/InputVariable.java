package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;
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
    private String constantFieldName;

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

    @Override
    public void buildSource(CalculatorBuilder builder) {
        // Pass in the constant value as a parameter to the builder, if we use the constant value ant it is not a primitive type.
        if (sourceVariable == null && !ClassUtils.isWrappedPrimitiveType(constantValue.getClass())) {
            constantFieldName = builder.addParameter(getName(), constantValue, constantValue.getClass());
        }
    }

    @Override
    public String getExpression() {
        if (sourceVariable != null) {
            // If we have a source value specified, get the value of that
            return sourceVariable.getIdentifier();
        }
        else {
            if (ClassUtils.isWrappedPrimitiveType(constantValue.getClass())) {
                // If the constant is a primitive insert it directly in the source.
                return ClassUtils.wrappedPrimitiveTypeAsConstantString(constantValue);
            }
            else {
                // Otherwise we return a reference to the field holding the parameter, which we passed in in buildSource.
                return constantFieldName;
            }
        }
    }
}
