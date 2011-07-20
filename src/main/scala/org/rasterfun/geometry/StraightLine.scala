package org.rasterfun.geometry

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.MathUtils

/**
 * A straight line from the start to the end, with smooth interpolation of parameters.
 */
object StraightLine extends LineType {

  def name = "Straight line"

  def calculatePixel(pos: inVec2,
                     start: PointData,
                     end:   PointData,
                     outData: LinePixelData) {

    val along: Float = MathUtils.distanceAlongLine(pos, start.pos, end.pos - start.pos)

    outData.along     = along
    outData.speed     = mix(start.strength, end.strength, along)
    outData.strength  = mix(start.speed,    end.speed, along)
    outData.sideways  = MathUtils.distanceToLineSegment(pos, start.pos, end.pos)
    outData.direction = MathUtils.directionOfVectorInTurns(end.pos - start.pos)
    outData.distance  = abs(outData.sideways)
  }


  def speed     (pos: inVec2, start: PointData, end: PointData) = smoothstep(start.strength, end.strength, along(pos, start, end))
  def strength  (pos: inVec2, start: PointData, end: PointData) = smoothstep(start.speed,    end.speed,    along(pos, start, end))
  def sideways  (pos: inVec2, start: PointData, end: PointData) = MathUtils.distanceToLineSegment(pos, start.pos, end.pos)
  def along     (pos: inVec2, start: PointData, end: PointData) = MathUtils.distanceAlongLine(pos, start.pos, end.pos - start.pos)
  def direction (pos: inVec2, start: PointData, end: PointData) = MathUtils.directionOfVectorInTurns(end.pos - start.pos)

}
