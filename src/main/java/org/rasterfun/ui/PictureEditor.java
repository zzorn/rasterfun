package org.rasterfun.ui;

import org.rasterfun.library.ElementLibrary;

import javax.swing.*;

/**
 * An editor that can be used to generate a picture generator.
 */
public interface PictureEditor {

    /**
     * Specify the effects and other builtin and custom elements that are available for use.
     */
    void setLibrary(ElementLibrary library);

    /**
     * @return ui component with the editor.
     */
    JComponent getUiComponent();

}
