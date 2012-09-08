package org.rasterfun.picture;

import org.rasterfun.library.GeneratorElement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public final class PictureImpl implements Picture {

    private final int width;
    private final int height;
    private final int channelCount;
    private final String[] channelNames;
    private final float data[];
    private String name;

    private final HashMap<String, Integer> channelNameToIndex = new HashMap<String, Integer>();

    public PictureImpl(String name, int width, int height, List<String> channelNames) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.channelNames = channelNames.toArray(new String[channelNames.size()]);
        this.channelCount = this.channelNames.length;
        this.data = new float[width * height * channelCount];

        // Initialize lookup map
        for (int i = 0; i < this.channelNames.length; i++) {
            channelNameToIndex.put(this.channelNames[i], i);
        }
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
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public int getChannelIndex(String channelName) {
        final Integer channelIndex = channelNameToIndex.get(channelName);
        if (channelIndex != null) return channelIndex;
        else throw new IllegalArgumentException("There is no channel with the name '"+channelName+"', " +
                                                "the available channels are: " + Arrays.toString(channelNames));
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

    @Override
    public float getPixel(String channel, int x, int y) {
        return getPixel(getChannelIndex(channel), x, y);
    }

    @Override
    public float getPixel(int channelIndex, int x, int y) {
        return data[(y * width + x) * channelCount + channelIndex];
    }

    @Override
    public int getChannelCount() {
        return channelCount;
    }
}
