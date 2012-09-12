package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.library.GeneratorElement;

import java.util.List;

/**
 * Used to calculate the value at a specific point.
 * Creates the calculation as a java snippet that is compiled to bytecode for better performance.
 */
// TODO: generator parameters / properties are visible as outputVariables to effects.
// TODO: Variables and variable binding might completely replace parameters
// Variables should have availability also, based on source location
public interface Effect extends GeneratorElement {

    /**
     * Called to allow the effect to add variables to itself.
     * @param nameSpacePrefix any prefix that should be used for variables created by this effect.
     */
    void initVariables(String nameSpacePrefix);

    /**
     * Adds source to the passed in build context.
     * Can use any local variables specified in the context, and may add own local variables to the context.
     */
    void buildSource(CalculatorBuilder builder);

    /**
     * @return list of inputs that the effect expects.
     *         Can be bound to either constant values, or output variables of effects before this one.
     */
    List<InputVariable> getInputVariables();

    /**
     * @return list of output variables that the effect produces.
     */
    List<OutputVariable> getOutputVariables();


}
