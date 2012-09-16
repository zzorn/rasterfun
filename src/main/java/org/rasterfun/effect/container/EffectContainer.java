package org.rasterfun.effect.container;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.Effect;
import org.rasterfun.effect.variable.InputOutputVariable;
import org.rasterfun.effect.variable.OutputVariable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Something that contains effects, and forms a namespace for them.
 * Generators and composite effects contain EffectContainers.
 */
public interface EffectContainer {

    /**
     * @return the container that this container is in, or null if this is the root level.
     */
    EffectContainer getParentContainer();

    /**
     * Sets the parent of this container.  Should not be called from user code, called automatically.
     */
    void setParentContainer(EffectContainer parent);

    /**
     * Generates source to the passed in build context.
     * @param builder
     * @param prefix
     * @param container the container containing this container, or null if root.
     */
    void buildSource(RendererBuilder builder, String prefix, EffectContainer container);

    /**
     * @return the output variables that can be assigned to an input variable of the specified effect,
     * contained in this container.
     */
    List<OutputVariable> getAvailableOutputsFor(Effect effect);

    /**
     * @return incoming parameters for this effect container.
     */
    List<InputOutputVariable> getInputs();

    /**
     * @return the input variables used to connect the outputs from the effects in this container.
     * Mapping the effect outputs to channels and output variables visible outside the EffectContainer.
     */
    List<InputOutputVariable> getOutputs();

    /**
     * Adds an input for this effect container, with the specified name, description, type and default value.
     */
    <T> void addInput(String name, String description, Class<T> type, T defaultValue);

    /**
     * Removes the specified input.
     */
    void removeInput(String name);

    /**
     * Adds an output for this effect container, with the specified name, description, type and default value.
     */
    <T> void addOutput(String name, String description, Class<T> type, T defaultValue);

    /**
     * Removes the specified output.
     */
    void removeOutput(String name);

    /**
     * @return the effects in this container, in the order they are applied.
     */
    List<Effect> getEffects();

    /**
     * @param effect the effect to add to the last position in this container.
     */
    void addEffect(Effect effect);

    /**
     * @param effect the effect to add to this container.
     * @param position the position to add the effect at, 0 == first, effects.size() == last.
     */
    void addEffect(Effect effect, int position);

    /**
     * Moves an effect that is already in this container to a new position in it.
     * @param effect an effect in this container.
     * @param position new target position, 0 == first, effects.size() == last.
     */
    void moveEffect(Effect effect, int position);

    /**
     * @param effect the effect to remove from this container.
     */
    void removeEffect(Effect effect);

    /**
     * Adds a supported channel, will be received in the inputs, and can be written to in the outputs.
     */
    void addChannel(String channel);

    /**
     * Removes a supported channel.
     */
    void removeChannel(String channel);

    /**
     * Sets the supported channels to the specified list.
     */
    void setChannels(List<String> channels);

    /**
     * @param channelVar the variable that should be used to set a value for this channel,
     *                   or null to clear any value setting for the channel in this effect container.
     */
    void setChannelVar(String channel, OutputVariable channelVar);

    /**
     * @return the names of the supported channels.
     */
    Collection<String> getChannels();

    /**
     * Puts the names of the channels used in this effect container and any effects to the channelsOut set.
     */
    void getRequiredChannels(Set<String> channelsOut);

    /**
     * @return a copy of this container.
     */
    EffectContainer copy();

    /**
     * @param listener a listener that is notified when this container or any effect in it changes.
     */
    void addListener(EffectContainerListener listener);

    /**
     * @param listener listener to remove.
     */
    void removeListener(EffectContainerListener listener);

}
