package org.rasterfun.core;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.core.listeners.ProgressListenerDelegate;
import org.rasterfun.core.tasks.CompileAndRenderTask;
import org.rasterfun.core.tasks.PictureCalculationTask;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;

import java.util.concurrent.Future;

/**
 * Represents an ongoing or completed calculation of a single picture.
 * May provide a smaller preview picture of the final picture.
 */
public final class PictureCalculation {

    private final Parameters parameters;
    private final PictureImpl picture;
    private final CalculatorBuilder calculatorBuilder;
    private final boolean generatePreview;
    private final double previewImageScaleFactor;
    private final int minPreviewImageSize;

    private PictureImpl previewPicture = null;

    private boolean started = false;

    private CompileAndRenderTask calculationTask;

    private Future<Picture> pictureFuture = null;

    private ProgressListenerDelegate previewListeners = new ProgressListenerDelegate();
    private ProgressListenerDelegate pictureListeners = new ProgressListenerDelegate();

    /**
     * Creates a calculation to generate the specified picture, and a preview picture for it.
     *
     * @param parameters parameters to use for the calculated picture.
     *                   They should not be changed while the picture is being calculated (use snapshot method to get a static copy to pass in).
     * @param picture an empty picture to fill.
     * @param calculatorBuilder the source for the calculator, used to generate the actual calculator.
     */
    public PictureCalculation(Parameters parameters, PictureImpl picture, CalculatorBuilder calculatorBuilder) {
        this(parameters, picture, calculatorBuilder, true);
    }

    /**
     * Creates a calculation to generate the specified picture, and a preview picture as well if specified.
     *
     * @param parameters parameters to use for the calculated picture.
     *                   They should not be changed while the picture is being calculated (use snapshot method to get a static copy to pass in).
     * @param picture an empty picture to fill.
     * @param calculatorBuilder the source for the calculator, used to generate the actual calculator.
     * @param generatePreview if true, a smaller preview image will be rendered, if it would not be too small.
     */
    public PictureCalculation(Parameters parameters, PictureImpl picture, CalculatorBuilder calculatorBuilder, boolean generatePreview) {
        this(parameters, picture, calculatorBuilder, generatePreview, 0.1, 8);
    }

    /**
     * Creates a calculation to generate the specified picture, and a preview picture as well if specified.
     *
     * @param parameters parameters to use for the calculated picture.
     *                   They should not be changed while the picture is being calculated (use snapshot method to get a static copy to pass in).
     * @param picture an empty picture to fill.
     * @param calculatorBuilder the source for the calculator, used to generate the actual calculator.
     * @param generatePreview if true, a smaller preview image will be rendered.
     * @param previewImageScaleFactor if a preview should be generated, this tells the scaling to use for it.
     *                                E.g. 0.1 will generate a preview picture that has 1/10 the width and height of the original.
     * @param minPreviewImageSize if the preview picture would have a width or height smaller than this, it will not be generated.
     */
    public PictureCalculation(Parameters parameters, PictureImpl picture, CalculatorBuilder calculatorBuilder, boolean generatePreview, double previewImageScaleFactor, int minPreviewImageSize) {
        // Take a static snapshot of the parameters, so that any changes done to them later are not visible to the calculation.
        this.parameters = parameters.copy();
        this.calculatorBuilder = calculatorBuilder;
        this.picture = picture;
        this.generatePreview = generatePreview;
        this.previewImageScaleFactor = previewImageScaleFactor;
        this.minPreviewImageSize = minPreviewImageSize;
    }

    /**
     * Starts the calculation of the picture.
     * Can only be called once, called by default when the PictureGenerator generatePicture(s) method is called,
     * so there is normally no need for the API user to call this.
     */
    public void start() {
        if (started) throw new IllegalStateException("Can not start calculation of picture '"+picture.getName()+"', it has already been started.");
        started = true;

        // Create empty preview picture if we should generate one
        if (generatePreview) {
            // Calculate preview image size
            int previewWidth  = (int)(previewImageScaleFactor * picture.getWidth());
            int previewHeight = (int)(previewImageScaleFactor * picture.getHeight());

            // Only generate it if it is big enough
            if (previewHeight > minPreviewImageSize &&
                previewWidth  > minPreviewImageSize) {

                // Initialize empty preview picture
                previewPicture = new PictureImpl(picture.getName(), previewWidth, previewHeight, picture.getChannelNames());
            }
        }

        // Create task to calculate the picture (and the preview picture if we have one)
        calculationTask = new CompileAndRenderTask(calculatorBuilder, parameters, picture, previewPicture, pictureListeners, previewListeners);
        pictureFuture = RasterfunApplication.getExecutor().submit(calculationTask);
    }

    /**
     * @return future for the picture being calculated.
     */
    public Future<Picture> getPictureFuture() {
        return pictureFuture;
    }

    /**
     * @return a preview picture being calculated, or null if there is no preview.
     *
     * Before the calculation is completed, this is updated from separate calculation threads,
     * so the data may not be valid.
     */
    public Picture getPreview() {
        return previewPicture;
    }

    /**
     * @return the picture being calculated.
     *
     * Before the calculation is completed, this is updated from separate calculation threads,
     * so the data may not be valid.
     */
    public Picture getPicture() {
        return picture;
    }

    /**
     * @return the picture, waiting until it has been calculated if calculation is still ongoing.
     */
    public Picture getPictureAndWait() {
        try {
            return pictureFuture.get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return true if the calculation of the picture has finished, or the calculation has stopped for some other reason.
     */
    public boolean isDone() {
        return pictureFuture.isDone();
    }

    /**
     * Stops the calculation.
     */
    public void stop() {
        if (calculationTask != null) {
            calculationTask.stop();
        }
    }

    /**
     * @return a collection of listeners for the preview picture calculation.
     * You can add your own with addListener.
     *
     * Note the listener is called from the calculation thread, so if the listener does any UI updates
     * it should call SwingUtils.invokeLater or similar, and if it does state updates they should
     * take into account concurrency concerns.
     *
     */
    public ProgressListenerDelegate getPreviewListeners() {
        return previewListeners;
    }

    /**
     * @return a collection of listeners for the picture calculation.
     * You can add your own with addListener.
     *
     * Note the listener is called from the calculation thread, so if the listener does any UI updates
     * it should call SwingUtils.invokeLater or similar, and if it does state updates they should
     * take into account concurrency concerns.
     *
     */
    public ProgressListenerDelegate getPictureListeners() {
        return pictureListeners;
    }
}
