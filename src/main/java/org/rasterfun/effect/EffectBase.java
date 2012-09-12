package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.compiler.SourceLocation;
import org.rasterfun.library.ParametrizedGeneratorElementBase;
import org.rasterfun.utils.ParameterChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common functionality for Effects.
 */
public abstract class EffectBase extends ParametrizedGeneratorElementBase implements Effect {

    private final List<InputVariable> inputVariables = new ArrayList<InputVariable>();
    private final List<OutputVariable> outputVariables = new ArrayList<OutputVariable>();
    private final List<InternalVariable> internalVariables = new ArrayList<InternalVariable>();

    public final List<InputVariable> getInputVariables() {
        return Collections.unmodifiableList(inputVariables);
    }

    public final List<OutputVariable> getOutputVariables() {
        return Collections.unmodifiableList(outputVariables);
    }

    public final <T> InputVariable addInput(String name, T initialValue, String description, Class<T> type) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(initialValue,  "initialValue");
        ParameterChecker.checkNotNull(type,  "type");

        InputVariable inputVariable = new InputVariable(type, name, description, initialValue);
        inputVariables.add(inputVariable);
        return inputVariable;
    }

    public final <T> OutputVariable addOutput(String name, String description, Class<T> type) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(type,  "type");

        OutputVariable outputVariable = new OutputVariable(type, name, description);
        outputVariables.add(outputVariable);
        return outputVariable;
    }

    public final <T> OutputVariable addOutput(String name, String description, Class<T> type, String expression) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(type, "type");
        ParameterChecker.checkNonEmptyString(expression, "expression");

        OutputVariable outputVariable = new OutputVariable(type, name, description, expression);
        outputVariables.add(outputVariable);
        return outputVariable;
    }

    public final <T> InternalVariable addInternal(String name, Class<T> type, String expression) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNotNull(type, "type");
        ParameterChecker.checkNonEmptyString(expression, "expression");

        InternalVariable internalVariable = new InternalVariable(type, name, expression);
        internalVariables.add(internalVariable);
        return internalVariable;
    }

    @Override
    public void buildSource(CalculatorBuilder builder) {
        for (InputVariable variable : inputVariables) {
            addVariableToSource(builder, variable);
        }
        for (InternalVariable variable : internalVariables) {
            addVariableToSource(builder, variable);
        }
        for (OutputVariable variable : outputVariables) {
            addVariableToSource(builder, variable);
        }
    }

    private void addVariableToSource(CalculatorBuilder builder, Variable variable) {
        // TODO: Add location to variable, support some of the locations only
        SourceLocation location = SourceLocation.AT_PIXEL;

        // TODO: Add namespace support, so that effects and nested effects in a generator get unique prefixes
        String nameSpace = "";
        String identifier = nameSpace + "_" + variable.createIdentifierPart();

        // TODO: Convert boxed type names to primitive type names
        // TODO: Restrict allowed type names
        String typeName = variable.getType().getName();

        builder.addImport(variable.getType());
        builder.addVariable(location, identifier, variable.getExpression(), typeName, true);
    }
}
