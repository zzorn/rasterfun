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
     * Adds the specified parameter to these parameters.
     */
    void addParameter(Parameter parameter);
}
