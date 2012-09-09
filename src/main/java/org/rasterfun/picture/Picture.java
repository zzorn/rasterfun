package org.rasterfun.picture;

import org.rasterfun.library.GeneratorElement;

/**
 * Contains the image data for a picture as float values.
 * May have arbitrary channels.
 */
public interface Picture extends GeneratorElement {

    int getWidth();

    int getHeight();

    String[] getChannelNames();

    /**
     * @return the index for the specified channel
     *         Throws an exception if there was no such channel.
     */
    int getChannelIndex(String channelName);

    /**
     * @return the value at the specified channel and coordinate.
     *         Does not perform any range checking.
     *         Note that this is much slower than the getPixel version that takes a channelIndex as parameter.
     */
    float getPixel(String channel, int x, int y);

    /**
     * @return the value at the specified channel index and coordinate.
     *         Does not perform any range checking.
     */
    float getPixel(int channelIndex, int x, int y);

    /**
     * @return number of channels in the picture.
     */
    int getChannelCount();

    /**
     * @param name new name for this picture.
     */
    void setName(String name);

    /**
     * @param channelNames new channel names for this picture.
     *                     Just renames existing channels, should not change the number of channels.
     */
    void setChannelNames(String[] channelNames);

    /**
     * @return the underlying data for the picture.
     */
    float[] getData();
}
