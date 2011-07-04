package org.rasterfun.geometry

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * Represents a quadratic spline segment (start & endpoint and one control point).
 * Cubic splines can be relatively well approximated with 2 or 4 quadratic splines,
 * and arbitrary brush strokes can be broken down into approximated quadratic splines.
 *
 * Based on algorithm from http://blog.gludion.com/2009/08/distance-to-quadratic-bezier-curve.html
 */
case class Spline(start: inVec2, control: inVec2, end: inVec2) /*extends Segment*/ {

  /* *
   * Returns the distance to closest tangent point on the spline (positive for one side, negative for the other),
   * and the position of the closest tangent point along the spine (0 for start, 1 for end).
   *
   * Returns true if there is a tangent point on the spine to the input pos, false if none
   * (the pos lies beyond the start or end of the spline - in this case the distance will be the distance to the start or endpoint,
   * and the position will be the start or endpoint).
   * /
  def distanceAndPosition(pos: Vec2, distAndPos: outVec2): Boolean = {
		// a temporary util vect = p0 - (x,y)
    val
		pos.x = p0.x - x;
		pos.y = p0.y - y;
		// search points P of bezier curve with PM.(dP / dt) = 0
		// a calculus leads to a 3d degree equation :
		var a:Number = B.x * B.x + B.y * B.y;
		var b:Number = 3 * (A.x * B.x + A.y * B.y);
		var c:Number = 2 * (A.x * A.x + A.y * A.y) + pos.x * B.x + pos.y * B.y;
		var d:Number = pos.x * A.x + pos.y * A.y;
		var sol:Object = thirdDegreeEquation(a,b,c,d);
		if (sol == null) return null;
		// find the closest point:
		var t:Number;
		var dist:Number;
		var tMin:Number;
		var distMin:Number = Number.MAX_VALUE;
		var lx:Number;
		var ly:Number;
		for (var i = 1; i <= sol.count; i++)
		{
			t = sol["s" + i];
			if (t >= 0 && t <= 1)
			{
				pos = getPos(t);
				lx = x - pos.x;
				ly = y - pos.y;
				dist = Math.sqrt(lx * lx + ly * ly);
				if (dist < distMin)
				{
					// minimum found!
					tMin = t;
					distMin = dist;
					posMin.x = pos.x;
					posMin.y = pos.y;
				}
			}
		}
		if (tMin != null)
		{
			// normal:
			nor.x = A.y + tMin * B.y;
			nor.y = -(A.x + tMin * B.x);
			// normalize:
			var lNorm:Number = Math.sqrt(nor.x * nor.x + nor.y * nor.y);
			if (lNorm > 0)
			{
				nor.x /= lNorm;
				nor.y /= lNorm;
			}
			nearest.t = tMin;
			nearest.pos = posMin;
			nearest.nor = nor;
			if ((x - posMin.x) * nor.x + (y - posMin.y) * nor.y < 0)
			{
				// dist is more like an oriented distance. positive = on the left of the curve, negative = on the right...
				distMin *= -1;
			}
			nearest.dist = distMin;
			return nearest;
		}
		else return null;
	}

*/

}