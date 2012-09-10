package org.rasterfun.utils;

/**
 * Utility functions related to math.
 */
public final class MathTools {

    /**
     * Tau is 2 Pi, see http://www.tauday.com
     */
    public static final double Tau = Math.PI * 2;

    /**
     * Floating point version of Tau.
     */
    public static final float TauFloat = (float) Tau;

    public static float mix(float t, float start, float end) {
        return start + t * (end - start);
    }

    public static double mix(double t, double start, double end) {
        return start + t * (end - start);
    }

    public static double mixAndClamp(double t, double start, double end) {
        return clamp(start + t * (end - start), start, end);
    }

    public static float mixAndClamp(float t, float start, float end) {
        return clamp(start + t * (end - start), start, end);
    }

    public static double relPos(double t, double start, double end) {
        if (end == start) return 0.5;
        else return (t - start) / (end - start);
    }

    public static double map(double t, double sourceStart, double sourceEnd, double targetStart, double targetEnd) {
        double r = relPos(t, sourceStart, sourceEnd);
        return mix(r, targetStart, targetEnd);
    }

    public static double mapAndClamp(double t, double sourceStart, double sourceEnd, double targetStart, double targetEnd) {
        double r = relPos(t, sourceStart, sourceEnd);
        return clamp(mix(r, targetStart, targetEnd), targetStart, targetEnd);
    }


    public static float toDegrees(double angle) {
        return (float)(360.0 * angle / Tau);
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double rollToZeroToOne(double value) {
        return value - Math.floor(value);
    }

    public static float clampToZeroToOne(float value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }

    public static double clampToZeroToOne(double value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }

    public static double clampToMinusOneToOne(double value) {
        if (value < -1) return -1;
        if (value > 1) return 1;
        return value;
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return (float) Math.sqrt(x*x + y*y);
    }

    public static float distanceSquared(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return x*x + y*y;
    }

    /**
     * Fast floor function (much faster than Math.floor()).
     */
    public static int fastFloor(final double value) {
        return value < 0.0 ? (int)(value - 1) : (int) value;
    }

}
