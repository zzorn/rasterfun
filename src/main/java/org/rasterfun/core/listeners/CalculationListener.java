package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

/**
 * A listener that is notified about calculation progress.
 */
public interface CalculationListener {

    /**
     * Used to report progress with picture rendering.
     *
     * @param calculationIndex index number of the calculation task.
     * @param completedPixels the number of pixels that the calculation completed since the last update call.
     */
    void onCalculationProgress(int calculationIndex, int completedPixels);

    /**
     * Indicates that one slice of the picture at the specified location is completed.
     */
    void onPictureSliceReady(int calculationIndex, int pictureIndex, Picture picture, boolean isPreview);

    /**
     * Called if there was some exception or other error while running a task.
     *
     * @param calculationIndex the calculation index of the task.
     * @param shortSummary A one line, user readable summary of the problem.
     * @param longDescription A multiline error report.
     * @param cause the causing exception, or null if none.
     */
    void onError(int calculationIndex, String shortSummary, String longDescription, Throwable cause);
}
