package org.rasterfun.ui.preview.arranger;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.picture.Picture;
import org.rasterfun.utils.FastImageRenderer;

import java.awt.*;
import java.util.List;

/**
 * Arranges and renders pictures generated by one PictureGenerator for viewing.
 * E.g. in grid, or tiling endless mapping, or perspective projected tiling view.
 */
public interface PictureArranger extends FastImageRenderer {

    /**
     * Called when a new set of pictures are rendered.
     * Should cause the arranger to clear the area.
     * @param builders information on the sizes and names of the pictures that will be calculated.
     */
    void setContentInfo(List<CalculatorBuilder> builders);

    boolean zoom(int steps, double focusX, double focusY);

    boolean setScale(double targetScale);

    boolean setZoomLevel(int step);

    boolean pan(double deltaX, double deltaY);

    /**
     * Called when a preview is available for the specified picture index.
     */
    void setPreview(int pictureIndex, Picture preview);

    /**
     * Called the final picture is available for the specified picture index.
     */
    void setPicture(int pictureIndex, Picture picture);

    Color getBackgroundColor();

    List<ZoomLevel> getZoomLevels();

    ZoomLevel getCurrentZoomLevel();
}