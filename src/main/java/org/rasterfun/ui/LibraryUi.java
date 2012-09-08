package org.rasterfun.ui;

import org.rasterfun.library.ElementLibrary;

import javax.swing.*;

/**
 * User interface for browsing the library and selecting/dragging elements from there to be used in the
 * currently edited generator.
 * Updated when the library changes.
 */
public class LibraryUi {

    private final ElementLibrary library;

    public LibraryUi(ElementLibrary library) {
        this.library = library;
    }

    /**
     * @return ui component with the editor.
     */
    JComponent getUiComponent() {
        final JPanel libraryPanel = new JPanel();



        return libraryPanel;
    }

}
