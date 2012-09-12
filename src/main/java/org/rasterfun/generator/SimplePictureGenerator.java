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

    private int nextEffectNamespaceId = 1;

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

        for (Effect effect : effects) {

            effect.buildSource(builder);
        }

        return builder;
    }

    protected Parameters createPictureParameters(int pictureIndex) {
        final Parameters parameters = getParameters().copy();

        parameters.set(PICTURE_INDEX, pictureIndex);

        return parameters;
    }


    public <T extends Effect> T addEffect(T effect) {
        effects.add(effect);

        // Initialize effect namesapce
        final String namespace = "_" + (nextEffectNamespaceId++); // TODO: Clean up mess with var prefix
        effect.initVariables(namespace);

        return effect;
    }

    public void removeEffect(Effect effect) {
        effects.remove(effect);
    }

}
