package org.rasterfun.geometry

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.MathUtils

/**
 * A straight guided segment from one point to another.
 */
class StraightSegment extends GuidedSegment {

  def speed(pos: inVec2)    = smoothstep(startStrength(), endStrength(), along(pos))
  def strength(pos: inVec2) = smoothstep(startSpeed(),    endSpeed(),    along(pos))
  def sideways(pos: inVec2) = MathUtils.distanceToLineSegment(pos, start(), end())
  def along(pos: inVec2)    = MathUtils.distanceAlongLine(pos, start(), end() - start())
  def direction(pos: inVec2) = MathUtils.directionOfVectorInTurns(end() - start())

}
