package org.rasterfun.core;

import org.rasterfun.parameters.Parameters;

/**
 * Calculates pixel values for a picture.
 * Implemented by generated java bytecode classes.
 *
 * Note that a single calculator instance may have its calculatePixels method invoked
 * simultaneously from multiple threads to different areas on the image data, so do not store any
 * changing temporary data in fields, just local variables.
 */
public interface PixelCalculator {

    /**
     * @return the channels produced by this pixel generator, in the order they are produced.
     */
    String[] getChannelNames();

    /**
     * Generates all pixels for the given picture.
     * Pixels are stored in y major order, with the values for each channel directly following each other in a pixel.
     * E.g. a width 3, height 2 picture with 2 channels a and b, will have the following data layout (where a01 is the value for the
     * 'a' channel at the location x: 0, y: 1): a00,b00, a10,b10, a20,b20,   a01,b01, a11,b11, a21,b21.
     *
     * @param parameters picture generator and  effect parameter values to use when generating the pixels.
     * @param width width of the picture.
     * @param height height of the picture.
     * @param pixelData an array with width * height * getChannels().length number of float entries.
     * @param startX the x column to start calculating at.
     * @param startY the y row to start calculating at.
     * @param endX the x column to stop calculating before.
     * @param endY the y row to stop calculating before.
     * @param listener a listener should be notified about the progress of this calculation.
     * @param calculatorIndex index of this calculator, used when reporting to the listener.
     */
    void calculatePixels(Parameters parameters,
                         int width,
                         int height,
                         float[] pixelData,
                         int startX,
                         int startY,
                         int endX,
                         int endY,
                         CalculationListener listener,
                         int calculatorIndex);

}
