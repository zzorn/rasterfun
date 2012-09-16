package org.rasterfun.ui.generator;

import org.rasterfun.generator.Generator;

import javax.swing.*;

/**
 *
 */
public class GeneratorEditorImpl implements GeneratorEditor {

    private Generator generator;

    public GeneratorEditorImpl() {
    }

    @Override
    public JComponent getGeneratorBrowseUi() {
        // TODO: Implement
        return new JPanel();
    }

    @Override
    public JComponent getGeneratorEditorUi() {
        // TODO: Implement
        return new JPanel();
    }

    @Override
    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    @Override
    public Generator getGenerator() {
        return generator;
    }
}
