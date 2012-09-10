package org.rasterfun.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

import static org.rasterfun.utils.ParameterChecker.checkPositiveNonZeroInteger;

/**
 * Fast, low level access image container.
 */
public class FastImage {

    private final int width;
    private final int height;

    private MemoryImageSource imageSource = null;
    private Image image = null;
    private int[] imageData = null;

    public FastImage(int width, int height) {
        checkPositiveNonZeroInteger(width, "width");
        checkPositiveNonZeroInteger(height, "height");

        this.width = width;
        this.height = height;

        initialize();
    }

    private void initialize() {

        // Don't include alpha for normal on screen rendering, as it takes longer due to masking.
        // For reference, a color model with an alpha channel would be created with
        // new DirectColorModel(32, 0xff0000, 0x00ff00, 0x0000ff, 0xff000000);
        DirectColorModel rgbColorModel = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);

        imageData = new int[width * height];
        imageSource = new MemoryImageSource(width, height, rgbColorModel, imageData, 0, width);
        imageSource.setAnimated(true);

        image = Toolkit.getDefaultToolkit().createImage(imageSource);

        clear();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getBuffer() {
        return imageData;
    }

    public void clear() {
        clearToColor(Color.WHITE);
    }

    public void clearToColor(Color color) {
        ParameterChecker.checkNotNull(color, "color");

        Arrays.fill(imageData, color.getRGB());
    }

    public Image getImage() {
        return image;
    }

    public void renderToGraphics(Graphics context) {
        context.drawImage(image, 0, 0, null);
    }

    public BufferedImage createBufferedImage() {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // TODO: Draw alpha pixels correctly

        buf.getGraphics().drawImage(image, 0, 0, null);

        return buf;
    }
}

