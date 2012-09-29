package org.rasterfun.distribution;

import java.util.Collection;

/**
 *
 */
public interface ManualDistribution extends Distribution {

    DistributionPoint addPoint();

    DistributionPoint addPoint(DistributionPoint point);

    void addPoints(Collection<DistributionPoint> pointsToAdd);

    void removePoint(DistributionPoint point);
}
