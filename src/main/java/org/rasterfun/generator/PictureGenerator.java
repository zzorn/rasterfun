package org.rasterfun.generator;

import org.rasterfun.core.PictureCalculation;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.picture.Picture;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.ui.PictureEditor;
import org.rasterfun.ui.PicturePreviewer;

import java.util.List;

/**
 * Something that generates pictures based on provided parameters.
 */
public interface PictureGenerator extends GeneratorElement {

    /**
     * @return the parameters defined for this picture generator, with their current values.
     * Edits to these parameters change the picture generator parameters, copy them first if you do not want that.
     */
    Parameters getParameters();

    /**
     * @return a class that can be used to render the picture(s) to the screen for a preview picture,
     * optionally in some picture generator specific arrangement (e.g. tile seamless pictures randomly).
     * The previewer should update whenever the picture generator parameters are updated.
     */
    PicturePreviewer getPreviewer();

    /**
     * @return an editor that can be used to edit the parameters and effects of this picture generator.
     * Changes in the editor will update the picture parameters, and trigger any preview to re-render.
     */
    PictureEditor getEditor();

    /**
     * Starts calculating the pictures produced by this generator, using the default values for the parameters.
     * @return a list of picture calculations, that can be queried for the progress of the calculation, a preview picture and the final picture.
     */
    List<PictureCalculation> generatePictures();

    /**
     * Starts calculating the pictures produced by this generator.
     * @param parameters parameters to be used to calculate the pictures.  Should match the parameters defined by this picture generator.
     * @return a list of picture calculations, that can be queried for the progress of the calculation, a preview picture and the final picture.
     */
    List<PictureCalculation> generatePictures(Parameters parameters);

    /**
     * Generates the pictures produced by this generator, using its current parameter values.
     * Blocks until the pictures are generated.
     *
     * @return a list of the generated pictures.
     */
    List<Picture> generatePicturesAndWait();

    /**
     * Generates the pictures produced by this generator.
     * Blocks until the pictures are generated.
     *
     * @param parameters parameters to be used to calculate the pictures.  Should match the parameters defined by this picture generator.
     * @return a list of the generated pictures.
     */
    List<Picture> generatePicturesAndWait(Parameters parameters);

}
