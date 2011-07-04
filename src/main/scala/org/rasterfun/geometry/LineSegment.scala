package org.rasterfun.geometry

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.MathUtils

/**
 * 
 */
// TODO: 2D editor for points (shape editor / drawing tools)
// Show the handles on the preview picture when the shape is selected?
class LineSegment extends Segment {

  val start = p('start, Vec2(0, 0))
  val end   = p('end,   Vec2(1, 1))

  val startStrength = p('startStrength, 1f)
  val endStrength   = p('endStrength, 0f)
  val startSpeed = p('startSpeed, 0f)
  val endSpeed   = p('endSpeed, 1f)

  def speed(pos: inVec2)    = smoothstep(startStrength(), endStrength(), along(pos))
  def strength(pos: inVec2) = smoothstep(startSpeed(),    endSpeed(),    along(pos))
  def sideways(pos: inVec2) = MathUtils.distanceToLineSegment(pos, start(), end())
  def along(pos: inVec2)    = MathUtils.distanceAlongLine(pos, start(), end() - start())

}
