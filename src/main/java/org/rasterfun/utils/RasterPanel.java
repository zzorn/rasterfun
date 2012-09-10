package org.rasterfun.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A Swing Panel that shows a backing FastImage.
 */
public class RasterPanel extends JPanel {

    private FastImageRenderer renderer;
    private FastImage fastImage = null;
    private boolean imagePainted = false;

    public RasterPanel() {
        this(null);

        setOpaque(true);
    }

    /**
     * @param renderer something that can render the contents of the image when needed.
     */
    public RasterPanel(FastImageRenderer renderer) {
        this.renderer = renderer;

        // Listen to resizes, and re-render the image when a resize happened
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {

                final int width = getWidth();
                final int height = getHeight();

                // Create image if we have any size to work with.
                if (width > 0 && height > 0) fastImage = new FastImage(width, height);
                else fastImage = null;

                // Notify renderer about the new size
                if (RasterPanel.this.renderer != null) {
                    RasterPanel.this.renderer.setViewSize(width, height);
                }

                reRender();
            }
        });
    }

    /**
     * @return the renderer used to render this image.
     */
    public FastImageRenderer getRenderer() {
        return renderer;
    }

    /**
     * @param renderer the renderer to use for rendering this image.
     */
    public void setRenderer(FastImageRenderer renderer) {
        this.renderer = renderer;
        renderer.setViewSize(getWidth(), getHeight());
        reRender();
    }

    /**
     * Triggers a re-render of the image and a repaint of this panel.
     */
    public void reRender() {
        imagePainted = false;
        repaint();
    }

    public void paintComponent(Graphics g) {
        if (fastImage != null) {

            // Re-render the image if needed
            if (!imagePainted && renderer != null) {
                renderer.renderImage(fastImage);
                imagePainted = true;

                // Flush the image to be sure we have the latest version
                fastImage.getImage().flush();
            }

            // Draw the renderer image to the panel
            fastImage.renderToGraphics(g);
        }
    }

}