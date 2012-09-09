package org.rasterfun.ui.preview;

import javax.swing.*;

/**
 *
 */
public class PicturePreviewerImpl implements PicturePreviewer {
    private JPanel previewPanel;

    @Override
    public JComponent getUiComponent() {
        if (previewPanel == null) previewPanel = createUi();

        return previewPanel;
    }

    private JPanel createUi() {
        return new JPanel();
    }

}
