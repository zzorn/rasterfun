package org.rasterfun.utils;

import javax.swing.*;
import java.awt.*;

/**
 * A Swing JFrame with sensible default settings.
 */
public class SimpleFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    public SimpleFrame() {
        this("", null);
    }

    public SimpleFrame(String title) {
        this(title, null);
    }

    public SimpleFrame(String title, JComponent content) {
        this(title, content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public SimpleFrame(String title, JComponent content, int initialWidth, int initialHeight) {
        setTitle(title);
        if (content != null) setContentPane(content);
        setPreferredSize(new Dimension(initialWidth, initialHeight));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }
}

