package org.rasterfun.ui.preview.arranger;

import static java.lang.Math.abs;

/**
 * Simple descriptor of a zoom level, to allow showing them in a combo box.
 */
public final class ZoomLevel {
    private final int step;
    private final double scale;
    private final String desc;

    public ZoomLevel(int step, double scale) {
        this(step, scale, createDesc(scale));
    }

    public ZoomLevel(int step, double scale, String desc) {
        this.step = step;
        this.scale = scale;
        this.desc = desc;
    }

    public int getStep() {
        return step;
    }

    public double getScale() {
        return scale;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZoomLevel zoomLevel = (ZoomLevel) o;

        if (Double.compare(zoomLevel.scale, scale) != 0) return false;
        if (step != zoomLevel.step) return false;
        if (desc != null ? !desc.equals(zoomLevel.desc) : zoomLevel.desc != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = step;
        temp = scale != +0.0d ? Double.doubleToLongBits(scale) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        return result;
    }

    private static String createDesc(double scale) {
        // Pad to suitable length
        return String.format("%8s", (int) (scale * 100) + "% ");
    }

    double distanceToScale(double otherScale) {
        return abs(scale - otherScale);
    }

}
