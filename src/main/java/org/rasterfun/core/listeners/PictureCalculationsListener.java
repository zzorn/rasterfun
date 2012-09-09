package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

import java.util.List;

/**
 * Listen to the calculation of one or more pictures.
 */
public interface PictureCalculationsListener {

    /**
     * Used to report overall progress of the whole calculation.
     * @param progress goes from 0 to 1.
     */
    void onProgress(float progress);

    /**
     * Called when a preview is ready.
     */
    void onPreviewReady(int pictureIndex, Picture preview);

    /**
     * Called when a picture is ready.
     */
    void onPictureReady(Picture picture, int pictureIndex);

    /**
     * Called if the task could not finish for some reason.
     * @param description a description of the reason the task could not finish.
     */
    void onError(String description, Throwable cause);

    /**
     * Called when all pictures are calculated.
     */
    void onReady(List<Picture> pictures);

}
