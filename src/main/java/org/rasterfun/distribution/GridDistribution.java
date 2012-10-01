package org.rasterfun.distribution;

import java.util.List;

/**
 * Distribution based on a regular grid, possibly with some jitter.
 */
public class GridDistribution implements Distribution {

    private long seed = 42;
    private float spacingX = 1;
    private float spacingY = 1;
    private float offsetX = 0;
    private float offsetY = 0;
    private float stepX = 0;
    private float stepY = 0;
    private float jitterX = 0;
    private float jitterY = 0;


    @Override
    public DistributionPoint getClosestPoint(float x, float y, DistributionPoint closestOut) {
        // TODO: Implement
        return null;
    }

    @Override
    public void getClosest2Points(float x, float y, DistributionPoint closestOut, DistributionPoint secondClosestOut) {
        // TODO: Implement

    }

    @Override
    public void getClosest3Points(float x,
                                  float y,
                                  DistributionPoint out1,
                                  DistributionPoint out2,
                                  DistributionPoint out3) {
        // TODO: Implement

    }

    @Override
    public void getClosest4Points(float x,
                                  float y,
                                  DistributionPoint out1,
                                  DistributionPoint out2,
                                  DistributionPoint out3,
                                  DistributionPoint out4) {
        // TODO: Implement

    }

    @Override
    public void getOverlappingPoints(float x, float y, List<DistributionPoint> overlappingOut) {
        // TODO: Implement

    }
}
