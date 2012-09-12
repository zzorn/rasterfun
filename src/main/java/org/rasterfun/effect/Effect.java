package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.parameters.Parameters;

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
     * @return the parameters used to configure this effect.  Initialized with default values.
     */
    Parameters getParameters();

    /**
     * Adds source to the passed in build context.
     * Can use any local variables specified in the context, and may add own local variables to the context.
     */
    void buildSource(CalculatorBuilder builder);

    /**
     * Binds the specified input variable (of this effect)
     * to get the value from the specified source variable.
     */
    void bindInputToVariable(InputVariable inputVariable, OutputVariable sourceVariable);

    /**
     * Binds the specified input variable to use specified constant number.
     */
    void bindInputToNumber(InputVariable inputVariable, float constant);

    /**
     * Binds the specified input variable to use specified constant value.
     */
    void bindInputToElement(InputVariable inputVariable, GeneratorElement element);

    /**
     * @return list of inputs that the effect expects.
     */
    List<InputVariable> getInputVariables();

    /**
     * @return list of output variables that the effect produces.
     */
    List<OutputVariable> getOutputVariables();


}
