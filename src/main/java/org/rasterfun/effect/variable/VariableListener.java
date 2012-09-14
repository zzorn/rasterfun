package org.rasterfun.effect.variable;

/**
 * Listener that is notified about variable changes.
 */
public interface VariableListener {

    /**
     * Called when the value of the variable changes, or the variable is bound to some output variable.
     * @param variable the variable that changed.
     */
    void onVariableChanged(Variable variable);

}
