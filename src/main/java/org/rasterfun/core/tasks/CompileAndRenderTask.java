package org.rasterfun.core.tasks;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.listeners.PictureCalculationListener;
import org.rasterfun.picture.Picture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Task that first compiles the picture calculator, and then creates a picture and possibly a preview picture from it.
 */
public class CompileAndRenderTask implements Callable<Picture> {

    private final CalculatorBuilder builder;
    private final int pictureIndex;
    private final Picture picture;
    private final Picture preview;
    private final PictureCalculationListener listener;
    private final AtomicReference<PictureCalculationTask> previewTask = new AtomicReference<PictureCalculationTask>();
    private final AtomicReference<PictureCalculationTask> pictureTask = new AtomicReference<PictureCalculationTask>();

    private final AtomicBoolean running = new AtomicBoolean(true);


    public CompileAndRenderTask(CalculatorBuilder builder, int pictureIndex, Picture picture, Picture preview, PictureCalculationListener listener) {
        this.builder = builder;
        this.pictureIndex = pictureIndex;
        this.picture = picture;
        this.preview = preview;
        this.listener = listener;
    }

    @Override
    public Picture call() throws Exception {
        // Check if we were stopped before we even got started
        if (!running.get()) return null;

        // Compile pixel calculator
        if (listener != null) {
            listener.onProgress(picture, pictureIndex, 0.0f, 0);
            listener.onStatusChanged(picture, pictureIndex, "Compiling generator for '"+picture.getName()+"'");
        }
        final PixelCalculator pixelCalculator = builder.compilePixelCalculator();

        // Check if we were stopped during compilation
        if (!running.get()) return null;

        // Create task to calculate the preview image
        Future<Picture> previewFuture = null;
        if (preview != null) {
            previewTask.set(new PictureCalculationTask(preview, pictureIndex, pixelCalculator, listener, true));
            previewFuture = RasterfunApplication.getExecutor().submit(previewTask.get());
        }

        // Create task to calculate the main picture
        pictureTask.set(new PictureCalculationTask(picture, pictureIndex, pixelCalculator, listener, false));
        final Future<Picture> pictureFuture = RasterfunApplication.getExecutor().submit(pictureTask.get());

        // Wait until main picture is done
        final Picture calculatedPicture = pictureFuture.get();

        // If preview calculation is still ongoing, wait for it
        // NOTE: The alternative would be to stop it, but it might be useful to have previews for thumbnail purposes,
        // and the calculation time is usually 1% of the final picture calculation time (if width & height scaled by 0.1),
        // so no harm in making sure everything is consistent.
        if (previewFuture != null) previewFuture.get();

        // Return main picture
        running.set(false);
        return calculatedPicture;
    }

    /**
     * Stops the calculation of the preview and picture.
     */
    public void stop() {
        boolean wasRunning = running.getAndSet(false);
        if (wasRunning) {
            stopTask(previewTask);
            stopTask(pictureTask);
        }
    }

    private void stopTask(AtomicReference<PictureCalculationTask> task) {
        final PictureCalculationTask taskToStop = task.get();
        if (taskToStop != null) taskToStop.stop();
    }

}
