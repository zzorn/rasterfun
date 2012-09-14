package org.rasterfun.generator;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.Effect;
import org.rasterfun.parameters.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates one or more pictures using a set of effects.
 */
public class SimpleGenerator extends GeneratorBase {

    public static final String NUMBER = "number";

    private List<Effect> effects = new ArrayList<Effect>();

    private int nextEffectNamespaceId = 1;

    public SimpleGenerator() {
        getParameters().set(Generator.WIDTH, 128);
        getParameters().set(Generator.HEIGHT, 128);
        getParameters().set(Generator.CHANNELS, RendererBuilder.DEFAULT_CHANNELS);
        getParameters().set(NUMBER, 1);
    }

    @Override
    public List<RendererBuilder> createBuilders() {

        final ArrayList<RendererBuilder> builders = new ArrayList<RendererBuilder>();

        final Integer builderCount = getParameters().get(NUMBER, 1);
        for (int i = 0; i < builderCount; i++) {
            builders.add(createPictureBuilder(i));
        }

        return builders;
    }

    private RendererBuilder createPictureBuilder(int pictureIndex) {
        final RendererBuilder builder = new RendererBuilder(createPictureParameters(pictureIndex));

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

        // Initialize effect namespace
        final String namespace = "_" + (nextEffectNamespaceId++); // TODO: Clean up mess with var prefix
        effect.initVariables(namespace);

        notifyGeneratorChanged();

        return effect;
    }

    public void removeEffect(Effect effect) {
        effects.remove(effect);

        notifyGeneratorChanged();
    }

}
