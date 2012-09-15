package org.rasterfun.effect.variable;

/**
 * Represents a generated variable in the code, used to pass information between effects.
 */
// TODO: Add location to variable?, support some of the locations only
public interface Variable {

    /* TODO?
    / **
     * @return the effect that this variable is in.
     *         Needed e.g. when presenting input variables to user in clear manner.
     * /
    Effect getEffect();
    */

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

    /**
     * @param listener a listener that gets notified when the variable is changed (binding or expression etc. changed).
     */
    void addListener(VariableListener listener);

    /**
     * @param listener listener to remove.
     */
    void removeListener(VariableListener listener);
}
