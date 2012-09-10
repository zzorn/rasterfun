package org.rasterfun.utils;

/**
 * Something that renders an image to a FastImage.
 */
public interface FastImageRenderer {

    /**
     * Called when the view area changes size.
     */
    void setViewSize(int width, int height);

    /**
     * @param target the target to render to.
     */
    void renderImage(FastImage target);

}
