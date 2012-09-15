package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.variable.*;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.utils.ParameterChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for effect implementations.
 */
public abstract class EffectBase extends AbstractEffect {

    private final List<InputVariable> inputVariables = new ArrayList<InputVariable>();
    private final List<OutputVariable> outputVariables = new ArrayList<OutputVariable>();

    private final VariableListener variableListener = new VariableListener() {
        @Override
        public void onVariableChanged(Variable variable) {
            notifyEffectChanged();
        }
    };

    @Override
    public final GeneratorElement copy() {
        final EffectBase theCopy = createCopiedInstance();

        // NOTE: This looses any bindings to variable output values.  A copy of a whole effect container will be able to keep those.

        doCopyDynamicVariables(this, theCopy);

        // Copy over constant values
        final List<InputVariable> newInputs = theCopy.getInputVariables();
        assert newInputs.size() == inputVariables.size() : "The number of input variables should be the same in the original and in the copy";
        for (int i = 0; i < inputVariables.size(); i++) {
            InputVariable oldInputVariable = inputVariables.get(i);
            InputVariable newInputVariable = newInputs.get(i);

            newInputVariable.setValue(oldInputVariable.getValue());
        }

        return theCopy;
    }

    /**
     * Can be used to copy any dynamic variables (=variables not created automatically by the effect constructor)
     * from the original to the copy.
     * @param original
     * @param theCopy
     */
    protected void doCopyDynamicVariables(EffectBase original, EffectBase theCopy) {}

    protected EffectBase createCopiedInstance() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Copy of "+getClass().getName()+" did not succeed: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Copy of "+getClass().getName()+" did not succeed: " + e.getMessage(), e);
        }
    }

    public final List<InputVariable> getInputVariables() {
        return Collections.unmodifiableList(inputVariables);
    }

    public final List<OutputVariable> getOutputVariables() {
        return Collections.unmodifiableList(outputVariables);
    }

    public final <T> InputVariable addInput(String name, T initialValue, String description, Class<T> type) {
        ParameterChecker.checkIsIdentifier(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(initialValue, "initialValue");
        ParameterChecker.checkNotNull(type, "type");

        InputVariable variable = new InputVariable(type, name, description, initialValue);

        inputVariables.add(variable);

        variable.addListener(variableListener);

        notifyEffectChanged();

        return variable;
    }

    public final <T> OutputVariable addOutput(String name,
                                              String description,
                                              Class<T> type,
                                              VariableExpression expression) {
        ParameterChecker.checkIsIdentifier(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(type, "type");
        ParameterChecker.checkNotNull(expression, "expression");

        OutputVariable variable = new OutputVariable(type, name, description, expression);

        outputVariables.add(variable);

        variable.addListener(variableListener);

        notifyEffectChanged();

        return variable;
    }


    @Override
    public void generateCode(RendererBuilder builder, String effectNamespace, EffectContainer container) {

        beforeBuildSource(builder, effectNamespace + "_before", container);

        // Generate code for output variables
        int varId = 0;
        for (OutputVariable variable : outputVariables) {

            // Determine id for variable
            variable.setCodeIdentifier(effectNamespace + "_" + varId);

            // Build variable source
            String localNamespace = effectNamespace + "_" + varId + "_internal_";
            variable.buildSource(builder, container, this, localNamespace);

            varId++;
        }

        afterBuildSource(builder, effectNamespace + "_after", container);

    }

    protected void beforeBuildSource(RendererBuilder builder, String namespace, EffectContainer container) {}
    protected void afterBuildSource(RendererBuilder builder, String namespace, EffectContainer container) {}


}
