package org.rasterfun.geometry

import org.rasterfun.component.Comp
import org.rasterfun.components.IntensityComp
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.editors.SelectionEditorFactory

/**
 * A segment of a stroke or line, as a renderable component.
 * Can provide distance to the line center, and distance along the line.
 */
// TODO: Support for 2D value raster for this (distance to closest line
// (- for left side, + for right side, 0 on the line),
// and distance along the line (0 at line start, and 1 at line end?, then <0 and >0 are line ends..
// Maybe also more dimensions, for parameters like pressure, velocity, tilt, etc.
// A number of the parameters together can be used to determine the priority / z value of a specific stroke,
// the distance to the stroke can be used by default.
// TODO: 2D editor for points (shape editor / drawing tools)
// Show the handles on the preview picture when the shape is selected?
class Segment extends Comp {

  val startPos = p('start, Vec2(0, 0)) onChange(updatePoints _)
  val endPos   = p('end,   Vec2(1, 1)) onChange(updatePoints _)

  val startStrength = p('startStrength, 1f) onChange(updatePoints _)
  val endStrength   = p('endStrength, 0f) onChange(updatePoints _)
  val startSpeed = p('startSpeed, 0f) onChange(updatePoints _)
  val endSpeed   = p('endSpeed, 1f) onChange(updatePoints _)
  val startDirection = p('startDirection, 0f) onChange(updatePoints _) // Direction angle in turns (0..1)
  val endDirection = p('endDirection, 0f) onChange(updatePoints _) // Direction angle in turns (0..1)

  val lineType = p[LineType]('lineType, StraightLine).editor(new SelectionEditorFactory[LineType](LineType.types))

  private val start = new PointData()
  private val end = new PointData()

  updatePoints()

  private def updatePoints() {
    start.pos = startPos()
    end.pos = endPos()
    start.strength = startStrength()
    end.strength = endStrength()
    start.direction = startDirection()
    end.direction = endDirection()
    start.speed = startSpeed()
    end.speed = endSpeed()
  }

  override def channels = super.channels union
                          Set('u, 'v, 'strength, 'speed, 'direction)

  def along     (pos: inVec2): Float = lineType().along     (pos, start, end)
  def sideways  (pos: inVec2): Float = lineType().sideways  (pos, start, end)
  def strength  (pos: inVec2): Float = lineType().strength  (pos, start, end)
  def speed     (pos: inVec2): Float = lineType().speed     (pos, start, end)
  def direction (pos: inVec2): Float = lineType().direction (pos, start, end) // In turns (0..1)

  def rgba(pos: inVec2) = Vec4(along(pos),
                               sideways(pos),
                               speed(pos),
                               strength(pos))


  override def intensity(pos: inVec2) = abs(sideways(pos))

  override def nonStandardChannel(channel: Symbol, pos: inVec2): Float = {
    channel match {
      case 'u => along(pos)
      case 'v => sideways(pos)
      case 'strength => strength(pos)
      case 'speed => speed(pos)
      case 'direction => direction(pos)
      case _ => 0f
    }
  }
}

