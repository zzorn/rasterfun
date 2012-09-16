package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.variable.InputVariable;
import org.rasterfun.effect.variable.OutputVariable;
import org.rasterfun.library.GeneratorElement;

import java.util.List;
import java.util.Set;

/**
 * Used to calculate the value at a specific point.
 * Creates the calculation as a java snippet that is compiled to bytecode for better performance.
 */
// TODO: Variables should have availability also, based on source location
public interface Effect extends GeneratorElement {

    // Names for common channels
    // TODO: Check these, in particular, what was the shadow/highlight channel called?

    static final String VALUE = "value";

    static final String RED = "red";
    static final String GREEN = "green";
    static final String BLUE = "blue";

    static final String ALPHA = "alpha";

    static final String HUE = "hue";
    static final String SAT = "sat";
    static final String LUM = "lum";

    static final String HEIGHT = "height"; // Also used for bumpmap (or should they be separate?)
    static final String SPECULAR = "specular";
    static final String LUMINOSITY = "luminosity";

    static final String NORMAL_X = "normal_x";
    static final String NORMAL_Y = "normal_y";
    static final String NORMAL_Z = "normal_z";


    /**
     * @return the container that this effect is in, or null if it is free floating (e.g. in library).
     */
    EffectContainer getContainer();

    /**
     * @param container the container that this effect is in, or null if it is free floating (e.g. in library).
     */
    void setContainer(EffectContainer container);

    /**
     * Adds source to the passed in build context.
     * Can use any local variables specified in the context, and may add own local variables to the context.
     */
    void generateCode(RendererBuilder builder, String effectNamespace, EffectContainer container);

    /**
     * @return list of inputs that the effect expects.
     *         Can be bound to either constant values, or output variables of effects before this one.
     */
    List<InputVariable> getInputVariables();

    /**
     * @return list of output variables that the effect produces.
     */
    List<OutputVariable> getOutputVariables();

    /**
     * @param listener listener to be notified about changes to the effect.
     */
    void addListener(EffectListener listener);

    /**
     * @param listener listener to remove.
     */
    void removeListener(EffectListener listener);

    /**
     * Puts the names of the channels used in this effect to the channelsOut set.
     */
    Set<String> getRequiredChannels(Set<String> channelsOut);


}
