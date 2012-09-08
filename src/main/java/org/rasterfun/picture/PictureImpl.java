package org.rasterfun.picture;

import org.rasterfun.library.GeneratorElement;

import java.util.List;

/**
 *
 */
public class PictureImpl implements Picture {

    private final int width;
    private final int height;
    private final int channelCount;
    private final List<String> channelNames;
    private final float data[];
    private String name;

    public PictureImpl(String name, int width, int height, List<String> channelNames) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.channelNames = channelNames;
        channelCount = channelNames.size();
        data = new float[width * height * channelCount];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<String> getChannelNames() {
        return channelNames;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getData() {
        return data;
    }

    @Override
    public GeneratorElement copy() {
        // TODO: Implement
        return null;
    }
}
