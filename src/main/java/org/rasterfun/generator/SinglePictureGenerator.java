package org.rasterfun.generator;

import org.rasterfun.core.compiler.CalculatorBuilder;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SinglePictureGenerator extends PictureGeneratorBase {

    public SinglePictureGenerator() {
        getParameters().set(PictureGenerator.NAME, getName());
        getParameters().set(PictureGenerator.WIDTH, 128);
        getParameters().set(PictureGenerator.HEIGHT, 128);
        getParameters().set(PictureGenerator.CHANNELS, CalculatorBuilder.DEFAULT_CHANNELS);
    }

    @Override
    protected List<CalculatorBuilder> createPictureSources() {
        // Compose the source
        final CalculatorBuilder builder = new CalculatorBuilder(getParameters());

        // TODO: Create source

        return Collections.singletonList(builder);
    }



}
