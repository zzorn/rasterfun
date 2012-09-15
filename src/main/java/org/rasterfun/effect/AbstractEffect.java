package org.rasterfun.effect;

import org.rasterfun.effect.container.EffectContainer;

import java.util.HashSet;
import java.util.Set;

/**
 * Some common functionality of normal effects and composite effects.
 */
public abstract class AbstractEffect implements Effect {

    private final Set<EffectListener> listeners = new HashSet<EffectListener>(3);

    private EffectContainer container;

    public final EffectContainer getContainer() {
        return container;
    }

    public final void setContainer(EffectContainer container) {
        this.container = container;
    }

    /**
     * Notifies listeners that this effect changed.
     */
    protected final void notifyEffectChanged() {
        for (EffectListener listener : listeners) {
            listener.onEffectChanged(this);
        }
    }

    @Override
    public final void addListener(EffectListener listener) {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(EffectListener listener) {
        listeners.remove(listener);
    }
}
