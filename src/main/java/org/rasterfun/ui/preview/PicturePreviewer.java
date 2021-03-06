package org.rasterfun.ui.preview;

import org.rasterfun.generator.Generator;

import javax.swing.*;

/**
 * Used to preview the picture(s) produced by a picture generator.
 */
public interface PicturePreviewer {

    /**
     * @return ui component with the preview, and optionally any other needed controls for manipulating the preview.
     */
    JComponent getUiComponent();

    Generator getGenerator();

    void setGenerator(Generator generator);
}
