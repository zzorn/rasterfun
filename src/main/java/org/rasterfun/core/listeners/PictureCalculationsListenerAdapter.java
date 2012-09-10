package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

import java.util.List;

/**
 * Adapter for PictureCalculationsListener, override the methods that you are interested in.
 */
public abstract class PictureCalculationsListenerAdapter implements PictureCalculationsListener {

    @Override
    public void onProgress(int calculationIndex, float progress) {
    }

    @Override
    public void onPreviewReady(int calculationIndex, int pictureIndex, Picture preview) {
    }

    @Override
    public void onPictureReady(int calculationIndex, int pictureIndex, Picture picture) {
    }

    @Override
    public void onError(int calculationIndex, String shortDescription, String longDescription, Throwable cause) {
    }

    @Override
    public void onReady(int calculationIndex, List<Picture> pictures) {
    }
}
