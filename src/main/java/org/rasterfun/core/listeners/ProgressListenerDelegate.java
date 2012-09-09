package org.rasterfun.core.listeners;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread safe delegate for ProgressListener events.
 *
 * Sends listener notifications in the same thread that reported them.
 */
public class ProgressListenerDelegate implements ProgressListener {

    // Use copy on write array to maintain thread safety.  Usually there is only one or a few listeners,
    // so this is not a memory or performance concern.
    private CopyOnWriteArrayList<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();

    /**
     * @param listener a listener that is notified about progress and status updates on this calculation.
     */
    public void addListener(ProgressListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener listener to remove.
     */
    public void removeListener(ProgressListener listener) {
        listeners.remove(listener);
    }


    // ProgressListener implementation:

    @Override
    public void onProgress(final float progress) {
        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onProgress(progress);
            }
        }
    }

    @Override
    public void onStatusChanged(final String description) {
        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onStatusChanged(description);
            }
        }
    }

    @Override
    public void onError(final String description, final Throwable cause) {
        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onError(description, cause);
            }
        }
    }

    @Override
    public void onReady() {
        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onReady();
            }
        }
    }

}
