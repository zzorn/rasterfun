package org.rasterfun.geometry

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * Non-freehand segment, which path is rendered using some function.
 */
// TODO: 2D editor for points (shape editor / drawing tools)
// Show the handles on the preview picture when the shape is selected?
trait GuidedSegment extends Segment {

  val start = p('start, Vec2(0, 0))
  val end   = p('end,   Vec2(1, 1))

  val startStrength = p('startStrength, 1f)
  val endStrength   = p('endStrength, 0f)
  val startSpeed = p('startSpeed, 0f)
  val endSpeed   = p('endSpeed, 1f)
  val startDirection = p('startDirection, 0f) // Direction angle in turns (0..1)
  val endDirection = p('endDirection, 0f) // Direction angle in turns (0..1)

}

