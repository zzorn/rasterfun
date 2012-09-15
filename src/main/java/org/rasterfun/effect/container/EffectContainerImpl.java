package org.rasterfun.effect.container;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.Effect;
import org.rasterfun.effect.EffectListener;
import org.rasterfun.effect.variable.*;
import org.rasterfun.utils.ParameterChecker;

import java.util.*;

import static org.rasterfun.utils.ParameterChecker.checkNotNull;

/**
 *
 */
public class EffectContainerImpl implements EffectContainer {

    private EffectContainer parent = null;
    private final Map<String, OutputVariable> channels = new LinkedHashMap<String, OutputVariable>();
    private final List<Effect> effects = new ArrayList<Effect>();
    private final List<InputOutputVariable> inputs = new ArrayList<InputOutputVariable>();
    private final List<InputOutputVariable> outputs = new ArrayList<InputOutputVariable>();
    private final Set<EffectContainerListener> listeners = new HashSet<EffectContainerListener>();

    private final EffectListener effectListener = new EffectListener() {
        @Override
        public void onEffectChanged(Effect effect) {
            notifyEffectChanged(effect);
        }
    };

    private final VariableListener variableListener = new VariableListener() {
        @Override
        public void onVariableChanged(Variable variable) {
            notifyContainerChanged();
        }
    };


    @Override
    public EffectContainer getParentContainer() {
        return parent;
    }

    @Override
    public void setParentContainer(EffectContainer parent) {
        this.parent = parent;
        notifyContainerChanged();
    }

    @Override
    public void buildSource(RendererBuilder builder, String namespace, EffectContainer container) {

        // Build inputs
        int inputNum = 1;
        for (InputOutputVariable input : inputs) {
            final OutputVariable target = input.getOutputVariable();
            target.setCodeIdentifier(namespace + "in" + (inputNum++));
            input.generateCode(builder);
        }

        // Build effects
        int effectNum = 1;
        for (Effect effect : effects) {
            String effectNamespace = namespace + "effect" + (effectNum++) + "_";
            effect.generateCode(builder, effectNamespace, this);
        }

        // Write to channels
        for (Map.Entry<String, OutputVariable> entry : channels.entrySet()) {
            final String channel = entry.getKey();
            final OutputVariable source = entry.getValue();

            if (source != null) builder.addChannelAssignment(channel, source.getCodeIdentifier());
        }

        // Write to outputs
        int outputNum = 1;
        for (InputOutputVariable output : outputs) {
            final OutputVariable target = output.getOutputVariable();
            target.setCodeIdentifier(namespace + "out" + (outputNum++));
            output.generateCode(builder);
        }
    }

    @Override
    public List<OutputVariable> getAvailableOutputsFor(Effect effect) {
        ParameterChecker.checkNotNull(effect, "effect");
        ParameterChecker.checkContained(effect, effects, "effects");

        List<OutputVariable> availableOutputs = new ArrayList<OutputVariable>();

        // Add input parameters visible in this container
        for (InputOutputVariable input : inputs) {
            availableOutputs.add(input.getOutputVariable());
        }

        // Add channels visible in container
        // TODO: Create output variable representations of channels, so that we can use them as inputs to effect input variables.

        // Add outputs of earlier effects
        for (Effect e : effects) {
            if (e == effect) return availableOutputs;
            else {
                availableOutputs.addAll(e.getOutputVariables());
            }
        }

        throw new IllegalStateException("We should have run into the effect in the loop above");
    }

    @Override
    public List<InputOutputVariable> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    @Override
    public List<InputOutputVariable> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }

    @Override
    public <T> void addInput(String name, String description, Class<T> type, T defaultValue) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNotNull(type, "type");
        for (InputOutputVariable input : inputs) {
            if (name.equals(input.getName())) throw new IllegalArgumentException("An input named '"+name+"' already exists");
        }

        final InputOutputVariable input = new InputOutputVariable(name, description, type, defaultValue);
        inputs.add(input);

        input.addListener(variableListener);

        notifyContainerChanged();
    }

    @Override
    public void removeInput(String name) {
        InputOutputVariable inputToRemove = null;
        for (InputOutputVariable input : inputs) {
            if (name.equals(input.getName())) {
                inputToRemove = input;
            }
        }

        if (inputToRemove != null) {
            inputToRemove.removeListener(variableListener);
            inputToRemove.getOutputVariable().removeAllUsers();
            inputs.remove(inputToRemove);
            notifyContainerChanged();
        } else {
            throw new IllegalArgumentException("No input named '"+name+"' found");
        }
    }

    @Override
    public <T> void addOutput(String name, String description, Class<T> type, T defaultValue) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNotNull(type, "type");
        for (InputOutputVariable output : outputs) {
            if (name.equals(output.getName())) throw new IllegalArgumentException("An output named '"+name+"' already exists");
        }

        final InputOutputVariable output = new InputOutputVariable(name, description, type, defaultValue);
        outputs.add(output);
        output.addListener(variableListener);

        notifyContainerChanged();
    }

    @Override
    public void removeOutput(String name) {
        InputOutputVariable outputToRemove = null;
        for (InputOutputVariable output : outputs) {
            if (name.equals(output.getName())) {
                outputToRemove = output;
            }
        }

        if (outputToRemove != null) {
            outputToRemove.removeListener(variableListener);
            outputToRemove.getOutputVariable().removeAllUsers();
            outputs.remove(outputToRemove);
            notifyContainerChanged();
        } else {
            throw new IllegalArgumentException("No output named '"+name+"' found");
        }
    }

    @Override
    public List<Effect> getEffects() {
        return Collections.unmodifiableList(effects);
    }

    @Override
    public void addEffect(Effect effect) {
        addEffect(effect, effects.size());
    }

    @Override
    public void addEffect(Effect effect, int position) {
        ParameterChecker.checkNotNull(effect, "effect");
        ParameterChecker.checkIntegerInRange(position, "position", 0, effects.size() + 1);
        ParameterChecker.checkNotAlreadyContained(effect, effects, "effects");

        effects.add(position, effect);
        effect.setContainer(this);
        effect.addListener(effectListener);

        notifyContainerChanged();
    }

    @Override
    public void moveEffect(Effect effect, int position) {
        // TODO: Implement
        notifyContainerChanged();
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void removeEffect(Effect effect) {
        ParameterChecker.checkNotNull(effect, "effect");
        ParameterChecker.checkContained(effect, effects, "effects");

        // Remove any references to the outputs of the effect
        for (OutputVariable outputVariable : effect.getOutputVariables()) {
            outputVariable.removeAllUsers();

            // Remove uses by channel mappings
            for (Map.Entry<String, OutputVariable> entry : channels.entrySet()) {
                if (entry.getValue() == outputVariable) entry.setValue(null);
            }
        }

        effect.removeListener(effectListener);
        effect.setContainer(null);
        effects.remove(effect);

        notifyContainerChanged();
    }

    @Override
    public void addChannel(String channel) {
        ParameterChecker.checkIsIdentifier(channel, "channel");
        ParameterChecker.checkNotAlreadyContained(channel, channels, "channels");

        channels.put(channel, null);

        notifyContainerChanged();
    }

    @Override
    public void removeChannel(String channel) {
        ParameterChecker.checkNotNull(channel, "channel");
        ParameterChecker.checkContained(channel, channels.keySet(), "channels");

        channels.remove(channel);

        notifyContainerChanged();
    }

    @Override
    public void setChannels(List<String> channels) {
        // Check if anything would change
        if (channelsDiffer(channels)) {

            // Clear old
            this.channels.clear();

            // Copy new
            for (String channel : channels) {
                ParameterChecker.checkIsIdentifier(channel, "channel");
                ParameterChecker.checkNotAlreadyContained(channel, this.channels, "channels");

                this.channels.put(channel, null);
            }

            // Fire change notification
            notifyContainerChanged();
        }
    }

    /**
     * @return true if the provided list of channels differs from the containers current channel list.
     */
    private boolean channelsDiffer(List<String> otherChannels) {
        if (channels.size() != otherChannels.size()) return true;
        else {
            int index = 0;
            for (Map.Entry<String, OutputVariable> entry : channels.entrySet()) {
                if (!entry.getKey().equals(otherChannels.get(index++))) return true;
            }
            return false;
        }
    }

    @Override
    public void setChannelVar(String channel, OutputVariable channelVar) {
        ParameterChecker.checkContained(channel, channels.keySet(), "channels");
        if (channelVar != null && !hasOutputVar(channelVar)) throw new IllegalArgumentException("The output variable '"+channelVar+"' is not in this effect container");

        channels.put(channel, channelVar);

        notifyContainerChanged();
    }

    private boolean hasOutputVar(OutputVariable outputVariable) {
        for (Effect effect : effects) {
            if (effect.getOutputVariables().contains(outputVariable)) return true;
        }

        return false;
    }

    @Override
    public Collection<String> getChannels() {
        return Collections.unmodifiableCollection(channels.keySet());
    }

    @Override
    public EffectContainer copy() {
        final EffectContainerImpl theCopy = new EffectContainerImpl();

        // Maintain mapping from original output variables to the copies,
        // so that we can copy the references inside the copy as well.
        Map<OutputVariable, OutputVariable> originalToCopy = new HashMap<OutputVariable, OutputVariable>();

        // Copy channels
        for (Map.Entry<String, OutputVariable> entry : channels.entrySet()) {
            theCopy.channels.put(entry.getKey(), entry.getValue());
        }

        // Copy inputs
        for (InputOutputVariable oldInput : inputs) {
            final InputOutputVariable newInput = new InputOutputVariable(oldInput.getName(),
                                                                         oldInput.getDescription(),
                                                                         oldInput.getType(),
                                                                         oldInput.getValue());
            theCopy.inputs.add(newInput);
            originalToCopy.put(oldInput.getOutputVariable(), newInput.getOutputVariable());

            newInput.addListener(theCopy.variableListener);
        }
        
        // Copy effects
        for (Effect effect : effects) {
            final Effect newEffect = (Effect) effect.copy();
            theCopy.effects.add(newEffect);
            
            // Map inputs
            List<InputVariable> newInputVariables = newEffect.getInputVariables();
            List<InputVariable> oldInputVariables = effect.getInputVariables();
            assert newInputVariables.size() == oldInputVariables.size() : "The number of input variables in an effect and its copy should be the same.";
            for (int i = 0; i < newInputVariables.size(); i++) {
                InputVariable oldInputVar = oldInputVariables.get(i);
                InputVariable newInputVar = newInputVariables.get(i);
                OutputVariable newOutputVar = originalToCopy.get(oldInputVar.getSourceVariable());
                newInputVar.setToVariable(newOutputVar);
            }
            
            // Record outputs
            List<OutputVariable> newOutputVariables = newEffect.getOutputVariables();
            List<OutputVariable> oldOutputVariables = effect.getOutputVariables();
            assert newOutputVariables.size() == oldOutputVariables.size() : "The number of output variables in an effect and its copy should be the same.";
            for (int i = 0; i < newOutputVariables.size(); i++) {
                OutputVariable oldOutputVar = oldOutputVariables.get(i);
                OutputVariable newOutputVar = newOutputVariables.get(i);
                originalToCopy.put(oldOutputVar, newOutputVar);
            }

            // Listen
            newEffect.addListener(theCopy.effectListener);
        }

        // Copy outputs
        for (InputOutputVariable oldOutput : outputs) {
            final InputOutputVariable newOutput = new InputOutputVariable(oldOutput.getName(),
                                                                          oldOutput.getDescription(),
                                                                          oldOutput.getType(),
                                                                          oldOutput.getValue());
            theCopy.outputs.add(newOutput);

            final OutputVariable newSource = originalToCopy.get(oldOutput.getInputVariable().getSourceVariable());
            newOutput.getInputVariable().setToVariable(newSource);

            newOutput.addListener(theCopy.variableListener);
        }
        
        // Copy channel mappings
        for (Map.Entry<String, OutputVariable> entry : channels.entrySet()) {
            final OutputVariable newSource = originalToCopy.get(entry.getValue());
            theCopy.channels.put(entry.getKey(), newSource);
        }

        return theCopy;
    }

    @Override
    public void addListener(EffectContainerListener listener) {
        checkNotNull(listener, "listener");
        listeners.add(listener);
    }

    @Override
    public void removeListener(EffectContainerListener listener) {
        listeners.remove(listener);
    }

    protected final void notifyContainerChanged() {
        for (EffectContainerListener listener : listeners) {
            listener.onContainerChanged(this);
        }
    }

    protected final void notifyEffectChanged(Effect effect) {
        for (EffectContainerListener listener : listeners) {
            listener.onEffectChanged(effect);
        }
    }
}
