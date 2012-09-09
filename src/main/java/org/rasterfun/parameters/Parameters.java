package org.rasterfun.parameters;

import java.util.Collection;
import java.util.Map;

/**
 * Encapsulates parameters passed to or stored in a picture generator.
 */
public interface Parameters extends Copyable<Parameters> {

    /**
     * Sets the parameter with the specified name to the specified value, overriding any earlier value.
     */
    <T> void set(String name, T value);

    /**
     * @return the value of the parameter with the specified name, or null if not found.
     */
    <T> T get(String name);

    /**
     * @return the value of the parameter with the specified name, or the default value if not found.
     */
    <T> T get(String name, T defaultValue);

    /**
     * @return the value of the parameter with the specified name, or the default value if not found or if the value is null.
     */
    <T> T getNotNull(String name, T defaultValue);

    /**
     * @return the names of the stored parameters, in the order they were added.
     */
    Collection<String> getNames();

    /**
     * @return a read only map with the parameter names and values.
     */
    Map<String, Object> getValues();

    /**
     * @return a unique copy of these parameters.
     * Deep copies any contained values that implement Copyable,
     * just copies references to contained values that do not implement Copyable.
     */
    Parameters copy();

    /**
     * Adds all the parameters in the specified source parameters.
     * The values are copied shallowly by reference, so any changes done to the values in the source
     * are reflected in the values of this Properties instance.
     *
     * This is faster and uses less memory than addParameterCopies.
     *
     * Existing properties with the same names are replaced.
     */
    void addParameterReferences(Parameters source);

    /**
     * Adds copies of all the parameters in the specified source parameters.
     * Deep copies any source values that implement Copyable,
     * just copies references to source values that do not implement Copyable.
     *
     * This is slower and more memory intensive than addParameterReferences.
     *
     * Existing properties with the same names are replaced.
     */
    void addParameterCopies(Parameters source);

}
