package org.rasterfun.ui;

import javax.swing.*;

/**
 * Used to preview the picture(s) produced by a picture generator.
 */
public interface PicturePreviewer {

    /**
     * @return ui component with the preview, and optionally any other needed controls for manipulating the preview.
     */
    JComponent getUiComponent();

}
