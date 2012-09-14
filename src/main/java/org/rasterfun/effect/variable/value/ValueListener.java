package org.rasterfun.effect.variable.value;

/**
 * Listener supported by value classes that can be assigned to variables, e.g. gradients.
 */
public interface ValueListener {

    /**
     * Called when the value is changed.
     * @param value the value object that changed.
     */
    void onValueChanged(VariableValue value);

}
