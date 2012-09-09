package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

import java.util.List;

/**
 * Adapter for PictureCalculationsListener, override the methods that you are interested in.
 */
public abstract class PictureCalculationsListenerAdapter implements PictureCalculationsListener {

    @Override
    public void onProgress(float progress) {
    }

    @Override
    public void onPreviewReady(int pictureIndex, Picture preview) {
    }

    @Override
    public void onPictureReady(Picture picture, int pictureIndex) {
    }

    @Override
    public void onError(String description, Throwable cause) {
    }

    @Override
    public void onReady(List<Picture> pictures) {
    }
}
