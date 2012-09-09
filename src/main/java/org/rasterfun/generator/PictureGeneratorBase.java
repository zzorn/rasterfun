package org.rasterfun.generator;

import org.rasterfun.core.PictureCalculation;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.parameters.ParametersImpl;
import org.rasterfun.parameters.ParametersListener;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.PictureEditor;
import org.rasterfun.ui.preview.PicturePreviewer;
import org.rasterfun.ui.preview.PicturePreviewerImpl;
import org.rasterfun.utils.ParameterChecker;

import java.util.ArrayList;
import java.util.List;

import static org.rasterfun.utils.ParameterChecker.*;

/**
 * Common functionality for PictureGenerators.
 */
public abstract class PictureGeneratorBase implements PictureGenerator {

    private final Parameters parameters = new ParametersImpl();
    private String name = getClass().getSimpleName();
    private List<GeneratorListener> listeners = null;

    private final ParametersListener parameterListener = new ParametersListener() {
        @Override
        public void onParameterChanged(Parameters parameters, String name, Object oldValue, Object newValue) {
            // Notify out listeners when our parameters are changed.
            if (listeners != null) {
                for (GeneratorListener listener : listeners) {
                    listener.onChanged(PictureGeneratorBase.this);
                }
            }
        }
    };

    protected PictureGeneratorBase() {
        // Listen to our own parameters
        parameters.addListener(parameterListener);
    }

    @Override
    public Parameters getParameters() {
        return parameters;
    }

    @Override
    public PicturePreviewer getPreviewer() {
        return new PicturePreviewerImpl();
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
    public void addListener(GeneratorListener listener) {
        checkNotNull(listener, "listener");
        if (listeners == null) {
            listeners = new ArrayList<GeneratorListener>();
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(GeneratorListener listener) {
        checkNotNull(listener, "listener");
        if (listeners != null) listeners.remove(listener);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkNonEmptyString(name, "name");
        this.name = name;
    }

    @Override
    public final GeneratorElement copy() {
        // Clone instance
        final PictureGeneratorBase theCopy = createCopyOfClass();

        // Copy parameters
        theCopy.getParameters().addParameterCopies(parameters);

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
