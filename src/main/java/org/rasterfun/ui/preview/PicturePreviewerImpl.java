package org.rasterfun.ui.preview;

import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.core.listeners.PictureCalculationsListenerAdapter;
import org.rasterfun.core.listeners.PictureCalculationsListenerSwingThreadAdapter;
import org.rasterfun.generator.GeneratorListener;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.preview.arranger.Arranger;
import org.rasterfun.ui.preview.arranger.ArrangerListener;
import org.rasterfun.ui.preview.arranger.RowsAndColumnsArranger;
import org.rasterfun.ui.preview.arranger.ZoomLevel;
import org.rasterfun.utils.JComboBoxWithWheelScroll;
import org.rasterfun.utils.RasterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Swing panel that shows the pictures generated by a specified generator.
 */
public class PicturePreviewerImpl implements PicturePreviewer {

    private static final int PAN_BUTTON = MouseEvent.BUTTON1;
    private static final int RESET_ZOOM_BUTTON = MouseEvent.BUTTON2; // Clicking the scrollwheel sets zoom to 1:1
    private final PictureGenerator generator;
    private PictureCalculations calculations = null;

    private List<Picture> pictures = null;
    private List<Picture> previews = null;

    private Arranger arranger = new RowsAndColumnsArranger();

    private JPanel mainPanel;
    private RasterPanel previewPanel;
    private JProgressBar progressBar;
    private JLabel statusBar;
    private JComboBox zoomCombo;
    private JButton zoom100Button;

    // Listens to and handles mouse gestures
    private final MouseAdapter mouseListener = new MouseAdapter() {
        private int lastPanX = 0;
        private int lastPanY = 0;
        private boolean panOngoing = false;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // Zoom
            arranger.zoom(-e.getWheelRotation(), e.getX(), e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == PAN_BUTTON) {
                panOngoing = true;
                lastPanX = e.getX();
                lastPanY = e.getY();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == PAN_BUTTON && panOngoing) {
                panOngoing = false;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == RESET_ZOOM_BUTTON) {
                arranger.setScale(1);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // Pan
            if (panOngoing) {
                int deltaX = e.getX() - lastPanX;
                int deltaY = e.getY() - lastPanY;

                arranger.pan(deltaX, deltaY);

                lastPanX = e.getX();
                lastPanY = e.getY();
            }
        }
    };

    // Listens to parameter changes in the generator
    private final GeneratorListener generatorListener = new GeneratorListener() {
        @Override
        public void onGeneratorChanged(PictureGenerator generator) {
            reGenerate();
        }
    };

    // Listens to picture generation progress
    private final PictureCalculationsListener calculationsListener =
            new PictureCalculationsListenerSwingThreadAdapter(
                    new PictureCalculationsListenerAdapter() {
        @Override
        public void onProgress(final float progress) {
            progressBar.setValue((int) (progress * 100));
        }

        @Override
        public void onPreviewReady(int pictureIndex, Picture preview) {
            arranger.setPreview(pictureIndex, preview);
            reRender();
        }

        @Override
        public void onPictureReady(Picture picture, int pictureIndex) {
            arranger.setPicture(pictureIndex, picture);
            reRender();
        }

        @Override
        public void onError(String description, Throwable cause) {
            progressBar.setValue(0);
            setStatusBarMessage(description);
            statusBar.setToolTipText(description);

            // Log the stack trace to standard out for debugging
            cause.printStackTrace();

            reRender();
        }

        @Override
        public void onReady(List<Picture> pictures) {
            setStatusBarMessage("Done.");
            reRender();
        }
    });

    private final ArrangerListener arrangerListener = new ArrangerListener() {
        @Override
        public void onZoomChanged(ZoomLevel zoomLevel) {
            zoomCombo.setSelectedItem(zoomLevel);
            reRender();
        }

        @Override
        public void onCenterChanged(double centerX, double centerY) {
            reRender();
        }

        @Override
        public void onLayoutUpdated(ZoomLevel zoomLevel, double centerX, double centerY) {
            zoomCombo.setSelectedItem(zoomLevel);
            reRender();
        }
    };

    public PicturePreviewerImpl(PictureGenerator generator) {
        this.generator = generator;
    }

    @Override
    public JComponent getUiComponent() {
        if (mainPanel == null) buildUi();

        return mainPanel;
    }

    private void buildUi() {
        // Create UI components
        previewPanel  = new RasterPanel(arranger);
        progressBar   = new JProgressBar();
        statusBar     = new JLabel("");
        zoomCombo     = createZoomCombo();
        zoom100Button = createZoom100Button();

        // Arrange components
        JPanel bottomLeft = new JPanel(new FlowLayout());
        bottomLeft.add(new JLabel("Zoom"));
        bottomLeft.add(zoomCombo);
        bottomLeft.add(zoom100Button);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bottomLeft, BorderLayout.WEST);
        bottomPanel.add(statusBar, BorderLayout.CENTER);
        bottomPanel.add(progressBar, BorderLayout.EAST);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Listen to view panning and zooming
        previewPanel.addMouseListener(mouseListener);
        previewPanel.addMouseMotionListener(mouseListener);
        previewPanel.addMouseWheelListener(mouseListener);

        // Listen to changes to the generator
        generator.addListener(generatorListener);

        // Listens to updates in the layout or panning
        arranger.addListener(arrangerListener);

        // Regenerate the image
        reGenerate();
    }

    private JButton createZoom100Button() {
        JButton button = new JButton("1:1");
        button.setMargin(new Insets(2, 1, 1, 1));
        button.setToolTipText("Set zoom to 100% and center view");
        button.setFocusable(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                arranger.setZoomLevel(Arranger.DEFAULT_ZOOM_LEVEL);
                arranger.center();
            }
        });

        return button;
    }

    private JComboBox createZoomCombo() {
        final JComboBox zoomCombo = new JComboBoxWithWheelScroll(arranger.getZoomLevels().toArray());
        zoomCombo.setEditable(false);
        zoomCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZoomLevel zoomLevel = (ZoomLevel) zoomCombo.getSelectedItem();
                if (zoomLevel != null) {
                    arranger.setZoomLevel(zoomLevel.getStep());
                }
            }
        });

        zoomCombo.setSelectedItem(arranger.getCurrentZoomLevel());

        return zoomCombo;
    }

    private void reGenerate() {
        // Stop any earlier calculations if they are running
        if (calculations != null) {
            calculations.removeListener(calculationsListener);
            calculations.stop();
        }

        // Reset progress ui
        progressBar.setValue(0);
        setStatusBarMessage("Calculating...");
        statusBar.setToolTipText(null);

        // Create calculation
        calculations = generator.generatePicturesWithoutStarting(pictures, previews);
        calculations.addListener(calculationsListener);

        // Clear preview
        arranger.setContentInfo(calculations.getCalculatorBuilders());

        // Start calculating
        calculations.start();

        // Draw current view
        reRender();
    }

    private void setStatusBarMessage(final String message) {
        statusBar.setText("  " + message);
    }

    private void reRender() {
        // Render the visible image from our calculated pictures
        if (previewPanel != null) {
            previewPanel.reRender();
        }
    }



}
