package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

/**
 * Listener that can be notified on calculation progress.
 */
public interface PictureCalculationListener {

    /**
     * @param progress the progress on the task, goes from 0.0 to 1.0
     * @param scanLinesCalculated number of vertical lines calculated since the last update call.
     */
    void onProgress(Picture picture, int pictureIndex, float progress, int scanLinesCalculated);

    /**
     * @param description describes what is currently happening.
     */
    void onStatusChanged(Picture picture, int pictureIndex, String description);

    /**
     * Called if the task could not finish for some reason.
     * @param description a description of the reason the task could not finish.
     */
    void onError(Picture picture, int pictureIndex, String description, Throwable cause);

    /**
     * Called when the preview is ready.
     */
    void onPreviewReady(int pictureIndex, Picture preview);

    /**
     * Called when the task finished
     */
    void onReady(int pictureIndex, Picture picture);

}
