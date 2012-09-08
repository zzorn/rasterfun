package org.rasterfun.effect;

import org.rasterfun.core.CalculatorBuilder;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.parameters.Parameters;

/**
 * Used to calculate the value at a specific point.
 * Creates the calculation as a java snippet that is compiled to bytecode for better performance.
 */
public interface Effect extends GeneratorElement {

    /**
     * @return the parameters used to configure this effect.  Initialized with default values.
     */
    Parameters getParameters();

    /**
     * Adds source to the passed in build context.
     * Can use any local variables specified in the context, and may add own local variables to the context.
     */
    void buildSource(CalculatorBuilder context);

}
