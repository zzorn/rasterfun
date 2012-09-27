package org.rasterfun.distribution;

/**
 *
 */
public class DistributionPoint {

    public float x;
    public float y;
    public float radius;
    public long randomSeed;

    public float distanceSquared;

    // TODO: System to keep arbitrary values (e.g. colors, values, heights, etc), maybe float array?
    public float angle;
    public float weight;
    public float picIndex;

    public DistributionPoint() {
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(DistributionPoint source) {
        x = source.x;
        y = source.y;
        distanceSquared = source.distanceSquared;
        radius = source.radius;
        angle = source.angle;
        weight = source.weight;
        randomSeed = source.randomSeed;
        picIndex = source.picIndex;
    }
}
