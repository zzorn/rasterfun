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
    public void onProgress(final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onProgress(progress);
            }
        });
    }

    @Override
    public void onPreviewReady(final int pictureIndex, final Picture preview) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onPreviewReady(pictureIndex, preview);
            }
        });
    }

    @Override
    public void onPictureReady(final Picture picture, final int pictureIndex) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onPictureReady(picture, pictureIndex);
            }
        });
    }

    @Override
    public void onError(final String description, final Throwable cause) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onError(description, cause);
            }
        });
    }

    @Override
    public void onReady(final List<Picture> pictures) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                delegate.onReady(pictures);
            }
        });
    }
}
