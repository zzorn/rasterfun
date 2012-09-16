package org.rasterfun.ui.generator;

import org.rasterfun.generator.Generator;

import javax.swing.*;

/**
 * An editor that can be used to edit the parameters and effects of this picture generator.
 * Changes in the editor will update the picture parameters, and trigger any preview to re-render.
 */
public interface GeneratorEditor {

    /**
     * @return ui component with a view of the effects in a generator.
     */
    JComponent getGeneratorBrowseUi();

    /**
     * @return ui component with a property editor for the currently selected effect or other element.
     */
    JComponent getGeneratorEditorUi();


    void setGenerator(Generator generator);

    Generator getGenerator();

}
