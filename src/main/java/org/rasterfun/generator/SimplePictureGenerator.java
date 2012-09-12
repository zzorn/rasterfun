package org.rasterfun.generator;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.effect.Effect;
import org.rasterfun.parameters.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SimplePictureGenerator extends PictureGeneratorBase {

    public static final String NUMBER = "number";

    private List<Effect> effects = new ArrayList<Effect>();

    public SimplePictureGenerator() {
        getParameters().set(PictureGenerator.WIDTH, 128);
        getParameters().set(PictureGenerator.HEIGHT, 128);
        getParameters().set(PictureGenerator.CHANNELS, CalculatorBuilder.DEFAULT_CHANNELS);
        getParameters().set(NUMBER, 1);
    }

    @Override
    protected List<CalculatorBuilder> createPictureSources() {

        final ArrayList<CalculatorBuilder> builders = new ArrayList<CalculatorBuilder>();

        final Integer builderCount = getParameters().get(NUMBER, 1);
        for (int i = 0; i < builderCount; i++) {
            builders.add(createPictureBuilder(i));
        }

        return builders;
    }

    private CalculatorBuilder createPictureBuilder(int pictureIndex) {
        final CalculatorBuilder builder = new CalculatorBuilder(createPictureParameters(pictureIndex));

        int effectIndex = 0;
        for (Effect effect : effects) {
            final String namespace = "_" + (effectIndex++); // TODO: Clean up mess with var prefix
            effect.initVariables(namespace);

            effect.buildSource(builder);
        }

        return builder;
    }

    protected Parameters createPictureParameters(int pictureIndex) {
        final Parameters parameters = getParameters().copy();

        parameters.set(PICTURE_INDEX, pictureIndex);

        return parameters;
    }


    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public void removeEffect(Effect effect) {
        effects.remove(effect);
    }

}
