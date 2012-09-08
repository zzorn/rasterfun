package org.rasterfun.parameters;

/**
 * Encapsulates parameters passed to or stored in a picture generator.
 */
public interface Parameters {

    /**
     * @return the value of the integer parameter with the specified name, or the default value if not found.
     */
    int getInt(String name, int defaultValue);

    /**
     * @return the value of the string parameter with the specified name, or the default value if not found.
     */
    String getString(String name, String defaultValue);

    /**
     * @return a unique copy of these parameters.
     * Deep copies any contained GeneratorElements.
     */
    Parameters copy();

    /**
     * @return an unmodifiable copy of the parameters as they look currently.
     * When it is not needed any more, call release on it to avoid unnecessary memory usage in the Parameters class.
     */
    Parameters snapshot();

    /**
     * If this is a snapshot, calling release will free any reserved resources.
     * The snapshot can not be used after that.
     */
    void release();

    /**
     * Adds the specified parameter to these parameters.
     */
    void addParameter(Parameter parameter);
}
