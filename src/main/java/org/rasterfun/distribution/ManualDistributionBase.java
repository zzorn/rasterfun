package org.rasterfun.distribution;

import java.util.Collection;

/**
 *
 */
public abstract class ManualDistributionBase implements ManualDistribution {

    @Override
    public DistributionPoint addPoint() {
        return addPoint(new DistributionPoint());
    }

    @Override
    public void addPoints(Collection<DistributionPoint> pointsToAdd) {
        for (DistributionPoint distributionPoint : pointsToAdd) {
            addPoint(distributionPoint);
        }
    }

}
