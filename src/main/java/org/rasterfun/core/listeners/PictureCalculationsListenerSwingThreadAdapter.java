package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

import javax.swing.*;
import java.util.List;

/**
 * Delegates events it gets to the specified listener, in the Swing thread.
 */
public class PictureCalculationsListenerSwingThreadAdapter implements PictureCalculationsListener {
    private final PictureCalculationsListener delegate;

    public PictureCalculationsListenerSwingThreadAdapter(PictureCalculationsListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onProgress(final int calculationIndex, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onProgress(calculationIndex, progress);
            }
        });
    }

    @Override
    public void onPreviewReady(final int calculationIndex, final int pictureIndex, final Picture preview) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onPreviewReady(calculationIndex, pictureIndex, preview);
            }
        });
    }


    @Override
    public void onPictureReady(final int calculationIndex, final int pictureIndex, final Picture picture) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onPictureReady(calculationIndex, pictureIndex, picture);
            }
        });
    }

    @Override
    public void onError(final int calculationIndex, final String description, final Throwable cause) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onError(calculationIndex, description, cause);
            }
        });
    }

    @Override
    public void onReady(final int calculationIndex, final List<Picture> pictures) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onReady(calculationIndex, pictures);
            }
        });
    }
}
