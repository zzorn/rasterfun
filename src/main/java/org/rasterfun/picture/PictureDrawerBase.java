package org.rasterfun.picture;

import org.rasterfun.utils.FastImage;

/**
 *
 */
public abstract class PictureDrawerBase implements PictureDrawer {
    @Override
    public final void draw(FastImage fastImage, int destX1, int destY1, int destX2, int destY2, Picture source, int sourceX1, int sourceY1, int sourceX2, int sourceY2) {
        draw(fastImage.getBuffer(), fastImage.getWidth(), fastImage.getHeight(),
             destX1, destY1, destX2, destY2,
             source,
             sourceX1, sourceY1, sourceX2, sourceY2,
             true);

    }

    public final void draw(int[] targetData, int targetW, int targetH,
                           int destX1, int destY1, int destX2, int destY2,
                           Picture source,
                           int sourceX1, int sourceY1, int sourceX2, int sourceY2) {
        draw(targetData, targetW, targetH,
             destX1, destY1, destX2, destY2,
             source,
             sourceX1, sourceY1, sourceX2, sourceY2,
             true);
    }

}
