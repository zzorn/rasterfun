package org.rasterfun.core.listeners;

/**
 * Listener that can be notified on calculation progress.
 */
public interface ProgressListener {

    /**
     * @param progress the progress on the task, goes from 0.0 to 1.0
     */
    void onProgress(float progress);

    /**
     * @param description describes what is currently happening.
     */
    void onStatusChanged(String description);

    /**
     * Called if the task could not finish for some reason.
     * @param description a description of the reason the task could not finish.
     */
    void onError(String description, Throwable cause);

    /**
     * Called when the task finished
     */
    void onReady();

}
