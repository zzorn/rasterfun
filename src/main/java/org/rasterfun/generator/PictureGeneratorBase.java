package org.rasterfun.generator;

import org.rasterfun.core.PictureCalculation;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.parameters.ParametersImpl;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.PictureEditor;
import org.rasterfun.ui.PicturePreviewer;

import java.util.List;

/**
 * Common functionality for PictureGenerators.
 */
public abstract class PictureGeneratorBase implements PictureGenerator {

    private String name = getClass().getSimpleName();
    private Parameters parameters = new ParametersImpl();

    @Override
    public Parameters getParameters() {
        return parameters;
    }

    @Override
    public PicturePreviewer getPreviewer() {
        // TODO: Provide default previewer
        return null;
    }

    @Override
    public PictureEditor getEditor() {
        // TODO: Provide default editor? (just edit properties)
        return null;
    }

    @Override
    public final List<PictureCalculation> generatePictures() {
        return generatePictures(getParameters());
    }

    @Override
    public List<Picture> generatePicturesAndWait() {
        return generatePicturesAndWait(getParameters());
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public final GeneratorElement copy() {
        // Clone instance
        final PictureGeneratorBase theCopy = createCopyOfClass();

        // Copy parameters
        theCopy.setParameters(getParameters().copy());

        // Do instance specific state copying
        initializeCopy(theCopy);

        return theCopy;
    }

    /**
     * Called when a copy has been done of this generator.
     * Provides opportunity for generator implementations to copy over any implementation specific state.
     */
    protected void initializeCopy(PictureGeneratorBase theCopy) {}

    protected PictureGeneratorBase createCopyOfClass() {
        try {
            return getClass().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Problem when copying a class instance of type "+getClass().getSimpleName()+": " + e, e);
        }
    }
}
