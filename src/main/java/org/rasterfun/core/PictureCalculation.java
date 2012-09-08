package org.rasterfun.core;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents an ongoing or completed calculation of a single picture.
 * May provide a smaller preview picture of the final picture.
 */
public final class PictureCalculation implements ProgressListener {

    private final PictureImpl picture;
    private final Parameters parameters;
    private final CalculatorBuilder calculatorBuilder;

    private Future<Picture> previewFuture = null;
    private Future<Picture> pictureFuture = null;

    private final AtomicReference<Double> progress = new AtomicReference<Double>(0.0);
    private final AtomicReference<String> problemDescription = new AtomicReference<String>(null);
    private final AtomicReference<String> currentStatus = new AtomicReference<String>(null);
    private boolean started = false;

    private CopyOnWriteArrayList<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();
    private PictureCalculationTask calculationTask;

    /**
     * @param parameters parameters to use for the calculated picture.
     *                   They should not be changed while the picture is being calculated (use snapshot method to get a static copy to pass in).
     * @param picture an empty picture to fill.
     * @param calculatorBuilder the source for the calculator, used to generate the actual calculator.
     */
    public PictureCalculation(Parameters parameters, PictureImpl picture, CalculatorBuilder calculatorBuilder) {
        // Take a static snapshot of the parameters, so that any changes done to them later are not visible to the calculation.
        this.parameters = parameters.snapshot();
        this.calculatorBuilder = calculatorBuilder;
        this.picture = picture;

    }

    /**
     * Starts the calculation of the picture.
     * Can only be called once, called by default when the PictureGenerator generatePicture(s) method is called,
     * so there is normally no need for the API user to call this.
     */
    public void start() {
        if (started) throw new IllegalStateException("Can not start calculation of picture '"+picture.getName()+"', it has already been started.");
        started = true;

        calculationTask = new PictureCalculationTask(picture, parameters, calculatorBuilder, this);
        pictureFuture = RasterfunApplication.getExecutor().submit(calculationTask);
    }

    /**
     * @return future for a preview picture being calculated.
     */
    public Future<Picture> getPreviewFuture() {
        return previewFuture;
    }

    /**
     * @return future for the picture being calculated.
     */
    public Future<Picture> getPictureFuture() {
        return pictureFuture;
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
     * @return the current progress of this calculation, goes from 0 to 1.
     */
    public double getProgress() {
        return progress.get();
    }

    /**
     * @return the current (human-readable) status of this calculation.
     */
    public String getCurrentStatus() {
        return currentStatus.get();
    }

    /**
     * @return true if the calculation of the picture has finished, or the calculation has stopped for some other reason.
     */
    public boolean isDone() {
        return pictureFuture.isDone();
    }

    /**
     * @return true if there was some error during calculation.
     */
    public boolean isFailed() {
        return problemDescription.get() != null;
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
     * @return description of any problem encountered, or null if no problems.
     */
    public String getProblemDescription() {
        return problemDescription.get();
    }

    /**
     * @param listener a listener that is notified about progress and status updates on this calculation.
     *                 The listener is called from the calculation thread, so if the listener does any UI updates
     *                 it should call SwingUtils.invokeLater or similar, and if it does state updates they should
     *                 take into account concurrency concerns.
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
        this.progress.set((double)progress);

        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onProgress(progress);
            }
        }
    }

    @Override
    public void onStatusChanged(final String description) {
        this.currentStatus.set(description);

        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onStatusChanged(description);
            }
        }
    }

    @Override
    public void onError(final String description, final Throwable cause) {
        if (description != null) problemDescription.set(description);

        // Cancel calculation
        pictureFuture.cancel(false);

        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onError(description, cause);
            }
        }
    }

}
