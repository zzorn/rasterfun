package org.rasterfun.core;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.core.listeners.CalculationListener;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.core.listeners.PictureCalculationsListenerDelegate;
import org.rasterfun.core.tasks.CompileTask;
import org.rasterfun.core.tasks.RenderTask;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;
import org.rasterfun.utils.ParameterChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Represents the calculation of one or more pictures.  Reports total progress over all pictures.
 */
public class PictureCalculations {

    public static final double DEFAULT_PREVIEW_IMAGE_SCALE_FACTOR = 0.1;
    public static final int DEFAULT_MIN_PREVIEW_IMAGE_SIZE = 8;

    private final List<RendererBuilder> rendererBuilders = new ArrayList<RendererBuilder>();
    private final List<Picture> pictures = new ArrayList<Picture>();
    private final List<Picture> previews = new ArrayList<Picture>();
    private final boolean generatePreviews;
    private final double previewImageScaleFactor;
    private final int minPreviewImageSize;

    private boolean started = false;

    private final PictureCalculationsListenerDelegate listeners = new PictureCalculationsListenerDelegate();

    private final List<Future<Picture>> rendererFutures = new ArrayList<Future<Picture>>();
    private final List<RenderTask> renderTasks = new ArrayList<RenderTask>();

    private int calculationIndex;
    private int totalPixels;
    private int totalPictures;
    private int slicesPerPreview;
    private int slicesPerPicture;
    private AtomicInteger totalCompletedPixels = new AtomicInteger(0);
    private AtomicInteger totalCompletedPictures = new AtomicInteger(0);
    private AtomicIntegerArray pictureSlicesCompleted;
    private AtomicIntegerArray previewSlicesCompleted;


    private final CalculationListener renderListener = new CalculationListener() {
        @Override
        public void onCalculationProgress(int calculationIndex, int completedPixels) {
            final int totalCompleted = totalCompletedPixels.addAndGet(completedPixels);
            float progress = (float) totalCompleted / totalPixels;
            listeners.onProgress(calculationIndex, progress);
        }

        @Override
        public void onPictureSliceReady(int calculationIndex, int pictureIndex, Picture picture, boolean isPreview) {

            if (isPreview) {
                final int previewSlicesReady = previewSlicesCompleted.incrementAndGet(pictureIndex);

                // For increased sanity points..
                assert previewSlicesReady <= slicesPerPreview :
                        "We should not have more slices ready than what there can be in a preview picture.  " +
                        "slicesPerPreview: " + slicesPerPreview + ", " +
                        "previewSlicesReady:" + previewSlicesReady;

                if (previewSlicesReady == slicesPerPreview) {
                    // The picture is complete, notify listeners
                    listeners.onPreviewReady(calculationIndex, pictureIndex, picture);
                }
            }
            else {
                final int pictureSlicesReady = pictureSlicesCompleted.incrementAndGet(pictureIndex);

                // For increased sanity points..
                assert pictureSlicesReady <= slicesPerPicture :
                        "We should not have more slices ready than what there can be in a picture.  " +
                        "slicesPerPicture: " + slicesPerPicture + ", " +
                        "pictureSlicesReady:" + pictureSlicesReady;

                if (pictureSlicesReady == slicesPerPicture) {
                    // The picture is complete, notify listeners
                    listeners.onPictureReady(calculationIndex, pictureIndex, picture);

                    // Check if everything is complete
                    final int currentlyCompletedPictures = totalCompletedPictures.incrementAndGet();
                    if (currentlyCompletedPictures == totalPictures) {
                        // Yep, notify
                        listeners.onReady(calculationIndex, pictures);
                    }
                }
            }

        }

        @Override
        public void onError(int calculationIndex, String shortSummary, String longDescription, Throwable cause) {
            listeners.onError(calculationIndex, shortSummary, longDescription, cause);
        }
    };


    /**
     * Creates a calculation to generate the specified picture, and a preview picture as well if they would be large enough.
     * The picture to render to will be allocated as needed.
     *
     * @param rendererBuilder the source for the calculator, used to generate the actual calculator.
     */
    public PictureCalculations(RendererBuilder rendererBuilder) {
        this(Collections.singletonList(rendererBuilder), null, null, true);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if they would be large enough.
     * The pictures to render to will be allocated as needed.
     *
     * @param rendererBuilders the source for the calculators, used to generate the actual calculators.
     */
    public PictureCalculations(List<RendererBuilder> rendererBuilders) {
        this(rendererBuilders, null, null, true);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if they would be large enough.
     *
     * @param rendererBuilders the source for the calculators, used to generate the actual calculators.
     * @param pictures target pictures to reuse and render to.  If null, or wrong size or numbers, new pictures will be created.
     */
    public PictureCalculations(List<RendererBuilder> rendererBuilders, List<Picture> pictures, List<Picture> previews) {
        this(rendererBuilders, pictures, previews, true);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if specified.
     *
     * @param rendererBuilders the source for the calculators, used to generate the actual calculators.
     * @param pictures target pictures to reuse and render to.  If null, or wrong size or numbers, new pictures will be created.
     * @param generatePreviews if true, smaller preview images will be rendered.
     */
    public PictureCalculations(List<RendererBuilder> rendererBuilders,
                               List<Picture> pictures,
                               List<Picture> previews,
                               boolean generatePreviews) {
        this(rendererBuilders, pictures, previews, generatePreviews, DEFAULT_PREVIEW_IMAGE_SCALE_FACTOR, DEFAULT_MIN_PREVIEW_IMAGE_SIZE);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if specified.
     *
     * @param rendererBuilders the source for the calculators, used to generate the actual calculators.
     * @param pictures target pictures to reuse and render to.  If null, or wrong size or numbers, new pictures will be created.
     * @param generatePreviews if true, smaller preview images will be rendered.
     * @param previewImageScaleFactor if a preview should be generated, this tells the scaling to use for it.
     *                                E.g. 0.1 will generate a preview picture that has 1/10 the width and height of the original.
     * @param minPreviewImageSize if the preview picture would have a width or height smaller than this, it will not be generated.
     */
    public PictureCalculations(List<RendererBuilder> rendererBuilders,
                               List<Picture> pictures,
                               List<Picture> previews,
                               boolean generatePreviews,
                               double previewImageScaleFactor,
                               int minPreviewImageSize) {
        ParameterChecker.checkNotNull(rendererBuilders, "rendererBuilders");
        ParameterChecker.checkPositiveNonZeroNormalNumber(previewImageScaleFactor, "previewImageScaleFactor");
        ParameterChecker.checkPositiveNonZeroInteger(minPreviewImageSize, "minPreviewImageSize");

        this.rendererBuilders.addAll(rendererBuilders);
        if (pictures != null) this.pictures.addAll(pictures);
        if (previews != null) this.previews.addAll(previews);
        this.generatePreviews = generatePreviews;
        this.previewImageScaleFactor = previewImageScaleFactor;
        this.minPreviewImageSize = minPreviewImageSize;
    }



    /**
     * Starts the calculation of the picture.
     * Can only be called once, called by default when the PictureGenerator generatePicture(s) method is called,
     * so there is normally no need for the API user to call this.
     */
    public void start() {
        start(0);
    }

    /**
     * Starts the calculation of the picture.
     * Can only be called once, called by default when the PictureGenerator generatePicture(s) method is called,
     * so there is normally no need for the API user to call this.
     *
     * @param calculationIndex an index passed along to the calculations, and used in notification messages to listeners,
     *                         to make it easier to tell different calculations apart,
     *                         if the same listener is used to listen to them.
     */
    public void start(int calculationIndex) {
        if (started) throw new IllegalStateException("Can not start calculation, it has already been started.");
        started = true;

        this.calculationIndex = calculationIndex;

        totalPictures = rendererBuilders.size();

        // Create or reuse the needed pictures and preview pictures
        try {
            int pictureIndex = 0;
            for (RendererBuilder builder : rendererBuilders) {
                final String   name         = builder.getName();
                final int      width        = builder.getWidth();
                final int      height       = builder.getHeight();
                final List<String> channels = builder.getPictureChannels();

                // Check the passed in images, if any are missing or the wrong size then we re-create them
                Picture picture = getPictureAtOrNull(pictures, pictureIndex);
                picture = reuseOrRecreate(picture, name, width, height, channels);
                pictures.set(pictureIndex, picture);

                // Calculate preview size
                int previewWidth  = (int)(previewImageScaleFactor * picture.getWidth());
                int previewHeight = (int)(previewImageScaleFactor * picture.getHeight());

                // Create or reuse preview if we should generate one
                Picture preview = getPictureAtOrNull(previews, pictureIndex);
                if (shouldGeneratePreview(previewWidth, previewHeight)) {
                    preview = reuseOrRecreate(preview, name + " Preview", previewWidth, previewHeight, channels);
                    previews.set(pictureIndex, preview);
                }
                else {
                    previews.set(pictureIndex, null);
                }

                pictureIndex++;
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            // Abort this calculation
            pictures.clear();
            previews.clear();

            // Notify user
            listeners.onError(calculationIndex,
                              "Not enough memory for pictures",
                              "The computer does not have enough memory to hold \n" +
                              "all the pictures produced by this picture generator.  \n" +
                              "Try reducing the number of generated images or their size",
                              outOfMemoryError);

            // Clear any preview images
            for (int i = 0; i < totalPictures; i++) {
                listeners.onPreviewReady(calculationIndex, i, null);
                listeners.onPictureReady(calculationIndex, i, null);
            }

            // Force a garbage run
            System.gc();

            return;
        }

        // Discard unused pictures
        discardDownToLength(pictures, rendererBuilders.size());
        discardDownToLength(previews, rendererBuilders.size());

        // Calculate total pixels to calculate, so that we can estimate progress
        for (Picture picture : pictures) {
            if (picture != null) totalPixels += picture.getWidth() * picture.getHeight();
        }

        // Calculate number of slices to divide the previews and pictures in for processing
        slicesPerPreview = 1; // Just do one task per preview for now
        slicesPerPicture = 1 + Runtime.getRuntime().availableProcessors() / totalPictures;
        pictureSlicesCompleted = new AtomicIntegerArray(pictures.size());
        previewSlicesCompleted = new AtomicIntegerArray(previews.size());

        // Start compiling all the image calculators
        List<Future<Renderer>> pixelCalculatorFutures = new ArrayList<Future<Renderer>>();
        for (RendererBuilder rendererBuilder : rendererBuilders) {
            pixelCalculatorFutures.add(RasterfunApplication.getExecutor().submit(new CompileTask(calculationIndex,
                    rendererBuilder,
                                                                                                 renderListener)));
        }

        // Start calculating preview pictures
        createPictureRenderingTasks(calculationIndex,
                                    pixelCalculatorFutures,
                                    previews,
                                    true,
                                    slicesPerPreview);

        // Start calculating actual pictures
        createPictureRenderingTasks(calculationIndex,
                                    pixelCalculatorFutures,
                                    pictures,
                                    false,
                                    slicesPerPicture);
    }

    private void createPictureRenderingTasks(int calculationIndex,
                                             List<Future<Renderer>> pixelCalculatorFutures,
                                             final List<Picture> pictures,
                                             final boolean forPreviews,
                                             final int slicesPerPicture) {

        final ExecutorService executor = RasterfunApplication.getExecutor();

        int pictureIndex = 0;
        for (Picture picture: pictures) {
            if (picture != null) {
                int rowsPerSlice = picture.getHeight() / slicesPerPicture;
                int y = 0;
                for (int i = 0; i < slicesPerPicture; i++) {

                    // Calculate which pixels to calculate
                    int startY = y;

                    // Advance a fixed step, for last step make sure we get all pixels
                    if (i < slicesPerPicture - 1) y += rowsPerSlice;
                    else y = picture.getHeight();

                    int endY = y;

                    // Create render task to render the slice
                    final RenderTask renderTask = new RenderTask(calculationIndex,
                                                                 pictureIndex,
                                                                 forPreviews,
                                                                 startY,
                                                                 endY,
                                                                 picture,
                                                                 pixelCalculatorFutures.get(pictureIndex),
                                                                 renderListener);

                    // Keep track of the task instance so that we can stop it if needed.
                    renderTasks.add(renderTask);

                    // Keep track of the future so that we can wait for all tasks to complete if we want.
                    rendererFutures.add(executor.submit(renderTask));
                }
            }

            pictureIndex++;
        }
    }

    /**
     * @return the calculation index specified for this set of picture calculations.
     *         It is specified when calling start, and reported in the listeners, to help distinguish calculation runs.
     */
    public int getCalculationIndex() {
        return calculationIndex;
    }

    /**
     * @return a list with the preview pictures being calculated, or null if there is no preview for a specific picture.
     *
     * Before the calculation is completed, this is updated from separate calculation threads,
     * so the data may not be valid.
     */
    public List<Picture> getPreviews() {
        return previews;
    }

    /**
     * @return a list with the pictures being calculated.
     *
     * Before the calculation is completed, this is updated from separate calculation threads,
     * so the data may not be valid.
     */
    public List<Picture> getPictures() {
        return pictures;
    }

    /**
     * @return the pictures, waiting until they have been calculated if calculation is still ongoing.
     * Returns null if there was some error or the calculation was stopped.
     */
    public List<Picture> getPicturesAndWait() {
        try {
            // Ensure all calculation tasks are ready
            for (Future<Picture> rendererFuture : rendererFutures) {
                if (rendererFuture != null) rendererFuture.get();
            }

            // Return the pictures
            return pictures;

        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error when waiting for pictures to finish rendering: " + e.getMessage(), e);
        }
    }

    /**
     * @return a read only list with the builders that are used to build the pictures.
     * Contain some picture metadata such as picture size and name.
     */
    public List<RendererBuilder> getRendererBuilders() {
        return Collections.unmodifiableList(rendererBuilders);
    }

    /**
     * Stops the calculations.
     */
    public void stop() {
        for (RenderTask renderTask : renderTasks) {
            renderTask.stop();
        }
    }

    /**
     * Add a listener that gets notified about progress, completed pictures and previews, and when all pictures are completed.
     *
     * Note the listener is called from the calculation thread, so if the listener does any UI updates
     * it should call SwingUtils.invokeLater or similar, and if it does state updates they should
     * take into account concurrency concerns.
     *
     */
    public void addListener(PictureCalculationsListener listener) {
        listeners.addListener(listener);
    }

    /**
     * @param listener listener to remove
     */
    public void removeListener(PictureCalculationsListener listener) {
        listeners.removeListener(listener);
    }


    private boolean shouldGeneratePreview(int previewWidth, int previewHeight) {
        return generatePreviews &&
               previewHeight >= minPreviewImageSize &&
               previewWidth  >= minPreviewImageSize;
    }

    private Picture reuseOrRecreate(Picture picture, String name, int width, int height, List<String> channels) {
        if (picture == null ||
            // Recreate
            picture.getWidth()  != width ||
            picture.getHeight() != height ||
            picture.getChannelCount() != channels.size()) {
            picture = new PictureImpl(name, width, height, channels);
        }
        else {
            // Reuse
            picture.setName(name);
            picture.setChannelNames(channels);
        }

        return picture;
    }

    private Picture getPictureAtOrNull(List<Picture> list, int pictureIndex) {
        Picture picture = null;
        if (pictureIndex < list.size()) {
            picture = list.get(pictureIndex);
        }
        else {
            // Create space filled with nulls up to the index
            while (list.size() <= pictureIndex + 1) {
                list.add(null);
            }
        }
        return picture;
    }

    private void discardDownToLength(List<Picture> list, int targetLength) {
        while (list.size() > targetLength) {
            // Remove last
            list.remove(list.size() - 1);
        }
    }

    /**
     * @return true if this calculation is completed.
     */
    public boolean isDone() {
        for (Future<Picture> rendererFuture : rendererFutures) {
            if (!rendererFuture.isDone()) return false;
        }
        return true;
    }
}
