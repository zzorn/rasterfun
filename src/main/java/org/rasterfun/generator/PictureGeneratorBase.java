package org.rasterfun.generator;

import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.library.ParametrizedGeneratorElementBase;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.parameters.ParametersListener;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.PictureEditor;
import org.rasterfun.ui.preview.PicturePreviewer;
import org.rasterfun.ui.preview.PicturePreviewerImpl;

import java.util.ArrayList;
import java.util.List;

import static org.rasterfun.utils.ParameterChecker.checkNotNull;

/**
 * Common functionality for PictureGenerators.
 */
public abstract class PictureGeneratorBase extends ParametrizedGeneratorElementBase implements PictureGenerator {

    private List<GeneratorListener> listeners = null;

    protected PictureGeneratorBase() {
        getParameters().set(PictureGenerator.NAME, getDefaultName());

        // Listen to our own parameters
        getParameters().addListener(new ParametersListener() {
            @Override
            public void onParameterChanged(Parameters parameters, String name, Object oldValue, Object newValue) {
                // Notify out listeners when our parameters are changed.
                notifyGeneratorChanged();
            }
        });
    }

    @Override
    public PicturePreviewer getPreviewer() {
        return new PicturePreviewerImpl(this);
    }

    @Override
    public PictureEditor getEditor() {
        // TODO: Provide default editor? (just edit properties)
        return null;
    }

    @Override
    public final PictureCalculations generatePictures() {
        return generatePictures(null);
    }

    @Override
    public final PictureCalculations generatePictures(PictureCalculationsListener listener) {
        return generatePictures(listener, null, null);
    }

    @Override
    public final PictureCalculations generatePictures(PictureCalculationsListener listener,
                                                      List<Picture> picturesToReuse,
                                                      List<Picture> previewsToReuse) {
        // Create calculation
        final PictureCalculations calculation = generatePicturesWithoutStarting(picturesToReuse, previewsToReuse);
        if (listener != null) calculation.addListener(listener);

        // Start it
        calculation.start();

        // Return reference to ongoing calculation
        return calculation;
    }

    @Override
    public final PictureCalculations generatePicturesWithoutStarting(List<Picture> picturesToReuse, List<Picture> previewsToReuse) {
        // Compose the source
        final List<CalculatorBuilder> builders = createPictureSources();

        // Create calculation task
        return new PictureCalculations(builders, picturesToReuse, previewsToReuse);
    }

    /**
     * @return the CalculatorBuilder with the source to generate each picture that this generator produces.
     */
    protected abstract List<CalculatorBuilder> createPictureSources();

    @Override
    public final void addListener(GeneratorListener listener) {
        checkNotNull(listener, "listener");
        if (listeners == null) {
            listeners = new ArrayList<GeneratorListener>();
        }
        listeners.add(listener);
    }

    @Override
    public final void removeListener(GeneratorListener listener) {
        checkNotNull(listener, "listener");
        if (listeners != null) listeners.remove(listener);
    }

    /**
     * @return default name for generators of this type.
     * The actual name is stored in the Parameters.
     */
    protected String getDefaultName() {
        return getClass().getSimpleName();
    }


    /**
     * Tells listeners of this PictureGenerator that it has changed, and any previews need to be redrawn, etc.
     */
    protected final void notifyGeneratorChanged() {
        if (listeners != null) {
            for (GeneratorListener listener : listeners) {
                listener.onGeneratorChanged(this);
            }
        }
    }

}
