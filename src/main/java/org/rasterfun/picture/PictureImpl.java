package org.rasterfun.picture;

import org.rasterfun.library.GeneratorElement;
import org.rasterfun.utils.ParameterChecker;

import java.util.Arrays;
import java.util.HashMap;

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

    public PictureImpl(String name, int width, int height, String[] channelNames) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNotNull(channelNames, "channelNames");
        ParameterChecker.checkPositiveNonZeroInteger(width, "width");
        ParameterChecker.checkPositiveNonZeroInteger(height, "height");
        ParameterChecker.checkPositiveNonZeroInteger(channelNames.length, "channelNames.length");

        this.name = name;
        this.width = width;
        this.height = height;
        this.channelNames = Arrays.copyOf(channelNames, channelNames.length);
        this.channelCount = this.channelNames.length;
        this.data = new float[width * height * channelCount];

        createChannelNamesLookup();
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

    @Override
    public int getChannelIndex(String channelName, int defaultValue) {
        final Integer channelIndex = channelNameToIndex.get(channelName);
        if (channelIndex != null) return channelIndex;
        else return defaultValue;
    }

    public void setName(String name) {
        ParameterChecker.checkNonEmptyString(name, "name");
        this.name = name;
    }

    @Override
    public void setChannelNames(String[] channelNames) {
        ParameterChecker.checkNotNull(channelNames, "channelNames");
        if (channelNames.length != this.channelNames.length) throw new IllegalArgumentException("New channel names must have same size as the existing ones.");

        System.arraycopy(channelNames, 0, this.channelNames, 0, this.channelNames.length);
        createChannelNamesLookup();
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

    private void createChannelNamesLookup() {
        channelNameToIndex.clear();
        for (int i = 0; i < this.channelNames.length; i++) {
            channelNameToIndex.put(this.channelNames[i], i);
        }
    }

}
