package org.rasterfun.geometry

import org.rasterfun.component.Comp
import org.rasterfun.components.IntensityComp
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * A segment of a stroke or line.
 * Can provide distance to the line center, and distance along the line.
 */
// TODO: Support for 2D value raster for this (distance to closest line
// (- for left side, + for right side, 0 on the line),
// and distance along the line (0 at line start, and 1 at line end?, then <0 and >0 are line ends..
// Maybe also more dimensions, for parameters like pressure, velocity, tilt, etc.
// A number of the parameters together can be used to determine the priority / z value of a specific stroke,
// the distance to the stroke can be used by default.
trait Segment extends Comp {


  override def channels = super.channels union
                          Set('along, 'sideways, 'strength, 'speed)

  def along(pos: inVec2): Float
  def sideways(pos: inVec2): Float
  def strength(pos: inVec2): Float
  def speed(pos: inVec2): Float

  def rgba(pos: inVec2) = Vec4(along(pos),
                               sideways(pos),
                               speed(pos),
                               strength(pos))


  override def intensity(pos: inVec2) = abs(sideways(pos))

  override def nonStandardChannel(channel: Symbol, pos: inVec2): Float = {
    channel match {
      case 'along => along(pos)
      case 'sideways => sideways(pos)
      case 'strength => strength(pos)
      case 'speed => speed(pos)
      case _ => 0f
    }
  }
}

