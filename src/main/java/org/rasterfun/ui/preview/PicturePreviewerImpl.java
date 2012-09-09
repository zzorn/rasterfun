package org.rasterfun.ui.preview;

import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.generator.GeneratorListener;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.picture.Picture;

import javax.swing.*;
import java.util.List;

/**
 *
 */
public class PicturePreviewerImpl implements PicturePreviewer, GeneratorListener, PictureCalculationsListener {

    private final PictureGenerator generator;
    private PictureCalculations calculations = null;

    private List<Picture> pictures = null;
    private List<Picture> previews = null;

    public PicturePreviewerImpl(PictureGenerator generator) {
        this.generator = generator;
    }

    private JPanel previewPanel;

    @Override
    public JComponent getUiComponent() {
        if (previewPanel == null) previewPanel = createUi();

        return previewPanel;
    }

    private JPanel createUi() {

        final JPanel panel = new JPanel();

        // Create UI


        // Start listening to generator changes
        generator.addListener(this);

        return panel;
    }

    @Override
    public void onGeneratorChanged(PictureGenerator generator) {
        // Stop any earlier calculations if they are running
        if (calculations != null) calculations.stop();

        // Recalculate pictures
        calculations = generator.generatePictures(this, pictures, previews);
    }


    @Override
    public void onProgress(float progress) {
        // TODO: Implement

    }

    @Override
    public void onPreviewReady(int pictureIndex, Picture preview) {
        // TODO: Implement

    }

    @Override
    public void onPictureReady(Picture picture, int pictureIndex) {
        // TODO: Implement

    }

    @Override
    public void onError(String description, Throwable cause) {
        // TODO: Implement

    }

    @Override
    public void onReady(List<Picture> pictures) {
        // TODO: Implement

    }
}
