package org.rasterfun.core.tasks;

import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.picture.Picture;
import org.rasterfun.utils.ParameterChecker;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Task that renders a Picture using a PixelCalculator.
 */
public class RenderTask implements Callable<Picture> {

    private final int calculationIndex;
    private final int pictureIndex;
    private final boolean isPreview;
    private final int startY;
    private final int endY;
    private final Picture picture;
    private final Future<PixelCalculator> pixelCalculatorFuture;
    private final CalculationListener listener;

    private PixelCalculator pixelCalculator = null;
    private boolean stopped = false;

    public RenderTask(int calculationIndex,
                      int pictureIndex,
                      boolean preview,
                      int startY,
                      int endY,
                      Picture picture,
                      Future<PixelCalculator> pixelCalculatorFuture,
                      CalculationListener listener) {
        ParameterChecker.checkNotNull(picture, "picture");
        ParameterChecker.checkNotNull(pixelCalculatorFuture, "pixelCalculatorFuture");
        ParameterChecker.checkPositiveNonZeroInteger(picture.getWidth(), "picture.getWidth()");
        ParameterChecker.checkPositiveNonZeroInteger(picture.getHeight(), "picture.getHeight()");

        this.calculationIndex = calculationIndex;
        this.pictureIndex = pictureIndex;
        isPreview = preview;
        this.startY = startY;
        this.endY = endY;
        this.picture = picture;
        this.pixelCalculatorFuture = pixelCalculatorFuture;
        this.listener = listener;
    }

    @Override
    public Picture call() throws Exception {
        try {
            // Wait for pixel renderer compilation task to complete
            pixelCalculator = pixelCalculatorFuture.get();

            // If we weren't stopped, proceed to rendering phase
            if (!stopped && pixelCalculator != null) {

                // Render the part of the picture we have been assigned
                pixelCalculator.calculatePixels(picture.getWidth(),
                                                picture.getHeight(),
                                                picture.getChannelNames(),
                                                picture.getData(),
                                                0,
                                                startY,
                                                picture.getWidth(),
                                                endY,
                                                isPreview ? null : listener,
                                                calculationIndex);

                // Notify listener
                listener.onPictureSliceReady(calculationIndex, pictureIndex, picture, isPreview);
            }

            return picture;
        }
        catch (Exception e) {
            if (listener != null) {
                final String picType = isPreview ? "preview" : "picture";
                listener.onError(calculationIndex,
                                 "Problem when rendering "+ picType +": " + e.getMessage(),
                                 "There was an unexpected problem when rendering the "+picType+".  \n" +
                                 "The full exception is: \n" + e, e);
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Stops the calculation of the picture.
     */
    public void stop() {
        stopped = true;
        if (pixelCalculator != null) pixelCalculator.stop();
    }


}
