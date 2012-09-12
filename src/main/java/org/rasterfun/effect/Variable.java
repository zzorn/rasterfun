package org.rasterfun.effect;

/**
 * Represents a generated variable in the code, used to pass information between effects.
 */
public interface Variable {

    String getUserReadableName();

    String getDescription();

    String getIdentifierName();

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

}
