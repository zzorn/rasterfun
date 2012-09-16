package org.rasterfun.generator;

import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.PictureEditor;
import org.rasterfun.ui.preview.PicturePreviewer;

import java.util.List;

/**
 * Something that generates pictures based on provided parameters.
 */
// TODO: Return calculations unstarted, start manually
public interface Generator extends GeneratorElement {

    /**
     * @return a class that can be used to render the picture(s) to the screen for a preview picture,
     * optionally in some picture generator specific arrangement (e.g. tile seamless pictures randomly).
     * The previewer should update whenever the picture generator parameters are updated.
     */
    PicturePreviewer createPreviewer();

    /**
     * @return an editor that can be used to edit the parameters and effects of this picture generator.
     * Changes in the editor will update the picture parameters, and trigger any preview to re-render.
     */
    PictureEditor createEditor();

    /**
     * Starts calculating the pictures produced by this generator, using the default values for the parameters.
     *
     * @return an object representing the ongoing calculation, that can be queried for a preview picture and the final picture.
     *         Also has a method to wait for the calculation to complete and return the calculated pictures.
     */
    PictureCalculations generatePictures();

    /**
     * Starts calculating the pictures produced by this generator.
     *
     *
     * @param listener a listener that is notified about the progress of the calculation.
     * @return an object representing the ongoing calculation, that can be queried for a preview picture and the final picture.
     *         Also has a method to wait for the calculation to complete and return the calculated pictures.
     */
    PictureCalculations generatePictures(PictureCalculationsListener listener);

    /**
     * Starts calculating the pictures produced by this generator.
     *
     *
     * @param listener a listener that is notified about the progress of the calculation.
     * @param picturesToReuse a list of pictures to reuse when calculating the pictures.  Should be the same size and
     *                        channel number to be useful.  Normally you would pass in pictures that were returned by
     *                        an earlier call to generatePictures when you are regenerating a view.
     *                        If null, new pictures will be allocated instead.
     * @param previewsToReuse same as picturesToReuse, except for preview pictures.
     * @return an object representing the ongoing calculation, that can be queried for a preview picture and the final picture.
     *         Also has a method to wait for the calculation to complete and return the calculated pictures.
     */
    PictureCalculations generatePictures(PictureCalculationsListener listener,
                                         List<Picture> picturesToReuse,
                                         List<Picture> previewsToReuse);

    /**
     * Returns a calculator for pictures produced by this generator.
     * Does not start the calculator before returning it.
     *
     * @param picturesToReuse a list of pictures to reuse when calculating the pictures.  Should be the same size and
     *                        channel number to be useful.  Normally you would pass in pictures that were returned by
     *                        an earlier call to generatePictures when you are regenerating a view.
     *                        If null, new pictures will be allocated instead.
     * @param previewsToReuse same as picturesToReuse, except for preview pictures.
     * @return an object representing the ongoing calculation, that can be queried for a preview picture and the final picture.
     *         Also has a method to wait for the calculation to complete and return the calculated pictures.
     */
    // TODO: By default, do not start the calculator, and do not take listener parameter
    PictureCalculations generatePicturesWithoutStarting(List<Picture> picturesToReuse,
                                                        List<Picture> previewsToReuse);

    /**
     * @param listener a listener that should be notified when the generator changes.
     */
    void addListener(GeneratorListener listener);

    /**
     * @param listener listener to remove.
     */
    void removeListener(GeneratorListener listener);


    /**
     * @return the RendererBuilder with the source to generate each picture that this generator produces.
     */
    public abstract List<RendererBuilder> createBuilders();

}