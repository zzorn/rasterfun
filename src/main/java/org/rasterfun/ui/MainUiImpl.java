package org.rasterfun.ui;

import org.rasterfun.generator.Generator;
import org.rasterfun.ui.generator.GeneratorEditor;
import org.rasterfun.ui.generator.GeneratorEditorImpl;
import org.rasterfun.ui.preview.PicturePreviewer;
import org.rasterfun.ui.preview.PicturePreviewerImpl;
import org.rasterfun.utils.SimpleFrame;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;

/**
 *
 */
public class MainUiImpl implements MainUi {

    private boolean isUiBuilt = false;
    private PicturePreviewer picturePreviewer;
    private GeneratorEditor generatorEditor;
    private SimpleFrame frame;

    public MainUiImpl() {
    }


    @Override
    public void showGenerator(Generator generator) {
        // TODO: Implement

        if (picturePreviewer != null) picturePreviewer.setGenerator(generator);
        if (generatorEditor != null) generatorEditor.setGenerator(generator);
    }

    @Override
    public void show() {
        if (!isUiBuilt) buildUi();
        else frame.setVisible(true);
    }

    private void buildUi() {
        isUiBuilt = true;

        // Create UI parts
        this.generatorEditor = new GeneratorEditorImpl();
        picturePreviewer = new PicturePreviewerImpl();
        JComponent generatorBrowser = this.generatorEditor.getGeneratorBrowseUi();
        JComponent generatorEditor = this.generatorEditor.getGeneratorEditorUi();
        JComponent projectBrowser = new JPanel();
        JComponent libraryBrowser = new JPanel();
        JComponent preview = picturePreviewer.getUiComponent();

        // Arrange UI
        JPanel mainPanel      = new JPanel(new BorderLayout());
        JSplitPane midSplit   = new JSplitPane(VERTICAL_SPLIT,   true, preview, generatorBrowser);
        JSplitPane rightSplit = new JSplitPane(HORIZONTAL_SPLIT, true, midSplit, generatorEditor);
        JSplitPane leftSplit  = new JSplitPane(VERTICAL_SPLIT,   true, projectBrowser, libraryBrowser);
        JSplitPane majorSplit = new JSplitPane(HORIZONTAL_SPLIT, true, leftSplit, rightSplit );

        midSplit.setResizeWeight(0.75);
        rightSplit.setResizeWeight(0.75);
        leftSplit.setResizeWeight(0.5);
        majorSplit.setResizeWeight(0.25);

        mainPanel.add(majorSplit, BorderLayout.CENTER);

        // Create and show frame
        frame = new SimpleFrame("RasterFun", mainPanel);
    }
}
