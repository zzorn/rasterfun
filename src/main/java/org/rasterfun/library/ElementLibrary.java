package org.rasterfun.library;

import org.rasterfun.ui.LibraryUi;

import javax.swing.*;

/**
 * Library that can be used to retrieve and store picture generators, effects, gradients,
 * raster pictures, and other components.
 * May have a nested hierarchical structure with sub-libraries.
 */
public interface ElementLibrary {

    /**
     * @return user interface for browsing the library and selecting/dragging elements from there to be used in the
     * currently edited generator.
     * Updated when the library changes.
     */
    LibraryUi getLibraryUi();

}
