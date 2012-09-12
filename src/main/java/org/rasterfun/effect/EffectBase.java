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
    private final List<InternalVariable> internalVariables = new ArrayList<InternalVariable>();
    private final List<OutputVariable> outputVariables = new ArrayList<OutputVariable>();

    private String namespace = null;
    private int nextFreeVariableIndex = 1;

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
        ParameterChecker.checkNotNull(type, "type");

        System.out.println("EffectBase.addInput");
        System.out.println("name = " + name);

        InputVariable variable = new InputVariable(type, name, description, initialValue);
        initializeNamespace(variable);

        inputVariables.add(variable);
        return variable;
    }

    public final <T> InternalVariable addInternal(String name, Class<T> type, String expression) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNotNull(type, "type");
        ParameterChecker.checkNonEmptyString(expression, "expression");

        InternalVariable variable = new InternalVariable(type, name, expression);
        initializeNamespace(variable);

        internalVariables.add(variable);
        return variable;
    }

    public final <T> OutputVariable addOutput(String name, String description, Class<T> type, String expression) {
        return addOutput(name, description, type, expression, null);
    }

    public final <T> OutputVariable addOutput(String name, String description, Class<T> type, String expression, String channel) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(type, "type");
        ParameterChecker.checkNonEmptyString(expression, "expression");

        OutputVariable variable = new OutputVariable(type, name, description, expression, channel);
        initializeNamespace(variable);

        outputVariables.add(variable);
        return variable;
    }


    @Override
    public final void initVariables(String nameSpacePrefix) {
        inputVariables.clear();
        internalVariables.clear();
        outputVariables.clear();

        setNamespace(nameSpacePrefix);

        initVariables();
    }

    protected abstract void initVariables();

    private void setNamespace(String namespace) {
        //ParameterChecker.checkIsIdentifier(namespace, "namespace");
        ParameterChecker.checkNotNull(namespace, "namespace");
        this.namespace = namespace;
    }

    @Override
    public final void buildSource(CalculatorBuilder builder) {

        System.out.println("EffectBase.buildSource");

        for (InputVariable variable : inputVariables) {
            addVariableToSource(builder, variable);
        }

        for (InternalVariable variable : internalVariables) {
            addVariableToSource(builder, variable);
        }

        onBuildSource(builder);

        for (OutputVariable variable : outputVariables) {
            addVariableToSource(builder, variable);
        }

        // Write output variables to channels, where a channel is specified
        for (OutputVariable outputVariable : outputVariables) {
            final String channel = outputVariable.getWriteToChannel();
            if (builder.hasChannel(channel)) {
                builder.setVariable(SourceLocation.AT_PIXEL, channel, outputVariable.getVarIdentifier());
            }
        }

    }

    protected void onBuildSource(CalculatorBuilder builder) {}

    /**
     * Creates a unique identifier that can be used for temporary variable names and the like.
     */
    protected final String createTemporaryVariableName() {
        return createTemporaryVariableName("temp");
    }

    /**
     * Creates a unique identifier that can be used for temporary variable names and the like.
     * @param namePart postfix to use for the name.
     */
    protected final String createTemporaryVariableName(String namePart) {
        ParameterChecker.checkIsIdentifier(namePart, "namePart");
        final String identifier = createVariablePrefix() + "_" + namePart;

        // Sanity check
        ParameterChecker.checkIsIdentifier(identifier, "generated identifier");
        return identifier;
    }

    private void addVariableToSource(CalculatorBuilder builder, Variable variable) {

        // Do variable specific building
        variable.buildSource(builder);

        // TODO: Add location to variable, support some of the locations only
        SourceLocation location = SourceLocation.AT_PIXEL;

        // TODO: Restrict allowed types

        // Convert boxed type names to primitive type names
        String typeName;
        if (variable.getType().equals(Boolean.TYPE)) typeName = "boolean";
        else if (variable.getType().equals(Byte.TYPE)) typeName = "byte";
        else if (variable.getType().equals(Short.TYPE)) typeName = "short";
        else if (variable.getType().equals(Integer.TYPE)) typeName = "int";
        else if (variable.getType().equals(Long.TYPE)) typeName = "long";
        else if (variable.getType().equals(Float.TYPE)) typeName = "float";
        else if (variable.getType().equals(Double.TYPE)) typeName = "double";
        else if (variable.getType().equals(Character.TYPE)) typeName = "char";
        else {
            typeName = variable.getType().getName();
            builder.addImport(variable.getType());
        }

        System.out.println("EffectBase.addVariableToSource");


        // Add variable to builder at the specified location
        builder.addVariable(location, variable.getIdentifier(), variable.getExpression(), typeName, true);
    }

    private void initializeNamespace(Variable variable) {
        final String variablePrefix = createVariablePrefix();
        variable.setNamespace(variablePrefix);
    }

    private String createVariablePrefix() {
        if (namespace == null) throw new IllegalStateException("Namespace has not yet been initialized.");

        final int index = nextFreeVariableIndex++;
        return namespace + "_" + index;
    }

}
