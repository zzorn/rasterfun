package org.rasterfun.picture;

import static org.rasterfun.utils.MathTools.clamp;
import static org.rasterfun.utils.MathTools.map;

/**
 * PictureDrawer that renders RGBA channels to a target image, replacing any missing color channel with the value channel,
 * or channel number 0 if no value channel is present.
 */
public class RgbPictureDrawer extends PictureDrawerBase {

    private int transparencyGridSize = 16;
    private final float greyLevel1 = 0.33f;
    private final float greyLevel2 = 0.66f;

    @Override
    public void draw(int[] targetData, int targetW, int targetH,
                     int destX1, int destY1, int destX2, int destY2,
                     Picture source, 
                     int sourceX1, int sourceY1, int sourceX2, int sourceY2,
                     final boolean drawAlphaGrid) {

        final int greyValue1 = clamp((int) (255 * greyLevel1), 0, 255);
        final int greyValue2 = clamp((int) (255 * greyLevel2), 0, 255);

        final int defaultIndex = source.getChannelIndex(Picture.VALUE, 0);
        final int rIndex = source.getChannelIndex(Picture.RED, defaultIndex);
        final int gIndex = source.getChannelIndex(Picture.GREEN, defaultIndex);
        final int bIndex = source.getChannelIndex(Picture.BLUE, defaultIndex);
        final int aIndex = source.getChannelIndex(Picture.ALPHA, defaultIndex);
        final int srcW = source.getWidth();
        final int srcH = source.getHeight();
        final int srcChannelCount = source.getChannelCount();
        final float[] srcData = source.getData();

        // Loop over the destination area
        for (int dy = clamp(destY1, 0, targetH); dy < destY2 && dy < targetH; dy++) {
            for (int dx = clamp(destX1, 0, targetW); dx < destX2 && dx < targetW; dx++) {

                // Map destination position to source position
                final int sx = (int) map(dx, destX1, destX2, sourceX1, sourceX2);
                final int sy = (int) map(dy, destY1, destY2, sourceY1, sourceY2);

                // Check that we are inside the picture
                if (sx >= 0 && sy >= 0 && sx < srcW && sy < srcH) {
                    // Get the pixel values at the source
                    int srcIndex = (sy * srcW + sx) * srcChannelCount;
                    int r = (int) (srcData[srcIndex + rIndex] * 255 + 0.5f);
                    int g = (int) (srcData[srcIndex + gIndex] * 255 + 0.5f);
                    int b = (int) (srcData[srcIndex + bIndex] * 255 + 0.5f);
                    int a = (int) (srcData[srcIndex + aIndex] * 255 + 0.5f);

                    // Clamp Alpha
                    if (a < 0) a = 0; else if (a > 255) a = 255;

                    // Calculate alpha grid
                    if (a < 255 && drawAlphaGrid) {
                        // Calculate alpha grid value
                        boolean darkGrid = ((((dx - destX1) / transparencyGridSize) & 1) ^
                                            (((dy - destY1) / transparencyGridSize) & 1)) != 0;
                        int gridValue = darkGrid ? greyValue1 : greyValue2;

                        // Overlay colors on alpha grid with linear interpolation
                        r = (255 * gridValue + a * (r - gridValue)) / 255;
                        g = (255 * gridValue + a * (g - gridValue)) / 255;
                        b = (255 * gridValue + a * (b - gridValue)) / 255;

                        // Alpha is now solid
                        a = 255;
                    }

                    // Clamp RGB
                    if (r < 0) r = 0; else if (r > 255) r = 255;
                    if (g < 0) g = 0; else if (g > 255) g = 255;
                    if (b < 0) b = 0; else if (b > 255) b = 255;

                    // Compose Java color and write it to target
                    final int color = ((a & 0xFF) << 24) |
                                      ((r & 0xFF) << 16) |
                                      ((g & 0xFF) << 8)  |
                                       (b & 0xFF);
                    targetData[dy * targetW + dx] = color;
                }
            }
        }

    }
}
