package org.rasterfun.distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Note: Do not use for any large number of points!
 */
public class BruteForceDistribution implements Distribution {

    private List<DistributionPoint> points = new ArrayList<DistributionPoint>();


    public DistributionPoint addPoint() {
        return addPoint(new DistributionPoint());
    }

    public DistributionPoint addPoint(DistributionPoint point) {
        points.add(point);
        return point;
    }

    public void addPoints(Collection<DistributionPoint> pointsToAdd) {
        points.addAll(pointsToAdd);
    }

    public void removePoint(DistributionPoint point) {
        points.remove(point);
    }

    @Override
    public DistributionPoint getClosestPoint(float x, float y, DistributionPoint closestOut) {

        DistributionPoint closestPoint = closestOut;
        float closestDistanceSquared = Float.POSITIVE_INFINITY;
        for (DistributionPoint point : points) {
            final float dx = point.x - x;
            final float dy = point.y - y;
            final float distSquared = dx * dx + dy * dy;
            if (distSquared < closestDistanceSquared) {
                closestDistanceSquared = distSquared;
                closestPoint = point;
            }
        }

        closestOut.set(closestPoint);
        closestOut.distanceSquared = closestDistanceSquared;

        return closestOut;
    }

    @Override
    public void getClosest2Points(float x, float y, DistributionPoint closestOut, DistributionPoint secondClosestOut) {
        DistributionPoint closestPoint1 = closestOut;
        DistributionPoint closestPoint2 = closestOut;
        float closestDistanceSquared1 = Float.POSITIVE_INFINITY;
        float closestDistanceSquared2 = Float.POSITIVE_INFINITY;

        for (DistributionPoint point : points) {
            float dx = point.x - x;
            float dy = point.y - y;
            final float distSquared = dx * dx + dy * dy;
            if (distSquared < closestDistanceSquared1) {
                closestDistanceSquared2 = closestDistanceSquared1;
                closestPoint2 = closestPoint1;

                closestDistanceSquared1 = distSquared;
                closestPoint1 = point;
            }
            else if (distSquared < closestDistanceSquared2) {
                closestDistanceSquared2 = distSquared;
                closestPoint2 = point;
            }
        }

        closestOut.set(closestPoint1);
        secondClosestOut.set(closestPoint2);
        closestOut.distanceSquared = closestDistanceSquared1;
        secondClosestOut.distanceSquared = closestDistanceSquared2;
    }

    @Override
    public void getClosest3Points(float x,
                                  float y,
                                  DistributionPoint out1,
                                  DistributionPoint out2,
                                  DistributionPoint out3) {
        DistributionPoint closestPoint1 = out1;
        DistributionPoint closestPoint2 = out2;
        DistributionPoint closestPoint3 = out3;
        float closestDistanceSquared1 = Float.POSITIVE_INFINITY;
        float closestDistanceSquared2 = Float.POSITIVE_INFINITY;
        float closestDistanceSquared3 = Float.POSITIVE_INFINITY;

        for (DistributionPoint point : points) {
            final float dx = point.x - x;
            final float dy = point.y - y;
            final float distSquared = dx * dx + dy * dy;
            if (distSquared < closestDistanceSquared1) {
                closestDistanceSquared3 = closestDistanceSquared2;
                closestPoint3 = closestPoint2;

                closestDistanceSquared2 = closestDistanceSquared1;
                closestPoint2 = closestPoint1;

                closestDistanceSquared1 = distSquared;
                closestPoint1 = point;
            }
            else if (distSquared < closestDistanceSquared2) {
                closestDistanceSquared3 = closestDistanceSquared2;
                closestPoint3 = closestPoint2;

                closestDistanceSquared2 = distSquared;
                closestPoint2 = point;
            }
            else if (distSquared < closestDistanceSquared3) {
                closestDistanceSquared3 = distSquared;
                closestPoint3 = point;
            }
        }

        out1.set(closestPoint1);
        out2.set(closestPoint2);
        out3.set(closestPoint3);
        out1.distanceSquared = closestDistanceSquared1;
        out2.distanceSquared = closestDistanceSquared2;
        out3.distanceSquared = closestDistanceSquared3;
    }

    @Override
    public void getClosest4Points(float x,
                                  float y,
                                  DistributionPoint out1,
                                  DistributionPoint out2,
                                  DistributionPoint out3,
                                  DistributionPoint out4) {
        DistributionPoint closestPoint1 = out1;
        DistributionPoint closestPoint2 = out2;
        DistributionPoint closestPoint3 = out3;
        DistributionPoint closestPoint4 = out4;
        float closestDistanceSquared1 = Float.POSITIVE_INFINITY;
        float closestDistanceSquared2 = Float.POSITIVE_INFINITY;
        float closestDistanceSquared3 = Float.POSITIVE_INFINITY;
        float closestDistanceSquared4 = Float.POSITIVE_INFINITY;

        for (DistributionPoint point : points) {
            final float dx = point.x - x;
            final float dy = point.y - y;
            final float distSquared = dx * dx + dy * dy;
            if (distSquared < closestDistanceSquared1) {
                closestDistanceSquared4 = closestDistanceSquared3;
                closestPoint4 = closestPoint3;

                closestDistanceSquared3 = closestDistanceSquared2;
                closestPoint3 = closestPoint2;

                closestDistanceSquared2 = closestDistanceSquared1;
                closestPoint2 = closestPoint1;

                closestDistanceSquared1 = distSquared;
                closestPoint1 = point;
            }
            else if (distSquared < closestDistanceSquared2) {
                closestDistanceSquared4 = closestDistanceSquared3;
                closestPoint4 = closestPoint3;

                closestDistanceSquared3 = closestDistanceSquared2;
                closestPoint3 = closestPoint2;

                closestDistanceSquared2 = distSquared;
                closestPoint2 = point;
            }
            else if (distSquared < closestDistanceSquared3) {
                closestDistanceSquared4 = closestDistanceSquared3;
                closestPoint4 = closestPoint3;

                closestDistanceSquared3 = distSquared;
                closestPoint3 = point;
            }
            else if (distSquared < closestDistanceSquared4) {
                closestDistanceSquared4 = distSquared;
                closestPoint4 = point;
            }
        }

        out1.set(closestPoint1);
        out2.set(closestPoint2);
        out3.set(closestPoint3);
        out4.set(closestPoint4);
        out1.distanceSquared = closestDistanceSquared1;
        out2.distanceSquared = closestDistanceSquared2;
        out3.distanceSquared = closestDistanceSquared3;
        out4.distanceSquared = closestDistanceSquared4;
    }

    @Override
    public void getOverlappingPoints(float x, float y, List<DistributionPoint> overlappingOut) {
        for (DistributionPoint point : points) {
            final float dx = point.x - x;
            final float dy = point.y - y;
            final float radiusSquared = point.radius * point.radius;
            final float distSquared = dx * dx + dy * dy;
            if (distSquared <= radiusSquared) {
                point.distanceSquared = distSquared;
                overlappingOut.add(point);
            }
        }
    }
}
