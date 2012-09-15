package org.rasterfun.effect.variable.value;

import java.util.HashSet;
import java.util.Set;

/**
 * Common functionality for Value implementations.
 */
public abstract class ValueBase implements Value {

    private Set<ValueListener> listeners = new HashSet<ValueListener>(3);

    @Override
    public final void addListener(ValueListener listener) {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(ValueListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies listeners that the value has changed.
     */
    protected final void notifyValueChanged() {
        for (ValueListener listener : listeners) {
            listener.onValueChanged(this);
        }
    }
}
