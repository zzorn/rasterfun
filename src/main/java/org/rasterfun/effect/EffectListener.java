package org.rasterfun.effect;

/**
 * A listener that is notified when an effect changes.
 */
public interface EffectListener {

    /**
     * Called when a variable in an effect changed, some other change was made to an effect.
     * @param effect the changed effect.
     */
    void onEffectChanged(Effect effect);

}
