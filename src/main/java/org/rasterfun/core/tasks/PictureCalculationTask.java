package org.rasterfun.core.tasks;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.core.PixelCalculator;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.core.listeners.PictureCalculationListener;
import org.rasterfun.picture.Picture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Calculates all pixels in a picture, using the specified CalculatorBuilder to generate a PixelCalculator.
 * Split up the calculation in several tasks if we have many processors on the machine.
 */
public class PictureCalculationTask implements Callable<Picture>, CalculationListener {

    private final Picture picture;
    private final int     pictureIndex;
    private final PixelCalculator pixelCalculator;
    private final PictureCalculationListener listener;
    private final boolean isPreviewCalculation;
    private final int taskCount;
    private final AtomicIntegerArray taskProgress;
    private final AtomicReference<List<PixelCalculationTask>> calculationTasks = new AtomicReference<List<PixelCalculationTask>>(null);
    private final AtomicInteger calculatedScanLines = new AtomicInteger(0);

    public PictureCalculationTask(Picture picture, int pictureIndex, PixelCalculator pixelCalculator, PictureCalculationListener listener, boolean previewCalculation) {
        this.picture = picture;
        this.pictureIndex = pictureIndex;
        this.pixelCalculator = pixelCalculator;
        this.listener = listener;
        isPreviewCalculation = previewCalculation;

        // Count number of (virtual) processor cores we have, split it in that many threads,
        // except if we are calculating a preview image, no need to create many threads for that.
        this.taskCount = isPreviewCalculation ? 1 : Runtime.getRuntime().availableProcessors();

        // Initialize progress counters
        taskProgress = new AtomicIntegerArray(taskCount);
    }

    @Override
    public Picture call() throws Exception {
        try {
            // Split into many threads to take advantage of multiple cores.
            if (listener != null && !isPreviewCalculation) listener.onStatusChanged(picture, pictureIndex, "Generating image data for '"+picture.getName()+"'");
            int pixelsPerTask = picture.getHeight() / taskCount;
            int y = 0;
            List<PixelCalculationTask> tasks = new ArrayList<PixelCalculationTask>();
            for (int i = 0; i < taskCount; i++) {

                // Calculate which pixels to calculate
                int startY = y;

                // Advance fixed step, for last step make sure we get all pixels
                if (i < taskCount - 1) y += pixelsPerTask;
                else y = picture.getHeight();

                int endY = y;

                // Create the calculation task, forward any progress reports to ourselves, so we can calculate a total progress.
                final PixelCalculationTask task = new PixelCalculationTask(picture,
                                                                           startY,
                                                                           endY,
                                                                           pixelCalculator,
                                                                           this,
                                                                           i);

                // Keep track of the task instance so that we can stop it if needed.
                tasks.add(task);

            }
            calculationTasks.set(tasks);

            // Start the tasks
            List<Future<?>> futures = new ArrayList<Future<?>>();
            for (PixelCalculationTask task : tasks) {
                // Start the task, keep track of it's future result
                futures.add(RasterfunApplication.getExecutor().submit(task));
            }

            // Await all tasks to end
            for (Future<?> future : futures) {
                future.get();
            }

            // Update status for listener
            if (listener != null) {
                if (isPreviewCalculation) {
                    listener.onPreviewReady(pictureIndex, picture);
                }
                else {
                    listener.onStatusChanged(picture, pictureIndex, "Done generating picture '"+picture.getName()+"'.");
                    listener.onReady(pictureIndex, picture);
                }
            }

            // Return the picture, now with computed pixels
            return picture;

        } catch (Exception e) {
            // Notify listener if present
            if (listener != null)listener.onError(picture, pictureIndex, e.getMessage(), e);

            // Throw the error along to abort this calculation.
            throw e;
        }
    }

    @Override
    public void onCalculationProgress(int calculationIndex, int completedLines) {
        if (listener != null && !isPreviewCalculation) {
            final int totalCompletedLines = calculatedScanLines.addAndGet(completedLines);

            // Calculate total progress
            float progress = (float)totalCompletedLines / picture.getHeight();

            // Notify listener
            listener.onProgress(picture, pictureIndex, progress, completedLines);
        }
    }


    /**
     * Stops the calculation of all subtasks.
     */
    public void stop() {
        for (PixelCalculationTask task : calculationTasks.get()) {
            task.stop();
        }
    }
}
