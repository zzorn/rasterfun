package org.rasterfun.component;

/**
 *
 */
public interface Renderer {

    // Channel indexes in target array, for pictures:
    // TODO: Similar constants for maps?  How to handle?  (height, ecotype, etc)
    static final int RED = 0;
    static final int GREEN = 1;
    static final int BLUE = 2;
    static final int ALPHA = 3;
    static final int HEIGHT = 4;
    static final int LUMINANCE = 5;
    static final int SPECULARITY = 6;

    void renderPixel(float x, float y, float sampleSize, float[] target);

}
