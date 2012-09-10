package org.rasterfun.ui.preview;

import org.rasterfun.core.PictureCalculations;
import org.rasterfun.core.listeners.PictureCalculationsListener;
import org.rasterfun.core.listeners.PictureCalculationsListenerAdapter;
import org.rasterfun.core.listeners.PictureCalculationsListenerSwingThreadAdapter;
import org.rasterfun.generator.GeneratorListener;
import org.rasterfun.generator.PictureGenerator;
import org.rasterfun.picture.Picture;
import org.rasterfun.ui.preview.arranger.ArrangerListener;
import org.rasterfun.ui.preview.arranger.PictureArranger;
import org.rasterfun.ui.preview.arranger.RowsAndColumnsArranger;
import org.rasterfun.ui.preview.arranger.ZoomLevel;
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

    private PictureArranger arranger = new RowsAndColumnsArranger();

    private JPanel mainPanel;
    private RasterPanel previewPanel;
    private JProgressBar progressBar;
    private JLabel statusBar;
    private JComboBox zoomCombo;

    // Listens to and handles mouse gestures
    private final MouseAdapter mouseListener = new MouseAdapter() {
        private int lastPanX = 0;
        private int lastPanY = 0;
        private boolean panOngoing = false;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // Zoom
            boolean viewChanged = arranger.zoom(-e.getWheelRotation(), e.getX(), e.getY());
            if (viewChanged) {
                reRender();
            }
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
                final boolean zoomChanged = arranger.setScale(1);
                if (zoomChanged) {
                    reRender();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // Pan
            if (panOngoing) {
                int deltaX = e.getX() - lastPanX;
                int deltaY = e.getY() - lastPanY;

                boolean viewChanged = arranger.pan(deltaX, deltaY);
                if (viewChanged) reRender();

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
        previewPanel = new RasterPanel(arranger);
        progressBar  = new JProgressBar();
        statusBar    = new JLabel("");
        zoomCombo    = createZoomCombo();

        // Arrange components
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(zoomCombo, BorderLayout.WEST);
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

        // Regenerate the image
        reGenerate();
    }

    private JComboBox createZoomCombo() {
        final JComboBox zoomCombo = new JComboBox(arranger.getZoomLevels().toArray());
        zoomCombo.setEditable(false);
        zoomCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZoomLevel zoomLevel = (ZoomLevel) zoomCombo.getSelectedItem();
                if (zoomLevel != null) {
                    if (arranger.setZoomLevel(zoomLevel.getStep())) reRender();
                }
            }
        });

        zoomCombo.setSelectedItem(arranger.getCurrentZoomLevel());

        arranger.addListener(new ArrangerListener() {
            @Override
            public void onZoomChanged(ZoomLevel zoomLevel) {
                zoomCombo.setSelectedItem(zoomLevel);
            }

            @Override
            public void onCenterChanged(double centerX, double centerY) {
                // Ignore
            }
        });

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
