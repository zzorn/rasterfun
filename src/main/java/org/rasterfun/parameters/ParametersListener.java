package org.rasterfun.parameters;

/**
 * Listener that is notified about changes to parameters.
 */
public interface ParametersListener {

    /**
     * @param parameters the Parameters instance that contains the changed parameter.
     * @param name the name of the parameter that changed.
     * @param oldValue old value of parameter, or null if the parameter did not exist before.
     * @param newValue new value of the parameter, or null if the parameter was removed.
     */
    void onParameterChanged(Parameters parameters, String name, Object oldValue, Object newValue);

}
