package org.rasterfun.picture;

import org.rasterfun.utils.FastImage;

/**
 * Draws a picture to an array of java color intergers at some position with some optional scaling.
 */
public interface PictureDrawer {

    void draw(FastImage fastImage, int destX1, int destY1, int destX2, int destY2,
              Picture source, int sourceX1, int sourceY1, int sourceX2, int sourceY2);

    void draw(int[] targetData, int targetW, int targetH,
              int destX1, int destY1, int destX2, int destY2,
              Picture source,
              int sourceX1, int sourceY1, int sourceX2, int sourceY2);

    void draw(int[] targetData, int targetW, int targetH,
              int destX1, int destY1, int destX2, int destY2,
              Picture source,
              int sourceX1, int sourceY1, int sourceX2, int sourceY2,
              final boolean drawAlphaGrid);

}
