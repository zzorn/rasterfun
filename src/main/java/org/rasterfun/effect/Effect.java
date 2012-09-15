package org.rasterfun.effect;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.variable.InputVariable;
import org.rasterfun.effect.variable.OutputVariable;
import org.rasterfun.library.GeneratorElement;

import java.util.List;

/**
 * Used to calculate the value at a specific point.
 * Creates the calculation as a java snippet that is compiled to bytecode for better performance.
 */
// TODO: generator parameters / properties are visible as outputVariables to effects.
// TODO: Variables and variable binding might completely replace parameters
// Variables should have availability also, based on source location
// TODO: Rethink namespace concept, best if it is independent of the effect (pass in where needed)
public interface Effect extends GeneratorElement {

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

}
