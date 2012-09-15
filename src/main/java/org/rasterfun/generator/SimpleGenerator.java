package org.rasterfun.generator;

import org.rasterfun.core.compiler.RendererBuilder;
import org.rasterfun.effect.Effect;
import org.rasterfun.effect.container.EffectContainer;
import org.rasterfun.effect.container.EffectContainerImpl;
import org.rasterfun.effect.container.EffectContainerListener;
import org.rasterfun.library.GeneratorElement;
import org.rasterfun.utils.ParameterChecker;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generates one or more pictures using a set of effects.
 * All the pictures will have the same base name, size and channels.
 */
public final class SimpleGenerator extends GeneratorBase {

    private String name = "Picture";
    private int width = 128;
    private int height = 128;
    private int count = 1;

    private final EffectContainer effectContainer;

    private final EffectContainerListener effectContainerListener = new EffectContainerListener() {
        @Override
        public void onContainerChanged(EffectContainer container) {
            notifyGeneratorChanged();
        }

        @Override
        public void onEffectChanged(Effect effect) {
            notifyGeneratorChanged();
        }
    };



    private SimpleGenerator(SimpleGenerator source) {
        this(source.name,
             source.width,
             source.height,
             source.count,
             new ArrayList<String>(source.getChannels()),
             source.effectContainer.copy());
    }

    public SimpleGenerator() {
        this("Picture", 128, 128);
    }

    public SimpleGenerator(String name, int width, int height) {
        this(name, width, height, 1);
    }

    public SimpleGenerator(String name, int width, int height, int count) {
        this(name, width, height, count, Arrays.asList(new String[]{"red", "green", "blue", "alpha"}));
    }

    public SimpleGenerator(String name, int width, int height, int count, List<String> channels) {
        this(name, width, height, count,channels, new EffectContainerImpl());
    }

    private SimpleGenerator(String name, int width, int height, int count, List<String> channels, EffectContainer effectContainer) {
        this.effectContainer = effectContainer;
        this.effectContainer.addListener(effectContainerListener);

        setName(name);
        setChannels(channels);
        setSize(width, height);
        setCount(count);
    }


    public EffectContainer getEffectContainer() {
        return effectContainer;
    }

    @Override
    public GeneratorElement copy() {
        return new SimpleGenerator(this);
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCount() {
        return count;
    }

    public void setName(String name) {
        ParameterChecker.checkNonEmptyString(name, "name");

        if (!this.name.equals(name)) {
            this.name = name;
            notifyGeneratorChanged();
        }
    }

    public void setSize(int width, int height) {
        ParameterChecker.checkPositiveNonZeroInteger(height, "height");
        ParameterChecker.checkPositiveNonZeroInteger(width, "width");

        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            notifyGeneratorChanged();
        }
    }

    public void setWidth(int width) {
        ParameterChecker.checkPositiveNonZeroInteger(width, "width");

        if (this.width != width) {
            this.width = width;
            notifyGeneratorChanged();
        }
    }

    public void setHeight(int height) {
        ParameterChecker.checkPositiveNonZeroInteger(height, "height");

        if (this.height != height) {
            this.height = height;
            notifyGeneratorChanged();
        }
    }

    public void setCount(int count) {
        ParameterChecker.checkPositiveNonZeroInteger(count, "count");

        if (this.count != count) {
            this.count = count;
            notifyGeneratorChanged();
        }
    }

    public Collection<String> getChannels() {
        return effectContainer.getChannels();
    }

    public void setChannels(List<String> channels) {
        effectContainer.setChannels(channels);
    }

    public void addChannel(String channelName) {
        effectContainer.addChannel(channelName);
    }

    public void removeChannel(String channelName) {
        effectContainer.removeChannel(channelName);
    }

    public List<Effect> getEffects() {
        return effectContainer.getEffects();
    }

    @Override
    public List<RendererBuilder> createBuilders() {

        final ArrayList<RendererBuilder> builders = new ArrayList<RendererBuilder>();

        for (int i = 0; i < count; i++) {
            builders.add(createPictureBuilder(i, count));
        }

        return builders;
    }

    private RendererBuilder createPictureBuilder(int pictureIndex, int totalCount) {
        final RendererBuilder builder = new RendererBuilder(name, width, height, effectContainer.getChannels(), pictureIndex, totalCount);

        effectContainer.buildSource(builder, "var_", null);

        return builder;
    }

    public <T extends Effect> T addEffect(T effect) {
        effectContainer.addEffect(effect);
        return effect;
    }

    public void removeEffect(Effect effect) {
        effectContainer.removeEffect(effect);
    }


}
