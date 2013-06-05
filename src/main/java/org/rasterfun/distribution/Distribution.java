package org.rasterfun.distribution;

import java.util.List;

/**
 * Point distribution over space / over an area.
 * Basis for noise and texture particles.
 */
// TODO: Respect wraps
public interface Distribution {

    DistributionPoint getClosestPoint(float x, float y, DistributionPoint closestOut);

    void getClosest2Points(float x, float y, DistributionPoint closestOut, DistributionPoint secondClosestOut);

    void getClosest3Points(float x, float y, DistributionPoint out1, DistributionPoint out2, DistributionPoint out3);

    void getClosest4Points(float x, float y, DistributionPoint out1, DistributionPoint out2, DistributionPoint out3, DistributionPoint out4);

    void getOverlappingPoints(float x, float y, List<DistributionPoint> overlappingOut);


}
