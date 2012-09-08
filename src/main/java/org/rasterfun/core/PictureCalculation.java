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

    private AtomicReference<Double> progress = new AtomicReference<Double>(0.0);
    private AtomicReference<String> problemDescription = new AtomicReference<String>(null);
    private AtomicReference<String> currentStatus = new AtomicReference<String>(null);
    private boolean started = false;

    private CopyOnWriteArrayList<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();

    public PictureCalculation(Parameters parameters, PictureImpl picture, CalculatorBuilder calculatorBuilder) {
        this.parameters = parameters;
        this.calculatorBuilder = calculatorBuilder;
        this.picture = picture;

    }

    public void start() {
        if (started) throw new IllegalStateException("Can not start calculation of picture '"+picture.getName()+"', it has already been started.");
        started = true;

        PictureCalculationTask task = new PictureCalculationTask(picture, parameters, calculatorBuilder, this);
        pictureFuture = RasterfunApplication.getExecutor().submit(task);
    }

    public Future<Picture> getPreviewFuture() {
        return previewFuture;
    }

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
            onError("Problem when waiting for image calculation to finish: " + e.getMessage(), e);
            return null;
        }
    }

    public double getProgress() {
        return progress.get();
    }

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

    public String getCurrentStatus() {
        return currentStatus.get();
    }

    public boolean isReady() {
        return pictureFuture.isDone();
    }

    public boolean isFailed() {
        return problemDescription.get() != null || pictureFuture.isCancelled();
    }

    public String getProblemDescription() {
        return problemDescription.get();
    }

    @Override
    public void onError(final String description, final Throwable cause) {
        problemDescription.set(description);

        // Notify listeners
        if (!listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                listener.onError(description, cause);
            }
        }
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
}
