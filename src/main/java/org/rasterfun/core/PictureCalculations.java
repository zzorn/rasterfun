package org.rasterfun.core;

import org.rasterfun.RasterfunApplication;
import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.core.listeners.PictureCalculationsListenerDelegate;
import org.rasterfun.core.tasks.CalculatePicturesTask;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;
import org.rasterfun.utils.ParameterChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Represents the calculation of one or more pictures.  Reports total progress over all pictures.
 */
public class PictureCalculations {

    public static final double DEFAULT_PREVIEW_IMAGE_SCALE_FACTOR = 0.1;
    public static final int DEFAULT_MIN_PREVIEW_IMAGE_SIZE = 8;

    private final List<CalculatorBuilder> calculatorBuilders = new ArrayList<CalculatorBuilder>();
    private final List<Picture> pictures = new ArrayList<Picture>();
    private final List<Picture> previews = new ArrayList<Picture>();
    private final boolean generatePreviews;
    private final double previewImageScaleFactor;
    private final int minPreviewImageSize;

    private boolean started = false;
    private CalculatePicturesTask calculationTask;
    private Future<List<Picture>> picturesFuture = null;

    private final PictureCalculationsListenerDelegate listeners = new PictureCalculationsListenerDelegate();


    /**
     * Creates a calculation to generate the specified picture, and a preview picture as well if they would be large enough.
     * The picture to render to will be allocated as needed.
     *
     * @param calculatorBuilder the source for the calculator, used to generate the actual calculator.
     */
    public PictureCalculations(CalculatorBuilder calculatorBuilder) {
        this(Collections.singletonList(calculatorBuilder), null, null, true);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if they would be large enough.
     * The pictures to render to will be allocated as needed.
     *
     * @param calculatorBuilders the source for the calculators, used to generate the actual calculators.
     */
    public PictureCalculations(List<CalculatorBuilder> calculatorBuilders) {
        this(calculatorBuilders, null, null, true);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if they would be large enough.
     *
     * @param calculatorBuilders the source for the calculators, used to generate the actual calculators.
     * @param pictures target pictures to reuse and render to.  If null, or wrong size or numbers, new pictures will be created.
     */
    public PictureCalculations(List<CalculatorBuilder> calculatorBuilders, List<Picture> pictures, List<Picture> previews) {
        this(calculatorBuilders, pictures, previews, true);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if specified.
     *
     * @param calculatorBuilders the source for the calculators, used to generate the actual calculators.
     * @param pictures target pictures to reuse and render to.  If null, or wrong size or numbers, new pictures will be created.
     * @param generatePreviews if true, smaller preview images will be rendered.
     */
    public PictureCalculations(List<CalculatorBuilder> calculatorBuilders,
                               List<Picture> pictures,
                               List<Picture> previews,
                               boolean generatePreviews) {
        this(calculatorBuilders, pictures, previews, generatePreviews, DEFAULT_PREVIEW_IMAGE_SCALE_FACTOR, DEFAULT_MIN_PREVIEW_IMAGE_SIZE);
    }

    /**
     * Creates a calculation to generate the specified pictures, and preview pictures as well if specified.
     *
     * @param calculatorBuilders the source for the calculators, used to generate the actual calculators.
     * @param pictures target pictures to reuse and render to.  If null, or wrong size or numbers, new pictures will be created.
     * @param generatePreviews if true, smaller preview images will be rendered.
     * @param previewImageScaleFactor if a preview should be generated, this tells the scaling to use for it.
     *                                E.g. 0.1 will generate a preview picture that has 1/10 the width and height of the original.
     * @param minPreviewImageSize if the preview picture would have a width or height smaller than this, it will not be generated.
     */
    public PictureCalculations(List<CalculatorBuilder> calculatorBuilders,
                               List<Picture> pictures,
                               List<Picture> previews,
                               boolean generatePreviews,
                               double previewImageScaleFactor,
                               int minPreviewImageSize) {
        ParameterChecker.checkNotNull(calculatorBuilders, "calculatorBuilders");
        ParameterChecker.checkPositiveNonZeroNormalNumber(previewImageScaleFactor, "previewImageScaleFactor");
        ParameterChecker.checkPositiveNonZeroInteger(minPreviewImageSize, "minPreviewImageSize");

        this.calculatorBuilders.addAll(calculatorBuilders);
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

        // Create or reuse the needed pictures and preview pictures
        int pictureIndex = 0;
        for (CalculatorBuilder builder : calculatorBuilders) {
            final String   name     = builder.getName();
            final int      width    = builder.getWidth();
            final int      height   = builder.getHeight();
            final String[] channels = builder.getChannels();

            // Check the passed in images, if any are missing or the wrong size then we re-create them
            Picture picture = getPictureAtOrNull(pictures, pictureIndex);
            picture = reuseOrRecreate(picture, name, width, height, channels);
            pictures.set(pictureIndex, picture);

            // Calculate preview size
            int previewWidth  = (int)(previewImageScaleFactor * picture.getWidth());
            int previewHeight = (int)(previewImageScaleFactor * picture.getHeight());

            // Create or reuse preview if we should generate one
            Picture preview = null;
            if (shouldGeneratePreview(previewWidth, previewHeight)) {
                preview = getPictureAtOrNull(previews, pictureIndex);
                preview = reuseOrRecreate(preview, name + " Preview", previewWidth, previewHeight, channels);
            }
            previews.set(pictureIndex, preview);

            pictureIndex++;
        }

        // Discard unused pictures
        discardDownToLength(pictures, calculatorBuilders.size());
        discardDownToLength(previews, calculatorBuilders.size());

        // Create task to calculate the pictures, and start it
        calculationTask = new CalculatePicturesTask(calculationIndex, calculatorBuilders, pictures, previews, listeners);
        picturesFuture = RasterfunApplication.getExecutor().submit(calculationTask);
    }

    /**
     * @return future for the pictures being calculated.
     */
    public Future<List<Picture>> getPicturesFuture() {
        return picturesFuture;
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
            return picturesFuture.get();
        } catch (Exception e) {
            // The error has already been reported
            return null;
        }
    }

    /**
     * @return a read only list with the builders that are used to build the pictures.
     * Contain some picture metadata such as picture size and name.
     */
    public List<CalculatorBuilder> getCalculatorBuilders() {
        return Collections.unmodifiableList(calculatorBuilders);
    }

    /**
     * @return true if the calculation of the pictures has finished, or the calculation has stopped for some other reason.
     */
    public boolean isDone() {
        return picturesFuture.isDone();
    }

    /**
     * Stops the calculations.
     */
    public void stop() {
        if (calculationTask != null) {
            calculationTask.stop();
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

    private Picture reuseOrRecreate(Picture picture, String name, int width, int height, String[] channels) {
        if (picture == null ||
            // Recreate
            picture.getWidth()  != width ||
            picture.getHeight() != height ||
            picture.getChannelCount() != channels.length) {
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
        if (list.size() < pictureIndex) {
            picture = list.get(pictureIndex);
        }
        else {
            // Create space filled with nulls up to the index
            while (list.size() <= pictureIndex) {
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

}
