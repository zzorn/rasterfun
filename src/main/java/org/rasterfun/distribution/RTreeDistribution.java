package org.rasterfun.distribution;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntProcedure;
import gnu.trove.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * R-Tree based distribution, features fast neighbor and intersection lookup.
 * Queries are thread safe, additions and removals are not.
 */
public class RTreeDistribution extends ManualDistributionBase {

    private SpatialIndex spatialIndex = new RTree();
    private TIntObjectHashMap<DistributionPoint> pointLookupMap = new TIntObjectHashMap<DistributionPoint>();
    private TObjectIntHashMap<DistributionPoint> idLookupMap = new TObjectIntHashMap<DistributionPoint>();
    private int nextId = 1;

    private ThreadLocal<Point> tempPoint = new ThreadLocal<Point>();
    private ThreadLocal<Rectangle> tempRectangle = new ThreadLocal<Rectangle>();
    private ThreadLocal<List<DistributionPoint>> resultPoints = new ThreadLocal<List<DistributionPoint>>() {
        @Override
        protected List<DistributionPoint> initialValue() {
            return new ArrayList<DistributionPoint>();
        }
    };

    private final TIntProcedure resultCollector = new TIntProcedure() {
        @Override
        public boolean execute(int id) {
            final List<DistributionPoint> result = resultPoints.get();
            result.add(pointLookupMap.get(id));
            return true;
        }
    };

    @Override
    public DistributionPoint addPoint(DistributionPoint point) {
        final Rectangle rectangle = createRectangleForPoint(point);
        int id = nextId++;
        spatialIndex.add(rectangle, id);
        pointLookupMap.put(id, point);
        idLookupMap.put(point, id);

        return point;
    }

    private Rectangle createRectangleForPoint(DistributionPoint point) {
        final float x1 = point.x - point.radius;
        final float y1 = point.x + point.radius;
        final float x2 = point.y - point.radius;
        final float y2 = point.y + point.radius;
        return new Rectangle(x1, y1, x2, y2);
    }

    @Override
    public void removePoint(DistributionPoint point) {
        final Rectangle rectangle = createRectangleForPoint(point);
        final int id = idLookupMap.get(point);
        spatialIndex.delete(rectangle, id);
    }

    @Override
    public DistributionPoint getClosestPoint(float x, float y, DistributionPoint closestOut) {
        final List<DistributionPoint> result = getClosestPoints(x, y, 1);

        return assignResult(result, 0, closestOut);
    }

    @Override
    public void getClosest2Points(float x, float y, DistributionPoint closestOut, DistributionPoint secondClosestOut) {
        final List<DistributionPoint> result = getClosestPoints(x, y, 2);

        assignResult(result, 0, closestOut);
        assignResult(result, 1, secondClosestOut);
    }

    @Override
    public void getClosest3Points(float x,
                                  float y,
                                  DistributionPoint out1,
                                  DistributionPoint out2,
                                  DistributionPoint out3) {
        final List<DistributionPoint> result = getClosestPoints(x, y, 3);

        assignResult(result, 0, out1);
        assignResult(result, 1, out2);
        assignResult(result, 2, out3);
    }

    @Override
    public void getClosest4Points(float x,
                                  float y,
                                  DistributionPoint out1,
                                  DistributionPoint out2,
                                  DistributionPoint out3,
                                  DistributionPoint out4) {
        final List<DistributionPoint> result = getClosestPoints(x, y, 4);

        assignResult(result, 0, out1);
        assignResult(result, 1, out2);
        assignResult(result, 2, out3);
        assignResult(result, 3, out4);
    }

    @Override
    public void getOverlappingPoints(float x, float y, List<DistributionPoint> overlappingOut) {
        final List<DistributionPoint> result = clearResult();
        spatialIndex.intersects(getTempRectangle(x, y, 0, 0), resultCollector);

        overlappingOut.clear();
        overlappingOut.addAll(result);
    }

    private DistributionPoint assignResult(List<DistributionPoint> result,
                                           int index,
                                           DistributionPoint resultOut) {
        if (result.size() > index) {
            final DistributionPoint resultPoint = result.get(index);
            resultOut.set(resultPoint);
        }

        return resultOut;
    }

    private List<DistributionPoint> getClosestPoints(float x, float y, final int num) {
        final List<DistributionPoint> result = clearResult();
        spatialIndex.nearestN(getTempPoint(x, y), resultCollector, num, Float.POSITIVE_INFINITY);
        return result;
    }

    private List<DistributionPoint> clearResult() {
        final List<DistributionPoint> result = resultPoints.get();
        result.clear();
        return result;
    }

    private Point getTempPoint(float x, float y) {
        Point point = tempPoint.get();
        if (point == null) {
            point = new Point(x, y);
            tempPoint.set(point);
        }
        else {
            point.x = x;
            point.y = y;
        }
        return point;
    }

    private Rectangle getTempRectangle(float x, float y, float w, float h) {
        Rectangle rectangle = tempRectangle.get();
        if (rectangle == null) {
            rectangle = new Rectangle(x, y, x+w, y+h);
            tempRectangle.set(rectangle);
        }
        else {
            rectangle.set(x, y, x+w, y+h);
        }
        return rectangle;
    }

}
