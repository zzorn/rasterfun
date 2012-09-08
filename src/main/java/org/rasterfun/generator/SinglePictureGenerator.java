package org.rasterfun.generator;

import org.rasterfun.core.CalculatorBuilder;
import org.rasterfun.core.PictureCalculation;
import org.rasterfun.core.ProgressListener;
import org.rasterfun.parameters.Parameters;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureImpl;

import java.util.ArrayList;
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
        return Collections.singletonList(generatePicture(parameters, null));
    }

    public Picture generatePictureAndWait() {
        return generatePictureAndWait(getParameters());
    }

    public Picture generatePictureAndWait(Parameters parameters) {
        return generatePicture(parameters, null).getPictureAndWait();
    }

    public PictureCalculation generatePicture() {
        return generatePicture(getParameters(), null);
    }

    public PictureCalculation generatePicture(ProgressListener listener) {
        return generatePicture(getParameters(), listener);
    }

    public PictureCalculation generatePicture(Parameters parameters, ProgressListener listener) {

        // Create empty picture to draw on
        final PictureImpl picture = createNewBlankPicture(parameters);

        // Compose the source
        final CalculatorBuilder calculatorBuilder = buildSource();

        // Create calculation task and start it
        final PictureCalculation calculation = new PictureCalculation(parameters, picture, calculatorBuilder);
        if (listener != null) calculation.addListener(listener);
        calculation.start();

        // Return reference to ongoing calculation
        return calculation;
    }

    private PictureImpl createNewBlankPicture(Parameters parameters) {
        // Get size
        final int width = parameters.getInt("width", 128);
        final int height = parameters.getInt("height", 128);

        // Get name
        final String name = parameters.getString("name", getName());

        // Get channels
        String[] channels = parameters.getStringArray("channels", new String[]{"red", "green", "blue", "alpha"});

        // Create picture
        return new PictureImpl(name, width, height, channels);
    }


    private CalculatorBuilder buildSource() {
        // TODO
        return new CalculatorBuilder();
    }
}
