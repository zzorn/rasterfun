package org.rasterfun.effect;

import org.rasterfun.core.compiler.CalculatorBuilder;

/**
 * Represents a generated variable in the code, used to pass information between effects.
 */
public interface Variable {

    /**
     *
     * @return the type of the variable.
     *         Normally Float, but other types are supported as well.
     *         Generator parameters are exposed to effects as output variables,
     *         as well as the original pixel channels at the location.
     *         Effects may also populate variables with other values, e.g. select a gradient depending on location?
     */
    // TODO: Maybe have types for color triplets and directions as well? Could be implemented as multiple variables with postfixes.
    Class<?> getType();

    /**
     * @return user readable name for the variable.  Not actual identifier used in code.
     */
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    void buildSource(CalculatorBuilder builder);

    String getExpression();
    String getIdentifier();

    void setNamespace(String namespace);
}
