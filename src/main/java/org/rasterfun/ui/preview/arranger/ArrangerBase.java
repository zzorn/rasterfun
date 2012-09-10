package org.rasterfun.ui.preview.arranger;

import org.rasterfun.core.compiler.CalculatorBuilder;
import org.rasterfun.picture.Picture;
import org.rasterfun.picture.PictureDrawer;
import org.rasterfun.picture.RgbPictureDrawer;
import org.rasterfun.utils.FastImage;
import org.rasterfun.utils.MathTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.pow;
import static org.rasterfun.utils.MathTools.clamp;

/**
 *
 */
// TODO: This class is getting long, some functionality could maybe be refactored into a separate class?
public abstract class ArrangerBase implements Arranger {

    private static final int MAX_ZOOM_LEVEL = 10;
    private static final int MIN_ZOOM_LEVEL = -10;
    private static final double ZOOM_SCALE_PER_STEP =  2;

    private List<CalculatorBuilder> builders = null;

    private double centerX = 0;
    private double centerY = 0;
    private double minX = 0;
    private double maxX = 0;
    private double minY = 0;
    private double maxY = 0;

    private double scale = 1;
    private int scaleStep = 0;

    private int viewWidth = 0;
    private int viewHeight = 0;

    private boolean firstResizeHandled = false;
    private boolean userZoomedOrPanned = false;

    private List<Picture> pictures = new ArrayList<Picture>();
    private List<Picture> previews = new ArrayList<Picture>();
    private List<Integer> pictureCalculationIndexes = new ArrayList<Integer>();

    private PictureDrawer drawer = new RgbPictureDrawer();
    private List<ZoomLevel> zoomLevels;

    private List<ArrangerListener> listeners = new ArrayList<ArrangerListener>();

    protected ArrangerBase() {
        zoomLevels = new ArrayList<ZoomLevel>();
        for (int i = MIN_ZOOM_LEVEL; i <= MAX_ZOOM_LEVEL; i++) {
            zoomLevels.add(new ZoomLevel(i, scaleForScaleStep(i)));
        }
    }

    @Override
    public final void setContentInfo(List<CalculatorBuilder> builders) {
        // Check if we need to recalculate the layout
        boolean differencesFound = builderListsDifferEnoughForReLayout(this.builders, builders);

        this.builders = builders;

        onContentChanged(builders);

        if (differencesFound) {
            pictures.clear();
            previews.clear();
            pictureCalculationIndexes.clear();

            for (int i = 0; i < builders.size(); i++) {
                pictures.add(null);
                previews.add(null);
                pictureCalculationIndexes.add(0);
            }

            clearScreen();
            layoutAndNotify();
        }
    }

    private boolean builderListsDifferEnoughForReLayout(List<CalculatorBuilder> oldBuilders, List<CalculatorBuilder> newBuilders) {
        if (newBuilders == null || oldBuilders == null || oldBuilders.size() != newBuilders.size()) {
            // Different number, or either null, need to relayout always.
            return true;
        } else {
            int i = 0;
            for (CalculatorBuilder newBuilder : newBuilders) {
                CalculatorBuilder oldBuidler = oldBuilders.get(i++);

                // Check for differences
                if (builderDifferEnoughToForceReLayout(oldBuidler, newBuilder)) {
                    return true;
                }
            }
        }

        // No significant differences from layout perspective found, old layout stands.
        return false;
    }

    protected boolean builderDifferEnoughToForceReLayout(CalculatorBuilder oldBuidler, CalculatorBuilder newBuilder) {
        return oldBuidler.getWidth()  != newBuilder.getWidth() ||
               oldBuidler.getHeight() != newBuilder.getHeight();
    }

    @Override
    public final void zoom(int steps, double focusX, double focusY) {
        int oldScaleStep = scaleStep;
        double oldScale = scale;
        scaleStep += steps;
        scaleStep = clamp(scaleStep, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);

        if (scaleStep != oldScaleStep) {
            // Recalculate scale
            scale = scaleForScaleStep(scaleStep);

            // Use the focus point as the zoom origo, which should stay unmoving on the screen
            final double viewCenterX = (double) viewWidth / 2;
            final double viewCenterY = (double) viewHeight / 2;
            final double screenDX = focusX - viewCenterX;
            final double screenDY = focusY - viewCenterY;
            final double scaleChange = scale / oldScale;
            final double dx = screenDX * (1 - scaleChange);
            final double dy = screenDY * (1 - scaleChange);
            pan(dx, dy);

            userZoomedOrPanned = true;
            notifyZoomChanged();
        }
    }

    @Override
    public final void pan(double deltaX, double deltaY) {
        double oldCenterX = centerX;
        double oldCenterY = centerY;

        centerX -= deltaX / scale;
        centerY -= deltaY / scale;

        clampCenter(minX, minY, maxX, maxY);

        boolean centerMoved = centerX != oldCenterX ||
                              centerY != oldCenterY;

        if (centerMoved) {
            userZoomedOrPanned = true;
            notifyCenterChanged();
        }
    }

    @Override
    public void reLayout() {
        // Reset flag telling to leave user view settings alone
        userZoomedOrPanned = false;

        // Do relayout
        layoutAndNotify();
    }

    @Override
    public final void center() {
        double oldCenterX = centerX;
        double oldCenterY = centerY;

        centerX = 0;
        centerY = 0;

        clampCenter(minX, minY, maxX, maxY);

        if (centerX != oldCenterX ||
            centerY != oldCenterY) {
            userZoomedOrPanned = true;
            notifyCenterChanged();
        }
    }

    @Override
    public final void setPreview(int calculationIndex, int pictureIndex, Picture preview) {
        previews.set(pictureIndex, preview);

        // If the preview calculation index is larger than the calculation index for the corresponding
        // picture, wipe the picture.
        if (calculationIndex > pictureCalculationIndexes.get(pictureIndex)) {
            pictures.set(pictureIndex, null);
        }
    }

    @Override
    public final void setPicture(int calculationIndex, int pictureIndex, Picture picture) {
        pictures.set(pictureIndex, picture);
        pictureCalculationIndexes.set(pictureIndex, calculationIndex);
    }

    @Override
    public List<ZoomLevel> getZoomLevels() {
        return zoomLevels;
    }

    @Override
    public ZoomLevel getCurrentZoomLevel() {
        for (ZoomLevel zoomLevel : zoomLevels) {
            if (zoomLevel.getStep() == scaleStep) return zoomLevel;
        }
        return null;
    }

    public PictureDrawer getDrawer() {
        return drawer;
    }

    public void setDrawer(PictureDrawer drawer) {
        this.drawer = drawer;
    }

    protected final List<CalculatorBuilder> getBuilders() {
        if (builders == null) return Collections.emptyList();
        else return builders;
    }

    protected final double getScale() {
        return scale;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    @Override
    public final void setViewSize(int width, int height) {
        viewWidth = width;
        viewHeight = height;
        onViewSizeChanged(width, height);

        // Only relayout if this is the first resize, or the user has not yet manually changed the view.
        if (!firstResizeHandled || !userZoomedOrPanned) {
            layoutAndNotify();
        }
        
        firstResizeHandled = true;
    }

    protected void onViewSizeChanged(int width, int height) {}

    public final void setScale(double targetScale) {
        int originalScaleStep = scaleStep;

        // Find closest zoom level one step out that fits this one.
        ZoomLevel closestZoomLevel = null;
        for (ZoomLevel zoomLevel : zoomLevels) {
            if (closestZoomLevel == null ||
                zoomLevel.distanceToScale(targetScale) < closestZoomLevel.distanceToScale(targetScale)) {
                closestZoomLevel = zoomLevel;
            }
        }

        scaleStep = closestZoomLevel.getStep();

        // Check if scale changed
        if (scaleStep != originalScaleStep) {
            scale = scaleForScaleStep(scaleStep);
            notifyZoomChanged();
        }
    }

    private void notifyZoomChanged() {
        // Tell derived class about scale change
        onZoomChanged(scale);

        // Notify listeners
        ZoomLevel zoomLevel = getCurrentZoomLevel();
        for (ArrangerListener listener : listeners) {
            listener.onZoomChanged(zoomLevel);
        }
    }

    private void notifyCenterChanged() {
        // Tell derived class about center change
        onCenterChanged(centerX, centerY);

        // Tell listeners
        for (ArrangerListener listener : listeners) {
            listener.onCenterChanged(getCenterX(), getCenterY());
        }
    }

    private void layoutAndNotify() {
        // This is not a user initiated view change, so preserve the flag for whether the user has done any view changes yet
        boolean oldUserZoomedOrPanned = userZoomedOrPanned;

        // Tell derived class to layout itself
        calculateLayout();

        // Notify listeners
        ZoomLevel zoomLevel = getCurrentZoomLevel();
        for (ArrangerListener listener : listeners) {
            listener.onLayoutUpdated(zoomLevel, getCenterX(), getCenterY());
        }

        userZoomedOrPanned = oldUserZoomedOrPanned;
    }

    @Override
    public void setZoomLevel(int step) {
        // Check if scale changed
        if (step != scaleStep) {
            scaleStep = step;
            scale = scaleForScaleStep(scaleStep);
            userZoomedOrPanned = true;
            notifyZoomChanged();
        }
    }

    private double scaleForScaleStep(int step) {

        double s;
        final double halfStep = Math.floor(0.5 * step);
        final boolean oddStep = step % 2 != 0;
        if (oddStep) {
            double s1 =  pow(ZOOM_SCALE_PER_STEP, halfStep);
            double s2 =  pow(ZOOM_SCALE_PER_STEP, halfStep + 1);
            s = (s1 + s2) / 2;
        }
        else {
            s =  pow(ZOOM_SCALE_PER_STEP, halfStep);
        }

        return s;
    }

    public final double getCenterX() {
        return centerX;
    }

    public final double getCenterY() {
        return centerY;
    }

    @Override
    public final void renderImage(FastImage target) {
        renderImage(target, getCenterX(), getCenterY(), getScale());
    }

    /**
     * Render the pictures to the specified target, at the specified view position and scale.
     */
    protected abstract void renderImage(FastImage target, double centerX, double centerY, double scale);

    /**
     * @return the picture at the specified index, or the preview if the picture is not available, or null if there is nothing there yet.
     */
    protected final Picture getPictureOrPreviewOrNull(int pictureIndex) {
        final Picture picture = pictures.get(pictureIndex);
        final Picture preview = previews.get(pictureIndex);
        if (picture == null) return preview;
        else return picture;
    }

    protected final String getPictureName(int pictureIndex) {
        return builders.get(pictureIndex).getName();
    }

    protected final CalculatorBuilder getBuilder(int pictureIndex) {
        return builders.get(pictureIndex);
    }

    protected final int getPictureCount() {
        return getBuilders().size();
    }

    /**
     * Specify the area that there is image on.
     */
    protected final void setPanBounds(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        double oldCenterX = centerX;
        double oldCenterY = centerY;

        centerX = (minX + maxX) / 2;
        centerY = (minY + maxY) / 2;

        clampCenter(minX, minY, maxX, maxY);

        if (centerX != oldCenterX ||
            centerY != oldCenterY) {
            notifyCenterChanged();
        }
    }

    /**
     * Called when the content is no longer valid.
     */
    protected void clearScreen() {
    }

    /**
     * Called when the zoom changes.
     * @param scale the scale to show the image with,
     *              1 = 1 screen pixel is 1 picture pixel,
     *              2 = 2 screen pixels are 1 picture pixel,
     *              0.5 = 2 picture pixels are 1 screen pixel, etc.
     */
    protected void onZoomChanged(double scale) {}

    /**
     * Called when the center location changes.
     *
     * @param centerX the x pixel position of the source picture that should be at the center of the view image.
     * @param centerY the y pixel position of the source picture that should be at the center of the view image.
     */
    protected void onCenterChanged(double centerX, double centerY) {}

    /**
     * Called when we have new valid content.
     */
    protected void onContentChanged(List<CalculatorBuilder> builders) {}

    /**
     * Should do any needed basic layout calculations.
     * Called when the builders or view size changes.
     * Should call setPanBounds to specify the area that can be panned in.
     */
    protected abstract void calculateLayout();

    private void clampCenter(double minX, double minY, double maxX, double maxY) {
        centerX = MathTools.clamp(centerX, minX, maxX);
        centerY = MathTools.clamp(centerY, minY, maxY);
    }


    @Override
    public final void addListener(ArrangerListener listener) {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(ArrangerListener listener) {
        listeners.remove(listener);
    }




}
