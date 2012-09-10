package org.rasterfun.ui.preview.arranger;

/**
 *
 */
public interface ArrangerListener {

    void onZoomChanged(ZoomLevel zoomLevel);

    void onCenterChanged(double centerX, double centerY);

}
