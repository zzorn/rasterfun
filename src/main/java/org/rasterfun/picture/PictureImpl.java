package org.rasterfun.picture;

import org.rasterfun.library.GeneratorElement;
import org.rasterfun.utils.ParameterChecker;

import java.util.*;

/**
 *
 */
public final class PictureImpl implements Picture {

    private final int width;
    private final int height;
    private final int channelCount;
    private final List<String> channelNames;
    private final float data[];
    private String name;

    private final HashMap<String, Integer> channelNameToIndex = new HashMap<String, Integer>();

    public PictureImpl(String name, int width, int height, List<String> channelNames) {
        ParameterChecker.checkNonEmptyString(name, "name");
        ParameterChecker.checkNotNull(channelNames, "channelNames");
        ParameterChecker.checkPositiveNonZeroInteger(width, "width");
        ParameterChecker.checkPositiveNonZeroInteger(height, "height");
        ParameterChecker.checkPositiveNonZeroInteger(channelNames.size(), "channelNames.size");

        this.name = name;
        this.width = width;
        this.height = height;
        this.channelNames = new ArrayList<String>(channelNames);
        this.channelCount = this.channelNames.size();
        this.data = new float[width * height * channelCount];

        createChannelNamesLookup();
    }

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
        return Collections.unmodifiableList(channelNames);
    }

    @Override
    public int getChannelIndex(String channelName) {
        final Integer channelIndex = channelNameToIndex.get(channelName);
        if (channelIndex != null) return channelIndex;
        else throw new IllegalArgumentException("There is no channel with the name '"+channelName+"', " +
                                                "the available channels are: " + Arrays.toString(channelNames.toArray()));
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
    public void setChannelNames(List<String> channelNames) {
        ParameterChecker.checkNotNull(channelNames, "channelNames");
        if (channelNames.size() != this.channelNames.size()) throw new IllegalArgumentException("New channel names must have same size as the existing ones.");

        this.channelNames.clear();
        this.channelNames.addAll(channelNames);
        createChannelNamesLookup();
    }

    public float[] getData() {
        return data;
    }

    @Override
    public GeneratorElement copy() {
        // NOTE: Picture references in generator properties should probably be treated as references -
        // a change to the picture modifies the generator.  So we just return a reference to ourselves.
        return this;
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
        for (int i = 0; i < channelNames.size(); i++) {
            channelNameToIndex.put(channelNames.get(i), i);
        }
    }

}
