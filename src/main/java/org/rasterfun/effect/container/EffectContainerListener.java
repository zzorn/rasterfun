package org.rasterfun.effect.container;

import org.rasterfun.effect.EffectListener;

/**
 * Notified when an effect container is changed.
 */
public interface EffectContainerListener extends EffectListener {

    /**
     * Called when the container itself changed (e.g. new effect added, effect moved, channel added).
     */
    void onContainerChanged(EffectContainer container);
}
