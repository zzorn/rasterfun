package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

import java.util.List;

/**
 * Listen to the calculation of one or more pictures.
 */
public interface PictureCalculationsListener {

    /**
     * Used to report overall progress of the whole calculation.
     * @param calculationIndex id for the calculation run that is reporting,
     *                         useful to tell apart several calculations started after each other.
     * @param progress goes from 0 to 1.
     */
    void onProgress(int calculationIndex, float progress);

    /**
     * Called when a preview is ready.
     * @param calculationIndex id for the calculation run that is reporting,
     *                         useful to tell apart several calculations started after each other.
     */
    void onPreviewReady(int calculationIndex, int pictureIndex, Picture preview);

    /**
     * Called when a picture is ready.
     * @param calculationIndex id for the calculation run that is reporting,
     *                         useful to tell apart several calculations started after each other.
     */
    void onPictureReady(int calculationIndex, int pictureIndex, Picture picture);

    /**
     * Called if the task could not finish for some reason.
     * @param calculationIndex id for the calculation run that is reporting,
     *                         useful to tell apart several calculations started after each other.
     * @param description a description of the reason the task could not finish.
     */
    void onError(int calculationIndex, String description, Throwable cause);

    /**
     * Called when all pictures are calculated.
     * @param calculationIndex id for the calculation run that is reporting,
     *                         useful to tell apart several calculations started after each other.
     */
    void onReady(int calculationIndex, List<Picture> pictures);

}
