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
public abstract class PictureArrangerBase implements PictureArranger {

    private static final int MAX_ZOOM_LEVEL = 10;
    private static final int MIN_ZOOM_LEVEL = -10;
    private static final double ZOOM_SCALE_PER_STEP =  2;
    private static final double ZOOM_SCALE2_PER_STEP =  1.5;

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

    private List<Picture> pictures = new ArrayList<Picture>();
    private List<Picture> previews = new ArrayList<Picture>();

    private PictureDrawer drawer = new RgbPictureDrawer();
    private List<ZoomLevel> zoomLevels;

    protected PictureArrangerBase() {
        zoomLevels = new ArrayList<ZoomLevel>();
        for (int i = MIN_ZOOM_LEVEL; i <= MAX_ZOOM_LEVEL; i++) {
            zoomLevels.add(new ZoomLevel(i, scaleForScaleStep(i)));
        }
    }

    @Override
    public final void setContentInfo(List<CalculatorBuilder> builders) {
        this.builders = builders;

        pictures.clear();
        previews.clear();

        for (int i = 0; i < builders.size(); i++) {
            pictures.add(null);
            previews.add(null);
        }

        clearScreen();
        onContentChanged(builders);
        calculateLayout();
    }

    @Override
    public final boolean zoom(int steps, double focusX, double focusY) {
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

            onZoomChanged(scale);

            return true;
        }
        return false;
    }

    @Override
    public final boolean pan(double deltaX, double deltaY) {
        double oldCenterX = centerX;
        double oldCenterY = centerY;

        centerX -= deltaX / scale;
        centerY -= deltaY / scale;

        clampCenter(minX, minY, maxX, maxY);

        boolean centerMoved = centerX != oldCenterX ||
                              centerY != oldCenterY;

        if (centerMoved) onCenterChanged(centerX, centerY);

        return centerMoved;
    }

    @Override
    public final void setPreview(int pictureIndex, Picture preview) {
        previews.set(pictureIndex, preview);
    }

    @Override
    public final void setPicture(int pictureIndex, Picture picture) {
        pictures.set(pictureIndex, picture);
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
        calculateLayout();
    }

    protected void onViewSizeChanged(int width, int height) {}

    public final boolean setScale(double targetScale) {
        int originalScaleStep = scaleStep;

        // Find closest zoom level one step out that fits this one.
        scaleStep = 0;

        // Zoom in while scale would still be smaller than the target scale, and we don't hit max.
        while (scaleForScaleStep(scaleStep + 1) < targetScale && scaleStep < MAX_ZOOM_LEVEL) {
            scaleStep++;
        }

        // Zoom out while we are larger than the target scale, and we dont hit min
        while (scaleForScaleStep(scaleStep) > targetScale && scaleStep > MIN_ZOOM_LEVEL) {
            scaleStep--;
        }

        // Check if scale changed
        if (scaleStep != originalScaleStep) {
            scale = scaleForScaleStep(scaleStep);
            onZoomChanged(scale);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean setZoomLevel(int step) {
        // Check if scale changed
        if (step != scaleStep) {
            scaleStep = step;
            scale = scaleForScaleStep(scaleStep);
            onZoomChanged(scale);
            return true;
        }
        else {
            return false;
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
            centerY != oldCenterY) onCenterChanged(centerX, centerY);
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

}
