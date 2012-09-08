package org.rasterfun.picture;

import org.rasterfun.library.GeneratorElement;

import java.util.List;

/**
 * Contains the image data for a picture as float values.
 * May have arbitrary channels.
 */
public interface Picture extends GeneratorElement {

    int getWidth();

    int getHeight();

    List<String> getChannelNames();
}
