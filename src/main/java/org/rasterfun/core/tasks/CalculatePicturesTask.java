package org.rasterfun.core.tasks;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.listeners.PictureCalculationListener;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.picture.Picture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.rasterfun.utils.ParameterChecker.checkIntegerEquals;
import static org.rasterfun.utils.ParameterChecker.checkNotNull;

/**
 * Task to calculate a number of pictures.  Returns when all are calculated.
 * Reports total progress across all picture calculations.
 */
public class CalculatePicturesTask implements Callable<List<Picture>>, PictureCalculationListener {

    private final int calculationIndex;
    private final List<CalculatorBuilder> builders;
    private final List<Picture> pictures;
    private final List<Picture> previews;
    private final PictureCalculationsListener listener;

    private int totalScanLines;
    private final AtomicInteger calculatedScanLines = new AtomicInteger(0);

    private final AtomicReference<List<CompileAndRenderTask>> pictureTasks = new AtomicReference<List<CompileAndRenderTask>>();

    public CalculatePicturesTask(int calculationIndex,
                                 List<CalculatorBuilder> builders,
                                 List<Picture> pictures,
                                 List<Picture> previews,
                                 PictureCalculationsListener listener) {
        checkNotNull(builders, "builders");
        checkNotNull(pictures, "pictures");
        checkNotNull(previews, "previews");
        checkIntegerEquals(pictures.size(), "number of pictures", builders.size(), "number of builders");
        checkIntegerEquals(previews.size(), "number of previews", builders.size(), "number of builders");

        this.calculationIndex = calculationIndex;
        this.builders = builders;
        this.pictures = pictures;
        this.previews = previews;
        this.listener = listener;

        // Calculate sun of the height of all the pictures, so we can determine overall progress
        for (Picture picture : pictures) {
            totalScanLines += picture.getHeight();
        }
    }

    @Override
    public List<Picture> call() throws Exception {
        try {
            // Start calculation tasks for all pictures
            List<CompileAndRenderTask> tasks = new ArrayList<CompileAndRenderTask>();
            List<Future<Picture>> futures = new ArrayList<Future<Picture>>();
            int pictureIndex = 0;
            for (CalculatorBuilder builder : builders) {
                final Picture picture = pictures.get(pictureIndex);
                final Picture preview = previews.get(pictureIndex);

                final CompileAndRenderTask task = new CompileAndRenderTask(builder, pictureIndex, picture, preview, this);
                futures.add(RasterfunApplication.getExecutor().submit(task));
                tasks.add(task);
            }

            // Store the tasks so that we can stop them if needed
            pictureTasks.set(tasks);

            // Wait for all tasks to finish
            for (Future<Picture> future : futures) {
                future.get();
            }

            // Tell listener we are ready
            if (listener != null) listener.onReady(calculationIndex, pictures);

            // Return the calculated pictures
            return pictures;
        } catch (Exception e) {
            if (listener != null) listener.onError(calculationIndex, e.getMessage() + ": " + e, e);
            throw e;
        }
    }

    public void stop() {
        final List<CompileAndRenderTask> tasks = pictureTasks.get();
        if (tasks != null) {
            for (CompileAndRenderTask task : tasks) {
                task.stop();
            }
        }
    }

    @Override
    public void onProgress(Picture picture, int pictureIndex, float progress, int scanLinesCalculated) {
        if (listener != null) {
            final int calculatedSoFar = calculatedScanLines.addAndGet(scanLinesCalculated);
            float totalProgress = (float)calculatedSoFar / totalScanLines;
            listener.onProgress(calculationIndex, totalProgress);
        }
    }

    @Override
    public void onStatusChanged(Picture picture, int pictureIndex, String description) {
        // Ignore status updates, not easy to collate them from multiple picture calculations.
    }

    @Override
    public void onError(Picture picture, int pictureIndex, String description, Throwable cause) {
        if (listener != null) listener.onError(calculationIndex, description, cause);
    }

    @Override
    public void onPreviewReady(int pictureIndex, Picture preview) {
        if (listener != null) listener.onPreviewReady(calculationIndex, pictureIndex, preview);
    }

    @Override
    public void onReady(int pictureIndex, Picture picture) {
        if (listener != null) listener.onPictureReady(calculationIndex, pictureIndex, picture);
    }
}
