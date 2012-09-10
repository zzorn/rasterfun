package org.rasterfun.ui.preview.arranger;

/**
 * Listener that is notified about changes in the arranged view.
 */
public interface ArrangerListener {

    /**
     * Called when the zoom level has changed.
     * @param zoomLevel new zoom level.
     */
    void onZoomChanged(ZoomLevel zoomLevel);

    /**
     * Called when the center location has changed.
     * @param centerX x location on the previewed ensemble canvas that should be at the screen center.
     * @param centerY y location on the previewed ensemble canvas that should be at the screen center.
     */
    void onCenterChanged(double centerX, double centerY);

    /**
     * Called when the layout or content of the arranged preview ensemble has changed.
     * Passed in are the current zoom level and center location.
     */
    void onLayoutUpdated(ZoomLevel zoomLevel, double centerX, double centerY);

}
