package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.variable.*;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.utils.ParameterChecker;
import org.rasterfun.utils.StringUtils;

import java.util.*;

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

    private final Set<String> requiredChannels = new LinkedHashSet<String>(4);

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

    @Override
    public Set<String> getRequiredChannels(Set<String> channelsOut) {
        if (channelsOut == null) channelsOut = new LinkedHashSet<String>();
        channelsOut.addAll(requiredChannels);
        return channelsOut;
    }

    public final <T> InputVariable addInput(String name,
                                            T initialValue,
                                            Class<T> type,
                                            String description) {
        return addInput(name, initialValue, type, null, description);
    }

    public final <T> InputVariable addInput(String name,
                                            T initialValue,
                                            Class<T> type,
                                            OutputVariable sourceVar, String description) {
        ParameterChecker.checkIsIdentifier(name, "name");
        ParameterChecker.checkNonEmptyString(description, "description");
        ParameterChecker.checkNotNull(initialValue, "initialValue");
        ParameterChecker.checkNotNull(type, "type");

        InputVariable variable = new InputVariable(type, name, description, initialValue);

        inputVariables.add(variable);

        variable.addListener(variableListener);
        if (sourceVar != null) variable.setToVariable(sourceVar);

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

        beforeBuildSource(builder, effectNamespace + "before", container);

        // Generate code for output variables
        int varId = 0;
        for (OutputVariable variable : outputVariables) {

            // Determine id for variable
            variable.setCodeIdentifier(effectNamespace + "var" + varId + "_" + StringUtils.identifierFromName(variable.getName(), 'Q'));

            // Build variable source
            String localNamespace = effectNamespace + varId + "_internal_";
            variable.buildSource(builder, container, this, localNamespace);

            varId++;
        }

        afterBuildSource(builder, effectNamespace + "after", container);

    }

    protected void beforeBuildSource(RendererBuilder builder, String namespace, EffectContainer container) {}
    protected void afterBuildSource(RendererBuilder builder, String namespace, EffectContainer container) {}

    protected final void requireChannel(String channelName) {
        requiredChannels.add(channelName);
    }

    protected final void requireValueChannel() {
        requiredChannels.add(VALUE);
    }

    protected final void requireRGBAChannels() {
        requiredChannels.add(RED);
        requiredChannels.add(GREEN);
        requiredChannels.add(BLUE);
        requiredChannels.add(ALPHA);
    }

    @Override
    protected void onContainerChanged(EffectContainer container) {}


}
