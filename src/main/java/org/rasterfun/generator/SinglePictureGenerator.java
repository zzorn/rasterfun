package org.rasterfun.generator;

import org.rasterfun.core.CalculatorBuilder;
import org.rasterfun.core.PictureCalculation;
import org.rasterfun.core.listeners.ProgressListener;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SinglePictureGenerator extends PictureGeneratorBase {

    public SinglePictureGenerator() {
    }

    @Override
    public List<Picture> generatePicturesAndWait(Parameters parameters) {
        return Collections.singletonList(generatePictureAndWait(parameters));
    }

    @Override
    public List<PictureCalculation> generatePictures(Parameters parameters) {
        return Collections.singletonList(generatePicture(parameters, null, null));
    }

    public Picture generatePictureAndWait() {
        return generatePictureAndWait(getParameters());
    }

    public Picture generatePictureAndWait(Parameters parameters) {
        return generatePicture(parameters, null, null).getPictureAndWait();
    }

    public PictureCalculation generatePicture() {
        return generatePicture(getParameters(), null, null);
    }

    public PictureCalculation generatePicture(ProgressListener listener) {
        return generatePicture(getParameters(), listener, null);
    }

    public PictureCalculation generatePicture(ProgressListener listener, ProgressListener previewListener) {
        return generatePicture(getParameters(), listener, previewListener);
    }

    public PictureCalculation generatePicture(Parameters parameters, ProgressListener listener, ProgressListener previewListener) {

        // Create empty picture to draw on
        final PictureImpl picture = createNewBlankPicture(parameters);

        // Compose the source
        final CalculatorBuilder calculatorBuilder = buildSource();

        // Create calculation task and start it
        final PictureCalculation calculation = new PictureCalculation(parameters, picture, calculatorBuilder);
        if (listener != null) calculation.getPictureListeners().addListener(listener);
        if (previewListener != null) calculation.getPreviewListeners().addListener(previewListener);
        calculation.start();

        // Return reference to ongoing calculation
        return calculation;
    }

    private PictureImpl createNewBlankPicture(Parameters parameters) {
        // Get size
        final int width = parameters.get("width", 128);
        final int height = parameters.get("height", 128);

        // Get name
        final String name = parameters.get("name", getName());

        // Get channels
        String[] channels = parameters.get("channels", new String[]{"red", "green", "blue", "alpha"});

        // Create picture
        return new PictureImpl(name, width, height, channels);
    }


    private CalculatorBuilder buildSource() {
        // TODO
        return new CalculatorBuilder();
    }
}
