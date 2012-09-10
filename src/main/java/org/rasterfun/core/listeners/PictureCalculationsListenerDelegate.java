package org.rasterfun.core.listeners;

import org.rasterfun.picture.Picture;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Delegate that forwards incoming events to listeners.
 */
public class PictureCalculationsListenerDelegate implements PictureCalculationsListener {

    // Usually we have only a few listeners, so a CopyOnWriteArrayList is an easy way to make adding and removing listeners thread safe without much memory or performance overhead.
    private final CopyOnWriteArrayList<PictureCalculationsListener> listeners = new CopyOnWriteArrayList<PictureCalculationsListener>();

    public void onProgress(int calculationIndex, float progress) {
        for (PictureCalculationsListener listener : listeners) {
            listener.onProgress(calculationIndex, progress);
        }
    }
    public void onPreviewReady(int calculationIndex, int pictureIndex, Picture preview) {
        for (PictureCalculationsListener listener : listeners) {
            listener.onPreviewReady(calculationIndex, pictureIndex, preview);
        }
    }
    public void onPictureReady(int calculationIndex, int pictureIndex, Picture picture) {
        for (PictureCalculationsListener listener : listeners) {
            listener.onPictureReady(calculationIndex, pictureIndex, picture);
        }
    }
    public void onError(int calculationIndex, String shortDescription, String longDescription, Throwable cause) {
        for (PictureCalculationsListener listener : listeners) {
            listener.onError(calculationIndex, shortDescription, longDescription, cause);
        }
    }
    public void onReady(int calculationIndex, List<Picture> pictures) {
        for (PictureCalculationsListener listener : listeners) {
            listener.onReady(calculationIndex, pictures);
        }
    }

    public void addListener(PictureCalculationsListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PictureCalculationsListener listener) {
        listeners.remove(listener);
    }
}
